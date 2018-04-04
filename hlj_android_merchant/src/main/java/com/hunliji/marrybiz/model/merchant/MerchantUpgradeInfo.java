package com.hunliji.marrybiz.model.merchant;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/6/6.
 */

public class MerchantUpgradeInfo {
    @SerializedName("need_upgrade")
    boolean needUpgrade;
    String url;


    public boolean isNeedUpgrade() {
        return needUpgrade;
    }

    public void setNeedUpgrade(boolean needUpgrade) {
        this.needUpgrade = needUpgrade;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
