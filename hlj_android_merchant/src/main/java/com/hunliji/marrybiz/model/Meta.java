package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Suncloud on 2015/2/4.
 */
public class Meta {

    private int statusCode;
    private boolean result;
    private String errorMsg;

    public Meta(JSONObject jsonObject){
        if(jsonObject!=null){
            statusCode=jsonObject.optInt("status_code",0);
            result=jsonObject.optBoolean("result",false);
            errorMsg= JSONUtil.getString(jsonObject,"err_msg");
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isResult() {
        return result;
    }


}
