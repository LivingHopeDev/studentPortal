package com.studentmanagement.staff.service;

import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.StaffStatus;
import com.studentmanagement.common.enums.UserStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.communication.service.EmailService;
import com.studentmanagement.staff.dto.StaffRequest;
import com.studentmanagement.staff.dto.StaffResponse;
import com.studentmanagement.staff.dto.StaffStatusRequest;
import com.studentmanagement.staff.model.Staff;
import com.studentmanagement.staff.model.StaffRole;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.staff.repository.StaffRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        log.info("Creating staff: {} {} <{}>", request.getFirstName(), request.getLastName(), request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Staff creation failed: email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered");
        }

        String defaultPassword = generateRandomPassword();
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .fullName(request.getFirstName() + " " + request.getLastName())
                .role(request.getRole() != null ? request.getRole() : "STAFF")
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .mfaEnabled(false)
                .failedAttempts(0)
                .build();
        user = userRepository.save(user);

        Staff staff = Staff.builder()
                .user(user)
                .employeeNo(generateEmployeeNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .status(StaffStatus.ACTIVE)
                .build();
        staff = staffRepository.save(staff);

        if (request.getRole() != null) {
            staffRoleRepository.save(StaffRole.builder()
                    .staff(staff)
                    .role(request.getRole())
                    .build());
        }

        emailService.sendCredentialsEmail(request.getEmail(), user.getFullName(),
                staff.getEmployeeNo(), defaultPassword);

        log.info("Staff created successfully: id={}, employeeNo={}, role={}",
                staff.getId(), staff.getEmployeeNo(), request.getRole());
        return toResponse(staff, request.getRole());
    }

    public StaffResponse getStaff(UUID id) {
        log.debug("Fetching staff by id: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });
        return toResponse(staff, staff.getUser() != null ? staff.getUser().getRole() : null);
    }

    public List<StaffResponse> listStaff(int page, int size) {
        log.debug("Listing staff - page: {}, size: {}", page, size);
        return staffRepository.findAll().stream()
                .map(s -> toResponse(s, s.getUser() != null ? s.getUser().getRole() : null))
                .toList();
    }

    @Transactional
    public StaffResponse updateStaff(UUID id, StaffRequest request) {
        log.info("Updating staff: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });

        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setPhone(request.getPhone());
        staff.setDepartment(request.getDepartment());
        staff.setDesignation(request.getDesignation());

        if (request.getEmail() != null && !request.getEmail().equals(staff.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Update failed: email already in use: {}", request.getEmail());
                throw new DuplicateResourceException("Email already in use");
            }
            staff.setEmail(request.getEmail());
            if (staff.getUser() != null) {
                staff.getUser().setEmail(request.getEmail());
                userRepository.save(staff.getUser());
            }
        }

        staff = staffRepository.save(staff);
        log.info("Staff updated successfully: id={}", staff.getId());
        return toResponse(staff, staff.getUser() != null ? staff.getUser().getRole() : null);
    }

    @Transactional
    public StaffResponse updateStaffStatus(UUID id, StaffStatusRequest request) {
        log.info("Updating status for staff: {} to {}", id, request.getStatus());
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Status update failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });

        try {
            StaffStatus newStatus = StaffStatus.valueOf(request.getStatus().toUpperCase());
            staff.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            log.warn("Status update failed: invalid status: {}", request.getStatus());
            throw new BadRequestException("Invalid status: " + request.getStatus()
                    + ". Allowed: ACTIVE, INACTIVE, SUSPENDED");
        }

        if (staff.getUser() != null) {
            UserStatus userStatus = switch (staff.getStatus()) {
                case ACTIVE -> UserStatus.ACTIVE;
                case INACTIVE, SUSPENDED -> UserStatus.INACTIVE;
            };
            staff.getUser().setStatus(userStatus);
            userRepository.save(staff.getUser());
        }

        staff = staffRepository.save(staff);
        log.info("Staff {} status updated to {}", id, staff.getStatus());
        return toResponse(staff, staff.getUser() != null ? staff.getUser().getRole() : null);
    }

    @Transactional
    public void deleteStaff(UUID id) {
        log.info("Deleting staff: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Delete failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });
        staffRepository.delete(staff);
        log.info("Staff deleted successfully: id={}", id);
    }

    private String generateEmployeeNumber() {
        String prefix = "EMP-";
        String maxNo = staffRepository.findAll().stream()
                .map(Staff::getEmployeeNo)
                .filter(s -> s != null && s.startsWith(prefix))
                .max(String::compareTo)
                .orElse(prefix + "00000");
        int nextSeq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
        return prefix + String.format("%05d", nextSeq);
    }

    private String generateRandomPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        return gen.generatePassword(8, List.of(lowerCaseRule, upperCaseRule, digitRule));
    }

    private StaffResponse toResponse(Staff staff, String role) {
        return StaffResponse.builder()
                .id(staff.getId())
                .employeeNo(staff.getEmployeeNo())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .department(staff.getDepartment())
                .designation(staff.getDesignation())
                .role(role)
                .photoUrl(staff.getPhotoUrl())
                .status(staff.getStatus().name())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }
}
