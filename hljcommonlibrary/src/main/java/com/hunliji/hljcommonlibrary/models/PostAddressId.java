package com.hunliji.hljcommonlibrary.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/4/20.地址信息提交
 */

public class PostAddressId {
    @SerializedName(value = "address_id")
    private long addressId;

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }
}
