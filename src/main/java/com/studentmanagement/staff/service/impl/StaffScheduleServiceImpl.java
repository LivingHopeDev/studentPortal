package com.studentmanagement.staff.service.impl;

import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.scheduling.model.Timetable;
import com.studentmanagement.scheduling.repository.TimetableRepository;
import com.studentmanagement.staff.dto.StaffScheduleResponse;
import com.studentmanagement.staff.model.Staff;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.staff.service.StaffScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffScheduleServiceImpl implements StaffScheduleService {

    private final StaffRepository staffRepository;
    private final TimetableRepository timetableRepository;

    @Override
    @Transactional(readOnly = true)
    public StaffScheduleResponse getSchedule(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        List<Timetable> timetables = timetableRepository.findByStaffId(staffId);

        List<StaffScheduleResponse.ScheduleEntry> entries = timetables.stream()
                .map(t -> StaffScheduleResponse.ScheduleEntry.builder()
                        .day(t.getDayOfWeek())
                        .startTime(t.getStartTime().toString())
                        .endTime(t.getEndTime().toString())
                        .subjectName(t.getCourse() != null ? t.getCourse().getName() : null)
                        .className(t.getStudentClass() != null ? t.getStudentClass().getName() : null)
                        .build())
                .toList();

        log.info("Staff schedule retrieved: staffId={}, entries={}", staffId, entries.size());
        return StaffScheduleResponse.builder().entries(entries).build();
    }
}
