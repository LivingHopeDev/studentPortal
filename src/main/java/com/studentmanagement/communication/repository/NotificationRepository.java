package com.studentmanagement.communication.repository;

import com.studentmanagement.communication.model.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

}
