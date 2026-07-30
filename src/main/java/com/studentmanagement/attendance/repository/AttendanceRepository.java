package com.studentmanagement.attendance.repository;

import com.studentmanagement.attendance.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    List<Attendance> findByStudentId(UUID studentId);

    List<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentClassId(UUID classId);

}
