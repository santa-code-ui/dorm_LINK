package models;

import utils.JsonUtil;

/** Model for the Admin entity. */
public class Admin {

    private int adminId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String passwordHash;

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("adminId", adminId)
            .put("username", username)
            .put("fullName", fullName)
            .put("email", email)
            .put("phone", phone)
            .put("role", role);
    }
    @Override public String toString() { return toJson().toString(); }
}
