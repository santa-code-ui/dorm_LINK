package dao;

import models.RoomRequest;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data access for student room requests and the admin approval workflow. */
public class RoomRequestDao {

    private static final String BASE =
        "SELECT rr.request_id, rr.student_id, rr.room_id, rr.status, rr.note, rr.requested_at, " +
        "       s.full_name AS student_name, s.roll_no, r.room_no, h.hostel_name " +
        "FROM room_request rr " +
        "JOIN student s ON rr.student_id = s.student_id " +
        "JOIN room    r ON rr.room_id    = r.room_id " +
        "JOIN hostel  h ON r.hostel_id   = h.hostel_id ";

    public List<RoomRequest> listAll() throws SQLException {
        List<RoomRequest> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(BASE + "ORDER BY rr.requested_at DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public List<RoomRequest> listByStudent(int studentId) throws SQLException {
        List<RoomRequest> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE rr.student_id = ? ORDER BY rr.requested_at DESC")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public int insert(int studentId, int roomId, String note) throws SQLException {
        String sql = "INSERT INTO room_request (student_id, room_id, note) VALUES (?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, roomId);
            ps.setString(3, note);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    /** Returns {studentId, roomId} for a request, or null if not found. */
    public int[] idsFor(int requestId) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT student_id, room_id FROM room_request WHERE request_id = ?")) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new int[]{ rs.getInt(1), rs.getInt(2) } : null;
            }
        }
    }

    public boolean decide(int requestId, String status, int adminId) throws SQLException {
        String sql = "UPDATE room_request SET status = ?, decided_at = NOW(), decided_by = ? WHERE request_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            if (adminId > 0) ps.setInt(2, adminId); else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    private RoomRequest map(ResultSet rs) throws SQLException {
        RoomRequest o = new RoomRequest();
        o.setRequestId(rs.getInt("request_id"));
        o.setStudentId(rs.getInt("student_id"));
        o.setRoomId(rs.getInt("room_id"));
        o.setStatus(rs.getString("status"));
        o.setNote(rs.getString("note"));
        o.setRequestedAt(String.valueOf(rs.getTimestamp("requested_at")));
        o.setStudentName(rs.getString("student_name"));
        o.setRollNo(rs.getString("roll_no"));
        o.setRoomNo(rs.getString("room_no"));
        o.setHostelName(rs.getString("hostel_name"));
        return o;
    }
}
