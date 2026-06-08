package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.LogService;
import utils.AuthUtil;
import utils.JsonUtil;

import java.io.IOException;

/**
 * /api/logs        GET all (admin) · POST log {rollNo, type, gate}
 * /api/logs/mine   GET signed-in student's entry/exit history
 */
@WebServlet(name = "LogController", urlPatterns = {"/api/logs/*"})
public class LogController extends BaseController {

    private final LogService svc = new LogService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 1 && s[0].equals("mine")) writeData(res, svc.mine(AuthUtil.userId(req)));
            else writeData(res, svc.all());
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int id = svc.log(body(req));
            writeObject(res, JsonUtil.obj().put("logId", id).put("message", "Logged"));
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }
}
