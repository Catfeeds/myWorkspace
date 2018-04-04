package com.hunliji.hljkefulibrary.moudles;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/10/25.
 */

public class TransferToKefu {
    @SerializedName("id")
    private String uuid;
    @SerializedName("serviceSessionId")
    private String serviceSessionId;
    @SerializedName("label")
    private String btnLabel;

    public String getBtnLabel() {
        return btnLabel;
    }

    public String getServiceSessionId() {
        return serviceSessionId;
    }

    public String getUuid() {
        return uuid;
    }
}
