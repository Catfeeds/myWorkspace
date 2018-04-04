package me.suncloud.marrymemo.model.merchant.wrappers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/5/23 0023.
 */
public class MerchantChatLinkTriggerPostBody {

    public static final int TIME_TILL_SHOWING = 10;
    public static final int TIME_TILL_HIDE = 20;

    @SerializedName("merchant_id")
    private Long merchantId;
    @SerializedName("car_speech")
    private String carSpeech;
    private String type;

    public MerchantChatLinkTriggerPostBody(
            Long merchantId, MerchantChatLinkType typeEnum, String s) {
        this.merchantId = merchantId;
        this.type = typeEnum.type;
        this.carSpeech = s;
    }

    public enum MerchantChatLinkType {
        TYPE_MERCHANT_HOME("home"),
        TYPE_PACKAGE("package"),
        TYPE_EXAMPLE("example"),
        TYPE_CAR("car");
        private String type;

        MerchantChatLinkType(String type) {
            this.type = type;
        }
    }

}
