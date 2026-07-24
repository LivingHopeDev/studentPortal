package com.studentmanagement.student.service;

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
import com.studentmanagement.student.dto.EnrolmentRequest;
import com.studentmanagement.student.dto.StudentResponse;
import com.studentmanagement.student.dto.StudentStatusRequest;
import com.studentmanagement.student.model.GuardianInfo;
import com.studentmanagement.student.model.Programme;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.GuardianInfoRepository;
import com.studentmanagement.student.repository.ProgrammeRepository;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ProgrammeRepository programmeRepository;
    private final GuardianInfoRepository guardianInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;

    @Transactional
    public StudentResponse enrolStudent(EnrolmentRequest request) {
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Programme", "id", request.getProgrammeId()));

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

        return mapToResponse(student, programme.getName());
    }

    @Transactional
    public StudentResponse activateStudent(UUID id, StudentStatusRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (student.getStatus() != StudentStatus.PENDING) {
            throw new BadRequestException("Only PENDING students can be activated");
        }

        String statusStr = request.getStatus().toUpperCase();
        StudentStatus newStatus;
        try {
            newStatus = StudentStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }

        if (newStatus != StudentStatus.ACTIVE) {
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

        return mapToResponse(student,
                student.getProgramme() != null ? student.getProgramme().getName() : null);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return mapToResponse(student,
                student.getProgramme() != null ? student.getProgramme().getName() : null);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> listStudents(int page, int size, String sort, String status,
                                               UUID programmeId, String search) {
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
