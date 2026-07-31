# Student Management Portal
## Product Requirements Document & API Endpoint Contract
**Version:** 1.0 | **Status:** Draft — For Team Review | **Last Updated:** June 2026


---

## Table of Contents

- [Part 1 — Product Requirements Document](#part-1--product-requirements-document)
  - [1. Executive Summary](#1-executive-summary)
  - [2. Goals & Objectives](#2-goals--objectives)
  - [3. Scope](#3-scope)
  - [4. Stakeholders & User Personas](#4-stakeholders--user-personas)
  - [5. Functional Requirements](#5-functional-requirements)
  - [6. Non-Functional Requirements](#6-non-functional-requirements)
  - [7. Team Responsibilities](#7-team-responsibilities)
  - [8. Architecture Overview](#8-architecture-overview)
  - [9. Key Data Model Summary](#9-key-data-model-summary)
  - [10. Acceptance Criteria](#10-acceptance-criteria)
  - [11. Delivery Milestones](#11-delivery-milestones)
- [Part 2 — API Endpoint Contract](#part-2--api-endpoint-contract)
  - [12. API Conventions](#12-api-conventions)
  - [13. Auth Endpoints](#13-auth-endpoints)
  - [14. Student Endpoints](#14-student-endpoints)
  - [15. Academic Endpoints](#15-academic-endpoints)
  - [16. Attendance Endpoints](#16-attendance-endpoints)
  - [17. Fee Endpoints](#17-fee-endpoints)
  - [18. Staff Endpoints](#18-staff-endpoints)
  - [19. Schedule Endpoints](#19-schedule-endpoints)
  - [20. Communication Endpoints](#20-communication-endpoints)
  - [21. Report Endpoints](#21-report-endpoints)
  - [22. Standard Error Codes](#22-standard-error-codes)
  - [23. Required HTTP Headers](#23-required-http-headers)
  - [24. Webhook Contract](#24-webhook-contract-payment-gateway)
  - [25. Versioning & Deprecation Policy](#25-versioning--deprecation-policy)
  - [26. Team-Specific Notes](#26-team-specific-notes)
  - [27. Document Changelog](#27-document-changelog)

---

# Part 1 — Product Requirements Document

## 1. Executive Summary

The Student Management Portal (SMP) is a multi-platform system designed to digitalise and centralise the full lifecycle of student administration for academic institutions. It provides a unified experience across web and mobile for four primary actor types: Administrators, Teaching Staff, Students, and Guardians.

The portal eliminates fragmented, manual processes by delivering core modules for enrolment, academic records, attendance, fee management, communication, scheduling, and reporting — all backed by a secure, observable **monolithic backend**.

---

## 2. Goals & Objectives

### 2.1 Business Goals

- Reduce administrative overhead by 60% through workflow automation.
- Provide real-time visibility into student academic and attendance performance.
- Centralise all fee collection, receipting, and financial reporting.
- Improve guardian/student engagement via mobile-first communication channels.
- Enable data-driven decision-making through configurable reporting dashboards.

### 2.2 Technical Goals

- Deploy a maintainable, modular monolith with clear internal module boundaries.
- Achieve 99.9% uptime SLA for critical paths (auth, enrolment, attendance).
- Enforce role-based access control (RBAC) consistently across all surfaces.
- Support up to 100,000 registered users (10,000 concurrent sessions).
- Emit structured logs and metrics to an observability stack (Prometheus, Grafana, ELK).

---

## 3. Scope

### 3.1 In Scope

| Module | Description |
|---|---|
| Authentication & Identity | JWT-based login, MFA, RBAC, session management |
| Student Enrolment | Registration, profile management, class/programme assignment |
| Academic Records | Grades, transcripts, GPA calculation, result publication |
| Attendance Management | Daily/subject attendance, absence alerts, reporting |
| Fee Management | Fee schedules, invoicing, payment gateway integration, receipts |
| Staff Management | Staff profiles, role assignment, schedule management |
| Communication | In-app messaging, SMS/email notifications, announcements |
| Scheduling & Timetable | Class scheduling, exam timetable, venue management |
| Reporting & Analytics | Dashboards, exports (PDF/Excel), custom reports |
| Mobile Application | iOS and Android companion app (React Native) |

### 3.2 Out of Scope (v1.0)

- Alumni management module.
- Third-party LMS integrations (Moodle, Canvas).
- Biometric attendance hardware integration.
- Multi-tenancy (single institution deployment in v1.0).

---

## 4. Stakeholders & User Personas

| Persona | Role | Key Needs | Access Level |
|---|---|---|---|
| Super Admin | IT / System Admin | Full system config, audit logs, user provisioning | Full |
| School Admin | Registry / Admin Staff | Enrolment, fee management, reporting | High |
| Teacher / Lecturer | Academic Staff | Attendance, grade entry, class schedule | Medium |
| Student | Enrolled Learner | View results, timetable, fee status, messages | Low |
| Guardian / Parent | Parent or Guardian | Child's attendance, fees, academic progress | Low |
| Finance Officer | Accounts Staff | Fee records, receipts, payment reconciliation | Medium |

---

## 5. Functional Requirements

### 5.1 Authentication & Identity

#### 5.1.1 Login
- Support username/email + password login.
- Issue short-lived JWT access tokens (15 min) and long-lived refresh tokens (7 days) stored in HttpOnly cookies.
- Enforce account lockout after 5 failed attempts (15-minute cooldown).

#### 5.1.2 Multi-Factor Authentication (MFA)
- TOTP-based MFA (Google Authenticator compatible).
- MFA mandatory for Admin roles; optional for Staff and Students.

#### 5.1.3 Role-Based Access Control (RBAC)
- Roles: `SUPER_ADMIN`, `SCHOOL_ADMIN`, `TEACHER`, `STUDENT`, `GUARDIAN`, `FINANCE_OFFICER`.
- All API endpoints must declare required roles. Unauthorised access returns HTTP 403.

### 5.2 Student Enrolment
- Collect: full name, DOB, gender, nationality, guardian info, programme, admission date, student ID (auto-generated).
- Support bulk enrolment via CSV import with validation error reporting.
- Student profile photo upload (max 500KB stored, auto-resized).
- Status lifecycle: `PENDING → ACTIVE → SUSPENDED → GRADUATED → WITHDRAWN`.

### 5.3 Academic Records
- Grade entry by subject/teacher; supports letter grade and percentage.
- GPA auto-calculated on grade save using configurable grading scale.
- Transcript generation as downloadable PDF.
- Result publication workflow: `DRAFT → PUBLISHED`. Students see results only when `PUBLISHED`.
- Academic period (term/semester) management.

### 5.4 Attendance Management
- Attendance recorded per class session: `PRESENT / ABSENT / LATE / EXCUSED`.
- Auto-notify guardian via SMS/email when student is absent.
- Attendance threshold alerts: email Admin when student attendance drops below configurable % (default 75%).
- Monthly attendance report per student and per class.

### 5.5 Fee Management
- Configurable fee schedules per programme, level, and academic period.
- Invoice auto-generated on enrolment or term start.
- Payment gateway integration (Paystack / Flutterwave) with webhook reconciliation.
- Receipt generation as PDF on successful payment.
- Overdue payment alerts with configurable escalation schedule.
- Partial payment support with running balance tracking.

### 5.6 Staff Management
- Staff profile: name, employee ID, department, subjects/classes assigned, qualifications.
- Staff role assignment and permission management by Admin.
- Teaching schedule assignment linked to timetable module.

### 5.7 Communication
- In-app announcement broadcasts (Admin → All / Class / Role-based audience).
- Direct messaging between Staff and Students/Guardians (threaded).
- Email and SMS notification templates, configurable per event type.
- Push notification support for mobile app.

### 5.8 Scheduling & Timetable
- Class timetable builder: assign subject, teacher, room, day, time slot.
- Conflict detection (room/teacher double-booking).
- Exam timetable with venue and invigilator assignment.
- ICS calendar export for students and staff.

### 5.9 Reporting & Analytics
- Pre-built reports: enrolment summary, attendance overview, grade distribution, fee collection.
- Custom report builder with filter and column selection.
- Export formats: PDF, CSV, Excel.
- Admin dashboard: KPI cards + trend charts.

---

## 6. Non-Functional Requirements

| Category | Requirement | Target |
|---|---|---|
| Performance | API response time (p95) | < 300ms |
| Performance | File upload / PDF generation | < 3s |
| Availability | Uptime SLA (critical paths) | 99.9% monthly |
| Scalability | Concurrent sessions | 10,000 |
| Security | Auth token expiry | Access: 15m, Refresh: 7d |
| Security | Sensitive data at rest | AES-256 encrypted |
| Security | Transport | TLS 1.2+ enforced |
| Security | OWASP Top 10 | All items mitigated before prod |
| Observability | Structured logging | JSON to ELK |
| Observability | Metrics scraping | Prometheus + Grafana |
| Observability | Distributed tracing | OpenTelemetry |
| Compliance | Audit log retention | 2 years |
| Compliance | PII handling | NDPR / GDPR compliant |
| Backup | Database backup | Daily automated, 30-day retention |

---

## 7. Team Responsibilities

| Team | Responsibilities |
|---|---|
| Backend | Spring Boot monolith, database design, business logic, background jobs, payment integration |
| Frontend | React web SPA, admin dashboard, student/teacher portals, state management |
| Mobile | React Native iOS & Android app, push notifications, offline capability |
| QA | Test plans, API contract testing, E2E automation, regression, performance testing |
| DevOps | CI/CD pipelines, Docker/Kubernetes, secrets management, monitoring, IaC |

---

## 8. Architecture Overview

### 8.1 Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.x (Java 21) — modular monolith |
| Database | PostgreSQL (primary), Redis (cache/sessions) |
| File Storage | MinIO (S3-compatible) |
| Auth | Internal JWT (Spring Security) |
| Frontend | React 18 + TypeScript + TanStack Query |
| Mobile | React Native (Expo) |
| Container | Docker + Kubernetes |
| CI/CD | GitHub Actions |
| Observability | Prometheus + Grafana + ELK + OpenTelemetry |

### 8.2 Monolith Module Breakdown

| Module (Package) | Responsibility |
|---|---|
| `auth` | Login, token issuance/refresh, MFA, RBAC |
| `student` | Student profiles, enrolment, status lifecycle |
| `academic` | Grades, GPA, transcripts, academic periods |
| `attendance` | Attendance recording, thresholds, reporting |
| `fee` | Fee schedules, invoices, payments, receipts |
| `staff` | Staff profiles, assignments |
| `schedule` | Timetables, conflict detection, exam schedules |
| `communication` | Announcements, messaging, notifications |
| `report` | Report generation, exports |
| `common` | Shared DTOs, exceptions, utilities, config |

> **Module boundary rule:** modules communicate only through their public service interfaces — never by directly accessing another module's repository.

---

## 9. Key Data Model Summary

| Entity | Key Fields |
|---|---|
| `User` | id, email, passwordHash, role, mfaEnabled, status, createdAt |
| `Student` | id, userId, studentNo, firstName, lastName, dob, gender, programmeId, status, admissionDate |
| `Guardian` | id, studentId, fullName, relationship, email, phone |
| `Staff` | id, userId, employeeNo, department, subjects[] |
| `Programme` | id, name, durationYears, departmentId |
| `AcademicPeriod` | id, name, type(TERM/SEMESTER), startDate, endDate, isCurrent |
| `Grade` | id, studentId, subjectId, periodId, score, letterGrade, publishedAt |
| `AttendanceRecord` | id, studentId, sessionId, date, status(PRESENT/ABSENT/LATE/EXCUSED) |
| `FeeSchedule` | id, programmeId, periodId, components[], totalAmount |
| `Invoice` | id, studentId, scheduleId, totalAmount, paidAmount, balance, status |
| `Payment` | id, invoiceId, amount, gateway, reference, status, paidAt |
| `Timetable` | id, classId, subjectId, staffId, roomId, dayOfWeek, startTime, endTime |
| `Announcement` | id, title, body, audience, authorId, publishedAt, expiresAt |

---

## 10. Acceptance Criteria

### 10.1 Definition of Done (per feature)

1. Unit tests written with minimum 80% coverage on business logic.
2. Integration tests covering happy path and at least two error cases.
3. API contract validated against this document.
4. QA sign-off on functional test cases.
5. No HIGH/CRITICAL Sonar issues.
6. Feature flag enabled/disabled without redeployment.
7. Observability: logs, metrics, and alerts configured.

---

## 11. Delivery Milestones

| Milestone | Target | Scope |
|---|---|---|
| M0 — Foundation | Week 2 | Repo setup, CI/CD, DB schema, auth module, API gateway config |
| M1 — Core Student | Week 5 | Student module, staff module, basic admin portal |
| M2 — Academic | Week 8 | Academic module, attendance module, grade entry UI |
| M3 — Finance | Week 11 | Fee module, payment gateway, invoicing UI |
| M4 — Communication | Week 13 | Communication module, notifications, messaging UI |
| M5 — Scheduling | Week 15 | Schedule module, timetable builder |
| M6 — Mobile MVP | Week 17 | React Native app for Student & Guardian roles |
| M7 — Reporting | Week 19 | Report module, dashboards, exports |
| M8 — QA & Hardening | Week 21 | Full regression, load testing, security audit |
| M9 — Production | Week 23 | Production release, runbook, post-launch monitoring |

---

# Part 2 — API Endpoint Contract

## 12. API Conventions

### 12.1 Base URL

| Environment | Base URL |
|---|---|
| Development | `http://localhost:8080/api/v1` |
| Staging | `https://staging.smp.yourdomain.com/api/v1` |
| Production | `https://smp.yourdomain.com/api/v1` |

### 12.2 Standard Response Envelope

All responses (success **and** error) must use this envelope:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { },
  "error": null,
  "timestamp": "2026-06-22T10:30:00Z",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "pagination": null
}
```

**Error response:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "One or more fields are invalid",
    "details": [
      { "field": "email", "message": "must be a valid email address" },
      { "field": "dateOfBirth", "message": "must not be null" }
    ]
  },
  "timestamp": "2026-06-22T10:30:00Z",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "pagination": null
}
```

**Paginated response:**
```json
{
  "success": true,
  "message": "Students retrieved",
  "data": [ ],
  "error": null,
  "timestamp": "2026-06-22T10:30:00Z",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 350,
    "totalPages": 18
  }
}
```

### 12.3 Authentication

- All endpoints except `/auth/**` require `Authorization: Bearer <access_token>`.
- Access token expiry: **15 minutes**. Refresh via `POST /auth/refresh` using HttpOnly cookie.
- Roles are encoded in JWT claims. Spring Security validates on every request.

### 12.4 HTTP Status Codes

| Code | Meaning | When Used |
|---|---|---|
| `200 OK` | Success | GET, PUT, PATCH returning data |
| `201 Created` | Resource created | POST creating a new resource |
| `204 No Content` | Success, no body | DELETE or no-payload actions |
| `400 Bad Request` | Validation failure | Invalid input, missing fields |
| `401 Unauthorized` | Not authenticated | Missing or expired token |
| `403 Forbidden` | Not authorised | Valid token, insufficient role |
| `404 Not Found` | Resource missing | Entity does not exist |
| `409 Conflict` | State conflict | Duplicate resource, illegal state transition |
| `422 Unprocessable` | Semantic error | Structurally valid but semantically wrong |
| `429 Too Many Requests` | Rate limited | Client exceeded rate limit |
| `500 Internal Error` | Server fault | Unhandled exception |

### 12.5 Pagination & Filtering

- Paginated endpoints accept: `?page=0&size=20&sort=createdAt,desc`
- Default page size: `20`. Maximum: `100`.

### 12.6 Rate Limiting

- Global: 1,000 requests / 15 minutes per authenticated user.
- Auth endpoints: 10 requests / minute per IP.
- Exceeded limits return `429` with `Retry-After` header.

---

## 13. Auth Endpoints

**Base path:** `/api/v1/auth`

| Method | Path | Description | Auth |
|---|---|---|---|
| `POST` | `/auth/login` | Authenticate user, issue tokens | None |
| `POST` | `/auth/refresh` | Refresh access token via cookie | None |
| `POST` | `/auth/logout` | Invalidate session / revoke tokens | Bearer |
| `POST` | `/auth/mfa/setup` | Initiate TOTP MFA setup | Bearer |
| `POST` | `/auth/mfa/verify` | Verify TOTP code to complete MFA setup | Bearer |
| `POST` | `/auth/mfa/validate` | Validate MFA code at login | None |
| `POST` | `/auth/password/forgot` | Request password reset email | None |
| `POST` | `/auth/password/reset` | Reset password with token | None |
| `PUT` | `/auth/password/change` | Change password (authenticated) | Bearer |
| `GET` | `/auth/me` | Get current user profile | Bearer |
| `POST` | `/auth/users` | Provision new system user | `SUPER_ADMIN` |
| `GET` | `/auth/users` | List all users (paginated) | `SUPER_ADMIN` |
| `GET` | `/auth/users/{id}` | Get user by ID | `SUPER_ADMIN` |
| `PUT` | `/auth/users/{id}/roles` | Update user roles | `SUPER_ADMIN` |
| `PATCH` | `/auth/users/{id}/status` | Activate / deactivate user | `SUPER_ADMIN` |
| `GET` | `/auth/audit-logs` | Paginated auth audit log | `SUPER_ADMIN` |

### POST /auth/login

**Request Body:**
```json
{
  "email": "admin@school.edu",
  "password": "SecurePass123!",
  "mfaCode": "123456"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `email` | string | Yes | Valid email |
| `password` | string | Yes | Min 8 chars |
| `mfaCode` | string | Conditional | Required if MFA enabled |

**Response (200):**
```json
{
  "data": {
    "accessToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "uuid",
      "email": "admin@school.edu",
      "firstName": "Jane",
      "lastName": "Doe",
      "fullName": "Jane Doe",
      "role": "SCHOOL_ADMIN",
      "mfaEnabled": true
    }
  }
}
```

> Refresh token is set as `HttpOnly` cookie — not in the response body.

---

## 14. Student Endpoints

**Base path:** `/api/v1/students`

| Method | Path | Description | Roles |
|---|---|---|---|
| `POST` | `/students` | Enrol a new student | `ADMIN` |
| `GET` | `/students` | List students (paginated, filterable) | `ADMIN`, `TEACHER` |
| `GET` | `/students/{id}` | Get student profile by ID | `ADMIN`, `TEACHER` |
| `PUT` | `/students/{id}` | Update student profile | `ADMIN` |
| `PATCH` | `/students/{id}/status` | Change enrolment status | `ADMIN` |
| `DELETE` | `/students/{id}` | Soft-delete student record | `SUPER_ADMIN` |
| `POST` | `/students/bulk-import` | CSV bulk enrolment upload | `ADMIN` |
| `GET` | `/students/bulk-import/{jobId}` | Poll bulk import job status | `ADMIN` |
| `POST` | `/students/{id}/photo` | Upload student profile photo | `ADMIN`, `STUDENT` |
| `GET` | `/students/{id}/photo` | Get student photo URL | All |
| `GET` | `/students/{id}/guardian` | Get student's guardian info | `ADMIN` |
| `POST` | `/students/{id}/guardian` | Add/update guardian | `ADMIN` |
| `GET` | `/students/{id}/academic-summary` | Grades + attendance summary | All* |

### POST /students — Request Body

```json
{
  "firstName": "Amara",
  "lastName": "Obi",
  "dateOfBirth": "2005-03-14",
  "gender": "FEMALE",
  "nationality": "NG",
  "email": "amara.obi@student.edu",
  "phone": "+2348012345678",
  "programmeId": "uuid",
  "admissionDate": "2026-09-01",
  "guardianName": "Chukwu Obi",
  "guardianPhone": "+2348098765432",
  "guardianEmail": "chukwu.obi@email.com",
  "guardianRelationship": "PARENT"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `firstName` | string | Yes | |
| `lastName` | string | Yes | |
| `dateOfBirth` | string | Yes | ISO-8601: YYYY-MM-DD |
| `gender` | enum | Yes | `MALE` \| `FEMALE` \| `OTHER` |
| `nationality` | string | No | ISO 3166-1 alpha-2 |
| `email` | string | No | Creates auth user if provided |
| `phone` | string | No | E.164 format |
| `programmeId` | UUID | Yes | Must reference existing programme |
| `admissionDate` | string | Yes | ISO-8601 date |
| `guardianName` | string | No | |
| `guardianPhone` | string | No | |
| `guardianEmail` | string | No | |
| `guardianRelationship` | enum | No | `PARENT` \| `GUARDIAN` \| `SIBLING` \| `OTHER` |

### GET /students — Query Filters

| Param | Type | Notes |
|---|---|---|
| `page` | integer | Default: 0 |
| `size` | integer | Default: 20, max: 100 |
| `sort` | string | e.g. `lastName,asc` |
| `status` | enum | `ACTIVE`, `PENDING`, `SUSPENDED`, etc. |
| `programmeId` | UUID | Filter by programme |
| `search` | string | Full-text on name/studentNo |

### PATCH /students/{id}/status — Request Body

```json
{
  "status": "SUSPENDED",
  "reason": "Non-payment of fees"
}
```

Valid transitions: `PENDING → ACTIVE`, `ACTIVE → SUSPENDED`, `ACTIVE → GRADUATED`, `ACTIVE → WITHDRAWN`, `SUSPENDED → ACTIVE`

---

## 15. Academic Endpoints

**Base path:** `/api/v1/academic`

| Method | Path | Description | Roles |
|---|---|---|---|
| `GET` | `/academic/periods` | List academic periods | All |
| `POST` | `/academic/periods` | Create academic period | `ADMIN` |
| `PUT` | `/academic/periods/{id}` | Update academic period | `ADMIN` |
| `PATCH` | `/academic/periods/{id}/set-current` | Mark period as current | `ADMIN` |
| `GET` | `/academic/subjects` | List subjects | All |
| `POST` | `/academic/subjects` | Create subject | `ADMIN` |
| `PUT` | `/academic/subjects/{id}` | Update subject | `ADMIN` |
| `POST` | `/academic/grades` | Submit grade for student | `TEACHER` |
| `GET` | `/academic/grades` | List grades (filtered) | `ADMIN`, `TEACHER` |
| `GET` | `/academic/grades/student/{studentId}` | All grades for one student | All* |
| `PUT` | `/academic/grades/{id}` | Update grade (before publish) | `TEACHER` |
| `POST` | `/academic/grades/publish` | Publish grades for period+subject | `ADMIN` |
| `GET` | `/academic/gpa/student/{studentId}` | Get cumulative GPA | All* |
| `GET` | `/academic/transcripts/{studentId}` | Download transcript PDF | `ADMIN`, `STUDENT` |
| `GET` | `/academic/programmes` | List programmes | All |
| `POST` | `/academic/programmes` | Create programme | `ADMIN` |
| `PUT` | `/academic/programmes/{id}` | Update programme | `ADMIN` |

> **\*Scoped access:** Students and Guardians see only their own data. Teachers see only students in their classes.

### POST /academic/grades — Request Body

```json
{
  "studentId": "uuid",
  "subjectId": "uuid",
  "periodId": "uuid",
  "score": 78.5,
  "letterGrade": "B",
  "remarks": "Good improvement"
}
```

### POST /academic/grades/publish — Request Body

```json
{
  "periodId": "uuid",
  "subjectId": "uuid"
}
```

---

## 16. Attendance Endpoints

**Base path:** `/api/v1/attendance`

| Method | Path | Description | Roles |
|---|---|---|---|
| `POST` | `/attendance` | Record attendance for a session | `TEACHER` |
| `POST` | `/attendance/bulk` | Bulk record attendance (class list) | `TEACHER` |
| `GET` | `/attendance` | List attendance records (filtered) | `ADMIN`, `TEACHER` |
| `GET` | `/attendance/student/{studentId}` | Get attendance for student | All* |
| `GET` | `/attendance/student/{studentId}/summary` | Attendance % summary per subject | All* |
| `PUT` | `/attendance/{id}` | Correct an attendance record | `TEACHER`, `ADMIN` |
| `GET` | `/attendance/report/class/{classId}` | Attendance report for a class | `ADMIN`, `TEACHER` |
| `GET` | `/attendance/report/student/{studentId}` | Monthly attendance report PDF | `ADMIN`, `STUDENT` |
| `GET` | `/attendance/alerts` | Students below threshold | `ADMIN` |
| `PUT` | `/attendance/config/threshold` | Update absence alert threshold % | `ADMIN` |

### POST /attendance/bulk — Request Body

```json
{
  "sessionId": "uuid",
  "date": "2026-06-22",
  "records": [
    { "studentId": "uuid-1", "status": "PRESENT", "notes": "" },
    { "studentId": "uuid-2", "status": "ABSENT", "notes": "Called in sick" },
    { "studentId": "uuid-3", "status": "LATE", "notes": "15 minutes late" }
  ]
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `sessionId` | UUID | Yes | Timetable session reference |
| `date` | string | Yes | ISO-8601 date |
| `records[].studentId` | UUID | Yes | |
| `records[].status` | enum | Yes | `PRESENT` \| `ABSENT` \| `LATE` \| `EXCUSED` |
| `records[].notes` | string | No | Optional remark |

---

## 17. Fee Endpoints

**Base path:** `/api/v1/fees`

| Method | Path | Description | Roles |
|---|---|---|---|
| `GET` | `/fees/schedules` | List fee schedules | `ADMIN`, `FINANCE` |
| `POST` | `/fees/schedules` | Create fee schedule | `ADMIN` |
| `PUT` | `/fees/schedules/{id}` | Update fee schedule | `ADMIN` |
| `POST` | `/fees/invoices` | Generate invoice for student | `ADMIN`, `FINANCE` |
| `POST` | `/fees/invoices/bulk` | Bulk-generate invoices for period | `ADMIN` |
| `GET` | `/fees/invoices` | List invoices (paginated, filtered) | `ADMIN`, `FINANCE` |
| `GET` | `/fees/invoices/{id}` | Get invoice detail | `ADMIN`, `FINANCE`, `STUDENT` |
| `GET` | `/fees/invoices/student/{studentId}` | All invoices for a student | All* |
| `POST` | `/fees/payments/initiate` | Initiate payment (get gateway URL) | `STUDENT`, `ADMIN` |
| `POST` | `/fees/payments/webhook` | Payment gateway webhook handler | None (HMAC auth) |
| `GET` | `/fees/payments/{id}` | Get payment record | `ADMIN`, `FINANCE` |
| `GET` | `/fees/receipts/{paymentId}` | Download receipt PDF | `ADMIN`, `STUDENT` |
| `GET` | `/fees/report/summary` | Fee collection summary report | `ADMIN`, `FINANCE` |
| `GET` | `/fees/report/outstanding` | Outstanding balance report | `ADMIN`, `FINANCE` |

### POST /fees/payments/initiate — Request Body

```json
{
  "invoiceId": "uuid",
  "amount": 15000000,
  "gateway": "PAYSTACK",
  "callbackUrl": "https://smp.yourdomain.com/fees/payment-callback"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `invoiceId` | UUID | Yes | Invoice to pay against |
| `amount` | number | Yes | Amount in **minor unit** (e.g. kobo). Full or partial. |
| `gateway` | enum | Yes | `PAYSTACK` \| `FLUTTERWAVE` |
| `callbackUrl` | string | Yes | Frontend redirect URL after payment |

**Response (200):**
```json
{
  "data": {
    "paymentReference": "SMP-PAY-20260622-XYZ",
    "paymentUrl": "https://checkout.paystack.com/abc123",
    "expiresAt": "2026-06-22T11:30:00Z"
  }
}
```

> Frontend/mobile **must redirect** to `data.paymentUrl`. Do not render the gateway in an iframe.

---

## 18. Staff Endpoints

**Base path:** `/api/v1/staff`

| Method | Path | Description | Roles |
|---|---|---|---|
| `POST` | `/staff` | Create staff record | `ADMIN` |
| `GET` | `/staff` | List staff (paginated) | `ADMIN` |
| `GET` | `/staff/{id}` | Get staff profile | `ADMIN`, `SELF` |
| `PUT` | `/staff/{id}` | Update staff profile | `ADMIN` |
| `PATCH` | `/staff/{id}/status` | Activate / deactivate staff | `ADMIN` |
| `DELETE` | `/staff/{id}` | Soft-delete staff record | `SUPER_ADMIN` |
| `POST` | `/staff/{id}/photo` | Upload staff photo | `ADMIN`, `SELF` |
| `GET` | `/staff/{id}/schedule` | Get staff teaching schedule | `ADMIN`, `SELF` |
| `POST` | `/staff/{id}/subjects` | Assign subjects to staff | `ADMIN` |
| `DELETE` | `/staff/{id}/subjects/{subjectId}` | Remove subject assignment | `ADMIN` |

---

## 19. Schedule Endpoints

**Base path:** `/api/v1/schedules`

| Method | Path | Description | Roles |
|---|---|---|---|
| `POST` | `/schedules/timetable` | Create timetable entry | `ADMIN` |
| `GET` | `/schedules/timetable` | List timetable entries | All |
| `PUT` | `/schedules/timetable/{id}` | Update timetable entry | `ADMIN` |
| `DELETE` | `/schedules/timetable/{id}` | Delete timetable entry | `ADMIN` |
| `GET` | `/schedules/timetable/class/{classId}` | Get class weekly timetable | All |
| `GET` | `/schedules/timetable/staff/{staffId}` | Get staff weekly schedule | `ADMIN`, `TEACHER` |
| `POST` | `/schedules/timetable/check-conflicts` | Validate for booking conflicts | `ADMIN` |
| `POST` | `/schedules/exams` | Create exam schedule entry | `ADMIN` |
| `GET` | `/schedules/exams` | List exam schedules | All |
| `PUT` | `/schedules/exams/{id}` | Update exam entry | `ADMIN` |
| `DELETE` | `/schedules/exams/{id}` | Delete exam entry | `ADMIN` |
| `GET` | `/schedules/timetable/{id}/export.ics` | Export timetable as ICS | All |
| `GET` | `/schedules/rooms` | List available rooms | `ADMIN` |
| `POST` | `/schedules/rooms` | Create room | `ADMIN` |

---

## 20. Communication Endpoints

**Base path:** `/api/v1/communication`

| Method | Path | Description | Roles |
|---|---|---|---|
| `POST` | `/communication/announcements` | Create announcement | `ADMIN`, `TEACHER` |
| `GET` | `/communication/announcements` | List announcements (caller's audience) | All |
| `GET` | `/communication/announcements/{id}` | Get announcement detail | All |
| `PUT` | `/communication/announcements/{id}` | Update announcement | `ADMIN`, `TEACHER` |
| `DELETE` | `/communication/announcements/{id}` | Delete announcement | `ADMIN` |
| `GET` | `/communication/messages` | List conversation threads | All |
| `POST` | `/communication/messages` | Send a new message | All |
| `GET` | `/communication/messages/{threadId}` | Get messages in thread | All |
| `POST` | `/communication/messages/{threadId}/reply` | Reply to a thread | All |
| `PATCH` | `/communication/messages/{id}/read` | Mark message as read | All |
| `GET` | `/communication/notifications` | Get notification inbox | All |
| `PATCH` | `/communication/notifications/{id}/read` | Mark notification read | All |
| `DELETE` | `/communication/notifications/clear` | Clear all notifications | All |
| `GET` | `/communication/templates` | List notification templates | `ADMIN` |
| `PUT` | `/communication/templates/{id}` | Update notification template | `ADMIN` |

---

## 21. Report Endpoints

**Base path:** `/api/v1/reports`

| Method | Path | Description | Roles |
|---|---|---|---|
| `GET` | `/reports/dashboard` | Admin KPI dashboard data | `ADMIN` |
| `GET` | `/reports/enrolment` | Enrolment summary report | `ADMIN` |
| `GET` | `/reports/enrolment/export` | Export enrolment report | `ADMIN` |
| `GET` | `/reports/attendance` | Attendance overview report | `ADMIN` |
| `GET` | `/reports/attendance/export` | Export attendance report | `ADMIN` |
| `GET` | `/reports/grades` | Grade distribution report | `ADMIN` |
| `GET` | `/reports/grades/export` | Export grade report | `ADMIN` |
| `GET` | `/reports/fees` | Fee collection report | `ADMIN`, `FINANCE` |
| `GET` | `/reports/fees/export` | Export fee report | `ADMIN`, `FINANCE` |
| `POST` | `/reports/custom` | Run custom report query | `ADMIN` |
| `GET` | `/reports/custom/{jobId}` | Poll custom report job | `ADMIN` |
| `GET` | `/reports/custom/{jobId}/download` | Download completed report | `ADMIN` |

### Export Query Params (applies to all `/export` endpoints)

| Param | Type | Default | Notes |
|---|---|---|---|
| `format` | enum | `PDF` | `PDF` \| `CSV` \| `XLSX` |
| `from` | string | Period start | ISO-8601 date |
| `to` | string | Today | ISO-8601 date |
| `programmeId` | UUID | All | Filter by programme |
| `classId` | UUID | All | Filter by class |

---

## 22. Standard Error Codes

Teams must handle `error.code` programmatically. Never rely on `error.message` for logic.

| Error Code | HTTP | Description |
|---|---|---|
| `AUTH_INVALID_CREDENTIALS` | 401 | Wrong email or password |
| `AUTH_ACCOUNT_LOCKED` | 401 | Account locked after failed attempts |
| `AUTH_TOKEN_EXPIRED` | 401 | JWT access token has expired |
| `AUTH_TOKEN_INVALID` | 401 | Malformed or tampered token |
| `AUTH_MFA_REQUIRED` | 401 | MFA code required but not provided |
| `AUTH_MFA_INVALID` | 401 | Incorrect TOTP code |
| `ACCESS_DENIED` | 403 | Insufficient role for this operation |
| `RESOURCE_NOT_FOUND` | 404 | Requested entity does not exist |
| `DUPLICATE_RESOURCE` | 409 | Entity already exists |
| `INVALID_STATUS_TRANSITION` | 409 | Illegal lifecycle state change |
| `VALIDATION_FAILED` | 400 | Request body fails field validation |
| `PAYMENT_GATEWAY_ERROR` | 502 | Downstream payment gateway error |
| `FILE_TOO_LARGE` | 400 | Uploaded file exceeds size limit |
| `UNSUPPORTED_FILE_TYPE` | 400 | File type not accepted |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests from this client |
| `INTERNAL_ERROR` | 500 | Unhandled server fault; check requestId in logs |

---

## 23. Required HTTP Headers

| Header | Direction | Required | Notes |
|---|---|---|---|
| `Authorization` | Request | Yes (non-auth) | `Bearer <access_token>` |
| `Content-Type` | Request | Yes (body requests) | `application/json` or `multipart/form-data` |
| `Accept` | Request | Recommended | `application/json` |
| `X-Request-ID` | Request | Recommended | Client-generated UUID for tracing |
| `X-Client-Version` | Request | Mobile required | Semver app version e.g. `1.2.3` |
| `X-Request-ID` | Response | Always | Echoed from request or server-generated |
| `X-RateLimit-Limit` | Response | Always | Rate limit ceiling for window |
| `X-RateLimit-Remaining` | Response | Always | Remaining requests in current window |
| `Retry-After` | Response | On 429 | Seconds until rate limit resets |

---

## 24. Webhook Contract (Payment Gateway)

`POST /api/v1/fees/payments/webhook`

This endpoint does **not** use Bearer auth. It validates an HMAC-SHA512 signature.

### Verification Logic

1. Extract `X-Gateway-Signature` header from the request.
2. Compute HMAC-SHA512 of the raw request body using the gateway secret key.
3. Compare computed hash to header value (constant-time comparison to prevent timing attacks).
4. Reject with `401` if mismatch.
5. Respond `200` immediately; process asynchronously.

### Webhook Payload

```json
{
  "event": "payment.success",
  "reference": "SMP-PAY-20260622-XYZ",
  "amount": 15000000,
  "currency": "NGN",
  "gatewayReference": "PAYSTACK-TXN-123456",
  "paidAt": "2026-06-22T10:45:00Z",
  "metadata": {
    "invoiceId": "uuid",
    "studentId": "uuid"
  }
}
```

| Field | Type | Notes |
|---|---|---|
| `event` | string | `payment.success` \| `payment.failed` \| `payment.reversed` |
| `reference` | string | Internal payment reference |
| `amount` | number | Amount in minor units |
| `currency` | string | ISO 4217 (e.g. `NGN`) |
| `gatewayReference` | string | Gateway's own transaction ID |
| `paidAt` | string | ISO-8601 datetime |
| `metadata.invoiceId` | UUID | Passed at payment initiation |
| `metadata.studentId` | UUID | Passed at payment initiation |

---

## 25. Versioning & Deprecation Policy

- API version is part of the URL path: `/api/v1/...`
- Breaking changes require a new major version (`/api/v2/...`) with minimum **3-month** parallel support.
- Non-breaking additions (new optional fields, new endpoints) are backward-compatible — no versioning needed.
- Deprecated endpoints return `Deprecation` and `Sunset` headers with the removal date.
- All clients **must** handle unknown fields gracefully (ignore, do not fail).

---

## 26. Team-Specific Notes

### 26.1 Backend

- Implement the standard response envelope via a global `@ControllerAdvice` / `@ExceptionHandler`.
- Validate all inputs using Bean Validation (`@Valid`). Return `VALIDATION_FAILED` with a `details[]` array per field.
- Use correlation IDs (`X-Request-ID`) in all log entries via MDC.
- Publish a Spring application event for every state-changing operation (student enrolled, grade published, payment received). Other modules listen via `@TransactionalEventListener`.
- No endpoint returns a raw entity/JPA model; always map to a response DTO.
- Module boundary rule: modules must never directly autowire another module's repository. Call the public service interface only.

### 26.2 Frontend

- Consume the `data` field exclusively. Never inspect raw HTTP status codes for business logic.
- Handle `AUTH_TOKEN_EXPIRED` by triggering a silent refresh via `POST /auth/refresh` before retrying once.
- All list views must handle pagination using the `pagination` envelope fields.
- Display `error.message` to users for 400/422; show a generic fallback for 500.

### 26.3 Mobile

- Always include `X-Client-Version` header on every request.
- Cache GET responses (students, timetable, grades) with a 5-minute TTL for offline resilience.
- Register push notification device token via `POST /communication/notifications/register-device` (added in M6).
- Deep-link schema: `smp://students/{id}`, `smp://fees/invoices/{id}`, `smp://timetable`.

### 26.4 QA

- Validate every endpoint against this contract using Postman Collection or REST-Assured.
- Every error code in Section 22 must have a corresponding negative test case.
- Contract tests (Pact) must run in CI before any merge to `main`.
- Load test targets: `POST /auth/login` ≤ 200ms p99; `GET /students` ≤ 300ms p99 at 1,000 RPS.
- Verify HMAC webhook validation: test valid signature, invalid signature, and missing header.

### 26.5 DevOps

- Expose `GET /actuator/health` for Kubernetes liveness/readiness probes.
- Expose `GET /actuator/metrics` for Prometheus scraping.
- All secrets (DB credentials, JWT secret, gateway keys) via Kubernetes Secrets / Vault. Never hardcoded.
- CI pipeline gates: unit tests pass → contract tests pass → Sonar quality gate pass → Docker build succeeds → smoke test on staging.

---

## 27. Document Changelog

| Version | Date | Author | Changes |
|---|---|---|---|
| 1.0 | June 2026 | Engineering Lead | Initial release — full PRD + endpoint contract, monolith architecture |

---

*— End of Document —*
