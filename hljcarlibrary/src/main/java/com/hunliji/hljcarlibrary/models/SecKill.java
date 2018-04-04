package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import butterknife.BindView;

/**
 * Created by mo_yu on 2018/1/3.秒杀
 */

public class SecKill implements Parcelable {

    long id;
    @SerializedName("city_code")
    long cityCode;
    @SerializedName("content_id")
    long contentId;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("deleted")
    boolean deleted;
    @SerializedName("group_id")
    long groupId;
    String img;//封面图
    String title;//标题
    @SerializedName("updated_at")
    DateTime updatedAt;
    @SerializedName("extra_data")
    WeddingCarProduct extraData;
    SecKillParams params;

    public long getId() {
        return id;
    }

    public long getCityCode() {
        return cityCode;
    }

    public long getContentId() {
        return contentId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public WeddingCarProduct getExtraData() {
        return extraData;
    }

    public SecKillParams getParams() {
        return params;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.cityCode);
        dest.writeLong(this.contentId);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeLong(this.groupId);
        dest.writeString(this.img);
        dest.writeString(this.title);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        dest.writeParcelable(this.extraData, flags);
        dest.writeParcelable(this.params, flags);
    }

    public SecKill() {}

    protected SecKill(Parcel in) {
        this.id = in.readLong();
        this.cityCode = in.readLong();
        this.contentId = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.groupId = in.readLong();
        this.img = in.readString();
        this.title = in.readString();
        this.updatedAt  = HljTimeUtils.readDateTimeToParcel(in);
        this.extraData = in.readParcelable(WeddingCarProduct.class.getClassLoader());
        this.params = in.readParcelable(SecKillParams.class.getClassLoader());
    }

    public static final Parcelable.Creator<SecKill> CREATOR = new Parcelable.Creator<SecKill>() {
        @Override
        public SecKill createFromParcel(Parcel source) {return new SecKill(source);}

        @Override
        public SecKill[] newArray(int size) {return new SecKill[size];}
    };
}
