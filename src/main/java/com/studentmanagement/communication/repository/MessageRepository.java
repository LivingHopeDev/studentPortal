package com.studentmanagement.communication.repository;

import com.studentmanagement.communication.model.Message;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

}
