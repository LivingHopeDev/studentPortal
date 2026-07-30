package com.studentmanagement.scheduling.service;

import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.scheduling.dto.ConflictCheckResponse;
import com.studentmanagement.scheduling.dto.TimetableRequest;
import com.studentmanagement.scheduling.dto.TimetableResponse;
import com.studentmanagement.scheduling.model.Timetable;
import com.studentmanagement.scheduling.model.Venue;
import com.studentmanagement.scheduling.repository.TimetableRepository;
import com.studentmanagement.scheduling.repository.VenueRepository;
import com.studentmanagement.staff.model.Staff;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.student.model.StudentClass;
import com.studentmanagement.student.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final CourseRepository courseRepository;
    private final StaffRepository staffRepository;
    private final VenueRepository venueRepository;
    private final ClassRepository classRepository;

    @Transactional
    public TimetableResponse createEntry(TimetableRequest request) {
        log.info("Creating timetable entry");

        StudentClass studentClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentClass", "id", request.getClassId()));
        Course course = courseRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", request.getStaffId()));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", request.getVenueId()));

        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        List<String> conflicts = checkForConflicts(request.getDay(), startTime, endTime,
                venue.getId(), staff.getId(), studentClass.getId());
        if (!conflicts.isEmpty()) {
            log.warn("Conflict detected: {}", conflicts);
            throw new BadRequestException("Scheduling conflict: " + String.join("; ", conflicts));
        }

        Timetable entry = Timetable.builder()
                .studentClass(studentClass)
                .course(course)
                .staff(staff)
                .venue(venue)
                .dayOfWeek(request.getDay())
                .startTime(startTime)
                .endTime(endTime)
                .build();
        entry = timetableRepository.save(entry);
        log.info("Timetable entry created: id={}", entry.getId());
        return toResponse(entry);
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> listEntries(UUID classId, UUID staffId) {
        log.debug("Listing timetable entries - class: {}, staff: {}", classId, staffId);
        List<Timetable> entries;
        if (classId != null) {
            entries = timetableRepository.findByStudentClassId(classId);
        } else if (staffId != null) {
            entries = timetableRepository.findByStaffId(staffId);
        } else {
            entries = timetableRepository.findAll();
        }
        return entries.stream().map(this::toResponse).toList();
    }

    @Transactional
    public TimetableResponse updateEntry(UUID id, TimetableRequest request) {
        log.info("Updating timetable entry: {}", id);
        Timetable entry = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable", "id", id));

        if (request.getClassId() != null) {
            entry.setStudentClass(classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("StudentClass", "id", request.getClassId())));
        }
        if (request.getSubjectId() != null) {
            entry.setCourse(courseRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId())));
        }
        if (request.getStaffId() != null) {
            entry.setStaff(staffRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", request.getStaffId())));
        }
        if (request.getVenueId() != null) {
            entry.setVenue(venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", request.getVenueId())));
        }
        if (request.getDay() != null) entry.setDayOfWeek(request.getDay());
        if (request.getStartTime() != null) entry.setStartTime(LocalTime.parse(request.getStartTime()));
        if (request.getEndTime() != null) entry.setEndTime(LocalTime.parse(request.getEndTime()));

        entry = timetableRepository.save(entry);
        log.info("Timetable entry updated: {}", id);
        return toResponse(entry);
    }

    @Transactional
    public void deleteEntry(UUID id) {
        log.info("Deleting timetable entry: {}", id);
        Timetable entry = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable", "id", id));
        timetableRepository.delete(entry);
        log.info("Timetable entry deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getClassTimetable(UUID classId) {
        log.debug("Getting timetable for class: {}", classId);
        return timetableRepository.findByStudentClassId(classId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getStaffSchedule(UUID staffId) {
        log.debug("Getting schedule for staff: {}", staffId);
        return timetableRepository.findByStaffId(staffId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConflictCheckResponse checkConflicts(TimetableRequest request) {
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());
        List<String> conflicts = checkForConflicts(request.getDay(), startTime, endTime,
                request.getVenueId(), request.getStaffId(), request.getClassId());
        return ConflictCheckResponse.builder()
                .hasConflict(!conflicts.isEmpty())
                .conflicts(conflicts)
                .build();
    }

    @Transactional(readOnly = true)
    public String exportIcs(UUID id) {
        log.debug("Exporting ICS for timetable entry: {}", id);
        Timetable entry = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable", "id", id));

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//StudentManagementPortal//Timetable//EN\n");
        ics.append("BEGIN:VEVENT\n");
        ics.append("UID:").append(entry.getId()).append("@studentportal.edu\n");
        ics.append("DTSTAMP:").append(java.time.LocalDate.now().toString().replace("-", "")).append("T")
                .append(java.time.LocalTime.now().toString().replace(":", "")).append("\n");
        ics.append("SUMMARY:").append(entry.getCourse() != null ? entry.getCourse().getName() : "Class").append("\n");
        ics.append("DESCRIPTION:Class with ")
                .append(entry.getStaff() != null ? entry.getStaff().getUser().getFullName() : "N/A")
                .append(" at ")
                .append(entry.getVenue() != null ? entry.getVenue().getName() : "N/A")
                .append("\n");
        ics.append("LOCATION:").append(entry.getVenue() != null ? entry.getVenue().getName() : "N/A").append("\n");
        ics.append("DTSTART:").append(entry.getDayOfWeek()).append("T")
                .append(entry.getStartTime().toString().replace(":", "")).append("\n");
        ics.append("DTEND:").append(entry.getDayOfWeek()).append("T")
                .append(entry.getEndTime().toString().replace(":", "")).append("\n");
        ics.append("RRULE:FREQ=WEEKLY\n");
        ics.append("END:VEVENT\n");
        ics.append("END:VCALENDAR\n");

        return ics.toString();
    }

    private List<String> checkForConflicts(String day, LocalTime startTime, LocalTime endTime,
                                            UUID venueId, UUID staffId, UUID classId) {
        List<String> conflicts = new ArrayList<>();

        List<Timetable> venueConflicts = timetableRepository
                .findByVenueIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        venueId, day, startTime, endTime);
        if (!venueConflicts.isEmpty()) {
            conflicts.add("Venue is already booked for this time slot");
        }

        List<Timetable> staffConflicts = timetableRepository
                .findByStaffIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        staffId, day, startTime, endTime);
        if (!staffConflicts.isEmpty()) {
            conflicts.add("Staff member is already scheduled for this time slot");
        }

        List<Timetable> classConflicts = timetableRepository
                .findByStudentClassIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        classId, day, startTime, endTime);
        if (!classConflicts.isEmpty()) {
            conflicts.add("Class already has a session scheduled for this time slot");
        }

        return conflicts;
    }

    private TimetableResponse toResponse(Timetable entry) {
        return TimetableResponse.builder()
                .id(entry.getId())
                .classId(entry.getStudentClass() != null ? entry.getStudentClass().getId() : null)
                .subjectId(entry.getCourse() != null ? entry.getCourse().getId() : null)
                .staffId(entry.getStaff() != null ? entry.getStaff().getId() : null)
                .venueId(entry.getVenue() != null ? entry.getVenue().getId() : null)
                .day(entry.getDayOfWeek())
                .startTime(entry.getStartTime().toString())
                .endTime(entry.getEndTime().toString())
                .subjectName(entry.getCourse() != null ? entry.getCourse().getName() : null)
                .staffName(entry.getStaff() != null ? entry.getStaff().getUser().getFullName() : null)
                .build();
    }

}
