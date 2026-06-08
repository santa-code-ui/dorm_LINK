package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ComplaintService;
import utils.AuthUtil;
import utils.JsonUtil;

import java.io.IOException;

/**
 * /api/complaints        GET all (admin) · POST file (student)
 * /api/complaints/mine   GET signed-in student's complaints
 */
@WebServlet(name = "ComplaintController", urlPatterns = {"/api/complaints/*"})
public class ComplaintController extends BaseController {

    private final ComplaintService svc = new ComplaintService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 1 && s[0].equals("mine")) writeData(res, svc.listMine(AuthUtil.userId(req)));
            else writeData(res, svc.listAll());
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int id = svc.create(AuthUtil.userId(req), body(req));
            writeObject(res, JsonUtil.obj().put("complaintId", id).put("message", "Complaint submitted"));
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }
}
