package dao;

import models.Hostel;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HostelDao {

    public List<Hostel> listAll() throws SQLException {
        String sql =
            "SELECT h.hostel_id, h.hostel_name, h.block, h.gender, h.description, " +
            "       h.total_rooms, h.cover_image, h.avg_rating, " +
            "       COALESCE(SUM(GREATEST(r.capacity - r.occupied,0)),0) AS available " +
            "FROM hostel h LEFT JOIN room r ON r.hostel_id = h.hostel_id " +
            "GROUP BY h.hostel_id ORDER BY h.block";
        List<Hostel> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Hostel h = new Hostel();
                h.setHostelId(rs.getInt("hostel_id"));
                h.setHostelName(rs.getString("hostel_name"));
                h.setBlock(rs.getString("block"));
                h.setGender(rs.getString("gender"));
                h.setDescription(rs.getString("description"));
                h.setTotalRooms(rs.getInt("total_rooms"));
                h.setCoverImage(rs.getString("cover_image"));
                h.setAvgRating(rs.getDouble("avg_rating"));
                h.setAvailable(rs.getInt("available"));
                out.add(h);
            }
        }
        return out;
    }
}
