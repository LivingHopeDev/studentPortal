package com.studentmanagement.communication.service;

import com.studentmanagement.communication.dto.AnnouncementRequest;
import com.studentmanagement.communication.dto.AnnouncementResponse;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {

    AnnouncementResponse create(AnnouncementRequest request);

    List<AnnouncementResponse> list();

    AnnouncementResponse get(UUID id);

    AnnouncementResponse update(UUID id, AnnouncementRequest request);

    void delete(UUID id);
}
