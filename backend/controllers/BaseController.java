package controllers;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Base servlet with JSON/IO/CORS helpers shared by every controller.
 * All API responses are JSON; rows are wrapped as {"data":[...]} to match the
 * frontend's DL.get() unwrapping.
 */
public abstract class BaseController extends HttpServlet {

    /* ---- CORS (handy when the static frontend is served from a different origin) ---- */
    protected void cors(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Session-Role,X-Session-Id");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        cors(res);
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /* ---- request body ---- */
    protected Map<String, String> body(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return JsonUtil.parse(sb.toString());
    }

    /** Returns the trailing path segment, e.g. /rooms/requests/5 -> "5". */
    protected String lastSegment(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) return "";
        String[] parts = info.split("/");
        return parts.length == 0 ? "" : parts[parts.length - 1];
    }

    protected String[] segments(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null) return new String[0];
        String trimmed = info.startsWith("/") ? info.substring(1) : info;
        return trimmed.isEmpty() ? new String[0] : trimmed.split("/");
    }

    /* ---- responses ---- */
    protected void writeJson(HttpServletResponse res, String json) throws IOException {
        cors(res);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(json);
    }

    protected void writeData(HttpServletResponse res, List<?> rows) throws IOException {
        writeJson(res, JsonUtil.data(rows));
    }

    protected void writeObject(HttpServletResponse res, JsonUtil.Json obj) throws IOException {
        writeJson(res, obj.toString());
    }

    protected void writeError(HttpServletResponse res, int status, String msg) throws IOException {
        cors(res);
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(JsonUtil.message(msg));
    }

    protected void serverError(HttpServletResponse res, Exception e) throws IOException {
        e.printStackTrace();
        writeError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Server error: " + e.getMessage());
    }
}
