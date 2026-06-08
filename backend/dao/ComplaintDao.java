package dao;

import models.Complaint;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data access for student complaints (Maintenance/Food/Electricity/Water/Others). */
public class ComplaintDao {

    private static final String BASE =
        "SELECT cp.complaint_id, cp.student_id, cp.type, cp.subject, cp.description, cp.status, " +
        "       cp.created_at, s.full_name AS student_name, s.roll_no " +
        "FROM complaint cp JOIN student s ON cp.student_id = s.student_id ";

    public List<Complaint> listByStudent(int studentId) throws SQLException {
        return query(BASE + "WHERE cp.student_id = ? ORDER BY cp.created_at DESC", studentId);
    }

    public List<Complaint> listAll() throws SQLException {
        return query(BASE + "ORDER BY cp.created_at DESC", 0);
    }

    private List<Complaint> query(String sql, int studentId) throws SQLException {
        List<Complaint> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (studentId > 0) ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public int insert(int studentId, String type, String subject, String description) throws SQLException {
        String sql = "INSERT INTO complaint (student_id, type, subject, description) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setString(2, type);
            ps.setString(3, subject);
            ps.setString(4, description);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public boolean updateStatus(int complaintId, String status) throws SQLException {
        String sql = "UPDATE complaint SET status = ?, " +
                     "resolved_at = IF(? IN ('RESOLVED','CLOSED'), NOW(), resolved_at) WHERE complaint_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, complaintId);
            return ps.executeUpdate() > 0;
        }
    }

    private Complaint map(ResultSet rs) throws SQLException {
        Complaint o = new Complaint();
        o.setComplaintId(rs.getInt("complaint_id"));
        o.setStudentId(rs.getInt("student_id"));
        o.setType(rs.getString("type"));
        o.setSubject(rs.getString("subject"));
        o.setDescription(rs.getString("description"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
        o.setStudentName(rs.getString("student_name"));
        o.setRollNo(rs.getString("roll_no"));
        return o;
    }
}
