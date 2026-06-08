package services;

import dao.CourseDao;
import dao.StudentDao;
import models.Course;
import models.Student;
import utils.JsonUtil;
import utils.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Admin management of students and courses, plus dashboard statistics. */
public class StudentService {

    private final StudentDao students = new StudentDao();
    private final CourseDao  courses  = new CourseDao();

    /* ---- courses ---- */
    public List<Course> listCourses() throws SQLException { return courses.listAll(); }

    public int addCourse(Map<String, String> b) throws SQLException {
        Course c = new Course();
        c.setCourseCode(req(b, "courseCode"));
        c.setCourseName(req(b, "courseName"));
        c.setDepartment(b.getOrDefault("department", "General"));
        c.setDurationYears(JsonUtil.asInt(b, "durationYears", 4));
        return courses.insert(c);
    }

    public boolean deleteCourse(int id) throws SQLException { return courses.delete(id); }

    /* ---- students ---- */
    public List<Student> listStudents() throws SQLException { return students.listAll(); }

    public int addStudent(Map<String, String> b) throws SQLException {
        Student s = new Student();
        s.setRollNo(req(b, "rollNo"));
        s.setFullName(req(b, "fullName"));
        s.setEmail(req(b, "email"));
        s.setPhone(b.get("phone"));
        s.setGender(b.getOrDefault("gender", "OTHER"));
        s.setCourseId(JsonUtil.asInt(b, "courseId", 0));
        // Default password = roll number; the student resets it via OTP on first login.
        String pw = b.getOrDefault("password", s.getRollNo());
        return students.insert(s, PasswordUtil.hash(pw));
    }

    public boolean deleteStudent(int id) throws SQLException { return students.delete(id); }

    /* ---- dashboard ---- */
    public JsonUtil.Json stats() throws SQLException { return students.adminStats(); }

    private static String req(Map<String, String> b, String k) {
        String v = b.get(k);
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(k + " is required.");
        return v.trim();
    }
}
