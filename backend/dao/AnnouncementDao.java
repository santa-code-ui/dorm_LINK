package dao;

import models.Announcement;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data access for hostel / campus / university announcements. */
public class AnnouncementDao {

    private static final String BASE =
        "SELECT announcement_id, admin_id, title, body, category, is_active, created_at FROM announcement ";

    public List<Announcement> listActive() throws SQLException {
        return query(BASE + "WHERE is_active = TRUE ORDER BY created_at DESC");
    }

    public List<Announcement> listAll() throws SQLException {
        return query(BASE + "ORDER BY created_at DESC");
    }

    private List<Announcement> query(String sql) throws SQLException {
        List<Announcement> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public int insert(int adminId, String category, String title, String body) throws SQLException {
        String sql = "INSERT INTO announcement (admin_id, category, title, body) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (adminId > 0) ps.setInt(1, adminId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, category);
            ps.setString(3, title);
            ps.setString(4, body);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public boolean toggle(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE announcement SET is_active = NOT is_active WHERE announcement_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Announcement map(ResultSet rs) throws SQLException {
        Announcement o = new Announcement();
        o.setAnnouncementId(rs.getInt("announcement_id"));
        o.setAdminId(rs.getInt("admin_id"));
        o.setTitle(rs.getString("title"));
        o.setBody(rs.getString("body"));
        o.setCategory(rs.getString("category"));
        o.setActive(rs.getBoolean("is_active"));
        o.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
        return o;
    }
}
