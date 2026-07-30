package com.studentmanagement.scheduling.repository;

import com.studentmanagement.scheduling.model.Exam;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {

    List<Exam> findByCourseId(UUID courseId);

    List<Exam> findByDate(LocalDate date);

    List<Exam> findByVenueIdAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            UUID venueId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime);

}
