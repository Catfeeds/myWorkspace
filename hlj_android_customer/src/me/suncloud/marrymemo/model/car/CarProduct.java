package me.suncloud.marrymemo.model.car;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * Created by wangtao on 2017/4/21.
 */

public class CarProduct {

    private long id;
    private String title;
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("show_price")
    private double showPrice;
    @SerializedName("cover_image")
    private Photo cover;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public Photo getCover() {
        return cover;
    }
}
