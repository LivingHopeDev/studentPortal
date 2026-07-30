package com.studentmanagement.communication.service;

import com.studentmanagement.communication.dto.MessageResponse;
import com.studentmanagement.communication.dto.ReplyRequest;
import com.studentmanagement.communication.dto.SendMessageRequest;
import com.studentmanagement.communication.dto.ThreadResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse send(SendMessageRequest request);

    List<ThreadResponse> listThreads(int page, int size);

    List<MessageResponse> getThread(UUID threadId);

    MessageResponse reply(UUID threadId, ReplyRequest request);

    void markAsRead(UUID id);
}
