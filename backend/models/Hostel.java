package models;

import utils.JsonUtil;

/** Model for the Hostel entity. */
public class Hostel {

    private int hostelId;
    private String hostelName;
    private String block;
    private String gender;
    private String description;
    private int totalRooms;
    private String coverImage;
    private double avgRating;
    private int available;

    public int getHostelId() { return hostelId; }
    public void setHostelId(int hostelId) { this.hostelId = hostelId; }
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("hostelId", hostelId)
            .put("hostelName", hostelName)
            .put("block", block)
            .put("gender", gender)
            .put("description", description)
            .put("totalRooms", totalRooms)
            .put("coverImage", coverImage)
            .put("avgRating", avgRating)
            .put("available", available);
    }
    @Override public String toString() { return toJson().toString(); }
}
