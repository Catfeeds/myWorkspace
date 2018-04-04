package com.hunliji.marrybiz.model.merchant;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2018/3/21.
 * 用户数据里面的is_adv字段里面的数据
 */

public class MerchantIsAdv {
    @SerializedName("is_adv")
    boolean isAdv; // 出现在婚礼顾问-派单商家列表 中的 “所有非婚宴商家”

    public boolean isAdv() {
        return isAdv;
    }
}
