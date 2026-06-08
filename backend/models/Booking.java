package models;

import utils.JsonUtil;

/** Model for the Booking entity. */
public class Booking {

    private int bookingId;
    private int studentId;
    private int roomId;
    private String session;
    private String status;
    private String bookedAt;

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBookedAt() { return bookedAt; }
    public void setBookedAt(String bookedAt) { this.bookedAt = bookedAt; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("bookingId", bookingId)
            .put("studentId", studentId)
            .put("roomId", roomId)
            .put("session", session)
            .put("status", status)
            .put("bookedAt", bookedAt);
    }
    @Override public String toString() { return toJson().toString(); }
}
