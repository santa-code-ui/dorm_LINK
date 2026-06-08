package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.FeeService;
import utils.AuthUtil;
import utils.JsonUtil;

import java.io.IOException;

/**
 * /api/fees/mine          GET signed-in student's fees
 * /api/fees/receipts      GET signed-in student's payment receipts
 * /api/fees/dues          GET outstanding dues (admin)
 * /api/fees/pay           POST {feeId, amount, method}
 * /api/fees/remind/{id}   POST send a due reminder (admin)
 */
@WebServlet(name = "FeeController", urlPatterns = {"/api/fees/*"})
public class FeeController extends BaseController {

    private final FeeService svc = new FeeService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 1 && s[0].equals("mine"))          writeData(res, svc.mine(AuthUtil.userId(req)));
            else if (s.length >= 1 && s[0].equals("receipts")) writeData(res, svc.receipts(AuthUtil.userId(req)));
            else if (s.length >= 1 && s[0].equals("dues"))     writeData(res, svc.dues());
            else writeError(res, HttpServletResponse.SC_NOT_FOUND, "Unknown resource.");
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 1 && s[0].equals("pay")) {
                writeObject(res, svc.pay(body(req)));
            } else if (s.length >= 2 && s[0].equals("remind")) {
                svc.remind(Integer.parseInt(s[1]));
                writeObject(res, JsonUtil.obj().put("message", "Reminder sent"));
            } else writeError(res, 400, "Unsupported action.");
        } catch (NumberFormatException e) { writeError(res, 400, "Invalid id."); }
        catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }
}
