package com.studentmanagement.communication.repository;

import com.studentmanagement.communication.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(UUID senderId, UUID receiverId);

    List<Message> findByThreadIdOrderByCreatedAtAsc(UUID threadId);

    long countByThreadIdAndReceiverIdAndIsReadFalse(UUID threadId, UUID receiverId);
}
