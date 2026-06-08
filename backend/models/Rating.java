package models;

import utils.JsonUtil;

/** Model for the Rating entity. */
public class Rating {

    private int ratingId;
    private int studentId;
    private int hostelId;
    private int stars;
    private String review;
    private String createdAt;

    public int getRatingId() { return ratingId; }
    public void setRatingId(int ratingId) { this.ratingId = ratingId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getHostelId() { return hostelId; }
    public void setHostelId(int hostelId) { this.hostelId = hostelId; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("ratingId", ratingId)
            .put("studentId", studentId)
            .put("hostelId", hostelId)
            .put("stars", stars)
            .put("review", review)
            .put("createdAt", createdAt);
    }
    @Override public String toString() { return toJson().toString(); }
}
