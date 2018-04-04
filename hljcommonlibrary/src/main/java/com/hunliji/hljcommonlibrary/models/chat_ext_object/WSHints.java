package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by luohanlin on 2018/1/12.
 * 商家发过来的提示消息作为新增类型hints，之后的提示类消息hints与tips类型共用
 * 需要默认tips视图类型的消息归为tips，除此之外为hints
 */

public class WSHints implements Parcelable {

    public static final int ACTION_MERCHANT_SMART_REPLY = 0; // 商家智能接待

    private int action;
    private String title;
    private List<String> detail;

    public WSHints(int action, String title, List<String> detail) {
        this.action = action;
        this.title = title;
        this.detail = detail;
    }

    public int getAction() {
        return action;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDetail() {
        return detail;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeString(this.title);
        dest.writeStringList(this.detail);
    }

    protected WSHints(Parcel in) {
        this.action = in.readInt();
        this.title = in.readString();
        this.detail = in.createStringArrayList();
    }

    public static final Parcelable.Creator<WSHints> CREATOR = new Parcelable.Creator<WSHints>() {
        @Override
        public WSHints createFromParcel(Parcel source) {return new WSHints(source);}

        @Override
        public WSHints[] newArray(int size) {return new WSHints[size];}
    };
}
