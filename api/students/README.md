# /api/students  —  Students & Courses

Implemented by `backend/controllers/StudentController.java` → `StudentService`
→ `StudentDao` / `CourseDao`.

| Method | Path                          | Body                                   | Purpose |
|--------|-------------------------------|----------------------------------------|---------|
| GET    | `/api/students`               | —                                      | List all students (admin) |
| POST   | `/api/students`               | `{rollNo, fullName, email, phone, gender, courseId}` | Admin adds a student |
| DELETE | `/api/students/{id}`          | —                                      | Remove a student |
| GET    | `/api/students/courses`       | —                                      | List courses (also feeds the sign-up form) |
| POST   | `/api/students/courses`       | `{courseCode, courseName, department, durationYears}` | Add a course |
| DELETE | `/api/students/courses/{id}`  | —                                      | Delete a course |
| GET    | `/api/students/stats`         | —                                      | Admin dashboard figures (counts, occupancy, dues) |
