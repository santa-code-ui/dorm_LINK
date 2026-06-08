# Entity-Relationship Reference

## Entities (PK / FKs)
- ADMIN(admin_id PK)
- COURSE(course_id PK)
- HOSTEL(hostel_id PK)
- ROOM(room_id PK, hostel_id FK)
- STUDENT(student_id PK, course_id FK, room_id FK)
- ROOM_REQUEST(request_id PK, student_id FK, room_id FK, decided_by FK→admin)
- BOOKING(booking_id PK, student_id FK, room_id FK)
- ANNOUNCEMENT(announcement_id PK, admin_id FK)
- COMPLAINT(complaint_id PK, student_id FK)
- FEE(fee_id PK, student_id FK)
- PAYMENT(payment_id PK, fee_id FK)
- ENTRY_EXIT_LOG(log_id PK, student_id FK)
- RATING(rating_id PK, student_id FK, hostel_id FK)
- MESS_MENU(menu_id PK, hostel_id FK)
- PASSWORD_RESET_OTP(otp_id PK)

## Relationships (cardinality)
```
COURSE  1───N STUDENT
HOSTEL  1───N ROOM
ROOM    1───N STUDENT          (capacity controlled)
STUDENT 1───N ROOM_REQUEST     ROOM 1───N ROOM_REQUEST
STUDENT 1───N BOOKING
STUDENT 1───N COMPLAINT
STUDENT 1───N FEE              FEE  1───N PAYMENT
STUDENT 1───N ENTRY_EXIT_LOG
STUDENT 1───N RATING           HOSTEL 1───N RATING
HOSTEL  1───N MESS_MENU
ADMIN   1───N ANNOUNCEMENT     ADMIN 1───N ROOM_REQUEST (approvals)
```
See `database/schema.sql` for the authoritative column-level definition.
