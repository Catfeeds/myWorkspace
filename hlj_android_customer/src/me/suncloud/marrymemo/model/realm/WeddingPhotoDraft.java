package me.suncloud.marrymemo.model.realm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mo_yu on 2017/5/5.晒婚纱照草稿
 */

public class WeddingPhotoDraft extends RealmObject implements Parcelable {
    @PrimaryKey
    private long userId;
    private long id;
    @SerializedName("city")
    private String city;//用户输入的城市，例如杭州
    private RealmJsonPic cover;//封面
    @SerializedName("merchant_id")
    private Long merchantId;//用户选择的商家
    private String merchantJson;//商家信息
    private String preface;//开头的描述
    private String title;//婚纱照标题
    @SerializedName("unrecorded_merchant_name")
    private String unrecordedMerchantName;//用户输入的商家名字
    @SerializedName("wedding_photo_items")
    private RealmList<WeddingPhotoItemDraft> weddingPhotoItems;//婚纱照

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public RealmJsonPic getCover() {
        return cover;
    }

    public void setCover(RealmJsonPic cover) {
        this.cover = cover;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnrecordedMerchantName() {
        return unrecordedMerchantName;
    }

    public void setUnrecordedMerchantName(String unrecordedMerchantName) {
        this.unrecordedMerchantName = unrecordedMerchantName;
    }

    public RealmList<WeddingPhotoItemDraft> getWeddingPhotoItems() {
        return weddingPhotoItems;
    }

    public void setWeddingPhotoItems(RealmList<WeddingPhotoItemDraft> weddingPhotoItems) {
        this.weddingPhotoItems = weddingPhotoItems;
    }

    public String getMerchantJson() {
        return merchantJson;
    }

    public void setMerchantJson(String merchantJson) {
        this.merchantJson = merchantJson;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeLong(this.id);
        dest.writeString(this.city);
        dest.writeParcelable(this.cover, flags);
        dest.writeValue(this.merchantId);
        dest.writeString(this.merchantJson);
        dest.writeString(this.preface);
        dest.writeString(this.title);
        dest.writeString(this.unrecordedMerchantName);
        dest.writeTypedList(new ArrayList<>(this.weddingPhotoItems));
    }

    public WeddingPhotoDraft() {}

    protected WeddingPhotoDraft(Parcel in) {
        this.userId = in.readLong();
        this.id = in.readLong();
        this.city = in.readString();
        this.cover = in.readParcelable(RealmJsonPic.class.getClassLoader());
        this.merchantId = (Long) in.readValue(Long.class.getClassLoader());
        this.merchantJson = in.readString();
        this.preface = in.readString();
        this.title = in.readString();
        this.unrecordedMerchantName = in.readString();
        ArrayList<WeddingPhotoItemDraft> list = in.createTypedArrayList(WeddingPhotoItemDraft
                .CREATOR);
        this.weddingPhotoItems = new RealmList<>();
        if (list != null) {
            this.weddingPhotoItems.addAll(list);
        }
    }

    public static final Creator<WeddingPhotoDraft> CREATOR = new Creator<WeddingPhotoDraft>() {
        @Override
        public WeddingPhotoDraft createFromParcel(Parcel source) {
            return new WeddingPhotoDraft(source);
        }

        @Override
        public WeddingPhotoDraft[] newArray(int size) {return new WeddingPhotoDraft[size];}
    };
}
