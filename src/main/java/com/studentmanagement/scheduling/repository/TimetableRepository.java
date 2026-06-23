package com.studentmanagement.scheduling.repository;

import com.studentmanagement.scheduling.model.Timetable;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID> {

}
