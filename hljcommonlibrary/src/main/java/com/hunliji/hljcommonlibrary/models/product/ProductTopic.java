package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * 婚品专题model
 * Created by chen_bin on 2016/11/30 0030.
 */
public class ProductTopic implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "entity_id")
    long entityId;
    @SerializedName(value = "start_at")
    DateTime startAt;
    @SerializedName(value = "end_at")
    DateTime endAt;
    @SerializedName(value = "title")
    String title;
    @SerializedName(value = "desc")
    String desc;
    @SerializedName(value = "img_title")
    String imgTitle;
    @SerializedName(value = "goto_url")
    String gotoUrl;
    @SerializedName(value = "type")
    int type;
    @SerializedName(value = "hot_word_img")
    String hotWordImg;
    double price;

    public ProductTopic() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public DateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(DateTime startAt) {
        this.startAt = startAt;
    }

    public DateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(DateTime endAt) {
        this.endAt = endAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public String getHotWordImg() {
        return hotWordImg;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public String getGotoUrl() {
        return gotoUrl;
    }

    public void setGotoUrl(String gotoUrl) {
        this.gotoUrl = gotoUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.entityId);
        dest.writeSerializable(this.startAt);
        dest.writeSerializable(this.endAt);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.imgTitle);
        dest.writeString(this.gotoUrl);
        dest.writeInt(this.type);
        dest.writeString(this.hotWordImg);
        dest.writeDouble(this.price);
    }

    protected ProductTopic(Parcel in) {
        this.id = in.readLong();
        this.entityId = in.readLong();
        this.startAt = (DateTime) in.readSerializable();
        this.endAt = (DateTime) in.readSerializable();
        this.title = in.readString();
        this.desc = in.readString();
        this.imgTitle = in.readString();
        this.gotoUrl = in.readString();
        this.type = in.readInt();
        this.hotWordImg = in.readString();
        this.price = in.readDouble();
    }

    public static final Creator<ProductTopic> CREATOR = new Creator<ProductTopic>() {
        @Override
        public ProductTopic createFromParcel(Parcel source) {return new ProductTopic(source);}

        @Override
        public ProductTopic[] newArray(int size) {return new ProductTopic[size];}
    };
}