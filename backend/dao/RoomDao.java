package dao;

import models.Room;
import models.Student;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDao {

    private static final String BASE =
        "SELECT r.room_id, r.hostel_id, r.room_no, r.floor, r.room_type, r.capacity, " +
        "       r.occupied, r.rent_amount, r.status, r.photo, h.hostel_name, h.block " +
        "FROM room r JOIN hostel h ON r.hostel_id = h.hostel_id ";

    public List<Room> listAll() throws SQLException {
        return query(BASE + "ORDER BY h.block, r.room_no", 0);
    }

    public List<Room> listByHostel(int hostelId) throws SQLException {
        return query(BASE + "WHERE r.hostel_id = ? ORDER BY r.room_no", hostelId);
    }

    private List<Room> query(String sql, int hostelId) throws SQLException {
        List<Room> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (hostelId > 0) ps.setInt(1, hostelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public int insert(Room r) throws SQLException {
        String sql = "INSERT INTO room (hostel_id, room_no, floor, room_type, capacity, occupied, rent_amount, status) " +
                     "VALUES (?,?,?,?,?,0,?, 'AVAILABLE')";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getHostelId() > 0 ? r.getHostelId() : 1);
            ps.setString(2, r.getRoomNo());
            ps.setInt(3, r.getFloor());
            ps.setString(4, r.getRoomType() == null ? "DOUBLE" : r.getRoomType());
            ps.setInt(5, r.getCapacity() > 0 ? r.getCapacity() : 2);
            ps.setDouble(6, r.getRentAmount());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public Room findById(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE r.room_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    /** Roommates = other active students sharing the same room. */
    public List<Student> roommates(int roomId, int excludeStudentId) throws SQLException {
        String sql = "SELECT student_id, roll_no, full_name FROM student WHERE room_id = ? AND student_id <> ?";
        List<Student> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, excludeStudentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setRollNo(rs.getString("roll_no"));
                    s.setFullName(rs.getString("full_name"));
                    out.add(s);
                }
            }
        }
        return out;
    }

    /** Allocates a student to a room and bumps occupancy in one transaction. */
    public void allocate(int studentId, int roomId) throws SQLException {
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try (PreparedStatement a = c.prepareStatement("UPDATE student SET room_id = ? WHERE student_id = ?");
                 PreparedStatement b = c.prepareStatement("UPDATE room SET occupied = occupied + 1 WHERE room_id = ?")) {
                a.setInt(1, roomId); a.setInt(2, studentId); a.executeUpdate();
                b.setInt(1, roomId); b.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback(); throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    /** The room a student is currently allocated to (or null if none). */
    public Room findByStudent(int studentId) throws SQLException {
        String sql = BASE + "WHERE r.room_id = (SELECT room_id FROM student WHERE student_id = ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    private Room map(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setHostelId(rs.getInt("hostel_id"));
        r.setRoomNo(rs.getString("room_no"));
        r.setFloor(rs.getInt("floor"));
        r.setRoomType(rs.getString("room_type"));
        r.setCapacity(rs.getInt("capacity"));
        r.setOccupied(rs.getInt("occupied"));
        r.setRentAmount(rs.getDouble("rent_amount"));
        r.setStatus(rs.getString("status"));
        r.setPhoto(rs.getString("photo"));
        r.setHostelName(rs.getString("hostel_name"));
        r.setBlock(rs.getString("block"));
        return r;
    }
}
