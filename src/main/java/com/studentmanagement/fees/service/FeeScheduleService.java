package com.studentmanagement.fees.service;

import com.studentmanagement.fees.dto.FeeScheduleRequest;
import com.studentmanagement.fees.dto.FeeScheduleResponse;

import java.util.List;
import java.util.UUID;

public interface FeeScheduleService {

    List<FeeScheduleResponse> listSchedules();

    FeeScheduleResponse createSchedule(FeeScheduleRequest request);

    FeeScheduleResponse updateSchedule(UUID id, FeeScheduleRequest request);
}
