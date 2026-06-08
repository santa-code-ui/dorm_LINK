package models;

import utils.JsonUtil;

/** Model for the Announcement entity. */
public class Announcement {

    private int announcementId;
    private int adminId;
    private String title;
    private String body;
    private String category;
    private boolean active;
    private String createdAt;

    public int getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("announcementId", announcementId)
            .put("title", title)
            .put("body", body)
            .put("category", category)
            .put("active", active)
            .put("createdAt", createdAt);
    }
    @Override public String toString() { return toJson().toString(); }
}
