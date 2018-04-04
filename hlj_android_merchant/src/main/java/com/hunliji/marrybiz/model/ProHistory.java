package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Suncloud on 2016/6/20.
 */
public class ProHistory implements Identifiable {

    private long id;
    private DateTime createdAt;

    public ProHistory(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            if(date!=null){
                createdAt=new DateTime(date);
            }
        }
    }


    @Override
    public Long getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
