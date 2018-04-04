package com.hunliji.marrybiz.model.merchantservice;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2018/2/2.小程序详情
 */

public class WeAppDetail {

    public static final int STATUS_UN_BUY = 0;//未购买
    public static final int STATUS_IN_USE = 1;//使用中
    public static final int STATUS_OUT_DATE = 2;//已过期

    @SerializedName("register_status")
    int registerStatus;
    @SerializedName("server_end")
    DateTime serverEnd;//过期时间
    int status;//0待确认 1 已开通 2已过期

    public int getRegisterStatus() {
        return registerStatus;
    }

    public void setRegisterStatus(int registerStatus) {
        this.registerStatus = registerStatus;
    }

    public DateTime getServerEnd() {
        return serverEnd;
    }

    public void setServerEnd(DateTime serverEnd) {
        this.serverEnd = serverEnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
