package models;

import utils.JsonUtil;

/** Model for the Fee entity. */
public class Fee {

    private int feeId;
    private int studentId;
    private String session;
    private double hostelFee;
    private double messFee;
    private double totalDue;
    private double amountPaid;
    private double balance;
    private String status;
    private String dueDate;
    private String studentName;
    private String rollNo;

    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }
    public double getHostelFee() { return hostelFee; }
    public void setHostelFee(double hostelFee) { this.hostelFee = hostelFee; }
    public double getMessFee() { return messFee; }
    public void setMessFee(double messFee) { this.messFee = messFee; }
    public double getTotalDue() { return totalDue; }
    public void setTotalDue(double totalDue) { this.totalDue = totalDue; }
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("feeId", feeId)
            .put("session", session)
            .put("hostelFee", hostelFee)
            .put("messFee", messFee)
            .put("totalDue", totalDue)
            .put("amountPaid", amountPaid)
            .put("balance", balance)
            .put("status", status)
            .put("dueDate", dueDate)
            .put("studentName", studentName)
            .put("rollNo", rollNo);
    }
    @Override public String toString() { return toJson().toString(); }
}
