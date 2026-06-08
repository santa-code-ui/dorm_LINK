package dao;

import models.Student;
import utils.DBConnection;
import utils.JsonUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    public List<Student> listAll() throws SQLException {
        String sql =
            "SELECT s.student_id, s.roll_no, s.full_name, s.email, s.phone, s.gender, s.status, " +
            "       c.course_name, c.department, r.room_no, h.hostel_name, h.block " +
            "FROM student s " +
            "LEFT JOIN course c ON s.course_id = c.course_id " +
            "LEFT JOIN room   r ON s.room_id   = r.room_id " +
            "LEFT JOIN hostel h ON r.hostel_id = h.hostel_id " +
            "ORDER BY s.roll_no";
        List<Student> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("student_id"));
                s.setRollNo(rs.getString("roll_no"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setGender(rs.getString("gender"));
                s.setStatus(rs.getString("status"));
                s.setCourseName(rs.getString("course_name"));
                s.setDepartment(rs.getString("department"));
                String room = rs.getString("room_no");
                s.setRoom(room != null ? room : "—");
                String h = rs.getString("hostel_name");
                s.setHostel(h != null ? h + " (" + rs.getString("block") + ")" : "—");
                out.add(s);
            }
        }
        return out;
    }

    public int findIdByRoll(String roll) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT student_id FROM student WHERE roll_no = ?")) {
            ps.setString(1, roll);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    /** Admin-created student. Caller supplies an already-hashed password. */
    public int insert(Student s, String passwordHash) throws SQLException {
        String sql = "INSERT INTO student (roll_no, full_name, email, phone, gender, course_id, " +
                     "password_hash, status) VALUES (?,?,?,?,?,?,?, 'ACTIVE')";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getGender() == null ? "OTHER" : s.getGender());
            if (s.getCourseId() > 0) ps.setInt(6, s.getCourseId());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, passwordHash);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM student WHERE student_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Aggregated figures for the admin dashboard cards. */
    public JsonUtil.Json adminStats() throws SQLException {
        int students = count("SELECT COUNT(*) FROM student");
        int rooms = count("SELECT COUNT(*) FROM room");
        int pending = count("SELECT COUNT(*) FROM room_request WHERE status='PENDING'");
        int complaints = count("SELECT COUNT(*) FROM complaint WHERE status IN ('OPEN','IN_PROGRESS')");
        double feesDue = sum("SELECT COALESCE(SUM(total_due - amount_paid),0) FROM fee WHERE status <> 'PAID'");
        int totalBeds = count("SELECT COALESCE(SUM(capacity),0) FROM room");
        int occupiedBeds = count("SELECT COALESCE(SUM(occupied),0) FROM room");
        int occupancy = totalBeds == 0 ? 0 : (int) Math.round(occupiedBeds * 100.0 / totalBeds);
        return JsonUtil.obj()
                .put("students", students)
                .put("rooms", rooms)
                .put("pendingRequests", pending)
                .put("openComplaints", complaints)
                .put("feesDue", feesDue)
                .put("occupancy", occupancy);
    }

    private int count(String sql) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    private double sum(String sql) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }
}
