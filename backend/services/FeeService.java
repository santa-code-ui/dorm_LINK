package services;

import dao.FeeDao;
import models.Fee;
import models.Payment;
import utils.JsonUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Hostel/mess fee viewing, payment and admin clearance. */
public class FeeService {

    private final FeeDao dao = new FeeDao();

    public List<Fee> mine(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        return dao.listByStudent(studentId);
    }

    public List<Fee> dues() throws SQLException { return dao.listDues(); }

    public List<Payment> receipts(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        return dao.receiptsByStudent(studentId);
    }

    /** Records a payment; returns {"receiptNo":..,"message":..}. */
    public JsonUtil.Json pay(Map<String, String> b) throws SQLException {
        int feeId = JsonUtil.asInt(b, "feeId", 0);
        double amount = JsonUtil.asDouble(b, "amount", 0);
        String method = b.getOrDefault("method", "UPI");
        if (feeId <= 0) throw new IllegalArgumentException("Invalid fee reference.");
        if (amount <= 0) throw new IllegalArgumentException("Enter a valid amount.");
        String txnRef = "TXN" + System.currentTimeMillis();
        String receiptNo = dao.applyPayment(feeId, amount, method, txnRef);
        return JsonUtil.obj().put("receiptNo", receiptNo).put("message", "Payment successful");
    }

    /** Fee reminder — logs to the server; wire SMTP/notification here for production. */
    public void remind(int studentId) {
        System.out.println("[fee-reminder] Reminder dispatched to student #" + studentId);
    }
}
