package com.studentmanagement.academic.repository;

import com.studentmanagement.academic.model.Grade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

    List<Grade> findByStudentId(UUID studentId);

    List<Grade> findByCourseId(UUID courseId);

    List<Grade> findBySemesterId(UUID semesterId);

    List<Grade> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

    Optional<Grade> findByStudentIdAndCourseIdAndSemesterId(UUID studentId, UUID courseId, UUID semesterId);

}
