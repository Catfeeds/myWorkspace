package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * Created by Suncloud on 2014/12/31.
 */
public class Sale implements Serializable {

    private Date startTime;
    private Date endTime;
    private String state;
    private int leftQuantity;
    private float price;
    private int maxMin;

    public Sale(JSONObject jsonObject) {
        if (jsonObject != null) {
            startTime = JSONUtil.getDate(jsonObject, "provide_starttime");
            if(startTime!=null){
                startTime= TimeUtil.getLocalTime(startTime);
            }
            endTime = JSONUtil.getDate(jsonObject, "provide_endtime");
            if(endTime!=null){
                endTime= TimeUtil.getLocalTime(endTime);
            }
            state = JSONUtil.getString(jsonObject, "state");
            leftQuantity = jsonObject.optInt("left_quantity", 0);
            price = (float) jsonObject.optDouble("price", 0);
            maxMin = jsonObject.optInt("max_min", 0);
        }
    }

    /**
     * 给PHP返回的数据解析
     * @param jsonObject
     * @param noneSense
     */
    public Sale(JSONObject jsonObject, int noneSense) {
        if (jsonObject != null) {
            state = JSONUtil.getString(jsonObject, "state");
            leftQuantity = jsonObject.optInt("left_quantity", 0);
            price = (float) jsonObject.optDouble("price", 0);
            maxMin = jsonObject.optInt("max_min", 0);

            startTime = JSONUtil.getDataFromTimStamp(jsonObject, "provide_starttime");
            if (startTime != null) {
                startTime = TimeUtil.getLocalTime(startTime);
            }
            endTime = JSONUtil.getDataFromTimStamp(jsonObject, "provide_endtime");
            if (endTime != null) {
                endTime = TimeUtil.getLocalTime(endTime);
            }
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getEndTimeInMillis() {
        return endTime != null ? endTime.getTime() : 0;
    }

    public Date getStartTime() {
        return startTime;
    }


    public long getStartTimeInMillis() {
        return startTime != null ? startTime.getTime() : 0;
    }

    public float getPrice() {
        return price;
    }

    public int getLeftQuantity() {
        return leftQuantity;
    }

    public int getMaxMin() {
        return maxMin;
    }

    public String getState() {
        return state;
    }

}
