package dao;

import utils.DBConnection;

import java.sql.*;

/**
 * Ratings: one row per (student, hostel). Inserting/updating fires the
 * trg_rating_after_* triggers which recompute hostel.avg_rating in real time,
 * so the Book Hostel cards reflect the new average immediately.
 */
public class RatingDao {

    public void upsert(int studentId, int hostelId, int stars, String review) throws SQLException {
        String sql = "INSERT INTO rating (student_id, hostel_id, stars, review) VALUES (?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE stars = VALUES(stars), review = VALUES(review)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, hostelId);
            ps.setInt(3, stars);
            ps.setString(4, review);
            ps.executeUpdate();
        }
    }
}
