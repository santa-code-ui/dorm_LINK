package services;

import dao.AnnouncementDao;
import models.Announcement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Posting and listing hostel / campus / university announcements. */
public class AnnouncementService {

    private final AnnouncementDao dao = new AnnouncementDao();

    public List<Announcement> listActive() throws SQLException { return dao.listActive(); }
    public List<Announcement> listAll() throws SQLException { return dao.listAll(); }

    public int post(int adminId, Map<String, String> b) throws SQLException {
        String title = trim(b.get("title"));
        String body  = trim(b.get("body"));
        String cat   = b.getOrDefault("category", "GENERAL").toUpperCase();
        if (title.isEmpty() || body.isEmpty())
            throw new IllegalArgumentException("Title and message are required.");
        return dao.insert(adminId, cat, title, body);
    }

    public boolean toggle(int id) throws SQLException { return dao.toggle(id); }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
