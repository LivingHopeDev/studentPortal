package com.studentmanagement.staff.service;

import com.studentmanagement.staff.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StaffService {

    StaffResponse createStaff(StaffRequest request);

    StaffResponse getStaff(UUID id);

    List<StaffResponse> listStaff(int page, int size);

    StaffResponse updateStaff(UUID id, StaffRequest request);

    StaffResponse updateStaffStatus(UUID id, StaffStatusRequest request);

    void deleteStaff(UUID id);

    StaffPhotoResponse uploadPhoto(UUID id, MultipartFile file);

    StaffResponse assignSubjects(UUID id, AssignSubjectsRequest request);

    void removeSubject(UUID id, UUID courseId);
}
