package models;

import utils.JsonUtil;

/** Model for the Complaint entity. */
public class Complaint {

    private int complaintId;
    private int studentId;
    private String type;
    private String subject;
    private String description;
    private String status;
    private String createdAt;
    private String studentName;
    private String rollNo;

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("complaintId", complaintId)
            .put("type", type)
            .put("subject", subject)
            .put("description", description)
            .put("status", status)
            .put("createdAt", createdAt)
            .put("studentName", studentName)
            .put("rollNo", rollNo);
    }
    @Override public String toString() { return toJson().toString(); }
}
