package me.suncloud.marrymemo.model.merchant.wrappers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/5/23 0023.
 * 聊天界面增加了预约按钮之后，预约接口的参数减少两个
 * phone_num和fullName变为可选参数
 */
public class AppointmentPostBody {
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "fullname")
    private String fullName; // 可选
    @SerializedName(value = "phone_num")
    private String phone; // 可选
    @SerializedName(value = "from_type")
    private int fromType;
    @SerializedName(value = "from_id")
    private long formId;

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }
}
