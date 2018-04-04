package com.hunliji.marrybiz.model.experience;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2017/12/20 0020.
 */

public class AdvOrder implements Parcelable {

    long id;
    String remark;
    @SerializedName("user_demand")
    AdvOrderUserDemand demand;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public AdvOrderUserDemand getDemand() {
        return demand;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public AdvOrder() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.remark);
        dest.writeParcelable(this.demand, flags);
    }

    protected AdvOrder(Parcel in) {
        this.id = in.readLong();
        this.remark = in.readString();
        this.demand = in.readParcelable(AdvOrderUserDemand.class.getClassLoader());
    }

    public static final Creator<AdvOrder> CREATOR = new Creator<AdvOrder>() {
        @Override
        public AdvOrder createFromParcel(Parcel source) {return new AdvOrder(source);}

        @Override
        public AdvOrder[] newArray(int size) {return new AdvOrder[size];}
    };
}
