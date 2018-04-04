package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by LuoHanLin on 15/1/17.
 */
public class WeddingProgram implements Identifiable {

    private static final long serialVersionUID = -6307582680095192783L;
    private long id;
    private String title;
    private Long sortIndex;
    private String sharePath;
    private ArrayList<Item> items;
    private boolean emptyInit = false; // 创建空的plan时初始化为true
    private long position; // 在列表中的位置

    public WeddingProgram(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            title = JSONUtil.getString(jsonObject, "title");
            sortIndex = jsonObject.optLong("sort_no");
            sharePath = JSONUtil.getString(jsonObject, "share_path");
            items = new ArrayList<>();
            if (!jsonObject.isNull("items")) {
                JSONArray jsonArray = jsonObject.optJSONArray("items");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Item item = new Item(jsonArray.optJSONObject(i));
                        items.add(item);
                    }
                }
            }
            if (items.size() == 0) {
                this.emptyInit = true;
                Item item = new Item(0);
                items.add(item);
            } else {
                emptyInit = false;
            }
        }
    }

    public String getSharePath() {
        return sharePath;
    }

    @Override
    public Long getId() {
        return id;
    }

    public boolean isEmptyInit() {
        return emptyInit;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSortIndex() {
        return sortIndex;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }


    /**
     * 删除指定位置的item,并且如果删除后列表为空,显示添加提示
     *
     * @param childPosition
     */
    public void removeItem(int childPosition) {
        this.items.remove(childPosition);
        if (this.items.size() == 0) {
            Item item = new Item(0);
            items.add(item);
            this.emptyInit = true;
        } else {
            this.emptyInit = false;
        }
    }

    public class Item {
        private long id;
        private Integer hour;
        private Integer minute;
        private String summary;
        private String partners;

        private Item(long id) {
            this.id = id;
            this.hour = 12;
            this.minute = 12;
            this.summary = "给时光以生命,而不是给生命以时光";
            this.partners = "时光 生命";
        }

        public Item(int hour, int minute, String content, String partners) {
            this.summary = content;
            this.partners = partners;
            this.hour = hour;
            this.minute = minute;
        }

        private Item(JSONObject jsonObject) {
            if (jsonObject != null) {
                this.id = jsonObject.optLong("id", 0);
                this.hour = jsonObject.optInt("hour", 0);
                this.minute = jsonObject.optInt("minute", 0);
                this.summary = JSONUtil.getString(jsonObject, "summary");
                this.partners = JSONUtil.getString(jsonObject, "partners");
            }
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public String getSummary() {
            return summary;
        }

        public String getPartners() {
            return partners;
        }

    }
}
