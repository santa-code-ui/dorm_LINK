package models;

import utils.JsonUtil;

/** Model for the Payment entity. */
public class Payment {

    private int paymentId;
    private int feeId;
    private String receiptNo;
    private double amount;
    private String method;
    private String txnRef;
    private String paidAt;
    private String session;

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getTxnRef() { return txnRef; }
    public void setTxnRef(String txnRef) { this.txnRef = txnRef; }
    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("paymentId", paymentId)
            .put("feeId", feeId)
            .put("receiptNo", receiptNo)
            .put("amount", amount)
            .put("method", method)
            .put("txnRef", txnRef)
            .put("paidAt", paidAt)
            .put("session", session);
    }
    @Override public String toString() { return toJson().toString(); }
}
