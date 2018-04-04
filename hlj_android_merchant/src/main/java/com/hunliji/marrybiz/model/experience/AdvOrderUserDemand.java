package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by luohanlin on 2018/3/22.
 * 推荐客资中的用户的分类备注
 */

public class AdvOrderUserDemand implements Parcelable {
    @SerializedName("wedding_day")
    DateTime weddingDay;
    @SerializedName("wedding_budget")
    double weddingBudget;
    @SerializedName("user_hobby")
    String userHobby;

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public double getWeddingBudget() {
        return weddingBudget;
    }

    public String getUserHobby() {
        return userHobby;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.weddingDay);
        dest.writeDouble(this.weddingBudget);
        dest.writeString(this.userHobby);
    }

    public AdvOrderUserDemand() {}

    protected AdvOrderUserDemand(Parcel in) {
        this.weddingDay = (DateTime) in.readSerializable();
        this.weddingBudget = in.readDouble();
        this.userHobby = in.readString();
    }

    public static final Creator<AdvOrderUserDemand> CREATOR = new Creator<AdvOrderUserDemand>() {
        @Override
        public AdvOrderUserDemand createFromParcel(Parcel source) {
            return new AdvOrderUserDemand(source);
        }

        @Override
        public AdvOrderUserDemand[] newArray(int size) {return new AdvOrderUserDemand[size];}
    };
}
