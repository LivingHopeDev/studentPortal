package com.studentmanagement.attendance.service;

import com.studentmanagement.attendance.dto.AbsenceAlertResponse;
import com.studentmanagement.attendance.dto.ThresholdRequest;

import java.util.List;

public interface AbsenceAlertService {

    List<AbsenceAlertResponse> getAlerts();

    void updateAlertThreshold(ThresholdRequest request);
}
