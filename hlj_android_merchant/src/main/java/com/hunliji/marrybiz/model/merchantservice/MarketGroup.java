package com.hunliji.marrybiz.model.merchantservice;

import com.google.gson.annotations.SerializedName;
import com.hunliji.marrybiz.model.merchantservice.MerchantServer;

import java.util.List;

/**
 * 商家服务记录(到店礼 优惠卷 商家承诺 等等)
 * Created by jixnin on 2017/2/9 0009.
 */

public class MarketGroup {
    @SerializedName(value = "list")
    List<MarketItem> marketItems;
    private String title;


    public String getTitle() {
        return title;
    }

    public MarketGroup(String title) {
        this.title = title;
    }

    public List<MarketItem> getMarketItems() {
        return marketItems;
    }

    public void setMarketItems(List<MarketItem> marketItems) {
        this.marketItems = marketItems;
    }
}
