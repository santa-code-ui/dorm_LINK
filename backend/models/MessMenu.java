package models;

import utils.JsonUtil;

/** Model for the MessMenu entity. */
public class MessMenu {

    private int menuId;
    private int hostelId;
    private String dayOfWeek;
    private String breakfast;
    private String lunch;
    private String snacks;
    private String dinner;

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public int getHostelId() { return hostelId; }
    public void setHostelId(int hostelId) { this.hostelId = hostelId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getBreakfast() { return breakfast; }
    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }
    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }
    public String getSnacks() { return snacks; }
    public void setSnacks(String snacks) { this.snacks = snacks; }
    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }

    public JsonUtil.Json toJson() {
        return JsonUtil.obj()
            .put("menuId", menuId)
            .put("hostelId", hostelId)
            .put("dayOfWeek", dayOfWeek)
            .put("breakfast", breakfast)
            .put("lunch", lunch)
            .put("snacks", snacks)
            .put("dinner", dinner);
    }
    @Override public String toString() { return toJson().toString(); }
}
