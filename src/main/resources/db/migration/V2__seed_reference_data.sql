-- ============================================================
-- V2: Seed Reference Data for Student Management Portal
-- ============================================================

-- ============================================================
-- PROGRAMMES
-- ============================================================
INSERT INTO programmes (id, name, code, duration_years, description, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Computer Science', 'CS', 4, 'Bachelor of Science in Computer Science', NOW(), NOW()),
    (gen_random_uuid(), 'Business Administration', 'BA', 4, 'Bachelor of Business Administration', NOW(), NOW()),
    (gen_random_uuid(), 'Electrical Engineering', 'ENG', 5, 'Bachelor of Engineering in Electrical Engineering', NOW(), NOW());

-- ============================================================
-- DEPARTMENTS
-- ============================================================
INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Computer Science Department', 'CS-DEPT', p.id, 'Department of Computer Science', NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Information Technology Department', 'IT-DEPT', p.id, 'Department of Information Technology', NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Business Administration Department', 'BA-DEPT', p.id, 'Department of Business Administration', NOW(), NOW()
FROM programmes p WHERE p.code = 'BA';

INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Accounting Department', 'ACC-DEPT', p.id, 'Department of Accounting', NOW(), NOW()
FROM programmes p WHERE p.code = 'BA';

INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Electrical Engineering Department', 'EE-DEPT', p.id, 'Department of Electrical Engineering', NOW(), NOW()
FROM programmes p WHERE p.code = 'ENG';

INSERT INTO departments (id, name, code, programme_id, description, created_at, updated_at)
SELECT gen_random_uuid(), 'Mechanical Engineering Department', 'ME-DEPT', p.id, 'Department of Mechanical Engineering', NOW(), NOW()
FROM programmes p WHERE p.code = 'ENG';

-- ============================================================
-- STUDENT CLASSES
-- ============================================================
INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'CS 200 Level A', 'CS-2024-A', p.id, d.id, '200', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'CS' AND d.code = 'CS-DEPT';

INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'CS 300 Level A', 'CS-2023-A', p.id, d.id, '300', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'CS' AND d.code = 'CS-DEPT';

INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'CS 400 Level A', 'CS-2022-A', p.id, d.id, '400', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'CS' AND d.code = 'CS-DEPT';

INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'BA 200 Level A', 'BA-2024-A', p.id, d.id, '200', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'BA' AND d.code = 'BA-DEPT';

INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'BA 300 Level A', 'BA-2023-A', p.id, d.id, '300', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'BA' AND d.code = 'BA-DEPT';

INSERT INTO student_classes (id, name, code, programme_id, department_id, level, created_at, updated_at)
SELECT gen_random_uuid(), 'ENG 200 Level A', 'ENG-2024-A', p.id, d.id, '200', NOW(), NOW()
FROM programmes p, departments d WHERE p.code = 'ENG' AND d.code = 'EE-DEPT';

-- ============================================================
-- SEMESTERS
-- ============================================================
INSERT INTO semesters (id, name, code, type, start_date, end_date, is_current, created_at, updated_at)
VALUES
    (gen_random_uuid(), '2023/2024 First Semester', '2023/2024-FIRST', 'SEMESTER', '2023-09-01', '2024-01-15', FALSE, NOW(), NOW()),
    (gen_random_uuid(), '2023/2024 Second Semester', '2023/2024-SECOND', 'SEMESTER', '2024-02-01', '2024-06-15', FALSE, NOW(), NOW()),
    (gen_random_uuid(), '2024/2025 First Semester', '2024/2025-FIRST', 'SEMESTER', '2024-09-01', '2025-01-15', TRUE, NOW(), NOW());

-- ============================================================
-- COURSES
-- ============================================================
INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Introduction to Programming', 'CSC101', 'Fundamentals of programming using Python', 3, 'CS-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Data Structures and Algorithms', 'CSC201', 'Advanced data structures and algorithm analysis', 4, 'CS-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Database Systems', 'CSC301', 'Relational databases, SQL, and NoSQL systems', 3, 'CS-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Software Engineering', 'CSC401', 'Software development lifecycle and methodologies', 3, 'CS-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'CS';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Principles of Management', 'BUS101', 'Fundamental concepts of management and organisation', 3, 'BA-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'BA';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Financial Accounting', 'BUS201', 'Principles and practice of financial accounting', 3, 'BA-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'BA';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Marketing Management', 'BUS301', 'Marketing strategies and consumer behaviour', 3, 'BA-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'BA';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Engineering Mathematics I', 'ENG101', 'Calculus, linear algebra, and differential equations', 4, 'EE-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'ENG';

INSERT INTO courses (id, name, code, description, credits, department, programme_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Circuit Theory', 'ENG201', 'Analysis of electrical circuits and network theory', 3, 'EE-DEPT', p.id, NOW(), NOW()
FROM programmes p WHERE p.code = 'ENG';

-- ============================================================
-- VENUES
-- ============================================================
INSERT INTO venues (id, name, code, capacity, building, floor, created_at)
VALUES
    (gen_random_uuid(), 'Lecture Hall A', 'LH-A', 200, 'Main Academic Building', '1st Floor', NOW()),
    (gen_random_uuid(), 'Lecture Hall B', 'LH-B', 150, 'Main Academic Building', '2nd Floor', NOW()),
    (gen_random_uuid(), 'Computer Science Lab', 'CS-LAB', 60, 'Science Building', 'Ground Floor', NOW()),
    (gen_random_uuid(), 'Engineering Lab', 'ENG-LAB', 50, 'Engineering Building', '1st Floor', NOW()),
    (gen_random_uuid(), 'University Auditorium', 'AUD', 500, 'Student Centre', 'Ground Floor', NOW());

-- ============================================================
-- TIME SLOTS
-- ============================================================
INSERT INTO time_slots (id, day_of_week, start_time, end_time, type, created_at)
VALUES
    (gen_random_uuid(), 'MONDAY', '08:00', '09:30', 'CLASS', NOW()),
    (gen_random_uuid(), 'MONDAY', '09:45', '11:15', 'CLASS', NOW()),
    (gen_random_uuid(), 'MONDAY', '11:30', '13:00', 'CLASS', NOW()),
    (gen_random_uuid(), 'MONDAY', '14:00', '15:30', 'CLASS', NOW()),
    (gen_random_uuid(), 'MONDAY', '15:45', '17:15', 'CLASS', NOW());

-- ============================================================
-- ROLES
-- ============================================================
INSERT INTO roles (id, name, description)
VALUES
    (gen_random_uuid(), 'ADMIN', 'System administrator with full access'),
    (gen_random_uuid(), 'STAFF', 'Staff member with teaching and administrative access'),
    (gen_random_uuid(), 'STUDENT', 'Student with limited access');

-- ============================================================
-- PERMISSIONS
-- ============================================================
INSERT INTO permissions (id, name, resource, action)
VALUES
    (gen_random_uuid(), 'CREATE_STUDENT', 'STUDENT', 'CREATE'),
    (gen_random_uuid(), 'READ_STUDENT', 'STUDENT', 'READ'),
    (gen_random_uuid(), 'UPDATE_STUDENT', 'STUDENT', 'UPDATE'),
    (gen_random_uuid(), 'DELETE_STUDENT', 'STUDENT', 'DELETE'),
    (gen_random_uuid(), 'CREATE_COURSE', 'COURSE', 'CREATE'),
    (gen_random_uuid(), 'READ_COURSE', 'COURSE', 'READ'),
    (gen_random_uuid(), 'UPDATE_COURSE', 'COURSE', 'UPDATE'),
    (gen_random_uuid(), 'DELETE_COURSE', 'COURSE', 'DELETE'),
    (gen_random_uuid(), 'CREATE_GRADE', 'GRADE', 'CREATE'),
    (gen_random_uuid(), 'READ_GRADE', 'GRADE', 'READ'),
    (gen_random_uuid(), 'UPDATE_GRADE', 'GRADE', 'UPDATE'),
    (gen_random_uuid(), 'PUBLISH_GRADE', 'GRADE', 'PUBLISH'),
    (gen_random_uuid(), 'CREATE_ATTENDANCE', 'ATTENDANCE', 'CREATE'),
    (gen_random_uuid(), 'READ_ATTENDANCE', 'ATTENDANCE', 'READ'),
    (gen_random_uuid(), 'MANAGE_INVOICE', 'INVOICE', 'MANAGE'),
    (gen_random_uuid(), 'READ_INVOICE', 'INVOICE', 'READ'),
    (gen_random_uuid(), 'CREATE_ANNOUNCEMENT', 'ANNOUNCEMENT', 'CREATE'),
    (gen_random_uuid(), 'MANAGE_SYSTEM', 'SYSTEM', 'MANAGE');
