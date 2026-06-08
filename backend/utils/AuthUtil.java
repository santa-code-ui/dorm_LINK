package utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Reads the lightweight session headers the frontend attaches to every API call
 * (see frontend/js/dashboard.js apiFetch): X-Session-Role and X-Session-Id.
 *
 * This is deliberately simple for a campus project. To harden it, issue a signed
 * token (JWT) at login and validate it here instead of trusting the headers.
 */
public final class AuthUtil {

    private AuthUtil() { }

    public static String role(HttpServletRequest req) {
        String r = req.getHeader("X-Session-Role");
        return r == null ? "" : r.toUpperCase();
    }

    public static int userId(HttpServletRequest req) {
        try { return Integer.parseInt(req.getHeader("X-Session-Id")); }
        catch (Exception e) { return 0; }
    }

    public static boolean isAdmin(HttpServletRequest req)   { return "ADMIN".equals(role(req)); }
    public static boolean isStudent(HttpServletRequest req) { return "STUDENT".equals(role(req)); }
}
