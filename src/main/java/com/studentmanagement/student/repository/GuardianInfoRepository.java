package com.studentmanagement.student.repository;

import com.studentmanagement.student.model.GuardianInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuardianInfoRepository extends JpaRepository<GuardianInfo, UUID> {

    Optional<GuardianInfo> findByStudentId(UUID studentId);
}
