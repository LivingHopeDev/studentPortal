package com.studentmanagement.student.repository;

import com.studentmanagement.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByUserId(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByStudentNo(String studentNo);
}
