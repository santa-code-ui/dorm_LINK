# Data Flow Diagrams (text reference)

## Context / Level 0
```
[Admin]  ──┐                         ┌── manages students, rooms, fees, announcements
           ├──►  ( dorm_LINK system ) │
[Student]──┘                         └── books rooms, pays fees, files complaints, QR log
```

## Level 1
```
External entities : Admin, Student
Processes         : 1 Authentication · 2 Hostel Management · 3 Room Allocation
                    4 Fee Management · 5 Complaint Management · 6 Entry/Exit Management
Data stores       : Student DB · Room DB · Booking DB · Fee DB · Complaint DB · Log DB

Student/Admin ─► 1 Authentication ─► (User DB) ─► Dashboard
Student ─► 2/3 Browse & Request Room ─► Availability check (Room DB) ─► Admin approval ─► Booking DB ─► Allocation
Student ─► 4 Fees ─► Payment ─► Fee/Payment DB ─► Receipt
Student ─► 5 Complaint ─► Complaint DB
Student ─► 6 QR scan ─► Entry/Exit ─► Log DB (visible to student + admin)
```

## Level 2 — Authentication
```
Login Request ─► validate credentials ─► User DB ─► grant dashboard access
Forgot Password ─► OTP Service ─► verify ─► update password in User DB
```

## Level 2 — Hostel Booking
```
Browse Hostel ─► Room Request ─► Availability Check (Room DB)
            ─► Admin Approval ─► Booking DB ─► Student Allocation (room_id set, occupancy++)
```
