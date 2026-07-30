# Implementation Plan — Student Management Portal

## Legend
- ✅ Done
- 🔜 Next up
- ⏳ Planned
- ❌ Not started

---

## 1. Auth — ✅ complete
| Endpoint | Status |
|---|---|
| POST /api/v1/auth/login | ✅ |
| GET /api/v1/auth/verify-email | ✅ |
| POST /api/v1/auth/refresh | ✅ |
| POST /api/v1/auth/logout | ✅ |
| POST /api/v1/auth/password/forgot | ✅ |
| POST /api/v1/auth/password/reset | ✅ |
| PUT /api/v1/auth/password/change | ✅ |
| GET /api/v1/auth/me | ✅ |
| POST /api/v1/auth/users | ✅ |
| GET /api/v1/auth/users | ✅ |
| GET /api/v1/auth/users/{id} | ✅ |
| PUT /api/v1/auth/users/{id}/roles | ✅ |
| PATCH /api/v1/auth/users/{id}/status | ✅ |
| POST /api/v1/auth/mfa/setup | ✅ |
| POST /api/v1/auth/mfa/verify | ✅ |
| POST /api/v1/auth/mfa/validate | ✅ |
| GET /api/v1/auth/audit-logs | ✅ |

## 2. Students — ✅ complete
All 13 endpoints implemented.

## 3. Staff — ✅ complete
Create, list, get, update, status, delete all implemented.
Photo, schedule, subjects remain TBD.

## 4. Programmes — ✅ complete
List + create implemented. Update TBD.

## 5. Academic — ✅ complete
All 14 endpoints implemented across Grades, GPA, Transcripts, Subjects, and Periods.

## 6. Attendance — ✅ complete
All 10 endpoints implemented.

## 7. Communication — ✅ complete
All 15 endpoints implemented across Announcements, Messages, Notifications, and Templates.

## 8. Fees — ✅ complete
All 14 endpoints implemented across Schedules, Invoices, Payments, and Reports.

## 9. Scheduling — ✅ complete
All 14 endpoints implemented across Rooms, Exams, and Timetable (including ICS export and conflict checking).

## 10. Reports
11 endpoints + Dashboard — ❌ all stub.

---

## Recommended Order (one at a time)

1. **Academic (Grades, GPA, Subjects, Periods)** — foundational data
2. **Attendance** — depends on students + timetable
3. **Scheduling (Rooms → Exams → Timetable)** — depends on subjects + staff
4. **Fees** — depends on students
5. **Communication (Templates → Announcements → Notifications → Messages)** — mostly independent
6. **Reports + Dashboard** — depends on everything else
7. **Auth remaining endpoints** — user/role management, password flow
8. **Students remaining endpoints** — photo, guardian, bulk import, academic summary
