package com.studentmanagement.attendance.repository;

import com.studentmanagement.attendance.model.SubjectAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectAttendanceRepository extends JpaRepository<SubjectAttendance, UUID> {

    List<SubjectAttendance> findByStudentId(UUID studentId);

    List<SubjectAttendance> findByCourseId(UUID courseId);

    List<SubjectAttendance> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

}
