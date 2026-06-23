package com.studentmanagement.student.repository;

import com.studentmanagement.student.model.Programme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgrammeRepository extends JpaRepository<Programme, UUID> {

    Optional<Programme> findByCode(String code);
}
