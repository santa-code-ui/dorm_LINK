package dao;

import models.EntryExitLog;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data access for QR-based entry/exit logging. */
public class LogDao {

    private static final String BASE =
        "SELECT l.log_id, l.student_id, l.log_type, l.gate, l.logged_at, " +
        "       s.full_name AS student_name, s.roll_no " +
        "FROM entry_exit_log l JOIN student s ON l.student_id = s.student_id ";

    public List<EntryExitLog> listByStudent(int studentId) throws SQLException {
        return query(BASE + "WHERE l.student_id = ? ORDER BY l.logged_at DESC", studentId);
    }

    public List<EntryExitLog> listAll() throws SQLException {
        return query(BASE + "ORDER BY l.logged_at DESC LIMIT 200", 0);
    }

    private List<EntryExitLog> query(String sql, int studentId) throws SQLException {
        List<EntryExitLog> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (studentId > 0) ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public int insert(int studentId, String type, String gate) throws SQLException {
        String sql = "INSERT INTO entry_exit_log (student_id, log_type, gate) VALUES (?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setString(2, type);
            ps.setString(3, gate);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    private EntryExitLog map(ResultSet rs) throws SQLException {
        EntryExitLog o = new EntryExitLog();
        o.setLogId(rs.getInt("log_id"));
        o.setStudentId(rs.getInt("student_id"));
        o.setType(rs.getString("log_type"));
        o.setGate(rs.getString("gate"));
        o.setAt(String.valueOf(rs.getTimestamp("logged_at")));
        o.setStudentName(rs.getString("student_name"));
        o.setRollNo(rs.getString("roll_no"));
        return o;
    }
}
