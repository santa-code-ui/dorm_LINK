# dorm_LINK — Campus Oriented Hostel Manager

A full-stack hostel management system for educational campuses, with separate **Admin** and
**Student** portals. Built to the supplied system requirements and the exact folder layout in
*DormLink_Detailed_Folder_Structure*.

- **Frontend:** HTML · CSS (Neumorphism + Glassmorphism, SF Pro, "chameleon" hover) · vanilla JS
- **Backend:** Java (Jakarta Servlets, layered controllers → services → DAO → models)
- **Database:** MySQL (XAMPP / phpMyAdmin)
- **Auth:** salted SHA-256 passwords · email-OTP password reset
- **QR module:** entry/exit attendance logging

## Features

**Admin** — dashboard with live stats, manage courses, manage rooms, approve/reject room
requests, manage students, post hostel/campus/university announcements, fee clearance with
reminders, reports.

**Student** — dashboard with quick info + rotating announcements, profile, book hostel
(block-wise rooms, ratings, mess menu), room details + roommates + real-time hostel rating,
weekly mess menu, QR entry/exit logging, complaints (Maintenance/Food/Electricity/Water/Others),
fees with payment + downloadable receipts.

## Quick start

1. Import the database: run `database/schema.sql` then `database/seed_data.sql` in phpMyAdmin.
2. Set credentials in `backend/config/db.properties`.
3. Compile `backend/**` against the Tomcat servlet API + MySQL connector, deploy to **Tomcat 10.1+**
   alongside the static `frontend/` and `uploads/` folders.
4. Open `http://localhost:8080/dormlink/frontend/html/auth/signin.html`.

Full steps: **[`docs/INSTALLATION_GUIDE.md`](docs/INSTALLATION_GUIDE.md)**.

> **Preview without a backend:** open any HTML page directly — if the API is offline the UI shows a
> "Preview mode" banner and renders sample data, so the whole interface is reviewable instantly.

### Demo logins (from `seed_data.sql`)
| Role    | Identifier      | Password     |
|---------|-----------------|--------------|
| Admin   | `admin`         | `admin123`   |
| Admin   | `warden`        | `warden123`  |
| Student | `USTM2024CS001` | `student123` |

## Project structure
```
dorm_LINK/
├── database/      schema.sql · seed_data.sql · migrations/
├── docs/          INSTALLATION_GUIDE.md · DFD/ · ER_DIAGRAM/ · UML/
├── frontend/      assets/ · css/ · js/ · html/{auth,admin,student}/
├── backend/       controllers/ · services/ · dao/ · models/ · utils/ · config/
├── api/           REST route specs per module (auth, rooms, students, complaints, fees, logs)
├── uploads/       student_photos/ · receipts/ · hostel_images/
├── README.md · .gitignore · CNAME
```

## REST API
Each module is one Jakarta servlet mapped under `/api/...`; see the per-module specs in
[`api/`](api/). Endpoints are grouped as `/api/auth`, `/api/rooms`, `/api/students`,
`/api/complaints`, `/api/fees`, `/api/logs`, and `/api/announcements`.
All list responses are wrapped as `{"data":[...]}`; the frontend unwraps them in `js/dashboard.js`.

## Notes
- **Announcements** are served by `AnnouncementController` at `/api/announcements` (a cross-cutting
  feature, not one of the six PDF `api/` module folders).
- **Email OTP** is generated and logged server-side (`utils/OtpUtil`); connect SMTP to deliver it.
  A persistent `password_reset_otp` table is available if you prefer DB-backed OTPs.
- **QR images:** `utils/QrUtil` returns the URL each gate sticker should encode; render the PNG with
  any QR library into `frontend/assets/qr/`.

*USTM · Campus Oriented Hostel Manager project.*
