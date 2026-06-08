package services;

import dao.AuthDao;
import models.Admin;
import models.Student;
import utils.JsonUtil;
import utils.OtpUtil;
import utils.PasswordUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * Authentication workflows: login (admin by username / student by roll),
 * student self-registration, and the email-OTP password reset.
 */
public class AuthService {

    private final AuthDao dao = new AuthDao();

    /** Returns a session JSON object on success; throws AuthException otherwise. */
    public JsonUtil.Json login(String role, String identifier, String password)
            throws SQLException, AuthException {
        if (identifier == null || password == null || identifier.isEmpty())
            throw new AuthException("Enter your credentials.");

        if ("ADMIN".equalsIgnoreCase(role)) {
            Admin a = dao.findAdminByUsername(identifier);
            if (a == null || !PasswordUtil.verify(password, a.getPasswordHash()))
                throw new AuthException("Invalid username or password.");
            return JsonUtil.obj()
                    .put("role", "ADMIN")
                    .put("adminId", a.getAdminId())
                    .put("username", a.getUsername())
                    .put("fullName", a.getFullName())
                    .put("email", a.getEmail());
        } else {
            Student s = dao.findStudentByRoll(identifier);
            if (s == null || !PasswordUtil.verify(password, s.getPasswordHash()))
                throw new AuthException("Invalid roll number or password.");
            return JsonUtil.obj()
                    .put("role", "STUDENT")
                    .put("studentId", s.getStudentId())
                    .put("rollNo", s.getRollNo())
                    .put("fullName", s.getFullName())
                    .put("email", s.getEmail())
                    .put("department", s.getDepartment())
                    .put("room", s.getRoom())
                    .put("hostel", s.getHostel());
        }
    }

    public int register(Map<String, String> b) throws SQLException, AuthException {
        String roll = trim(b.get("rollNo"));
        String email = trim(b.get("email"));
        String pw = b.get("password");
        if (roll.isEmpty() || email.isEmpty() || pw == null || pw.length() < 6)
            throw new AuthException("Roll number, email and a 6+ character password are required.");
        if (dao.rollExists(roll))  throw new AuthException("That roll number is already registered.");
        if (dao.emailExists(email)) throw new AuthException("That email is already registered.");

        Student s = new Student();
        s.setRollNo(roll);
        s.setFullName(trim(b.get("fullName")));
        s.setEmail(email);
        s.setPhone(trim(b.get("phone")));
        s.setDob(b.get("dob"));
        s.setGender(b.getOrDefault("gender", "OTHER"));
        s.setAddress(trim(b.get("address")));
        s.setCourseId(JsonUtil.asInt(b, "courseId", 0));
        return dao.registerStudent(s, PasswordUtil.hash(pw));
    }

    /** Sends an OTP if the email belongs to an account of that role. */
    public void requestOtp(String role, String email) throws SQLException, AuthException {
        if (email == null || email.isEmpty()) throw new AuthException("Enter your registered email.");
        boolean exists = "ADMIN".equalsIgnoreCase(role)
                ? dao.findAdminByEmail(email) != null
                : dao.findStudentByEmail(email) != null;
        // For privacy we still report success; only generate when the account exists.
        if (exists) OtpUtil.generateAndSend(role, email);
    }

    public void resetPassword(String role, String email, String otp, String newPassword)
            throws SQLException, AuthException {
        if (newPassword == null || newPassword.length() < 6)
            throw new AuthException("Password must be at least 6 characters.");
        if (!OtpUtil.verify(role, email, otp))
            throw new AuthException("Invalid or expired OTP.");
        boolean ok = dao.updatePassword(role, email, PasswordUtil.hash(newPassword));
        if (!ok) throw new AuthException("Could not update password for that account.");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    /** Domain-level auth failure carrying a user-facing message. */
    public static class AuthException extends Exception {
        public AuthException(String m) { super(m); }
    }
}
