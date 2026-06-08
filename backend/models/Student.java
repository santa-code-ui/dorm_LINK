package models;

import utils.JsonUtil;

/** Model for the Student entity. */
public class Student {

    private int studentId;
    private String rollNo;
    private String fullName;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String address;
    private int courseId;
    private int roomId;
    private String status;
    private String passwordHash;
    private String courseName;
    private String room;
    private String hostel;
    private String department;

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getHostel() { return hostel; }
    public void setHostel(String hostel) { this.hostel = hostel; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("studentId", studentId)
            .put("rollNo", rollNo)
            .put("fullName", fullName)
            .put("email", email)
            .put("phone", phone)
            .put("dob", dob)
            .put("gender", gender)
            .put("address", address)
            .put("courseId", courseId)
            .put("roomId", roomId)
            .put("status", status)
            .put("courseName", courseName)
            .put("room", room)
            .put("hostel", hostel)
            .put("department", department);
    }
    @Override public String toString() { return toJson().toString(); }
}
