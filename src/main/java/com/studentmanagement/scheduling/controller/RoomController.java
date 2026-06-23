package com.studentmanagement.scheduling.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.scheduling.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules/rooms")
public class RoomController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listRooms() {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        return null;
    }

}
