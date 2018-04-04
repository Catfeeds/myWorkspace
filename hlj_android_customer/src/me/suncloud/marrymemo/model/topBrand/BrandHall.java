package me.suncloud.marrymemo.model.topBrand;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;

import org.joda.time.DateTime;

/**
 * 品牌馆用到的商家model
 * Created by jinxin on 2016/11/15 0015.
 */

public class BrandHall implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "group_id")
    long groupId;
    @SerializedName(value = "content_id")
    long contentId;
    @SerializedName(value = "title")
    String title;
    @SerializedName(value = "img")
    String img;
    @SerializedName(value = "weight")
    int weight;
    @SerializedName(value = "city_code")
    long cityCode;
    @SerializedName(value = "params")
    JsonElement params;
    @SerializedName(value = "params_value")
    String paramsValue;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "deleted")
    boolean deleted;
    @SerializedName(value = "extra_data")
    Merchant merchant;
    transient String comments; //标签
    transient int label = -1; //最大牌、最小资、超人气

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getCityCode() {
        return cityCode;
    }

    public void setCityCode(long cityCode) {
        this.cityCode = cityCode;
    }

    public JsonElement getParams() {
        return params;
    }

    public String getParamsValue() {
        return paramsValue;
    }

    public void setParamsValue(String paramsValue) {
        this.paramsValue = paramsValue;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getComments() {
        if (comments != null) {
            return comments;
        }
        try {
            comments = params.getAsJsonObject()
                    .get("comments")
                    .getAsString();
        } catch (Exception e) {
            comments = "";
        }
        return comments;
    }

    public int getLabel() {
        if (label > -1) {
            return label;
        }
        try {
            label = params.getAsJsonObject()
                    .get("label")
                    .getAsInt();
        } catch (Exception e) {
            label = 0;
        }
        return label;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.groupId);
        dest.writeLong(this.contentId);
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeInt(this.weight);
        dest.writeLong(this.cityCode);
        dest.writeString(this.paramsValue);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.merchant, flags);
    }

    public BrandHall() {}

    protected BrandHall(Parcel in) {
        this.id = in.readLong();
        this.groupId = in.readLong();
        this.contentId = in.readLong();
        this.title = in.readString();
        this.img = in.readString();
        this.weight = in.readInt();
        this.cityCode = in.readLong();
        this.paramsValue = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Parcelable.Creator<BrandHall> CREATOR = new Parcelable.Creator<BrandHall>
            () {
        @Override
        public BrandHall createFromParcel(Parcel source) {return new BrandHall(source);}

        @Override
        public BrandHall[] newArray(int size) {return new BrandHall[size];}
    };
}
