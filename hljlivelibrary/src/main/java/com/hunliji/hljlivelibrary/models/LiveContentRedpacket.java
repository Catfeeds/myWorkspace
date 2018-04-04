package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljlivelibrary.R;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/11/6.
 */

public class LiveContentRedpacket extends LiveContent {
    private long id;
    @SerializedName("group_name")
    private String groupName;
    @SerializedName("redemption_code")
    private String redemptionCode;
    @SerializedName("total_money")
    private double totalMoney;

    public static LiveContent parseLiveContentRedpacket(String contentStr) {
        LiveContentRedpacket liveContent = GsonUtil.getGsonInstance()
                .fromJson(contentStr, LiveContentRedpacket.class);
        return liveContent;
    }

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeString(this.groupName);
        dest.writeString(this.redemptionCode);
        dest.writeDouble(this.totalMoney);
    }

    public LiveContentRedpacket() {}

    protected LiveContentRedpacket(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.groupName = in.readString();
        this.redemptionCode = in.readString();
        this.totalMoney = in.readDouble();
    }

    public static final Creator<LiveContentRedpacket> CREATOR = new Creator<LiveContentRedpacket>
            () {
        @Override
        public LiveContentRedpacket createFromParcel(Parcel source) {
            return new LiveContentRedpacket(source);
        }

        @Override
        public LiveContentRedpacket[] newArray(int size) {return new LiveContentRedpacket[size];}
    };
}
