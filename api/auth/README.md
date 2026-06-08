# /api/auth  —  Authentication

Implemented by `backend/controllers/AuthController.java` → `AuthService` → `AuthDao`.

| Method | Path                | Body                                                              | Purpose |
|--------|---------------------|-------------------------------------------------------------------|---------|
| POST   | `/api/auth/login`   | `{role, identifier, password}`                                    | Admin (username) / Student (roll no.) login |
| POST   | `/api/auth/register`| `{rollNo, fullName, email, phone, dob, gender, address, courseId, password}` | Student self sign-up |
| POST   | `/api/auth/forgot`  | `{role, email}`                                                   | Generate + send a 6-digit OTP |
| POST   | `/api/auth/reset`   | `{role, email, otp, newPassword}`                                 | Verify OTP and set a new password |

Passwords are stored as `salt:sha256(salt+password)` (see `utils/PasswordUtil`).
OTPs live in `utils/OtpUtil` (10-minute TTL); wire SMTP where indicated to deliver them.
