package com.studentmanagement.staff.service.impl;

import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.StaffStatus;
import com.studentmanagement.common.enums.UserStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.communication.service.EmailService;
import com.studentmanagement.staff.dto.*;
import com.studentmanagement.staff.model.Staff;
import com.studentmanagement.staff.model.StaffCourse;
import com.studentmanagement.staff.model.StaffRole;
import com.studentmanagement.staff.repository.StaffCourseRepository;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.staff.repository.StaffRoleRepository;
import com.studentmanagement.staff.service.StaffService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final StaffCourseRepository staffCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath, "photos"));
        } catch (IOException e) {
            log.warn("Could not create upload directory: {}", e.getMessage());
        }
    }

    @Override
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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
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

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaff(UUID id) {
        log.debug("Fetching staff by id: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });
        return toResponse(staff, staff.getUser() != null ? staff.getUser().getRole() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> listStaff(int page, int size) {
        log.debug("Listing staff - page: {}, size: {}", page, size);
        return staffRepository.findAll().stream()
                .map(s -> toResponse(s, s.getUser() != null ? s.getUser().getRole() : null))
                .toList();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    @Transactional
    public StaffPhotoResponse uploadPhoto(UUID id, MultipartFile file) {
        log.info("Uploading photo for staff: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Photo upload failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        try {
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf('.'));
            String filename = "staff-" + id + extension;
            Path uploadDir = Paths.get(uploadPath, "photos");
            Files.createDirectories(uploadDir);
            Path targetPath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String photoUrl = "/uploads/photos/" + filename;
            staff.setPhotoUrl(photoUrl);
            staffRepository.save(staff);

            log.info("Photo uploaded for staff: {}, path: {}", id, photoUrl);
            return StaffPhotoResponse.builder().photoUrl(photoUrl).build();

        } catch (IOException e) {
            log.error("Failed to upload photo for staff: {}", id, e);
            throw new BadRequestException("Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public StaffResponse assignSubjects(UUID id, AssignSubjectsRequest request) {
        log.info("Assigning subjects to staff: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Assign subjects failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });

        Set<UUID> subjectIds = request.getSubjectIds();
        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new BadRequestException("At least one subject must be provided");
        }

        for (UUID courseId : subjectIds) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

            if (staffCourseRepository.findByStaffIdAndCourseId(id, courseId).isEmpty()) {
                staffCourseRepository.save(StaffCourse.builder()
                        .staff(staff)
                        .course(course)
                        .build());
            }
        }

        log.info("Subjects assigned to staff: {}, count={}", id, subjectIds.size());
        return toResponse(staff, staff.getUser() != null ? staff.getUser().getRole() : null);
    }

    @Override
    @Transactional
    public void removeSubject(UUID id, UUID courseId) {
        log.info("Removing subject from staff: {}, course: {}", id, courseId);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Remove subject failed: staff not found: {}", id);
                    return new ResourceNotFoundException("Staff", "id", id);
                });

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        staffCourseRepository.deleteByStaffIdAndCourseId(id, courseId);
        log.info("Subject removed from staff: {}, course: {}", id, courseId);
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
