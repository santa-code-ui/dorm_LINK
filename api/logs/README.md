# /api/logs  —  QR Entry / Exit Logging

Implemented by `backend/controllers/LogController.java` → `LogService` → `LogDao`.
A gate QR encodes the entry/exit page URL (see `utils/QrUtil`); the student confirms
their roll number, which resolves to their account before the row is written.

| Method | Path              | Body                          | Purpose |
|--------|-------------------|-------------------------------|---------|
| GET    | `/api/logs/mine`  | — (session)                   | Student's entry/exit history |
| GET    | `/api/logs`       | —                             | All logs (admin) |
| POST   | `/api/logs`       | `{rollNo, type, gate}`        | Log an `ENTRY` or `EXIT` |

> Announcements live at `/api/announcements` (`AnnouncementController`); they are a
> cross-cutting feature rather than one of the six PDF api/ modules, so they are
> documented in the project README.
