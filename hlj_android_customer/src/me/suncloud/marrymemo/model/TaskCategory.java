package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 15/4/29.
 */
public class TaskCategory implements Identifiable {
    private static final long serialVersionUID = -7014412106271952704L;
    private long id;
    private String title;
    private ArrayList<TaskItem> taskItems;
    private String type; // 用于标记是模板分类还是用户自定义的分类

    public TaskCategory() {
    }

    public TaskCategory(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.title = JSONUtil.getString(jsonObject, "name");
            this.type = "template";
            this.taskItems = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("childNodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                taskItems.add(new TaskItem(jsonArray.optJSONObject(i)));
            }
        }
    }

    public static TaskCategory addCustomTask(long id, TaskItem taskItem) {
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.type = "customize";
        taskCategory.id = id;
        taskCategory.title = "其他";
        taskCategory.taskItems = new ArrayList<>();
        taskCategory.taskItems.add(taskItem);
        taskCategory.setChecked(true);

        return taskCategory;
    }

    public static TaskCategory addCategoryTask(Category category, TaskItem taskItem) {
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.type = "template";
        taskCategory.id = category.getId();
        taskCategory.title = category.getName();
        taskCategory.taskItems = new ArrayList<>();
        taskCategory.taskItems.add(taskItem);
        taskCategory.setChecked(true);

        return taskCategory;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<TaskItem> getTaskItems() {
        return taskItems;
    }

    public String getType() {
        return type;
    }

    public boolean isChecked() {
        for (TaskItem taskItem : taskItems) {
            if (taskItem.isChecked()) {
                return true;
            }
        }

        return false;
    }

    public void setChecked(boolean checked) {
        for (TaskItem taskItem : this.taskItems) {
            taskItem.setChecked(checked);
        }
    }


    public static class TaskItem {
        private long id;
        private Date updateAt;
        private Date finishDate;
        private Date reminderTime;
        private String title;
        private String description;
        private boolean expired;
        private boolean checked;
        private boolean isDone;

        public TaskItem(JSONObject jsonObject) {
            if (jsonObject != null) {
                id = jsonObject.optLong("id");
                title = JSONUtil.getString(jsonObject, "title");
                description = JSONUtil.getString(jsonObject, "description");
                expired = jsonObject.optBoolean("expired", false);
                updateAt = JSONUtil.getDataFromTimStamp(jsonObject, "update_at");
            }
        }

        public TaskItem(String title, boolean checked, long finishTimeMills, long
                reminderTimeMills) {
            finishDate = new Date(finishTimeMills);
            reminderTime = new Date(reminderTimeMills);
            this.title = title;
            this.checked = checked;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Date getFinishDate() {
            return finishDate;
        }

        public Date getReminderTime() {
            return reminderTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean toggleChecked() {
            this.checked = !checked;

            return checked;
        }

        public String getDescription() {
            return description;
        }

        public boolean isExpired() {
            return expired;
        }

        public boolean isDone() {
            return isDone;
        }

        public void setIsDone(boolean isDone) {
            this.isDone = isDone;
        }

        public Date getUpdateAt() {
            return updateAt;
        }


    }


}
