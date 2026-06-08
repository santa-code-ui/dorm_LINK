# /api/complaints  —  Complaints

Implemented by `backend/controllers/ComplaintController.java` → `ComplaintService` → `ComplaintDao`.
Types: `MAINTENANCE`, `FOOD`, `ELECTRICITY`, `WATER`, `OTHERS`.

| Method | Path                    | Body                              | Purpose |
|--------|-------------------------|-----------------------------------|---------|
| GET    | `/api/complaints/mine`  | — (session)                       | Signed-in student's complaints |
| GET    | `/api/complaints`       | —                                 | All complaints (admin / reports) |
| POST   | `/api/complaints`       | `{type, subject, description}`    | File a complaint |
