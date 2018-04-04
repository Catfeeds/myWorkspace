package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;


/**
 * Created by Suncloud on 2015/6/10.
 */
public class Status {


    private int retCode=-1;
    private String errorMsg;

    public Status(JSONObject jsonObject){
        if(jsonObject!=null){
            retCode=jsonObject.optInt("RetCode",-1);
            errorMsg= JSONUtil.getString(jsonObject, "msg");
        }
    }

    public int getRetCode() {
        return retCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
