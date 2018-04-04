package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

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
            endTime = JSONUtil.getDate(jsonObject, "provide_endtime");
            state = JSONUtil.getString(jsonObject, "state");
            leftQuantity = jsonObject.optInt("left_quantity", 0);
            price = (float) jsonObject.optDouble("price", 0);
            maxMin = jsonObject.optInt("max_min", 0);
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
