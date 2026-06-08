package dao;

import models.Fee;
import models.Payment;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for hostel/mess fees and payments.
 * applyPayment() runs the payment + fee-status update in a single transaction
 * and returns the generated receipt number.
 */
public class FeeDao {

    public List<Fee> listByStudent(int studentId) throws SQLException {
        String sql = "SELECT fee_id, student_id, session, hostel_fee, mess_fee, total_due, " +
                     "amount_paid, status, due_date FROM fee WHERE student_id = ? ORDER BY session DESC";
        List<Fee> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs, false)); }
        }
        return out;
    }

    /** Admin fee-clearance list: everyone with an outstanding balance. */
    public List<Fee> listDues() throws SQLException {
        String sql = "SELECT f.fee_id, f.student_id, f.session, f.hostel_fee, f.mess_fee, f.total_due, " +
                     "f.amount_paid, f.status, f.due_date, s.full_name AS student_name, s.roll_no " +
                     "FROM fee f JOIN student s ON f.student_id = s.student_id " +
                     "WHERE f.status <> 'PAID' ORDER BY (f.total_due - f.amount_paid) DESC";
        List<Fee> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs, true));
        }
        return out;
    }

    public List<Payment> receiptsByStudent(int studentId) throws SQLException {
        String sql = "SELECT p.payment_id, p.fee_id, p.receipt_no, p.amount, p.method, p.txn_ref, " +
                     "p.paid_at, f.session FROM payment p JOIN fee f ON p.fee_id = f.fee_id " +
                     "WHERE f.student_id = ? ORDER BY p.paid_at DESC";
        List<Payment> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setFeeId(rs.getInt("fee_id"));
                    p.setReceiptNo(rs.getString("receipt_no"));
                    p.setAmount(rs.getDouble("amount"));
                    p.setMethod(rs.getString("method"));
                    p.setTxnRef(rs.getString("txn_ref"));
                    p.setPaidAt(String.valueOf(rs.getTimestamp("paid_at")));
                    p.setSession(rs.getString("session"));
                    out.add(p);
                }
            }
        }
        return out;
    }

    /** Records a payment against a fee and recomputes its status. Returns the receipt no. */
    public String applyPayment(int feeId, double amount, String method, String txnRef) throws SQLException {
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try {
                // 1. read current fee + lock the row
                double totalDue, amountPaid; String session;
                try (PreparedStatement q = c.prepareStatement(
                        "SELECT total_due, amount_paid, session FROM fee WHERE fee_id = ? FOR UPDATE")) {
                    q.setInt(1, feeId);
                    try (ResultSet rs = q.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Fee record not found.");
                        totalDue = rs.getDouble("total_due");
                        amountPaid = rs.getDouble("amount_paid");
                        session = rs.getString("session");
                    }
                }
                // 2. generate a sequential receipt number for the session
                String receiptNo = nextReceiptNo(c, session);

                // 3. insert payment
                try (PreparedStatement ins = c.prepareStatement(
                        "INSERT INTO payment (fee_id, receipt_no, amount, method, txn_ref) VALUES (?,?,?,?,?)")) {
                    ins.setInt(1, feeId);
                    ins.setString(2, receiptNo);
                    ins.setDouble(3, amount);
                    ins.setString(4, method == null ? "UPI" : method);
                    ins.setString(5, txnRef);
                    ins.executeUpdate();
                }
                // 4. update fee balance + status
                double newPaid = amountPaid + amount;
                String status = newPaid >= totalDue ? "PAID" : (newPaid > 0 ? "PARTIAL" : "UNPAID");
                try (PreparedStatement up = c.prepareStatement(
                        "UPDATE fee SET amount_paid = ?, status = ? WHERE fee_id = ?")) {
                    up.setDouble(1, newPaid);
                    up.setString(2, status);
                    up.setInt(3, feeId);
                    up.executeUpdate();
                }
                c.commit();
                return receiptNo;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private String nextReceiptNo(Connection c, String session) throws SQLException {
        String year = (session != null && session.length() >= 4) ? session.substring(0, 4) : "0000";
        int n;
        try (PreparedStatement q = c.prepareStatement(
                "SELECT COUNT(*) FROM payment p JOIN fee f ON p.fee_id = f.fee_id WHERE f.session = ?")) {
            q.setString(1, session);
            try (ResultSet rs = q.executeQuery()) { rs.next(); n = rs.getInt(1) + 1; }
        }
        return String.format("RCPT-%s-%04d", year, n);
    }

    private Fee map(ResultSet rs, boolean withStudent) throws SQLException {
        Fee f = new Fee();
        f.setFeeId(rs.getInt("fee_id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setSession(rs.getString("session"));
        f.setHostelFee(rs.getDouble("hostel_fee"));
        f.setMessFee(rs.getDouble("mess_fee"));
        f.setTotalDue(rs.getDouble("total_due"));
        f.setAmountPaid(rs.getDouble("amount_paid"));
        f.setBalance(rs.getDouble("total_due") - rs.getDouble("amount_paid"));
        f.setStatus(rs.getString("status"));
        f.setDueDate(String.valueOf(rs.getDate("due_date")));
        if (withStudent) {
            f.setStudentName(rs.getString("student_name"));
            f.setRollNo(rs.getString("roll_no"));
        }
        return f;
    }
}
