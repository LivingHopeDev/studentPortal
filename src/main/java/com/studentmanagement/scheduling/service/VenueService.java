package com.studentmanagement.scheduling.service;

import com.studentmanagement.scheduling.dto.RoomRequest;
import com.studentmanagement.scheduling.dto.RoomResponse;

import java.util.List;

public interface VenueService {

    List<RoomResponse> listRooms();

    RoomResponse createRoom(RoomRequest request);
}
