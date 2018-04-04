package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/8/9.
 */

public class WSExtObject {

    @SerializedName("track")
    private WSTrack track;
    @SerializedName("tips")
    private WSTips tips;
    @SerializedName("location")
    private WSLocation location;
    @SerializedName("hints")
    private WSHints hints;

    private transient WSMerchantStats merchantStats; // 客户端独有，服务器无关

    public WSExtObject() {
    }

    public WSExtObject(WSLocation location) {
        this.location = location;
    }

    public WSExtObject(WSTrack track) {
        this.track = track;
    }

    public WSExtObject(WSTips tips) {
        this.tips = tips;
    }

    public WSExtObject(WSMerchantStats merchantStats) {
        this.merchantStats = merchantStats;
    }

    public WSExtObject(WSHints hints) {
        this.hints = hints;
    }

    public WSTrack getTrack() {
        return track;
    }

    public WSTips getTips() {
        return tips;
    }

    public WSLocation getLocation() {
        return location;
    }

    public WSHints getHints() {
        return hints;
    }

    public WSMerchantStats getMerchantStats() {
        return merchantStats;
    }
}
