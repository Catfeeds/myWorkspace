package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.TimeUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Suncloud on 2015/12/21.
 */
public class ADVHMerchantHistory implements Identifiable {

    private long id;
    private String status;
    private Date createdAt;

    public ADVHMerchantHistory(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            status= JSONUtil.getString(jsonObject,"status");
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject,"created_at",true);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return TimeUtil.getLocalTime(createdAt);
    }

    public String getStatus() {
        return status;
    }
}
