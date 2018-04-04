package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import com.google.gson.JsonElement;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/9/12.
 */

public class XiaoxiInstallmentSubmitResult {

    String assetOrderId;
    ArrayList<JsonElement> failedItems;
    String smsSerialNo;

    public String getAssetOrderId() {
        return assetOrderId;
    }

    public ArrayList<JsonElement> getFailedItems() {
        return failedItems;
    }

    public String getSmsSerialNo() {
        return smsSerialNo;
    }
}
