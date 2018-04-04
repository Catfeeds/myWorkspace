package com.hunliji.marrybiz.model;

import org.json.JSONObject;

/**
 * Created by mo_yu on 2017/12/27.体验店合作商家
 */

public class ExperienceStore implements Identifiable {

    private long id;
    private long merchantId;
    private long cityCode;

    public ExperienceStore(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.merchantId = jsonObject.optLong("merchant_id");
            this.cityCode = jsonObject.optLong("city_code");
        }
    }


    @Override
    public Long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public long getCityCode() {
        return cityCode;
    }
}
