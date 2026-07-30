package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.PeriodRequest;
import com.studentmanagement.academic.dto.PeriodResponse;

import java.util.List;
import java.util.UUID;

public interface PeriodService {

    List<PeriodResponse> listPeriods();

    PeriodResponse createPeriod(PeriodRequest request);

    PeriodResponse updatePeriod(UUID id, PeriodRequest request);

    PeriodResponse setCurrentPeriod(UUID id);
}
