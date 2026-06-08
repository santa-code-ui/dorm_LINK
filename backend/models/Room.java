package models;

import utils.JsonUtil;

/** Model for the Room entity. */
public class Room {

    private int roomId;
    private int hostelId;
    private String roomNo;
    private int floor;
    private String roomType;
    private int capacity;
    private int occupied;
    private double rentAmount;
    private String status;
    private String photo;
    private String hostelName;
    private String block;

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public int getHostelId() { return hostelId; }
    public void setHostelId(int hostelId) { this.hostelId = hostelId; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("roomId", roomId)
            .put("hostelId", hostelId)
            .put("roomNo", roomNo)
            .put("floor", floor)
            .put("roomType", roomType)
            .put("capacity", capacity)
            .put("occupied", occupied)
            .put("rentAmount", rentAmount)
            .put("status", status)
            .put("photo", photo)
            .put("hostelName", hostelName)
            .put("block", block);
    }
    @Override public String toString() { return toJson().toString(); }
}
