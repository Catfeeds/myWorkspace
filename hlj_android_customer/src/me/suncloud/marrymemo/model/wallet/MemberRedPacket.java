package me.suncloud.marrymemo.model.wallet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/10/18.会员红包
 */

public class MemberRedPacket implements Parcelable {

    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "group_name")
    String groupName;
    @SerializedName(value = "total_money")
    double totalMoney;
    @SerializedName(value = "redemption_code")
    String redemptionCode; //红包码

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public MemberRedPacket() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.groupName);
        dest.writeDouble(this.totalMoney);
        dest.writeString(this.redemptionCode);
    }

    protected MemberRedPacket(Parcel in) {
        this.id = in.readLong();
        this.groupName = in.readString();
        this.totalMoney = in.readDouble();
        this.redemptionCode = in.readString();
    }

    public static final Creator<MemberRedPacket> CREATOR = new Creator<MemberRedPacket>() {
        @Override
        public MemberRedPacket createFromParcel(Parcel source) {return new MemberRedPacket(source);}

        @Override
        public MemberRedPacket[] newArray(int size) {return new MemberRedPacket[size];}
    };
}
