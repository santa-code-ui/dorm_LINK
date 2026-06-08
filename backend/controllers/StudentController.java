package controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.StudentService;
import utils.JsonUtil;

import java.io.IOException;
import java.util.Map;

/**
 * /api/students/courses          GET list · POST add
 * /api/students/courses/{id}     DELETE
 * /api/students/stats            GET admin dashboard figures
 * /api/students                  GET list · POST add
 * /api/students/{id}             DELETE
 */
@WebServlet(name = "StudentController", urlPatterns = {"/api/students/*"})
public class StudentController extends BaseController {

    private final StudentService svc = new StudentService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length == 0)              writeData(res, svc.listStudents());
            else if (s[0].equals("courses")) writeData(res, svc.listCourses());
            else if (s[0].equals("stats"))   writeObject(res, svc.stats());
            else writeError(res, HttpServletResponse.SC_NOT_FOUND, "Unknown resource.");
        } catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            Map<String, String> b = body(req);
            if (s.length >= 1 && s[0].equals("courses"))
                writeObject(res, JsonUtil.obj().put("courseId", svc.addCourse(b)).put("message", "Course added"));
            else
                writeObject(res, JsonUtil.obj().put("studentId", svc.addStudent(b)).put("message", "Student added"));
        } catch (IllegalArgumentException e) { writeError(res, 400, e.getMessage()); }
        catch (Exception e) { serverError(res, e); }
    }

    @Override protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String[] s = segments(req);
        try {
            if (s.length >= 2 && s[0].equals("courses")) {
                svc.deleteCourse(Integer.parseInt(s[1]));
                writeObject(res, JsonUtil.obj().put("message", "Course deleted"));
            } else if (s.length >= 1) {
                svc.deleteStudent(Integer.parseInt(s[0]));
                writeObject(res, JsonUtil.obj().put("message", "Student deleted"));
            } else writeError(res, 400, "Missing id.");
        } catch (NumberFormatException e) { writeError(res, 400, "Invalid id."); }
        catch (Exception e) { serverError(res, e); }
    }
}
