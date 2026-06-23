package com.studentmanagement.scheduling.repository;

import com.studentmanagement.scheduling.model.Exam;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {

}
