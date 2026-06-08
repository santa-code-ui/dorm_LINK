package services;

import dao.HostelDao;
import dao.MessMenuDao;
import dao.RatingDao;
import dao.RoomDao;
import dao.RoomRequestDao;
import models.Hostel;
import models.MessMenu;
import models.Room;
import models.RoomRequest;
import models.Student;
import utils.JsonUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Rooms, hostels, mess menu, ratings and the room-request / allocation workflow. */
public class RoomService {

    private final RoomDao        rooms    = new RoomDao();
    private final HostelDao      hostels  = new HostelDao();
    private final MessMenuDao    menus    = new MessMenuDao();
    private final RatingDao      ratings  = new RatingDao();
    private final RoomRequestDao requests = new RoomRequestDao();

    /* ---- rooms ---- */
    public List<Room> listRooms(int hostelId) throws SQLException {
        return hostelId > 0 ? rooms.listByHostel(hostelId) : rooms.listAll();
    }

    public int addRoom(Map<String, String> b) throws SQLException {
        Room r = new Room();
        r.setRoomNo(must(b, "roomNo"));
        r.setHostelId(JsonUtil.asInt(b, "hostelId", 1));
        r.setRoomType(b.getOrDefault("roomType", "DOUBLE"));
        r.setCapacity(JsonUtil.asInt(b, "capacity", 2));
        r.setFloor(JsonUtil.asInt(b, "floor", 0));
        r.setRentAmount(JsonUtil.asDouble(b, "rentAmount", 0));
        return rooms.insert(r);
    }

    public List<Hostel> hostels() throws SQLException { return hostels.listAll(); }

    /* ---- mess menu: day-keyed object {MON:{...},...} ---- */
    public JsonUtil.Json mess(int hostelId) throws SQLException {
        JsonUtil.Json out = JsonUtil.obj();
        for (MessMenu m : menus.listByHostel(hostelId)) out.put(m.getDayOfWeek(), m.toJson());
        return out;
    }

    /* ---- student's own room + roommates ---- */
    public String mine(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        Room r = rooms.findByStudent(studentId);
        if (r == null) return "{}";
        StringBuilder mates = new StringBuilder("[");
        List<Student> rm = rooms.roommates(r.getRoomId(), studentId);
        for (int i = 0; i < rm.size(); i++) {
            if (i > 0) mates.append(",");
            mates.append(JsonUtil.obj()
                    .put("fullName", rm.get(i).getFullName())
                    .put("rollNo", rm.get(i).getRollNo()));
        }
        mates.append("]");
        String base = r.toJson().toString();
        return base.substring(0, base.length() - 1) + ",\"roommates\":" + mates + "}";
    }

    /* ---- ratings (trigger updates hostel.avg_rating in real time) ---- */
    public void rate(int studentId, Map<String, String> b) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        int hostelId = JsonUtil.asInt(b, "hostelId", 0);
        int stars = JsonUtil.asInt(b, "stars", 0);
        if (hostelId <= 0) throw new IllegalArgumentException("Unknown hostel.");
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Rating must be 1–5 stars.");
        ratings.upsert(studentId, hostelId, stars, b.get("review"));
    }

    /* ---- room requests ---- */
    public List<RoomRequest> allRequests() throws SQLException { return requests.listAll(); }

    public int createRequest(int studentId, Map<String, String> b) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Not signed in.");
        int roomId = JsonUtil.asInt(b, "roomId", 0);
        if (roomId <= 0) throw new IllegalArgumentException("Pick a room to request.");
        return requests.insert(studentId, roomId, b.get("note"));
    }

    /** Admin decision. On APPROVED, the student is allocated to the room. */
    public void decideRequest(int requestId, String status, int adminId) throws SQLException {
        String s = status == null ? "" : status.toUpperCase();
        if (!s.equals("APPROVED") && !s.equals("REJECTED"))
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED.");
        requests.decide(requestId, s, adminId);
        if (s.equals("APPROVED")) {
            int[] ids = requests.idsFor(requestId);   // {studentId, roomId}
            if (ids != null) rooms.allocate(ids[0], ids[1]);
        }
    }

    private static String must(Map<String, String> b, String k) {
        String v = b.get(k);
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(k + " is required.");
        return v.trim();
    }
}
