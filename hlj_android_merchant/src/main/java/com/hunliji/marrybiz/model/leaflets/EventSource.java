package com.hunliji.marrybiz.model.leaflets;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;

import org.joda.time.DateTime;

/**
 * 活动微传单model
 * Created by jinxin on 2017/5/24 0024.
 */

public class EventSource implements Parcelable {

    long id;
    @SerializedName(value = "merchant_id")
    long merchantId;
    String address;
    @SerializedName(value = "sub_title1")
    String subTitle1;
    @SerializedName(value = "sub_title2")
    String subTitle2;
    int status;//0待审核 1审核通过 2审核不通过 3待定
    @SerializedName(value = "status_en")
    String statusEn;//ongoing 进行中 finished已结束 deleted已删除
    String title;
    @SerializedName(value = "finder_activity")
    EventInfo finderActivity;
    @SerializedName(value = "user")
    Author author;
    int wap;//1 WAP 0 APP 2 可疑报名 3.外部平台报名 4外部平台报名（微信）
    String tel;
    @SerializedName(value = "created_at")
    DateTime createAt;//报名时间
    String link;
    @SerializedName(value = "realname")
    String realName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubTitle1() {
        return subTitle1;
    }

    public void setSubTitle1(String subTitle1) {
        this.subTitle1 = subTitle1;
    }

    public String getSubTitle2() {
        return subTitle2;
    }

    public void setSubTitle2(String subTitle2) {
        this.subTitle2 = subTitle2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusEn() {
        return statusEn;
    }

    public void setStatusEn(String statusEn) {
        this.statusEn = statusEn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventInfo getFinderActivity() {
        return finderActivity;
    }

    public void setFinderActivity(EventInfo finderActivity) {
        this.finderActivity = finderActivity;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getWap() {
        return wap;
    }

    public void setWap(int wap) {
        this.wap = wap;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(DateTime createAt) {
        this.createAt = createAt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRealname() {
        return realName;
    }

    public void setRealname(String realname) {
        this.realName = realname;
    }

    public EventSource() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeString(this.address);
        dest.writeString(this.subTitle1);
        dest.writeString(this.subTitle2);
        dest.writeInt(this.status);
        dest.writeString(this.statusEn);
        dest.writeString(this.title);
        dest.writeParcelable(this.finderActivity, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeInt(this.wap);
        dest.writeString(this.tel);
        dest.writeSerializable(this.createAt);
        dest.writeString(this.link);
        dest.writeString(this.realName);
    }

    protected EventSource(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.address = in.readString();
        this.subTitle1 = in.readString();
        this.subTitle2 = in.readString();
        this.status = in.readInt();
        this.statusEn = in.readString();
        this.title = in.readString();
        this.finderActivity = in.readParcelable(EventInfo.class.getClassLoader());
        this.author = in.readParcelable(Author.class.getClassLoader());
        this.wap = in.readInt();
        this.tel = in.readString();
        this.createAt = (DateTime) in.readSerializable();
        this.link = in.readString();
        this.realName = in.readString();
    }

    public static final Creator<EventSource> CREATOR = new Creator<EventSource>() {
        @Override
        public EventSource createFromParcel(Parcel source) {return new EventSource(source);}

        @Override
        public EventSource[] newArray(int size) {return new EventSource[size];}
    };
}
