package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AnnouncementService;
import utils.AuthUtil;
import utils.JsonUtil;

import java.io.IOException;

/**
 * /api/announcements              GET active (?all=1 for every row) · POST create (admin)
 * /api/announcements/{id}/toggle  PUT show/hide
 */
@WebServlet(name = "AnnouncementController", urlPatterns = {"/api/announcements/*"})
public class AnnouncementController extends BaseController {

    private final AnnouncementService svc = new AnnouncementService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            boolean all = "1".equals(req.getParameter("all"));
            writeData(res, all ? svc.listAll() : svc.listActive());
        } catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int id = svc.post(AuthUtil.userId(req), body(req));
            writeObject(res, JsonUtil.obj().put("announcementId", id).put("message", "Announcement posted"));
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 2 && s[1].equals("toggle")) {
                svc.toggle(Integer.parseInt(s[0]));
                writeObject(res, JsonUtil.obj().put("message", "Visibility updated"));
            } else writeError(res, 400, "Unsupported update.");
        } catch (NumberFormatException e) { writeError(res, 400, "Invalid id."); }
        catch (Exception e) { serverError(res, e); }
    }
}
