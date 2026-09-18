# ScholarMatch

A Spring Boot web application that helps students discover, match, and track scholarships and government schemes based on their academic and financial profile.

## 1. Overview

Students across India often miss out on scholarships and government schemes simply because they don't know these opportunities exist or don't know if they qualify. ScholarMatch solves this by letting students build a profile (academic details, financial information, category, state, special status) and then automatically scoring how well they match against scholarships submitted by verified institutions or added by admins. Students can search, bookmark, and receive deadline reminders, while institutions submit scholarships for admin approval and admins manage the platform end-to-end.

## 2. Key Features

| Area | Features |
|---|---|
| Student | Registration/login, profile with academic & financial info, special status, document uploads, profile photo |
| Discovery | Scholarship search/filtering, eligibility/match scoring with detailed breakdown, bookmarking |
| Notifications | In-app notifications, automated deadline reminder emails for bookmarked scholarships |
| Institution | Registration/login, document submission for verification, scholarship submission, contact person management |
| Admin | Institution approval/rejection workflow, scholarship management, admin activity log, most-viewed/most-searched analytics |
| Auth & Security | JWT-based role authentication (Student/Institution/Admin), password hashing, login rate limiting, forgot/reset password |


## 3. User Roles

- **Student** — registers, completes academic/financial profile, searches and bookmarks scholarships, views match scores and eligibility breakdowns, receives deadline reminders.
- **Institution** — registers and submits verification documents, and once approved by an admin, can submit scholarships for the platform.
- **Admin** — approves/rejects institutions, manages scholarships and certificate types, reviews activity logs and view/search analytics.

## 4. How the System Works

**Student flow:** Register → complete academic & financial info (and optional special status) → browse/search scholarships → view eligibility/match score for each → bookmark scholarships of interest → receive in-app + email deadline reminders for bookmarked scholarships as the deadline approaches.

**Institution flow:** Register → upload verification documents → wait for admin approval (tracked in a verification log) → once approved, submit scholarships (optionally using AI to extract structured details from pasted text) → scholarships go live for students after being approved.

**Admin flow:** Log in → review pending institutions and approve/reject → manage scholarships, certificate types, and required documents → monitor admin activity log and scholarship view/search analytics.

## 5. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.2 |
| Data access | Spring JDBC (`JdbcTemplate`) — **not** JPA/Hibernate (explicitly excluded via `HibernateJpaAutoConfiguration`) |
| Database | MySQL (via `mysql-connector-j`) |
| Security | Spring Security + JWT (`jjwt` 0.12.6), custom login rate limiting filter |
| Frontend | Static HTML, CSS, vanilla JavaScript (fetch-based API client) |
| Email | Spring Mail (SMTP) for deadline reminder and password reset emails |
| Build | Maven |
| Utilities | Lombok, Bean Validation (`spring-boot-starter-validation`) |

## 6. Project Structure

```
ScholarMatch/
├── frontend/                     # Static HTML/CSS/JS client
│   ├── index.html, student.html, institution.html, admin.html, ...
│   ├── js/api.js                 # API client (base URL: http://localhost:8080/api)
│   └── css/style.css
├── src/main/java/com/example/scholarmatch/
│   ├── auth/                     # Login, forgot/reset password endpoints
│   ├── security/                 # JWT filter, JwtUtil, rate limiting, SecurityConfig
│   ├── student/                  # Student profile CRUD
│   ├── academicinfo/ financialinfo/ specialstatus/   # Student profile sub-sections
│   ├── institution/              # Institution registration & management
│   ├── institutioncontact/ institutiondocument/ institutionverificationlog/
│   ├── scholarship/              # Scholarship CRUD, search
│   ├── scholarshipmatchscore/    # EligibilityEngineService, match score API
│   ├── scholarshiprequireddocument/ scholarshipviewlog/
│   ├── bookmark/                 # Saved scholarships
│   ├── notification/             # In-app notifications
│   ├── reminder/                 # DeadlineReminderService (scheduled job)
│   ├── admin/ adminactivitylog/  # Admin management & audit log
│   ├── certificatetype/ certificatestage/ certificateportallink/
│   ├── searchlog/ passwordreset/ ai/ filestorage/ common/ exception/ config/
│   └── ScholarMatchApplication.java
├── sql/schema.sql                # Database schema (currently empty in this repo)
├── uploads/                      # Uploaded student/institution documents & photos
└── pom.xml
```

There is also a legacy standalone package under `src/model`, `src/service`, `src/repository`, `src/util`, `src/abstraction`, `src/menu` (with its own `Main.java`) — an earlier console-based prototype kept in the repository alongside the current Spring Boot application. It is **not** part of the running web application.

## 7. Database

ScholarMatch uses **MySQL**, accessed through Spring JDBC (`JdbcTemplate`) rather than JPA/Hibernate. Each domain module has its own model, `RowMapper`, and repository class. Based on the implemented modules, the major tables/entities are:

`student`, `institution`, `admin`, `scholarship`, `bookmark`, `notification`, `academic_info`, `financial_info`, `special_status`, `student_document`, `institution_document`, `institution_contact_person`, `institution_verification_log`, `admin_activity_log`, `search_log`, `scholarship_view_log`, `scholarship_required_document`, `scholarship_match_score`, `certificate_type`, `certificate_stage`, `certificate_portal_link`, `password_reset_token`

> Note: the `sql/schema.sql` file included in this repository is currently empty; the schema must be created from the entities/repositories or your own SQL before running the app.

## 8. API Overview

All endpoints are under `/api`. Grouped by controller:

| Group | Base path |
|---|---|
| Authentication | `/api/auth` (student/admin/institution login, forgot/reset password) |
| Students | `/api/students`, `/api/students/{id}/academic-info`, `/api/students/{id}/financial-info`, `/api/students/{id}/special-status`, `/api/students/{id}/documents`, `/api/students/{id}/bookmarks` |
| Scholarships | `/api/scholarships`, `/api/scholarships/{id}/views`, `/api/scholarships/{id}/required-documents` |
| Institutions | `/api/institutions`, `/api/institutions/{id}/documents`, `/api/institutions/{id}/contacts`, `/api/institutions/{id}/verification-log` |
| Admin | `/api/admins`, `/api/admin-activity-log` |
| Certificates | `/api/certificate-types`, `/api/certificate-types/{id}/stages`, `/api/certificate-types/{id}/portal-links` |
| Notifications | `/api/notifications` |
| Search log | `/api/search-log` |


## 9. Security

- **JWT authentication** — `JwtAuthenticationFilter` + `JwtUtil` issue and validate stateless tokens (session policy: `STATELESS`).
- **Password hashing** — via `PasswordHashUtil`.
- **Role-based authorization** — enforced in `SecurityConfig` with `hasRole`/`hasAnyRole` (`ADMIN`, `INSTITUTION`, `STUDENT`) per endpoint group.
- **Login rate limiting** — `LoginRateLimitFilter`, configurable (`auth.rate-limit.max-attempts`, `auth.rate-limit.window-ms`).
- **CORS** — enabled via Spring's default CORS customizer; **CSRF is disabled** (stateless JWT API).
- **Password reset flow** — token-based, via `PasswordResetService`/`PasswordResetToken`, with a configurable expiry (`app.reset-password.token-expiry-minutes`).

## 10. Scholarship Matching / Eligibility Engine

`EligibilityEngineService` computes a weighted match score for each student–scholarship pair:

| Factor | Weight |
|---|---|
| Category (e.g. GENERAL/OBC/SC/ST/EWS/MINORITY) | 25 |
| Annual income | 25 |
| Course (with alias mapping, e.g. "CSE" ↔ "Computer Science Engineering") | 20 |
| Marks / CGPA | 15 |
| State | 10 |
| Special status | 5 |

It also produces a detailed eligibility breakdown (`EligibilityDetailsResponse`), including certificate/document status, so students can see exactly why they matched or didn't.

## 11. Notifications and Deadline Reminders

`DeadlineReminderService` runs on a daily schedule (`@Scheduled(cron = "0 0 8 * * *")`, 08:00 server time). It finds active, approved scholarships whose deadline falls within a configurable window (`scholarship.reminder.days-before-deadline`, default **7 days**), then for every student who has **bookmarked** that scholarship:
1. Creates an in-app notification (type `DEADLINE_ALERT`), skipping duplicates.
2. Sends an email reminder if the student has an email address on file, and marks the notification as emailed.

A manual `runNow()` method exists for testing the job without waiting for the schedule.

## 12. Installation and Setup (Windows)

**Prerequisites:** JDK 17, Maven (or use the included `mvnw.cmd`), MySQL Server, a modern browser (or a static file server for the frontend).

```cmd
:: 1. Clone the repository
git clone https://github.com/bhavanimurugan-30/SCHOLARMATCH.git
cd SCHOLARMATCH

:: 2. Create the MySQL database
mysql -u root -p -e "CREATE DATABASE scholarmatch;"

:: 3. Configure src\main\resources\application.properties
::    (datasource URL/username/password, jwt.secret, mail credentials, etc. - see Configuration below)

:: 4. Run the backend
mvnw.cmd spring-boot:run

:: 5. Serve the frontend (from the frontend/ folder), e.g. with VS Code Live Server
::    or any static file server, on port 5500 to match the default reset-password URL
```

## 13. Configuration

The following values in `application.properties` are sensitive and **must not be committed with real values**:

| Property | Purpose |
|---|---|
| `spring.datasource.username` / `spring.datasource.password` | MySQL credentials |
| `jwt.secret` | JWT signing secret (must be a long, random, base64 value) |
| `spring.mail.username` / `spring.mail.password` | SMTP credentials for reminder/reset emails |
| `ai.anthropic.api-key` (`ANTHROPIC_API_KEY` env var) | Anthropic API key for the AI extraction feature |

Use environment variables or a local, git-ignored properties override for these values in real deployments.

## 14. Running the Application

- **Backend:** `http://localhost:8080` (API root: `http://localhost:8080/api`)
- **Frontend:** expected at `http://localhost:5500/frontend/...` (the reset-password link is built from `app.reset-password.base-url=http://localhost:5500/frontend/reset-password.html`); serve the `frontend/` folder with any static server on that port, or adjust the property to match your setup.

## 15. Testing

The project includes a single Spring Boot context-load test (`ScholarMatchApplicationTests`). There is no broader automated test suite (unit/integration tests for individual services or controllers) at this time.

## 16. Future Enhancements

*(Ideas only — not implemented)*
- Cloud deployment
- Mobile application
- Further refinements to the eligibility/matching algorithm

## 17. Project Status

The core web application (student, institution, and admin modules, authentication, eligibility engine, and deadline reminders) is implemented and functional for local development/review. The `sql/schema.sql` file is currently empty and the legacy console prototype under `src/model` etc. is not part of the active application.

## 18. Author

**Bhavani M.**
B.E. Computer Science and Engineering
Chennai Institute of Technology

## 19. License

No license has been specified for this repository.
