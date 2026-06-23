package com.studentmanagement.student.repository;

import com.studentmanagement.student.model.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<StudentClass, UUID> {
}
