package com.studentmanagement.student.service.impl;

import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.attendance.model.SubjectAttendance;
import com.studentmanagement.attendance.repository.SubjectAttendanceRepository;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.model.VerificationToken;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.auth.service.AuthService;
import com.studentmanagement.common.enums.Gender;
import com.studentmanagement.common.enums.StudentStatus;
import com.studentmanagement.common.enums.UserStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.communication.service.EmailService;
import com.studentmanagement.student.dto.*;
import com.studentmanagement.student.model.GuardianInfo;
import com.studentmanagement.student.model.Programme;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.GuardianInfoRepository;
import com.studentmanagement.student.repository.ProgrammeRepository;
import com.studentmanagement.student.repository.StudentRepository;
import com.studentmanagement.student.service.StudentService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ProgrammeRepository programmeRepository;
    private final GuardianInfoRepository guardianInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final GradeRepository gradeRepository;
    private final SubjectAttendanceRepository subjectAttendanceRepository;
    private final SemesterRepository semesterRepository;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    private final ConcurrentHashMap<UUID, BulkImportResponse> importJobs = new ConcurrentHashMap<>();

    @Override
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
    public StudentResponse enrolStudent(EnrolmentRequest request) {
        log.info("Enrolling student: {} {} <{}> into programme: {}",
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getProgrammeId());
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail())) {
            log.warn("Enrolment failed: email already registered: {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered");
        }

        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> {
                    log.warn("Enrolment failed: programme not found: {}", request.getProgrammeId());
                    return new ResourceNotFoundException("Programme", "id", request.getProgrammeId());
                });

        String tempPassword = generateRandomPassword();
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .fullName(request.getFirstName() + " " + request.getLastName())
                .role("STUDENT")
                .status(UserStatus.PENDING)
                .emailVerified(false)
                .mfaEnabled(false)
                .failedAttempts(0)
                .build();
        user = userRepository.save(user);

        LocalDate dob = request.getDateOfBirth() != null ? LocalDate.parse(request.getDateOfBirth()) : null;
        LocalDate admissionDate = request.getAdmissionDate() != null ? LocalDate.parse(request.getAdmissionDate()) : null;

        Student student = Student.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(dob)
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender().toUpperCase()) : null)
                .nationality(request.getNationality())
                .email(request.getEmail())
                .phone(request.getPhone())
                .programme(programme)
                .admissionDate(admissionDate)
                .status(StudentStatus.PENDING)
                .build();
        student = studentRepository.save(student);

        if (request.getGuardianName() != null) {
            GuardianInfo guardian = GuardianInfo.builder()
                    .student(student)
                    .fullName(request.getGuardianName())
                    .relationship(request.getGuardianRelationship() != null
                            ? com.studentmanagement.common.enums.GuardianRelationship.valueOf(request.getGuardianRelationship().toUpperCase())
                            : null)
                    .email(request.getGuardianEmail())
                    .phone(request.getGuardianPhone())
                    .isPrimary(true)
                    .build();
            guardianInfoRepository.save(guardian);
        }

        if (request.getEmail() != null) {
            VerificationToken verificationToken = authService.createVerificationToken(user);
            emailService.sendVerificationEmail(request.getEmail(), user.getFullName(), verificationToken.getToken());
        }

        log.info("Student enrolled successfully: id={}, studentNo={}, email={}",
                student.getId(), student.getStudentNo(), student.getEmail());
        return mapToResponse(student, programme.getName());
    }

    @Override
    @Transactional
    public StudentResponse activateStudent(UUID id, StudentStatusRequest request) {
        log.info("Activating student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Activation failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        if (student.getStatus() != StudentStatus.PENDING) {
            log.warn("Activation failed: student {} status is {}, not PENDING", id, student.getStatus());
            throw new BadRequestException("Only PENDING students can be activated");
        }

        String statusStr = request.getStatus().toUpperCase();
        StudentStatus newStatus;
        try {
            newStatus = StudentStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            log.warn("Activation failed: invalid status value: {}", request.getStatus());
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }

        if (newStatus != StudentStatus.ACTIVE) {
            log.warn("Activation failed: requested status {} is not ACTIVE", newStatus);
            throw new BadRequestException("Only ACTIVE status is allowed for activation");
        }

        student.setStatus(StudentStatus.ACTIVE);

        if (student.getStudentNo() == null) {
            student.setStudentNo(generateStudentNumber());
        }

        if (student.getUser() != null) {
            User user = student.getUser();
            String defaultPassword = generateRandomPassword();
            user.setPasswordHash(passwordEncoder.encode(defaultPassword));
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);

            if (user.getEmail() != null) {
                emailService.sendCredentialsEmail(user.getEmail(), user.getFullName(),
                        student.getStudentNo(), defaultPassword);
            }
        }

        student = studentRepository.save(student);
        log.info("Student activated successfully: id={}, studentNo={}", student.getId(), student.getStudentNo());

        return mapToResponse(student,
                student.getProgramme() != null ? student.getProgramme().getName() : null);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(UUID id, UpdateStudentRequest request) {
        log.info("Updating student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        if (request.getGender() != null) student.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        if (request.getNationality() != null) student.setNationality(request.getNationality());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getAdmissionDate() != null) student.setAdmissionDate(LocalDate.parse(request.getAdmissionDate()));

        if (request.getProgrammeId() != null) {
            Programme programme = programmeRepository.findById(request.getProgrammeId())
                    .orElseThrow(() -> {
                        log.warn("Update failed: programme not found: {}", request.getProgrammeId());
                        return new ResourceNotFoundException("Programme", "id", request.getProgrammeId());
                    });
            student.setProgramme(programme);
        }

        if (request.getEmail() != null && student.getUser() != null) {
            student.getUser().setEmail(request.getEmail());
            userRepository.save(student.getUser());
        }

        student = studentRepository.save(student);

        if (request.getGuardianName() != null) {
            GuardianInfo guardian = guardianInfoRepository.findByStudentId(student.getId())
                    .orElse(GuardianInfo.builder().student(student).isPrimary(true).build());
            guardian.setFullName(request.getGuardianName());
            if (request.getGuardianRelationship() != null)
                guardian.setRelationship(com.studentmanagement.common.enums.GuardianRelationship.valueOf(request.getGuardianRelationship().toUpperCase()));
            if (request.getGuardianEmail() != null) guardian.setEmail(request.getGuardianEmail());
            if (request.getGuardianPhone() != null) guardian.setPhone(request.getGuardianPhone());
            guardianInfoRepository.save(guardian);
        }

        String programmeName = student.getProgramme() != null ? student.getProgramme().getName() : null;
        log.info("Student updated successfully: {}", id);
        return mapToResponse(student, programmeName);
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        log.info("Deleting student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Delete failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        if (student.getUser() != null) {
            userRepository.delete(student.getUser());
        }
        studentRepository.delete(student);
        log.info("Student deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudent(UUID id) {
        log.debug("Fetching student by id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });
        return mapToResponse(student,
                student.getProgramme() != null ? student.getProgramme().getName() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> listStudents(int page, int size, String sort, String status,
                                               UUID programmeId, String search) {
        log.debug("Listing students - page: {}, size: {}, status: {}, programmeId: {}, search: {}",
                page, size, status, programmeId, search);
        List<Student> students = studentRepository.findAll();
        List<Student> filtered = students;

        if (status != null && !status.isBlank()) {
            filtered = filtered.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus().name().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        if (programmeId != null) {
            filtered = filtered.stream()
                    .filter(s -> s.getProgramme() != null && programmeId.equals(s.getProgramme().getId()))
                    .collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            filtered = filtered.stream()
                    .filter(s -> (s.getFirstName() != null && s.getFirstName().toLowerCase().contains(q))
                            || (s.getLastName() != null && s.getLastName().toLowerCase().contains(q))
                            || (s.getEmail() != null && s.getEmail().toLowerCase().contains(q))
                            || (s.getStudentNo() != null && s.getStudentNo().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        return filtered.stream()
                .map(s -> mapToResponse(s, s.getProgramme() != null ? s.getProgramme().getName() : null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BulkImportResponse bulkImport(MultipartFile file) {
        UUID jobId = UUID.randomUUID();
        log.info("Processing bulk import job: {}, file: {}", jobId, file.getOriginalFilename());

        List<String> errors = new ArrayList<>();
        int total = 0;
        int success = 0;
        int failed = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                throw new BadRequestException("CSV file is empty");
            }

            String[] columns = header.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                total++;
                try {
                    String[] values = line.split(",");
                    Map<String, String> row = new HashMap<>();
                    for (int i = 0; i < columns.length && i < values.length; i++) {
                        row.put(columns[i].trim(), values[i].trim());
                    }

                    EnrolmentRequest request = EnrolmentRequest.builder()
                            .firstName(row.getOrDefault("firstName", ""))
                            .lastName(row.getOrDefault("lastName", ""))
                            .email(row.get("email"))
                            .gender(row.getOrDefault("gender", "OTHER"))
                            .dateOfBirth(row.getOrDefault("dateOfBirth", "2000-01-01"))
                            .phone(row.get("phone"))
                            .nationality(row.get("nationality"))
                            .programmeId(UUID.fromString(row.get("programmeId")))
                            .admissionDate(row.getOrDefault("admissionDate", LocalDate.now().toString()))
                            .guardianName(row.get("guardianName"))
                            .guardianPhone(row.get("guardianPhone"))
                            .guardianEmail(row.get("guardianEmail"))
                            .guardianRelationship(row.get("guardianRelationship"))
                            .build();

                    enrolStudent(request);
                    success++;
                } catch (Exception e) {
                    failed++;
                    errors.add("Row " + total + ": " + e.getMessage());
                    log.warn("Bulk import row {} failed: {}", total, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Bulk import failed to read file: {}", e.getMessage());
            throw new BadRequestException("Failed to read CSV file: " + e.getMessage());
        }

        BulkImportResponse response = BulkImportResponse.builder()
                .jobId(jobId)
                .status(failed > 0 ? (success > 0 ? "PARTIAL" : "FAILED") : "COMPLETED")
                .total(total)
                .success(success)
                .failed(failed)
                .build();

        importJobs.put(jobId, response);
        log.info("Bulk import job {} completed: {}/{}/{} (total/success/failed)", jobId, total, success, failed);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BulkImportResponse getBulkImportStatus(UUID jobId) {
        BulkImportResponse job = importJobs.get(jobId);
        if (job == null) {
            log.warn("Bulk import job not found: {}", jobId);
            throw new ResourceNotFoundException("BulkImportJob", "id", jobId);
        }
        return job;
    }

    @Override
    @Transactional
    public PhotoResponse uploadPhoto(UUID id, MultipartFile file) {
        log.info("Uploading photo for student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Photo upload failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
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
            String filename = id + extension;
            Path uploadDir = Paths.get(uploadPath, "photos");
            Files.createDirectories(uploadDir);
            Path targetPath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String photoUrl = "/uploads/photos/" + filename;
            student.setPhotoUrl(photoUrl);
            studentRepository.save(student);

            log.info("Photo uploaded for student: {}, path: {}", id, photoUrl);
            return PhotoResponse.builder().photoUrl(photoUrl).build();
        } catch (IOException e) {
            log.error("Photo upload failed for student: {}", id, e);
            throw new BadRequestException("Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse getPhoto(UUID id) {
        log.debug("Getting photo for student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Get photo failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        if (student.getPhotoUrl() == null) {
            throw new ResourceNotFoundException("Photo", "studentId", id);
        }

        return PhotoResponse.builder().photoUrl(student.getPhotoUrl()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public GuardianResponse getGuardian(UUID id) {
        log.debug("Getting guardian for student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Get guardian failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        GuardianInfo guardian = guardianInfoRepository.findByStudentId(student.getId())
                .orElseThrow(() -> {
                    log.warn("Guardian not found for student: {}", id);
                    return new ResourceNotFoundException("Guardian", "studentId", id);
                });

        return GuardianResponse.builder()
                .name(guardian.getFullName())
                .phone(guardian.getPhone())
                .email(guardian.getEmail())
                .relationship(guardian.getRelationship() != null ? guardian.getRelationship().name() : null)
                .build();
    }

    @Override
    @Transactional
    public GuardianResponse upsertGuardian(UUID id, GuardianRequest request) {
        log.info("Upserting guardian for student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Upsert guardian failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        GuardianInfo guardian = guardianInfoRepository.findByStudentId(student.getId())
                .orElse(GuardianInfo.builder().student(student).isPrimary(true).build());

        if (request.getGuardianName() != null) guardian.setFullName(request.getGuardianName());
        if (request.getGuardianPhone() != null) guardian.setPhone(request.getGuardianPhone());
        if (request.getGuardianEmail() != null) guardian.setEmail(request.getGuardianEmail());
        if (request.getGuardianRelationship() != null)
            guardian.setRelationship(com.studentmanagement.common.enums.GuardianRelationship.valueOf(request.getGuardianRelationship().toUpperCase()));

        guardian = guardianInfoRepository.save(guardian);

        log.info("Guardian upserted for student: {}", id);
        return GuardianResponse.builder()
                .name(guardian.getFullName())
                .phone(guardian.getPhone())
                .email(guardian.getEmail())
                .relationship(guardian.getRelationship() != null ? guardian.getRelationship().name() : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicSummaryResponse getAcademicSummary(UUID id) {
        log.debug("Getting academic summary for student: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Academic summary failed: student not found: {}", id);
                    return new ResourceNotFoundException("Student", "id", id);
                });

        List<Grade> grades = gradeRepository.findByStudentId(student.getId());
        List<SubjectAttendance> attendanceRecords = subjectAttendanceRepository.findByStudentId(student.getId());

        double averageScore = grades.stream()
                .filter(g -> g.getScore() != null)
                .mapToDouble(g -> g.getScore().doubleValue())
                .average()
                .orElse(0.0);

        double attendanceRate = 0.0;
        if (!attendanceRecords.isEmpty()) {
            long present = attendanceRecords.stream()
                    .filter(a -> a.getStatus() == com.studentmanagement.common.enums.AttendanceStatus.PRESENT)
                    .count();
            attendanceRate = (double) present / attendanceRecords.size() * 100.0;
        }

        Map<UUID, List<Grade>> gradesByCourse = grades.stream()
                .filter(g -> g.getCourse() != null)
                .collect(Collectors.groupingBy(g -> g.getCourse().getId()));

        Map<UUID, List<SubjectAttendance>> attendanceByCourse = attendanceRecords.stream()
                .filter(a -> a.getCourse() != null)
                .collect(Collectors.groupingBy(a -> a.getCourse().getId()));

        Set<UUID> allCourseIds = new HashSet<>();
        allCourseIds.addAll(gradesByCourse.keySet());
        allCourseIds.addAll(attendanceByCourse.keySet());

        List<AcademicSummaryResponse.SubjectSummary> subjects = new ArrayList<>();
        for (UUID courseId : allCourseIds) {
            List<Grade> courseGrades = gradesByCourse.getOrDefault(courseId, List.of());
            List<SubjectAttendance> courseAttendance = attendanceByCourse.getOrDefault(courseId, List.of());

            String subjectName = courseGrades.stream()
                    .filter(g -> g.getCourse() != null)
                    .map(g -> g.getCourse().getName())
                    .findFirst()
                    .orElseGet(() -> courseAttendance.stream()
                            .filter(a -> a.getCourse() != null)
                            .map(a -> a.getCourse().getName())
                            .findFirst()
                            .orElse("Unknown"));

            Double score = courseGrades.stream()
                    .filter(g -> g.getScore() != null)
                    .findFirst()
                    .map(g -> g.getScore().doubleValue())
                    .orElse(null);

            String grade = courseGrades.stream()
                    .filter(g -> g.getLetterGrade() != null)
                    .findFirst()
                    .map(Grade::getLetterGrade)
                    .orElse(null);

            double subjectAttendanceRate = 0.0;
            if (!courseAttendance.isEmpty()) {
                long present = courseAttendance.stream()
                        .filter(a -> a.getStatus() == com.studentmanagement.common.enums.AttendanceStatus.PRESENT)
                        .count();
                subjectAttendanceRate = (double) present / courseAttendance.size() * 100.0;
            }

            subjects.add(AcademicSummaryResponse.SubjectSummary.builder()
                    .subjectName(subjectName)
                    .score(score)
                    .grade(grade)
                    .attendancePercentage(subjectAttendanceRate)
                    .build());
        }

        return AcademicSummaryResponse.builder()
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0)
                .subjects(subjects)
                .build();
    }

    private String generateStudentNumber() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "STU-" + year + "-";
        String maxNo = studentRepository.findAll().stream()
                .map(Student::getStudentNo)
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

    private StudentResponse mapToResponse(Student student, String programmeName) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender() != null ? student.getGender().name() : null)
                .nationality(student.getNationality())
                .email(student.getEmail())
                .phone(student.getPhone())
                .studentNumber(student.getStudentNo())
                .programmeId(student.getProgramme() != null ? student.getProgramme().getId() : null)
                .programmeName(programmeName)
                .admissionDate(student.getAdmissionDate())
                .status(student.getStatus().name())
                .build();
    }
}
