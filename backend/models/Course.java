package models;

import utils.JsonUtil;

/** Model for the Course entity. */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private String department;
    private int durationYears;
    private int students;

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getDurationYears() { return durationYears; }
    public void setDurationYears(int durationYears) { this.durationYears = durationYears; }
    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("courseId", courseId)
            .put("courseCode", courseCode)
            .put("courseName", courseName)
            .put("department", department)
            .put("durationYears", durationYears)
            .put("students", students);
    }
    @Override public String toString() { return toJson().toString(); }
}
