package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/6/10.
 */
public class ReturnStatus {


    private int retCode=-1;
    private String errorMsg;
    public ReturnStatus(JSONObject jsonObject) {
        if (jsonObject != null) {
            retCode = jsonObject.optInt("RetCode", -1);
            errorMsg = JSONUtil.getString(jsonObject, "msg");
        }
    }

    public ReturnStatus(int retCode, String errorMsg) {
        this.retCode = retCode;
        this.errorMsg = errorMsg;
    }

    public int getRetCode() {
        return retCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
