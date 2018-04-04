package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luohanlin on 2017/10/17.
 */

public class WSTips implements Parcelable {
    public static final int ACTION_MERCHANT_APPOINTMENT_TIP = 0; // 商家不空闲，请预约

    // 随便定义的数字，由客户端自己插入信息
    public static final int ACTION_CONTACT_WORDS_TIP = 1423; //
    public static final int ACTION_APPOINTMENT_SUCCESS_TIP = 1424; // 预约成功的提示
    public static final int ACTION_COUPON_SUCCESS_TIP = 1425; // 成功领券提示

    public static final String[] ARRAY_CONTACT_FILTER_WORDS = new String[]{"微信", "威信", "维信",
            "WX", "wx", "VX", "vx", "QQ", "qq", "扣扣", "手机", "联系方式", "+", "热线"};

    private int action;
    private String title;
    private String detail;

    public WSTips(int action, String title, String detail) {
        this.action = action;
        this.title = title;
        this.detail = detail;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeString(this.title);
        dest.writeString(this.detail);
    }

    public WSTips() {}

    protected WSTips(Parcel in) {
        this.action = in.readInt();
        this.title = in.readString();
        this.detail = in.readString();
    }

    public static final Parcelable.Creator<WSTips> CREATOR = new Parcelable.Creator<WSTips>() {
        @Override
        public WSTips createFromParcel(Parcel source) {return new WSTips(source);}

        @Override
        public WSTips[] newArray(int size) {return new WSTips[size];}
    };
}
