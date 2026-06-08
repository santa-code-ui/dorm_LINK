package services;

import dao.ComplaintDao;
import models.Complaint;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Student complaint submission and tracking. */
public class ComplaintService {

    private static final List<String> TYPES =
        Arrays.asList("MAINTENANCE", "FOOD", "ELECTRICITY", "WATER", "OTHERS");

    private final ComplaintDao dao = new ComplaintDao();

    public List<Complaint> listMine(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        return dao.listByStudent(studentId);
    }

    public List<Complaint> listAll() throws SQLException { return dao.listAll(); }

    public int create(int studentId, Map<String, String> b) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        String type = b.getOrDefault("type", "OTHERS").toUpperCase();
        if (!TYPES.contains(type)) type = "OTHERS";
        String subject = trim(b.get("subject"));
        String desc = trim(b.get("description"));
        if (subject.isEmpty() || desc.isEmpty())
            throw new IllegalArgumentException("Subject and description are required.");
        return dao.insert(studentId, type, subject, desc);
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        return dao.updateStatus(id, status);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
