package com.hunliji.hljcommonlibrary.models.merchant;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/7/12.
 * 商家轻松聊内容
 */

public class MerchantChatData {
    @SerializedName("example_speech")
    String exampleSpeech;
    @SerializedName("home_speech")
    String homeSpeech;
    @SerializedName("package_speech")
    String packageSpeech;

    public String getExampleSpeech() {
        return exampleSpeech;
    }

    public String getHomeSpeech() {
        return homeSpeech;
    }

    public String getPackageSpeech() {
        return packageSpeech;
    }
}
