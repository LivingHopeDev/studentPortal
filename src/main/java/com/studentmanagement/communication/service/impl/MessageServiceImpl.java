package com.studentmanagement.communication.service.impl;

import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.security.SecurityUtils;
import com.studentmanagement.communication.dto.MessageResponse;
import com.studentmanagement.communication.dto.ReplyRequest;
import com.studentmanagement.communication.dto.SendMessageRequest;
import com.studentmanagement.communication.dto.ThreadResponse;
import com.studentmanagement.communication.model.Message;
import com.studentmanagement.communication.repository.MessageRepository;
import com.studentmanagement.communication.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MessageResponse send(SendMessageRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRecipientId()));

        Message message = Message.builder()
                .sender(sender)
                .receiver(recipient)
                .subject(request.getSubject())
                .body(request.getBody())
                .threadId(UUID.randomUUID())
                .isRead(false)
                .build();
        message = messageRepository.save(message);

        log.info("Message sent: id={}, from={}, to={}", message.getId(), sender.getId(), recipient.getId());
        return toMessageResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThreadResponse> listThreads(int page, int size) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        List<Message> messages = messageRepository
                .findBySenderIdOrReceiverIdOrderByCreatedAtDesc(currentUserId, currentUserId);

        Map<UUID, List<Message>> grouped = messages.stream()
                .collect(Collectors.groupingBy(Message::getThreadId));

        List<ThreadResponse> threads = new ArrayList<>();
        for (Map.Entry<UUID, List<Message>> entry : grouped.entrySet()) {
            List<Message> threadMessages = entry.getValue();
            Message lastMessage = threadMessages.get(0);
            Message firstMessage = threadMessages.get(threadMessages.size() - 1);

            User otherParticipant = lastMessage.getSender().getId().equals(currentUserId)
                    ? lastMessage.getReceiver()
                    : lastMessage.getSender();

            long unreadCount = messageRepository
                    .countByThreadIdAndReceiverIdAndIsReadFalse(entry.getKey(), currentUserId);

            String preview = lastMessage.getBody();
            if (preview != null && preview.length() > 100) {
                preview = preview.substring(0, 100) + "...";
            }

            threads.add(ThreadResponse.builder()
                    .threadId(entry.getKey())
                    .subject(firstMessage.getSubject())
                    .otherParticipantId(otherParticipant.getId())
                    .otherParticipantName(otherParticipant.getFirstName() + " " + otherParticipant.getLastName())
                    .lastMessagePreview(preview)
                    .lastMessageAt(lastMessage.getCreatedAt() != null
                            ? LocalDateTime.ofInstant(lastMessage.getCreatedAt(), ZoneId.systemDefault())
                            : null)
                    .unreadCount((int) unreadCount)
                    .build());
        }

        threads.sort((a, b) -> b.getLastMessageAt().compareTo(a.getLastMessageAt()));
        return threads;
    }

    @Override
    @Transactional
    public List<MessageResponse> getThread(UUID threadId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        List<Message> messages = messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);

        if (messages.isEmpty()) {
            throw new ResourceNotFoundException("Thread", "id", threadId);
        }

        for (Message message : messages) {
            if (message.getReceiver().getId().equals(currentUserId) && !message.getIsRead()) {
                message.setIsRead(true);
                message.setReadAt(Instant.now());
                messageRepository.save(message);
            }
        }

        return messages.stream().map(this::toMessageResponse).toList();
    }

    @Override
    @Transactional
    public MessageResponse reply(UUID threadId, ReplyRequest request) {
        List<Message> existing = messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Thread", "id", threadId);
        }

        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Message lastMessage = existing.get(existing.size() - 1);
        User recipient = lastMessage.getSender().getId().equals(currentUserId)
                ? lastMessage.getReceiver()
                : lastMessage.getSender();

        Message reply = Message.builder()
                .sender(sender)
                .receiver(recipient)
                .subject(lastMessage.getSubject())
                .body(request.getBody())
                .threadId(threadId)
                .isRead(false)
                .build();
        reply = messageRepository.save(reply);

        log.info("Reply sent: threadId={}, from={}", threadId, sender.getId());
        return toMessageResponse(reply);
    }

    @Override
    @Transactional
    public void markAsRead(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (!message.getReceiver().getId().equals(currentUserId)) {
            log.warn("User {} attempted to mark message {} as read but is not the receiver", currentUserId, id);
            return;
        }

        message.setIsRead(true);
        message.setReadAt(Instant.now());
        messageRepository.save(message);

        log.info("Message marked as read: id={}", id);
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .threadId(message.getThreadId())
                .senderId(message.getSender().getId())
                .recipientId(message.getReceiver().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .subject(message.getSubject())
                .body(message.getBody())
                .read(message.getIsRead())
                .sentAt(message.getCreatedAt() != null
                        ? LocalDateTime.ofInstant(message.getCreatedAt(), ZoneId.systemDefault())
                        : null)
                .build();
    }
}
