package com.hunliji.hljcommonlibrary.models.questionanswer;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/10/24.
 */

public class QaVipMerchant {
    @SerializedName("answer_count")
    private int answerCount;
    private String id;
    @SerializedName("is_collected")
    private boolean isCollected;
    @SerializedName("logo_path")
    private String logoPath;
    private String name;
    @SerializedName("up_count")
    private int upCount;
    @SerializedName("cpm")
    private String cpm;
    @SerializedName("user_id")
    private long userId;//商家用户ID
    @SerializedName("hot_answer_count")
    private int hotAnswerCount;//优质回答数
    @SerializedName("property_id")
    private long propertyId;//商家类别id
    @SerializedName("property_name")
    private String propertyName;//商家类别名称

    public int getAnswerCount() {
        return answerCount;
    }

    public long getId() {
        long idL = 0;
        try {
            idL = Long.valueOf(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idL;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getName() {
        return name;
    }

    public int getUpCount() {
        return upCount;
    }

    public String getCpm() {
        return cpm;
    }

    public long getUserId() {
        return userId;
    }

    public int getHotAnswerCount() {
        return hotAnswerCount;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
