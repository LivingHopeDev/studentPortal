package com.studentmanagement.scheduling.service;

import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.scheduling.dto.RoomRequest;
import com.studentmanagement.scheduling.dto.RoomResponse;
import com.studentmanagement.scheduling.model.Venue;
import com.studentmanagement.scheduling.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    @Transactional(readOnly = true)
    public List<RoomResponse> listRooms() {
        log.debug("Listing all rooms/venues");
        return venueRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        log.info("Creating room: {}", request.getName());
        String code = request.getName().toUpperCase().replaceAll("\\s+", "_");

        if (venueRepository.findByCode(code).isPresent()) {
            throw new DuplicateResourceException("Room already exists with name: " + request.getName());
        }

        Venue venue = Venue.builder()
                .name(request.getName())
                .code(code)
                .capacity(request.getCapacity() != null ? request.getCapacity() : 0)
                .building(request.getBuilding())
                .build();
        venue = venueRepository.save(venue);
        log.info("Room created: id={}, code={}", venue.getId(), venue.getCode());
        return toResponse(venue);
    }

    private RoomResponse toResponse(Venue venue) {
        return RoomResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .building(venue.getBuilding())
                .capacity(venue.getCapacity())
                .build();
    }

}
