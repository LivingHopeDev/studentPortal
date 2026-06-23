package com.studentmanagement.academic.repository;

import com.studentmanagement.academic.model.Grade;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

}
