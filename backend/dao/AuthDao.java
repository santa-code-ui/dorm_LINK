package dao;

import models.Admin;
import models.Student;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data access for authentication: admin/student lookup, student registration,
 * and password updates used by the email-OTP reset flow.
 */
public class AuthDao {

    public Admin findAdminByUsername(String username) throws SQLException {
        String sql = "SELECT admin_id, username, full_name, email, phone, role, password_hash " +
                     "FROM admin WHERE username = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAdmin(rs) : null;
            }
        }
    }

    public Admin findAdminByEmail(String email) throws SQLException {
        String sql = "SELECT admin_id, username, full_name, email, phone, role, password_hash " +
                     "FROM admin WHERE email = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAdmin(rs) : null;
            }
        }
    }

    public Student findStudentByRoll(String rollNo) throws SQLException {
        return findStudent("s.roll_no = ?", rollNo);
    }

    public Student findStudentByEmail(String email) throws SQLException {
        return findStudent("s.email = ?", email);
    }

    private Student findStudent(String where, String value) throws SQLException {
        String sql =
            "SELECT s.student_id, s.roll_no, s.full_name, s.email, s.phone, s.dob, s.gender, " +
            "       s.address, s.course_id, s.room_id, s.status, s.password_hash, " +
            "       c.course_name, c.department, r.room_no, h.hostel_name, h.block " +
            "FROM student s " +
            "LEFT JOIN course c ON s.course_id = c.course_id " +
            "LEFT JOIN room   r ON s.room_id   = r.room_id " +
            "LEFT JOIN hostel h ON r.hostel_id = h.hostel_id " +
            "WHERE " + where;
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE email = ? UNION SELECT 1 FROM admin WHERE email = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean rollExists(String rollNo) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE roll_no = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public int registerStudent(Student s, String passwordHash) throws SQLException {
        String sql = "INSERT INTO student (roll_no, full_name, email, phone, dob, gender, " +
                     "address, course_id, password_hash, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?, 'ACTIVE')";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            if (s.getDob() != null && !s.getDob().isEmpty()) ps.setString(5, s.getDob());
            else ps.setNull(5, java.sql.Types.DATE);
            ps.setString(6, s.getGender());
            ps.setString(7, s.getAddress());
            if (s.getCourseId() > 0) ps.setInt(8, s.getCourseId());
            else ps.setNull(8, java.sql.Types.INTEGER);
            ps.setString(9, passwordHash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean updatePassword(String role, String email, String passwordHash) throws SQLException {
        String table = "ADMIN".equalsIgnoreCase(role) ? "admin" : "student";
        String sql = "UPDATE " + table + " SET password_hash = ? WHERE email = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    /* ---- row mappers ---- */
    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminId(rs.getInt("admin_id"));
        a.setUsername(rs.getString("username"));
        a.setFullName(rs.getString("full_name"));
        a.setEmail(rs.getString("email"));
        a.setPhone(rs.getString("phone"));
        a.setRole(rs.getString("role"));
        a.setPasswordHash(rs.getString("password_hash"));
        return a;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setDob(rs.getString("dob"));
        s.setGender(rs.getString("gender"));
        s.setAddress(rs.getString("address"));
        s.setCourseId(rs.getInt("course_id"));
        s.setRoomId(rs.getInt("room_id"));
        s.setStatus(rs.getString("status"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setCourseName(rs.getString("course_name"));
        s.setDepartment(rs.getString("department"));
        String roomNo = rs.getString("room_no");
        s.setRoom(roomNo != null ? roomNo : "—");
        String hostel = rs.getString("hostel_name");
        String block = rs.getString("block");
        s.setHostel(hostel != null ? hostel + " (" + block + ")" : "—");
        return s;
    }
}
