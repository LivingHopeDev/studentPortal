package com.studentmanagement.academic.repository;

import com.studentmanagement.academic.model.Semester;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {

}
