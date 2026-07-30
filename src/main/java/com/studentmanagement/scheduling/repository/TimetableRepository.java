package com.studentmanagement.scheduling.repository;

import com.studentmanagement.scheduling.model.Timetable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID> {

    List<Timetable> findByStudentClassId(UUID classId);

    List<Timetable> findByStaffId(UUID staffId);

    List<Timetable> findByVenueIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            UUID venueId, String dayOfWeek, java.time.LocalTime startTime, java.time.LocalTime endTime);

    List<Timetable> findByStaffIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            UUID staffId, String dayOfWeek, java.time.LocalTime startTime, java.time.LocalTime endTime);

    List<Timetable> findByStudentClassIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            UUID classId, String dayOfWeek, java.time.LocalTime startTime, java.time.LocalTime endTime);

}
