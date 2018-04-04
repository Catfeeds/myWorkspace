package me.suncloud.marrymemo.model.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by mo_yu on 2017/9/6.旅拍限时团购列表
 */

public class LimitBuyList implements Parcelable {

    private String city;
    private List<LimitBuyContent> content;
    private String status;

    public String getCity() {
        return city;
    }

    public List<LimitBuyContent> getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeTypedList(this.content);
        dest.writeString(this.status);
    }

    public LimitBuyList() {}

    protected LimitBuyList(Parcel in) {
        this.city = in.readString();
        this.content = in.createTypedArrayList(LimitBuyContent.CREATOR);
        this.status = in.readString();
    }

    public static final Creator<LimitBuyList> CREATOR = new Creator<LimitBuyList>() {
        @Override
        public LimitBuyList createFromParcel(Parcel source) {return new LimitBuyList(source);}

        @Override
        public LimitBuyList[] newArray(int size) {return new LimitBuyList[size];}
    };
}
