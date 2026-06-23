package com.studentmanagement.attendance.repository;

import com.studentmanagement.attendance.model.SubjectAttendance;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectAttendanceRepository extends JpaRepository<SubjectAttendance, UUID> {

}
