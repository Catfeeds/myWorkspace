package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * author:Bezier
 * 2015/1/10 14:03
 */
public class Rule implements Identifiable {
    public long id;
    private String name;
    private Date start_time;
    private Date end_time;
    private int type;
    private String showtxt;
    private String showimg;
    private boolean isTimeAble;
    private String bigImg;

    public Rule(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.name = JSONUtil.getString(json, "name");
            start_time = JSONUtil.getDataFromTimStamp(json, "start_time");
            if (start_time == null) {
                start_time = JSONUtil.getDateFromFormatLong(json, "start_time", true);
            }
            if (start_time != null) {
                start_time = TimeUtil.getLocalTime(start_time);
            }
            end_time = JSONUtil.getDataFromTimStamp(json, "end_time");
            if (end_time == null) {
                end_time = JSONUtil.getDateFromFormatLong(json, "end_time", true);
            }
            if (end_time != null) {
                end_time = TimeUtil.getLocalTime(end_time);
            }
            this.type = json.optInt("type");
            this.showtxt = JSONUtil.getString(json, "showtxt");
            this.showimg = JSONUtil.getString(json, "showimg");
            this.isTimeAble = json.optInt("is_time_viewable") > 0;
            this.bigImg = JSONUtil.getString(json, "bigimg");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getStart_time() {
        return start_time;
    }

    public long getStartTimeInMillis() {
        return start_time != null ? start_time.getTime() : 0;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public long getEndTimeInMillis() {
        return end_time != null ? end_time.getTime() : 0;
    }

    public int getType() {
        return type;
    }

    public String getShowtxt() {
        return showtxt;
    }

    public String getShowimg() {
        return !isTimeAble || end_time == null || end_time.after(new Date()) ? showimg : null;
    }

    public String getShowimg2() {
        return showimg;
    }

    public void setShowimg(String showimg) {
        this.showimg = showimg;
    }

    public boolean isTimeAble() {
        return isTimeAble;
    }

    public String getBigImg() {
        return !isTimeAble || end_time == null || end_time.after(new Date()) ?bigImg:null;
    }

    public void setBigImg(String bigImg) {
        this.bigImg = bigImg;
    }
}
