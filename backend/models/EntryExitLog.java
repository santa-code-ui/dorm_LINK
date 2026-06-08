package models;

import utils.JsonUtil;

/** Model for the EntryExitLog entity. */
public class EntryExitLog {

    private int logId;
    private int studentId;
    private String type;
    private String gate;
    private String timestamp;
    private String studentName;
    private String rollNo;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    /* keep old setter name so LogDao still compiles */
    public void setAt(String at) { this.timestamp = at; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("logId", logId)
            .put("type", type)
            .put("gate", gate)
            .put("timestamp", timestamp)   // <-- renamed from "at"
            .put("studentName", studentName)
            .put("rollNo", rollNo);
    }
    @Override public String toString() { return toJson().toString(); }
}
