package services;

import dao.LogDao;
import dao.StudentDao;
import models.EntryExitLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** QR entry/exit logging. A scan resolves the typed roll number to a student. */
public class LogService {

    private final LogDao logs = new LogDao();
    private final StudentDao students = new StudentDao();

    public List<EntryExitLog> mine(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        return logs.listByStudent(studentId);
    }

    public List<EntryExitLog> all() throws SQLException { return logs.listAll(); }

    public int log(Map<String, String> b) throws SQLException {
        String roll = b.get("rollNo");
        String type = b.getOrDefault("type", "ENTRY").toUpperCase();
        String gate = b.getOrDefault("gate", "MAIN");
        if (roll == null || roll.trim().isEmpty())
            throw new IllegalArgumentException("Roll number is required.");
        if (!type.equals("ENTRY") && !type.equals("EXIT"))
            throw new IllegalArgumentException("Type must be ENTRY or EXIT.");
        int studentId = students.findIdByRoll(roll.trim());
        if (studentId == 0) throw new IllegalArgumentException("No student found for roll " + roll);
        return logs.insert(studentId, type, gate);
    }
}
