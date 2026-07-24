package com.studentmanagement.common.seed;

import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.model.Result;
import com.studentmanagement.academic.model.Semester;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.attendance.model.Attendance;
import com.studentmanagement.attendance.repository.AttendanceRepository;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.AttendanceStatus;
import com.studentmanagement.common.enums.Gender;
import com.studentmanagement.common.enums.GradeStatus;
import com.studentmanagement.common.enums.GuardianRelationship;
import com.studentmanagement.common.enums.InvoiceStatus;
import com.studentmanagement.common.enums.MessageAudience;
import com.studentmanagement.common.enums.NotificationType;
import com.studentmanagement.common.enums.PaymentGateway;
import com.studentmanagement.common.enums.PaymentStatus;
import com.studentmanagement.common.enums.StaffStatus;
import com.studentmanagement.common.enums.StudentStatus;
import com.studentmanagement.common.enums.UserStatus;
import com.studentmanagement.communication.model.Announcement;
import com.studentmanagement.communication.model.Message;
import com.studentmanagement.communication.model.Notification;
import com.studentmanagement.communication.repository.AnnouncementRepository;
import com.studentmanagement.communication.repository.MessageRepository;
import com.studentmanagement.communication.repository.NotificationRepository;
import com.studentmanagement.staff.model.Staff;
import com.studentmanagement.staff.model.StaffRole;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.staff.repository.StaffRoleRepository;
import com.studentmanagement.student.model.GuardianInfo;
import com.studentmanagement.student.model.Programme;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.model.StudentClass;
import com.studentmanagement.student.repository.ClassRepository;
import com.studentmanagement.student.repository.GuardianInfoRepository;
import com.studentmanagement.student.repository.ProgrammeRepository;
import com.studentmanagement.student.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final ProgrammeRepository programmeRepository;
    private final ClassRepository classRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final GuardianInfoRepository guardianInfoRepository;
    private final AnnouncementRepository announcementRepository;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@studentportal.edu")) {
            log.info("Seed data already exists — skipping");
            return;
        }
        log.info("Seeding database...");

        String hash = passwordEncoder.encode("password123");
        Instant now = Instant.now();

        Programme cs = programmeRepository.findByCode("CS").orElseThrow();
        Programme ba = programmeRepository.findByCode("BA").orElseThrow();
        Programme eng = programmeRepository.findByCode("ENG").orElseThrow();

        StudentClass cs200 = classRepository.findById(
            findByCode("student_classes", "CS-2024-A")).orElseThrow();
        StudentClass cs300 = classRepository.findById(
            findByCode("student_classes", "CS-2023-A")).orElseThrow();
        StudentClass cs400 = classRepository.findById(
            findByCode("student_classes", "CS-2022-A")).orElseThrow();
        StudentClass ba200 = classRepository.findById(
            findByCode("student_classes", "BA-2024-A")).orElseThrow();
        StudentClass ba300 = classRepository.findById(
            findByCode("student_classes", "BA-2023-A")).orElseThrow();
        StudentClass eng200 = classRepository.findById(
            findByCode("student_classes", "ENG-2024-A")).orElseThrow();

        Course csc101 = courseRepository.findById(findByCode("courses", "CSC101")).orElseThrow();
        Course csc201 = courseRepository.findById(findByCode("courses", "CSC201")).orElseThrow();
        Course csc301 = courseRepository.findById(findByCode("courses", "CSC301")).orElseThrow();
        Course csc401 = courseRepository.findById(findByCode("courses", "CSC401")).orElseThrow();
        Course bus101 = courseRepository.findById(findByCode("courses", "BUS101")).orElseThrow();
        Course bus201 = courseRepository.findById(findByCode("courses", "BUS201")).orElseThrow();
        Course bus301 = courseRepository.findById(findByCode("courses", "BUS301")).orElseThrow();
        Course eng101 = courseRepository.findById(findByCode("courses", "ENG101")).orElseThrow();
        Course eng201 = courseRepository.findById(findByCode("courses", "ENG201")).orElseThrow();

        Semester sem1 = semesterRepository.findById(findByCode("semesters", "2023/2024-FIRST")).orElseThrow();
        Semester semCurrent = semesterRepository.findById(findByCode("semesters", "2024/2025-FIRST")).orElseThrow();

        // ---- USERS ----
        User admin = saveUser("admin@studentportal.edu", hash, "Dr. Michael Admin", "ADMIN", now);
        User lecturer = saveUser("lecturer@studentportal.edu", hash, "Prof. Sarah Lecturer", "STAFF", now);
        User finance = saveUser("finance@studentportal.edu", hash, "Mr. James Finance", "STAFF", now);

        List<UserData> studentData = List.of(
            new UserData("john.doe@studentportal.edu", "John", "Doe", Gender.MALE, LocalDate.of(2004, 5, 12), "NG", "+2348012345001", "12, Awolowo Road, Ikoyi, Lagos", cs, cs200, LocalDate.of(2024, 9, 1)),
            new UserData("jane.smith@studentportal.edu", "Jane", "Smith", Gender.FEMALE, LocalDate.of(2005, 2, 28), "GH", "+233501234502", "45 Independence Avenue, Accra", cs, cs200, LocalDate.of(2024, 9, 1)),
            new UserData("mike.johnson@studentportal.edu", "Michael", "Johnson", Gender.MALE, LocalDate.of(2003, 8, 15), "NG", "+2348022345003", "78 Ahmadu Bello Way, Kaduna", cs, cs300, LocalDate.of(2023, 9, 1)),
            new UserData("emma.williams@studentportal.edu", "Emma", "Williams", Gender.FEMALE, LocalDate.of(2002, 11, 3), "ZA", "+27712345004", "23 Nelson Mandela Blvd, Cape Town", cs, cs400, LocalDate.of(2022, 9, 1)),
            new UserData("daniel.brown@studentportal.edu", "Daniel", "Brown", Gender.MALE, LocalDate.of(2004, 7, 22), "NG", "+2347032345005", "5 Bishop Aboyade Cole, Victoria Island, Lagos", ba, ba200, LocalDate.of(2024, 9, 1)),
            new UserData("olivia.davis@studentportal.edu", "Olivia", "Davis", Gender.FEMALE, LocalDate.of(2005, 1, 10), "KE", "+254701234506", "90 Kenyatta Avenue, Nairobi", ba, ba200, LocalDate.of(2024, 9, 1)),
            new UserData("james.wilson@studentportal.edu", "James", "Wilson", Gender.MALE, LocalDate.of(2003, 4, 18), "NG", "+2348052345007", "32 Younis Bashorun Street, Abuja", ba, ba300, LocalDate.of(2023, 9, 1)),
            new UserData("sophia.garcia@studentportal.edu", "Sophia", "Garcia", Gender.FEMALE, LocalDate.of(2003, 9, 5), "US", "+2348092345008", "15 Tunde Amisu Street, Lekki, Lagos", ba, ba300, LocalDate.of(2023, 9, 1)),
            new UserData("william.taylor@studentportal.edu", "William", "Taylor", Gender.MALE, LocalDate.of(2004, 6, 30), "UK", "+2348062345009", "8 Toyin Street, Ikeja, Lagos", eng, eng200, LocalDate.of(2024, 9, 1)),
            new UserData("amy.martin@studentportal.edu", "Amy", "Martin", Gender.FEMALE, LocalDate.of(2005, 3, 14), "NG", "+2347022345010", "55 Opebi Road, Ikeja, Lagos", eng, eng200, LocalDate.of(2024, 9, 1))
        );

        List<String> guardianNames = List.of("Mr. David Doe", "Mrs. Grace Smith", "Dr. Robert Johnson",
            "Mr. Christopher Williams", "Mrs. Patricia Brown", "Mr. Peter Davis",
            "Mrs. Susan Wilson", "Mr. Carlos Garcia", "Mrs. Helen Taylor", "Mr. Kevin Martin");

        Map<Integer, GuardianRelationship> relMap = Map.of(
            0, GuardianRelationship.PARENT, 1, GuardianRelationship.PARENT, 2, GuardianRelationship.PARENT,
            3, GuardianRelationship.PARENT, 4, GuardianRelationship.PARENT, 5, GuardianRelationship.PARENT,
            6, GuardianRelationship.GUARDIAN, 7, GuardianRelationship.PARENT, 8, GuardianRelationship.GUARDIAN,
            9, GuardianRelationship.PARENT);

        Map<Integer, String> guardianPhones = Map.of(
            0, "+2348020001001", 1, "+233540001002", 2, "+2348030001003",
            3, "+27710001004", 4, "+2348050001005", 5, "+254710001006",
            6, "+2348020001007", 7, "+2348090001008", 8, "+2348030001009",
            9, "+2347020001010");

        List<Student> students = new ArrayList<>();
        int idx = 0;
        for (UserData ud : studentData) {
            User u = saveUser(ud.email, hash, ud.firstName + " " + ud.lastName, "STUDENT", now);

            Student s = Student.builder()
                .user(u)
                .studentNo("STU-" + (2025 - ud.admissionDate.getYear()) + "-" + String.format("%04d", idx + 1))
                .firstName(ud.firstName)
                .lastName(ud.lastName)
                .dateOfBirth(ud.dob)
                .gender(ud.gender)
                .nationality("NG")
                .email(ud.email)
                .phone(ud.phone)
                .address(ud.address)
                .programme(ud.programme)
                .studentClass(ud.studentClass)
                .status(StudentStatus.ACTIVE)
                .admissionDate(ud.admissionDate)
                .build();
            studentRepository.save(s);
            students.add(s);

            GuardianInfo g = GuardianInfo.builder()
                .student(s)
                .fullName(guardianNames.get(idx))
                .relationship(relMap.getOrDefault(idx, GuardianRelationship.PARENT))
                .email("guardian." + ud.email)
                .phone(guardianPhones.getOrDefault(idx, "+2348000000000"))
                .address(ud.address)
                .isPrimary(true)
                .build();
            guardianInfoRepository.save(g);
            idx++;
        }

        // ---- STAFF ----
        Staff adminStaff = Staff.builder()
            .user(admin)
            .employeeNo("EMP-001")
            .firstName("Michael")
            .lastName("Admin")
            .email("admin@studentportal.edu")
            .phone("+2348010000001")
            .department("Administration")
            .designation("System Administrator")
            .status(StaffStatus.ACTIVE)
            .build();
        staffRepository.save(adminStaff);

        Staff lecturerStaff = Staff.builder()
            .user(lecturer)
            .employeeNo("EMP-002")
            .firstName("Sarah")
            .lastName("Lecturer")
            .email("lecturer@studentportal.edu")
            .phone("+2348010000002")
            .department("Computer Science")
            .designation("Senior Lecturer")
            .status(StaffStatus.ACTIVE)
            .build();
        staffRepository.save(lecturerStaff);

        Staff financeStaff = Staff.builder()
            .user(finance)
            .employeeNo("EMP-003")
            .firstName("James")
            .lastName("Finance")
            .email("finance@studentportal.edu")
            .phone("+2348010000003")
            .department("Finance")
            .designation("Finance Officer")
            .status(StaffStatus.ACTIVE)
            .build();
        staffRepository.save(financeStaff);

        staffRoleRepository.save(StaffRole.builder().staff(adminStaff).role("ADMIN").build());
        staffRoleRepository.save(StaffRole.builder().staff(lecturerStaff).role("LECTURER").build());
        staffRoleRepository.save(StaffRole.builder().staff(financeStaff).role("FINANCE").build());

        // ---- GRADES & RESULTS ----
        record Enrolment(Student student, Course course, Semester semester) {}
        List<Enrolment> enrolments = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            List<Course> pastCourses = new ArrayList<>();
            List<Course> currentCourses = new ArrayList<>();

            if (s.getProgramme().getCode().equals("CS")) {
                if (s.getStudentClass().getCode().equals("CS-2022-A")) {
                    pastCourses.addAll(List.of(csc101, csc201));
                    currentCourses.addAll(List.of(csc301, csc401));
                } else if (s.getStudentClass().getCode().equals("CS-2023-A")) {
                    pastCourses.addAll(List.of(csc101));
                    currentCourses.addAll(List.of(csc201, csc301));
                } else {
                    currentCourses.addAll(List.of(csc101, csc201));
                }
            } else if (s.getProgramme().getCode().equals("BA")) {
                if (s.getStudentClass().getCode().equals("BA-2023-A")) {
                    pastCourses.addAll(List.of(bus101));
                    currentCourses.addAll(List.of(bus201, bus301));
                } else {
                    currentCourses.addAll(List.of(bus101, bus201));
                }
            } else if (s.getProgramme().getCode().equals("ENG")) {
                currentCourses.addAll(List.of(eng101, eng201));
            }

            for (Course c : pastCourses) {
                enrolments.add(new Enrolment(s, c, sem1));
            }
            for (Course c : currentCourses) {
                enrolments.add(new Enrolment(s, c, semCurrent));
            }
        }

        double[][] scoreSets = {
            {85.5, 72.0, 91.0, 68.5, 78.0, 88.5, 74.0, 82.0, 65.0, 95.0},
            {70.0, 63.5, 82.0, 75.0, 81.0, 70.0, 76.5, 79.0, 72.0, 68.0},
            {92.0, 88.0, 76.5, 71.0, 65.0, 93.0, 80.0, 88.5, 77.0, 84.0},
            {64.0, 79.5, 68.0, 83.0, 90.0, 62.0, 85.0, 71.5, 80.0, 73.0},
            {77.5, 85.0, 71.0, 90.5, 73.0, 81.0, 69.0, 76.0, 91.0, 78.0},
            {81.0, 67.0, 93.5, 78.0, 86.0, 75.0, 88.0, 83.5, 70.0, 89.0}
        };

        Map<Semester, List<Grade>> gradesBySemester = new java.util.HashMap<>();
        gradesBySemester.put(sem1, new ArrayList<>());
        gradesBySemester.put(semCurrent, new ArrayList<>());

        int scoreIdx = 0;
        for (Enrolment e : enrolments) {
            int setIdx = scoreIdx % scoreSets.length;
            int valIdx = (scoreIdx / scoreSets.length) % scoreSets[0].length;
            double score = scoreSets[setIdx][valIdx];
            scoreIdx++;

            Grade g = Grade.builder()
                .student(e.student)
                .course(e.course)
                .semester(e.semester)
                .score(BigDecimal.valueOf(score))
                .letterGrade(toLetterGrade(score))
                .remarks("Performance is satisfactory")
                .status(GradeStatus.PUBLISHED)
                .publishedAt(now)
                .publishedBy(lecturer)
                .build();
            gradeRepository.save(g);
            gradesBySemester.get(e.semester).add(g);
        }

        for (var entry : gradesBySemester.entrySet()) {
            Semester sem = entry.getKey();
            Map<Student, List<Grade>> byStudent = new java.util.HashMap<>();
            for (Grade g : entry.getValue()) {
                byStudent.computeIfAbsent(g.getStudent(), k -> new ArrayList<>()).add(g);
            }
            for (var se : byStudent.entrySet()) {
                List<Grade> gs = se.getValue();
                double weightedSum = 0;
                int totalCredits = 0;
                for (Grade g : gs) {
                    int credits = g.getCourse().getCredits();
                    weightedSum += g.getScore().doubleValue() * credits;
                    totalCredits += credits;
                }
                double gpa = totalCredits > 0 ? weightedSum / totalCredits : 0;
                BigDecimal gpaBd = BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP);

                Result r = Result.builder()
                    .student(se.getKey())
                    .semester(sem)
                    .gpa(gpaBd)
                    .totalCredits(totalCredits)
                    .build();
                entityManager.persist(r);
            }
        }

        // ---- ATTENDANCE ----
        LocalDate startDate = LocalDate.of(2024, 10, 7);
        for (int day = 0; day < 25; day++) {
            LocalDate date = startDate.plusDays(day);
            if (date.getDayOfWeek().getValue() > 5) continue;
            for (int si = 0; si < Math.min(5, students.size()); si++) {
                Student s = students.get((day + si) % students.size());
                AttendanceStatus st;
                double rnd = Math.random();
                if (rnd < 0.7) st = AttendanceStatus.PRESENT;
                else if (rnd < 0.8) st = AttendanceStatus.ABSENT;
                else if (rnd < 0.9) st = AttendanceStatus.LATE;
                else st = AttendanceStatus.EXCUSED;

                Attendance a = Attendance.builder()
                    .student(s)
                    .studentClass(s.getStudentClass())
                    .date(date)
                    .status(st)
                    .recordedBy(lecturer)
                    .build();
                attendanceRepository.save(a);
            }
        }

        // ---- FEE SCHEDULES ----
        String components = "{\"tuition\": 500000, \"library\": 25000, \"sports\": 15000, \"development\": 30000}";
        BigDecimal totalFee = BigDecimal.valueOf(570000);

        for (Programme prog : List.of(cs, ba, eng)) {
            for (Semester sem : List.of(sem1, semCurrent)) {
                entityManager.createNativeQuery(
                    "INSERT INTO fee_schedules (id, programme_id, semester_id, components, total_amount, created_at, updated_at) " +
                    "VALUES (?, ?, ?, CAST(? AS jsonb), ?, ?, ?)")
                    .setParameter(1, UUID.randomUUID())
                    .setParameter(2, prog.getId())
                    .setParameter(3, sem.getId())
                    .setParameter(4, components)
                    .setParameter(5, totalFee)
                    .setParameter(6, now)
                    .setParameter(7, now)
                    .executeUpdate();
            }
        }

        // ---- INVOICES & PAYMENTS ----
        List scheduleIds = entityManager.createNativeQuery(
            "SELECT id FROM fee_schedules ORDER BY created_at").getResultList();

        int invoiceNo = 1;
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            BigDecimal total = BigDecimal.valueOf(570000);
            InvoiceStatus invStatus;
            BigDecimal paid;
            boolean hasPayment = false;
            PaymentStatus payStatus = PaymentStatus.PENDING;
            BigDecimal payAmount = BigDecimal.ZERO;

            if (i < 4) {
                invStatus = InvoiceStatus.PAID;
                paid = total;
                hasPayment = true;
                payStatus = PaymentStatus.SUCCESS;
                payAmount = total;
            } else if (i < 7) {
                invStatus = InvoiceStatus.PARTIAL;
                paid = total.multiply(BigDecimal.valueOf(0.5));
                hasPayment = true;
                payStatus = PaymentStatus.SUCCESS;
                payAmount = paid;
            } else if (i < 9) {
                invStatus = InvoiceStatus.PENDING;
                paid = BigDecimal.ZERO;
            } else {
                invStatus = InvoiceStatus.OVERDUE;
                paid = BigDecimal.ZERO;
            }

            UUID invId = UUID.randomUUID();
            UUID schedId = (UUID) scheduleIds.get(i % scheduleIds.size());

            entityManager.createNativeQuery(
                "INSERT INTO invoices (id, student_id, schedule_id, invoice_no, total_amount, paid_amount, balance, status, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, invId)
                .setParameter(2, s.getId())
                .setParameter(3, schedId)
                .setParameter(4, "INV-2024-" + String.format("%04d", invoiceNo++))
                .setParameter(5, total)
                .setParameter(6, paid)
                .setParameter(7, total.subtract(paid))
                .setParameter(8, invStatus.name())
                .setParameter(9, LocalDate.of(2024, 12, 15))
                .setParameter(10, now)
                .setParameter(11, now)
                .executeUpdate();

            if (hasPayment) {
                PaymentGateway gateway = i % 2 == 0 ? PaymentGateway.PAYSTACK : PaymentGateway.FLUTTERWAVE;
                entityManager.createNativeQuery(
                    "INSERT INTO payments (id, invoice_id, amount, gateway, gateway_reference, reference, status, paid_at, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                    .setParameter(1, UUID.randomUUID())
                    .setParameter(2, invId)
                    .setParameter(3, payAmount)
                    .setParameter(4, gateway.name())
                    .setParameter(5, gateway.name() + "-REF-" + UUID.randomUUID().toString().substring(0, 8))
                    .setParameter(6, "PAY-REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .setParameter(7, payStatus.name())
                    .setParameter(8, now)
                    .setParameter(9, now)
                    .executeUpdate();
            }
        }

        // ---- ANNOUNCEMENTS ----
        announcementRepository.save(Announcement.builder()
            .title("Academic Calendar 2024/2025")
            .body("The academic calendar for the 2024/2025 session has been published. " +
                  "Lectures begin on September 1, 2024. First semester examinations will hold " +
                  "from January 6 to January 15, 2025.")
            .audience(MessageAudience.ALL)
            .author(admin)
            .publishedAt(now)
            .expiresAt(Instant.parse("2025-06-30T23:59:59Z"))
            .build());

        announcementRepository.save(Announcement.builder()
            .title("Course Registration Deadline")
            .body("All students are required to complete their course registration by " +
                  "September 30, 2024. Late registration will attract a penalty fee. " +
                  "Please consult your academic advisor before selecting courses.")
            .audience(MessageAudience.ALL)
            .author(admin)
            .publishedAt(now)
            .expiresAt(Instant.parse("2024-10-15T23:59:59Z"))
            .build());

        // ---- NOTIFICATIONS ----
        for (Student s : students) {
            notificationRepository.save(Notification.builder()
                .user(s.getUser())
                .title("Grades Published")
                .body("Your grades for the 2024/2025 First Semester have been published. " +
                      "Please log in to your portal to view your results.")
                .type(NotificationType.INFO)
                .isRead(false)
                .build());

            notificationRepository.save(Notification.builder()
                .user(s.getUser())
                .title("Fee Reminder")
                .body("Your tuition fee for the current semester is due by December 15, 2024. " +
                      "Please make payment to avoid penalties.")
                .type(NotificationType.WARNING)
                .isRead(false)
                .build());
        }

        // ---- MESSAGES ----
        UUID threadId = UUID.randomUUID();

        messageRepository.save(Message.builder()
            .sender(admin)
            .receiver(lecturer)
            .subject("New curriculum review")
            .body("Dear Prof. Lecturer,\n\nPlease review the proposed curriculum changes " +
                  "for the Computer Science department. The document has been shared with " +
                  "your faculty email.\n\nBest regards,\nDr. Michael Admin")
            .threadId(threadId)
            .isRead(true)
            .readAt(now)
            .build());

        messageRepository.save(Message.builder()
            .sender(lecturer)
            .receiver(admin)
            .subject("Re: New curriculum review")
            .body("Dear Dr. Admin,\n\nI have reviewed the proposed curriculum changes and " +
                  "provided my feedback. Overall, the changes look good. I have a few minor " +
                  "suggestions which I have noted in the document.\n\nBest regards,\nProf. Sarah Lecturer")
            .threadId(threadId)
            .isRead(false)
            .build());

        log.info("Database seeding completed successfully");
    }

    private User saveUser(String email, String hash, String fullName, String role, Instant now) {
        User u = User.builder()
            .email(email)
            .passwordHash(hash)
            .fullName(fullName)
            .role(role)
            .mfaEnabled(false)
            .status(UserStatus.ACTIVE)
            .emailVerified(true)
            .emailVerifiedAt(now)
            .failedAttempts(0)
            .build();
        return userRepository.save(u);
    }

    private UUID findByCode(String table, String code) {
        return (UUID) entityManager.createNativeQuery(
            "SELECT id FROM " + table + " WHERE code = ?")
            .setParameter(1, code)
            .getSingleResult();
    }

    private String toLetterGrade(double score) {
        if (score >= 70) return "A";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        if (score >= 45) return "D";
        if (score >= 40) return "E";
        return "F";
    }

    private record UserData(String email, String firstName, String lastName, Gender gender,
                            LocalDate dob, String nationality, String phone, String address,
                            Programme programme, StudentClass studentClass, LocalDate admissionDate) {}
}
