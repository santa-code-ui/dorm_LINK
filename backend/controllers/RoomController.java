package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.RoomService;
import utils.AuthUtil;
import utils.JsonUtil;

import java.io.IOException;
import java.util.Map;

/**
 * /api/rooms                 GET list (?hostelId=) · POST add (admin)
 * /api/rooms/hostels         GET hostel blocks
 * /api/rooms/mess            GET weekly mess menu (?hostelId=)
 * /api/rooms/mine            GET signed-in student's room + roommates
 * /api/rooms/rate            POST submit a hostel rating
 * /api/rooms/requests        GET all (admin) · POST create (student)
 * /api/rooms/requests/{id}   PUT decide {status: APPROVED|REJECTED}
 */
@WebServlet(name = "RoomController", urlPatterns = {"/api/rooms/*"})
public class RoomController extends BaseController {

    private final RoomService svc = new RoomService();

    private int intParam(HttpServletRequest req, String k) {
        try { return Integer.parseInt(req.getParameter(k)); } catch (Exception e) { return 0; }
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length == 0)                  writeData(res, svc.listRooms(intParam(req, "hostelId")));
            else if (s[0].equals("hostels"))    writeData(res, svc.hostels());
            else if (s[0].equals("mess"))       writeObject(res, svc.mess(intParam(req, "hostelId")));
            else if (s[0].equals("mine"))       writeJson(res, svc.mine(AuthUtil.userId(req)));
            else if (s[0].equals("requests"))   writeData(res, svc.allRequests());
            else writeError(res, HttpServletResponse.SC_NOT_FOUND, "Unknown resource.");
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            Map<String, String> b = body(req);
            if (s.length >= 1 && s[0].equals("rate")) {
                svc.rate(AuthUtil.userId(req), b);
                writeObject(res, JsonUtil.obj().put("message", "Rating saved"));
            } else if (s.length >= 1 && s[0].equals("requests")) {
                int id = svc.createRequest(AuthUtil.userId(req), b);
                writeObject(res, JsonUtil.obj().put("requestId", id).put("message", "Request sent"));
            } else {
                int id = svc.addRoom(b);
                writeObject(res, JsonUtil.obj().put("roomId", id).put("message", "Room added"));
            }
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 2 && s[0].equals("requests")) {
                Map<String, String> b = body(req);
                svc.decideRequest(Integer.parseInt(s[1]), b.get("status"), AuthUtil.userId(req));
                writeObject(res, JsonUtil.obj().put("message", "Decision recorded"));
            } else writeError(res, 400, "Unsupported update.");
        } catch (NumberFormatException e) { writeError(res, 400, "Invalid id."); }
        catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }
}
