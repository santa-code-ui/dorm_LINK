package dao;

import models.Course;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public List<Course> listAll() throws SQLException {
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.department, c.duration_years, " +
                     "(SELECT COUNT(*) FROM student s WHERE s.course_id = c.course_id) AS students " +
                     "FROM course c ORDER BY c.course_code";
        List<Course> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course o = new Course();
                o.setCourseId(rs.getInt("course_id"));
                o.setCourseCode(rs.getString("course_code"));
                o.setCourseName(rs.getString("course_name"));
                o.setDepartment(rs.getString("department"));
                o.setDurationYears(rs.getInt("duration_years"));
                o.setStudents(rs.getInt("students"));
                out.add(o);
            }
        }
        return out;
    }

    public int insert(Course o) throws SQLException {
        String sql = "INSERT INTO course (course_code, course_name, department, duration_years) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getCourseCode());
            ps.setString(2, o.getCourseName());
            ps.setString(3, o.getDepartment());
            ps.setInt(4, o.getDurationYears() > 0 ? o.getDurationYears() : 4);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM course WHERE course_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
