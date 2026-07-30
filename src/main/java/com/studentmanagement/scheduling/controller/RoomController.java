package com.studentmanagement.scheduling.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.scheduling.dto.RoomRequest;
import com.studentmanagement.scheduling.dto.RoomResponse;
import com.studentmanagement.scheduling.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedules/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listRooms() {
        log.debug("Listing rooms");
        List<RoomResponse> response = venueService.listRooms();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        log.info("Creating room: {}", request.getName());
        RoomResponse response = venueService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
    }

}
