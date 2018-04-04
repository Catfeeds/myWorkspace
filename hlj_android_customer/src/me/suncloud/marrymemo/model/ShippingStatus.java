package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/8/26.
 */
public class ShippingStatus implements Identifiable {

    private static final long serialVersionUID = 5185290302514726996L;
    private long id;
    private String status;
    private Date date;

    public ShippingStatus(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.date = JSONUtil.getDateFromFormatLong(jsonObject, "time", true);
            id = date.getTime();
            this.status = jsonObject.optString("context");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }
}
