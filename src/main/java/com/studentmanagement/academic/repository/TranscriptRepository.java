package com.studentmanagement.academic.repository;

import com.studentmanagement.academic.model.Transcript;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, UUID> {

}
