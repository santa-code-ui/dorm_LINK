package dao;

import models.MessMenu;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Weekly mess menu. Defaults to the first hostel's menu when none specified. */
public class MessMenuDao {

    public List<MessMenu> listByHostel(int hostelId) throws SQLException {
        String sql = "SELECT menu_id, hostel_id, day_of_week, breakfast, lunch, snacks, dinner " +
                     "FROM mess_menu " + (hostelId > 0 ? "WHERE hostel_id = ? " : "") +
                     "ORDER BY FIELD(day_of_week,'MON','TUE','WED','THU','FRI','SAT','SUN')";
        List<MessMenu> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (hostelId > 0) ps.setInt(1, hostelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MessMenu m = new MessMenu();
                    m.setMenuId(rs.getInt("menu_id"));
                    m.setHostelId(rs.getInt("hostel_id"));
                    m.setDayOfWeek(rs.getString("day_of_week"));
                    m.setBreakfast(rs.getString("breakfast"));
                    m.setLunch(rs.getString("lunch"));
                    m.setSnacks(rs.getString("snacks"));
                    m.setDinner(rs.getString("dinner"));
                    out.add(m);
                }
            }
        }
        return out;
    }
}
