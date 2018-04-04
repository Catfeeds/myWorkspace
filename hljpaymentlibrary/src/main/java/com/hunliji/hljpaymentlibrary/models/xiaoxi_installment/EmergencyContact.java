package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/11/9 0009.
 */
public class EmergencyContact implements Parcelable {
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "mobile")
    private String mobile;
    @SerializedName(value = "type")
    private int type;

    public transient final static int TYPE_PARENT = 1;//父母
    public transient final static int TYPE_FRIEND = 2; //亲友
    public transient final static int TYPE_SPOUSE = 3; //配偶
    public transient final static int TYPE_COLLEAGUE = 4; //同事;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.mobile);
        dest.writeInt(this.type);
    }

    public EmergencyContact() {}

    protected EmergencyContact(Parcel in) {
        this.name = in.readString();
        this.mobile = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<EmergencyContact> CREATOR = new Parcelable
            .Creator<EmergencyContact>() {
        @Override
        public EmergencyContact createFromParcel(Parcel source) {
            return new EmergencyContact(source);
        }

        @Override
        public EmergencyContact[] newArray(int size) {return new EmergencyContact[size];}
    };
}
