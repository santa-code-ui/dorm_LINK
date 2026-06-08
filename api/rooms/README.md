# /api/rooms  —  Rooms, Hostels, Mess, Ratings, Requests

Implemented by `backend/controllers/RoomController.java` → `RoomService`
→ `RoomDao` / `HostelDao` / `MessMenuDao` / `RatingDao` / `RoomRequestDao`.

| Method | Path                         | Body / Query                  | Purpose |
|--------|------------------------------|-------------------------------|---------|
| GET    | `/api/rooms`                 | `?hostelId=` (optional)       | List rooms (all or by hostel) |
| POST   | `/api/rooms`                 | `{roomNo, roomType, capacity, rentAmount, hostelId}` | Add a room (admin) |
| GET    | `/api/rooms/hostels`         | —                             | Hostel blocks + live availability + avg rating |
| GET    | `/api/rooms/mess`            | `?hostelId=` (optional)       | Weekly mess menu, keyed by day |
| GET    | `/api/rooms/mine`            | — (session)                   | Signed-in student's room + roommates |
| POST   | `/api/rooms/rate`            | `{hostelId, stars, review}`   | Rate a hostel (updates avg in real time) |
| GET    | `/api/rooms/requests`        | —                             | All room requests (admin) |
| POST   | `/api/rooms/requests`        | `{roomId, note}`              | Student requests a room |
| PUT    | `/api/rooms/requests/{id}`   | `{status: APPROVED\|REJECTED}`| Admin decision (APPROVED → allocates the room) |
