package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AuthService;

import java.io.IOException;
import java.util.Map;

/**
 * REST endpoints:
 *   POST /api/auth/login    {role, identifier, password}
 *   POST /api/auth/register {rollNo, fullName, email, phone, dob, gender, address, courseId, password}
 *   POST /api/auth/forgot   {role, email}
 *   POST /api/auth/reset    {role, email, otp, newPassword}
 */
@WebServlet(name = "AuthController", urlPatterns = {"/api/auth/*"})
public class AuthController extends BaseController {

    private final AuthService auth = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String action = lastSegment(req);
        try {
            Map<String, String> b = body(req);
            switch (action) {
                case "login":
                    writeObject(res, auth.login(b.get("role"), b.get("identifier"), b.get("password")));
                    break;
                case "register":
                    int id = auth.register(b);
                    writeObject(res, utils.JsonUtil.obj().put("studentId", id).put("message", "Registered"));
                    break;
                case "forgot":
                    auth.requestOtp(b.get("role"), b.get("email"));
                    writeObject(res, utils.JsonUtil.obj().put("message", "If the account exists, an OTP has been sent."));
                    break;
                case "reset":
                    auth.resetPassword(b.get("role"), b.get("email"), b.get("otp"), b.get("newPassword"));
                    writeObject(res, utils.JsonUtil.obj().put("message", "Password updated"));
                    break;
                default:
                    writeError(res, HttpServletResponse.SC_NOT_FOUND, "Unknown auth action.");
            }
        } catch (AuthService.AuthException e) {
            writeError(res, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            serverError(res, e);
        }
    }
}
