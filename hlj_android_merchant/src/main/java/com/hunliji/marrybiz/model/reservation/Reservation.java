package com.hunliji.marrybiz.model.reservation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import org.joda.time.DateTime;

/**
 * 预约
 * Created by jinxin on 2017/5/22 0022.
 */

public class Reservation implements Parcelable {

    long id;
    @SerializedName(value = "arrive_status")
    int arriveStatus;//到店状态 0 未处理 1到店 2未到店
    DateTime date;
    boolean deleted;
    @SerializedName(value = "from_id")
    long formId;//预约来源id
    @SerializedName(value = "from_type")
    int formType;//预约来源 1 商家 2 酒店 3活动 4 案例 5 婚礼纪 6微店
    @SerializedName(value = "fullname")
    String fullName;
    @SerializedName(value = "go_time")
    DateTime goTime;//预约时间
    @SerializedName(value = "look_at")
    DateTime lookAt;//到店时间
    @SerializedName(value = "created_at")
    DateTime createdAt;//提交时间
    @SerializedName(value = "merchant_id")
    long merchantId;
    Author user;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "phone_num")
    String phoneNum;//带*的手机号
    @SerializedName(value = "phone")
    String phone;//不带*的手机号
    @SerializedName(value = "is_look")
    boolean isLook;//是否查看

    public int getArriveStatus() {
        return arriveStatus;
    }

    public void setArriveStatus(int arriveStatus) {
        this.arriveStatus = arriveStatus;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public int getFormType() {
        return formType;
    }

    public void setFormType(int formType) {
        this.formType = formType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public DateTime getGoTime() {
        return goTime;
    }

    public void setGoTime(DateTime goTime) {
        this.goTime = goTime;
    }

    public DateTime getLookAt() {
        return lookAt;
    }

    public void setLookAt(DateTime lookAt) {
        this.lookAt = lookAt;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public Author getUser() {
        return user;
    }

    public void setUser(Author user) {
        this.user = user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }

    public Reservation() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.arriveStatus);
        dest.writeSerializable(this.date);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeLong(this.formId);
        dest.writeInt(this.formType);
        dest.writeString(this.fullName);
        dest.writeSerializable(this.goTime);
        dest.writeSerializable(this.lookAt);
        dest.writeSerializable(this.createdAt);
        dest.writeLong(this.merchantId);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.userId);
        dest.writeString(this.phoneNum);
        dest.writeString(this.phone);
        dest.writeByte(this.isLook ? (byte) 1 : (byte) 0);
    }

    protected Reservation(Parcel in) {
        this.id = in.readLong();
        this.arriveStatus = in.readInt();
        this.date = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.formId = in.readLong();
        this.formType = in.readInt();
        this.fullName = in.readString();
        this.goTime = (DateTime) in.readSerializable();
        this.lookAt = (DateTime) in.readSerializable();
        this.createdAt = (DateTime) in.readSerializable();
        this.merchantId = in.readLong();
        this.user = in.readParcelable(Author.class.getClassLoader());
        this.userId = in.readLong();
        this.phoneNum = in.readString();
        this.phone = in.readString();
        this.isLook = in.readByte() != 0;
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel source) {return new Reservation(source);}

        @Override
        public Reservation[] newArray(int size) {return new Reservation[size];}
    };
}
