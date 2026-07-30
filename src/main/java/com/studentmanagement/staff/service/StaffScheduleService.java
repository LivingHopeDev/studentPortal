package com.studentmanagement.staff.service;

import com.studentmanagement.staff.dto.StaffScheduleResponse;

import java.util.UUID;

public interface StaffScheduleService {

    StaffScheduleResponse getSchedule(UUID staffId);
}
