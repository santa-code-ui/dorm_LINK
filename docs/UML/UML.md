# UML Reference

## Layered class organisation (matches the folder structure)
```
controllers/      (HTTP layer — Jakarta servlets, one per module)
   AuthController · StudentController · RoomController
   ComplaintController · FeeController · LogController · AnnouncementController
        │ calls
services/         (business logic)
   AuthService · StudentService · RoomService
   ComplaintService · FeeService · LogService · AnnouncementService
        │ calls
dao/              (data access — JDBC)
   AuthDao · StudentDao · CourseDao · RoomDao · HostelDao · RoomRequestDao
   ComplaintDao · FeeDao · LogDao · AnnouncementDao · RatingDao · MessMenuDao
        │ maps rows to / from
models/           (POJOs with toJson())
   Admin · Student · Course · Hostel · Room · RoomRequest · Booking
   Announcement · Complaint · Fee · Payment · EntryExitLog · Rating · MessMenu
utils/            DBConnection · PasswordUtil · OtpUtil · QrUtil · JsonUtil · AuthUtil
config/           DatabaseConfig (reads db.properties)
```

## Key use-case sequences
```
Login:     signin.html → POST /api/auth/login → AuthService.login → AuthDao → session JSON
Booking:   book_hostel → POST /api/rooms/requests → (admin) PUT /api/rooms/requests/{id}
           → RoomService.decideRequest → RoomDao.allocate (sets room_id, occupancy++)
Payment:   fees → POST /api/fees/pay → FeeDao.applyPayment (txn) → receipt no.
QR log:    entry_exit_log → POST /api/logs → resolve roll → LogDao.insert
```
