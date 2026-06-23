package com.studentmanagement.communication.repository;

import com.studentmanagement.communication.model.Announcement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

}
