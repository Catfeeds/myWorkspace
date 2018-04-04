package com.hunliji.hljinsurancelibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;

/**
 * 保险列表
 * Created by chen_bin on 2017/5/25.
 */
public class HljHttpInsuranceProductsData extends HljHttpData<List<InsuranceProduct>> {
    @SerializedName(value = "is_show")
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }
}
