package com.studentmanagement.academic.repository;

import com.studentmanagement.academic.model.Course;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

}
