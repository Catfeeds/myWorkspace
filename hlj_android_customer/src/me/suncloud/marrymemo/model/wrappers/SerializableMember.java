package me.suncloud.marrymemo.model.wrappers;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by wangtao on 2017/7/7.
 */

public class SerializableMember implements Serializable {
    private long id;//会员id
    private long addressId;//标识是否已经填写收货地址

    public SerializableMember(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.addressId = jsonObject.optLong("address_id", 0);
            this.id = jsonObject.optLong("id", 0);
        }
    }

    public long getId() {
        return id;
    }

    public long getAddressId() {
        return addressId;
    }
}
