package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.TimeUtil;

import org.json.JSONObject;

import java.util.Date;

public class Rule implements Identifiable {

    private static final long serialVersionUID = 6468645596736353380L;

    public long id;
    private String name;
    private int status;
    private int type;
    private String showtxt;
    private String showimg;
    private Date start_time;
    private Date end_time;
    private boolean isTimeAble;

    public Rule(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.name = JSONUtil.getString(json, "name");
            start_time = JSONUtil.getDataFromTimStamp(json, "start_time");
            if(start_time==null){
                start_time = JSONUtil.getDateFromFormatLong(json,"start_time",true);
            }
            if (start_time != null) {
                start_time = TimeUtil.getLocalTime(start_time);
            }
            end_time = JSONUtil.getDataFromTimStamp(json, "end_time");
            if(end_time==null){
                end_time = JSONUtil.getDateFromFormatLong(json,"end_time",true);
            }
            if (end_time != null) {
                end_time = TimeUtil.getLocalTime(end_time);
            }
            this.status = json.optInt("status");
            this.type = json.optInt("type");
            this.showtxt = JSONUtil.getString(json, "showtxt");
            this.showimg = JSONUtil.getString(json, "showimg");
            this.isTimeAble = json.optInt("is_time_viewable")>0;
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

    public boolean isActivity(){
        Date date=new Date();
        return !isTimeAble||((start_time == null || start_time.before(date)) && (end_time == null || end_time.after(date)));
    }

    public String getShowimg() {
        return !isTimeAble||end_time == null || end_time.after(new Date())?showimg:null;
    }

    public boolean isTimeAble() {
        return isTimeAble;
    }
}
