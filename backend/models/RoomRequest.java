package models;

import utils.JsonUtil;

/** Model for the RoomRequest entity. */
public class RoomRequest {

    private int requestId;
    private int studentId;
    private int roomId;
    private String status;
    private String note;
    private String requestedAt;
    private String studentName;
    private String rollNo;
    private String roomNo;
    private String hostelName;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getRequestedAt() { return requestedAt; }
    public void setRequestedAt(String requestedAt) { this.requestedAt = requestedAt; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("requestId", requestId)
            .put("studentId", studentId)
            .put("roomId", roomId)
            .put("status", status)
            .put("note", note)
            .put("requestedAt", requestedAt)
            .put("studentName", studentName)
            .put("rollNo", rollNo)
            .put("roomNo", roomNo)
            .put("hostelName", hostelName);
    }
    @Override public String toString() { return toJson().toString(); }
}
