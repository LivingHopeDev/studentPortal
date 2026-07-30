package com.studentmanagement.staff.repository;

import com.studentmanagement.staff.model.StaffCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffCourseRepository extends JpaRepository<StaffCourse, UUID> {

    List<StaffCourse> findByStaffId(UUID staffId);

    Optional<StaffCourse> findByStaffIdAndCourseId(UUID staffId, UUID courseId);

    void deleteByStaffIdAndCourseId(UUID staffId, UUID courseId);
}
