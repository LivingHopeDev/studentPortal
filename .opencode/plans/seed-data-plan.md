# Seed Data Implementation Plan

## Overview
Seed the database with realistic QA data. Two files:
1. **Flyway migration** (`V2__seed_reference_data.sql`) — static reference data
2. **CommandLineRunner** (`DataSeeder.java`) — dynamic data needing BCrypt hashing

---

## File 1: `src/main/resources/db/migration/V2__seed_reference_data.sql`

### Programmes (3)
| Code | Name | Duration |
|------|------|----------|
| CS | Computer Science | 4 years |
| BA | Business Administration | 4 years |
| ENG | Electrical Engineering | 5 years |

### Departments (6)
CS → Computer Science Dept (CS-DEPT), IT Dept (IT-DEPT)
BA → Business Admin Dept (BA-DEPT), Accounting Dept (ACC-DEPT)
ENG → Electrical Eng Dept (EE-DEPT), Mechanical Eng Dept (ME-DEPT)

### Student Classes (6)
| Code | Name | Level | Programme |
|------|------|-------|-----------|
| CS-2024-A | CS 200 Level A | 200 | CS |
| CS-2023-A | CS 300 Level A | 300 | CS |
| CS-2022-A | CS 400 Level A | 400 | CS |
| BA-2024-A | BA 200 Level A | 200 | BA |
| BA-2023-A | BA 300 Level A | 300 | BA |
| ENG-2024-A | ENG 200 Level A | 200 | ENG |

### Semesters (3)
| Code | Name | Start | End | Current |
|------|------|-------|-----|---------|
| 2023/2024-FIRST | 2023/2024 First Semester | 2023-09-01 | 2024-01-15 | false |
| 2023/2024-SECOND | 2023/2024 Second Semester | 2024-02-01 | 2024-06-15 | false |
| 2024/2025-FIRST | 2024/2025 First Semester | 2024-09-01 | 2025-01-15 | **true** |

### Courses (9)
| Code | Name | Credits | Programme |
|------|------|---------|-----------|
| CSC101 | Introduction to Programming | 3 | CS |
| CSC201 | Data Structures and Algorithms | 4 | CS |
| CSC301 | Database Systems | 3 | CS |
| CSC401 | Software Engineering | 3 | CS |
| BUS101 | Principles of Management | 3 | BA |
| BUS201 | Financial Accounting | 3 | BA |
| BUS301 | Marketing Management | 3 | BA |
| ENG101 | Engineering Mathematics I | 4 | ENG |
| ENG201 | Circuit Theory | 3 | ENG |

### Venues (5)
LH-A (200), LH-B (150), CS-LAB (60), ENG-LAB (50), AUD (500)

### Time Slots (5)
Mon 08:00–09:30, 09:45–11:15, 11:30–13:00, 14:00–15:30, 15:45–17:15

### Roles (3)
ADMIN, STAFF, STUDENT

### Permissions (18)
CRUD per resource + PUBLISH_GRADE, MANAGE_INVOICE, MANAGE_SYSTEM

---

## File 2: `src/main/java/com/studentmanagement/common/seed/DataSeeder.java`

**Package:** `com.studentmanagement.common.seed`

**Type:** `@Component` implementing `CommandLineRunner`, ordered with `@Order(1)`

**Logic:** Check `userRepository.existsByEmail("admin@studentportal.edu")` — skip entirely if already seeded (idempotent). Wrapped in `@Transactional`.

### Injected Dependencies
- `PasswordEncoder` (BCrypt hashing)
- `UserRepository`, `StudentRepository`, `StaffRepository`, `StaffRoleRepository`
- `ProgrammeRepository`, `ClassRepository`, `CourseRepository`, `SemesterRepository`
- `GradeRepository`, `AttendanceRepository`, `GuardianInfoRepository`
- `AnnouncementRepository`, `NotificationRepository`, `MessageRepository`
- `EntityManager` (for FeeSchedule, Invoice, Payment — no repositories exist)

### Data to Seed

#### Users (13 total)
All with password `password123` (BCrypt-hashed at runtime via `PasswordEncoder`), `status=ACTIVE`, `email_verified=true`, `failed_attempts=0`.

| Email | Full Name | Role |
|-------|-----------|------|
| admin@studentportal.edu | Dr. Michael Admin | ADMIN |
| lecturer@studentportal.edu | Prof. Sarah Lecturer | STAFF |
| finance@studentportal.edu | Mr. James Finance | STAFF |
| john.doe@studentportal.edu | John Doe | STUDENT |
| jane.smith@studentportal.edu | Jane Smith | STUDENT |
| mike.johnson@studentportal.edu | Michael Johnson | STUDENT |
| emma.williams@studentportal.edu | Emma Williams | STUDENT |
| daniel.brown@studentportal.edu | Daniel Brown | STUDENT |
| olivia.davis@studentportal.edu | Olivia Davis | STUDENT |
| james.wilson@studentportal.edu | James Wilson | STUDENT |
| sophia.garcia@studentportal.edu | Sophia Garcia | STUDENT |
| william.taylor@studentportal.edu | William Taylor | STUDENT |
| amy.martin@studentportal.edu | Amy Martin | STUDENT |

#### Staff (3)
| Employee No | First Name | Last Name | Email | Designation | Dept |
|-------------|------------|-----------|-------|-------------|------|
| EMP-001 | Michael | Admin | admin@studentportal.edu | System Administrator | Administration |
| EMP-002 | Sarah | Lecturer | lecturer@studentportal.edu | Senior Lecturer | Computer Science |
| EMP-003 | James | Finance | finance@studentportal.edu | Finance Officer | Finance |

#### Students (10)
Realistic data with DOB (range 2000–2006), gender, nationality (NG, GH, KE, ZA, US, UK), phone (+234 format), address.

| First Name | Last Name | Programme | Class |
|------------|-----------|-----------|-------|
| John | Doe | CS | CS-2024-A |
| Jane | Smith | CS | CS-2024-A |
| Michael | Johnson | CS | CS-2023-A |
| Emma | Williams | CS | CS-2022-A |
| Daniel | Brown | BA | BA-2024-A |
| Olivia | Davis | BA | BA-2024-A |
| James | Wilson | BA | BA-2023-A |
| Sophia | Garcia | BA | BA-2023-A |
| William | Taylor | ENG | ENG-2024-A |
| Amy | Martin | ENG | ENG-2024-A |

Admission for L200 students: 2024-09-01; L300: 2023-09-01; L400: 2022-09-01

#### Guardians (10)
One primary guardian per student (parent/guardian relationships).

#### Grades
For each student, grades in each course they took in the current semester + one past semester. Scores randomly distributed 60–95 with letter grades:
- 70–100 → A
- 60–69 → B
- 50–59 → C
- 45–49 → D
- 40–44 → E
- <40 → F

All published. Published by admin user.

#### Results
GPA = average of (score × credits) / total credits, per student per semester.

#### Attendance
~30 records across random students, dates in Oct–Nov 2024. Status distribution: 70% PRESENT, 10% ABSENT, 10% LATE, 10% EXCUSED. Recorded by lecturer.

#### Fee Schedules
One per programme per active semester, with JSONB components:
```json
{"tuition": 500000, "library": 25000, "sports": 15000, "development": 30000}
```

#### Invoices
One per student per semester. Invoice numbers: INV-2024-0001 format.
Mix: 4 PAID, 3 PARTIAL, 2 PENDING, 1 OVERDUE.

#### Payments
For PAID invoices: full amount, SUCCESS, via PAYSTACK/FLUTTERWAVE with realistic references (PAY-REF-XXXXX).

#### Announcements (2)
1. "Academic Calendar 2024/2025" — published by admin, audience ALL
2. "Course Registration Deadline" — published by admin, audience ALL

#### Notifications (10)
One per student: "Your grades for 2024/2025 First Semester have been published." type=INFO.

#### Messages (2)
1. Admin → Lecturer: "Subject: New curriculum review — Please review the proposed curriculum changes."
2. Lecturer → Admin: "Subject: Re: New curriculum review — I have reviewed the changes and provided feedback."
