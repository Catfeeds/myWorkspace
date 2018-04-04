package com.hunliji.marrybiz.model.weddingcar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 婚车保险
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarInsurance implements Parcelable {

    @SerializedName("bride_identity")
    String brideIdentity;//新娘身份证号
    @SerializedName("bride_name")
    String brideName;//新娘
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("groom_identity")
    String groomIdentity;//新郎身份证
    @SerializedName("groom_name")
    String groomName;//新郎
    long id;
    @SerializedName("order_id")
    long orderId;
    int type;

    public String getBrideIdentity() {
        return brideIdentity;
    }

    public void setBrideIdentity(String brideIdentity) {
        this.brideIdentity = brideIdentity;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroomIdentity() {
        return groomIdentity;
    }

    public void setGroomIdentity(String groomIdentity) {
        this.groomIdentity = groomIdentity;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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
        dest.writeString(this.brideIdentity);
        dest.writeString(this.brideName);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeString(this.groomIdentity);
        dest.writeString(this.groomName);
        dest.writeLong(this.id);
        dest.writeLong(this.orderId);
        dest.writeInt(this.type);
    }

    public WeddingCarInsurance() {}

    protected WeddingCarInsurance(Parcel in) {
        this.brideIdentity = in.readString();
        this.brideName = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.groomIdentity = in.readString();
        this.groomName = in.readString();
        this.id = in.readLong();
        this.orderId = in.readLong();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<WeddingCarInsurance> CREATOR = new Parcelable
            .Creator<WeddingCarInsurance>() {
        @Override
        public WeddingCarInsurance createFromParcel(Parcel source) {
            return new WeddingCarInsurance(source);
        }

        @Override
        public WeddingCarInsurance[] newArray(int size) {return new WeddingCarInsurance[size];}
    };
}
