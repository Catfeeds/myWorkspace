package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * author:Bezier
 * 2015/1/29 11:48
 */
public class Task implements Identifiable {

    private long id;
    private long categoryId;
    private String title;
    private String state;
    private String description;
    private String categoryName;
    private String eventIdentifier;
    private Date remindTime;
    private Date updateTime;
    private Date endTime;
    private ArrayList<Cite> cites;
    private boolean isExpand;


    public Task(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            categoryId = json.optLong("to_do_category_id", 0);
            title = JSONUtil.getString(json, "title");
            state = JSONUtil.getString(json, "state");
            description = JSONUtil.getString(json, "description");
            categoryName = JSONUtil.getString(json, "to_do_category_name");
            eventIdentifier = JSONUtil.getString(json, "event_identifier");
            remindTime = JSONUtil.getDate(json, "remind_at");
            updateTime = JSONUtil.getDate(json, "updated_at");
            endTime = JSONUtil.getDate(json, "end_at");
//            if (endTime != null) {
//                endTime.setHours(23);
//                endTime.setMinutes(59);
//                endTime.setSeconds(59);
//            }
            if (!json.isNull("cites")) {
                JSONArray array = json.optJSONArray("cites");
                if (array != null && array.length() > 0) {
                    cites = new ArrayList<>();
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        Cite cite = new Cite(array.optJSONObject(i));
                        if(!JSONUtil.isEmpty(cite.getType())) {
                            cites.add(cite);
                        }
                    }
                }
            }

        }
    }
//
//    public void editTask(JSONObject json) {
//        if (json != null) {
//            id = json.optLong("id", id);
//            categoryId = json.optLong("to_do_category_id", categoryId);
//            title = json.optString("title", title);
//            description = json.optString("description", description);
//            categoryName = json.optString("to_do_category_name", categoryName);
//            remindTime = JSONUtil.getDate(json, "remind_at");
//            endTime = JSONUtil.getDate(json, "end_at");
//        }
//    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public ArrayList<Cite> getCites() {
        return cites;
    }

    public String getEventIdentifier() {
        return eventIdentifier;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getYear() {
        if (endTime == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        if (endTime == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public Date getRemindTime() {
        return remindTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        if (JSONUtil.isEmpty(description)) {
            return "";
        }
        return description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public boolean isDone() {
        return "succeed".equals(state);
    }

    public void setIsDone(boolean isDone) {
        state = isDone ? "succeed" : "init";
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setRemindTime(Date remindTime) {
        this.remindTime = remindTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
