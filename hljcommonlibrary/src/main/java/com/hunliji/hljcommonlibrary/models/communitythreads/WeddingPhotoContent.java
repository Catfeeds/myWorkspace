package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * Created by mo_yu on 17/5/4.晒婚纱照内容
 */
public class WeddingPhotoContent implements Parcelable {

    long id;
    String city;
    @SerializedName(value = "cover")
    Photo photo;//封面
    Merchant merchant;
    String preface;
    double price;
    @SerializedName("watch_count")
    int watchCount;
    @SerializedName(value = "unrecorded_merchant_name")
    String unrecordedMerchantName;

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

    public Photo getPhoto() {
        if (photo == null){
            photo = new Photo();
        }
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public String getUnrecordedMerchantName() {
        return unrecordedMerchantName;
    }

    public void setUnrecordedMerchantName(String unrecordedMerchantName) {
        this.unrecordedMerchantName = unrecordedMerchantName;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.city);
        dest.writeParcelable(this.photo, flags);
        dest.writeParcelable(this.merchant, flags);
        dest.writeString(this.preface);
        dest.writeDouble(this.price);
        dest.writeInt(this.watchCount);
        dest.writeString(this.unrecordedMerchantName);
    }

    public WeddingPhotoContent() {}

    protected WeddingPhotoContent(Parcel in) {
        this.id = in.readLong();
        this.city = in.readString();
        this.photo = in.readParcelable(Photo.class.getClassLoader());
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.preface = in.readString();
        this.price = in.readDouble();
        this.watchCount = in.readInt();
        this.unrecordedMerchantName = in.readString();
    }

    public static final Parcelable.Creator<WeddingPhotoContent> CREATOR = new Parcelable
            .Creator<WeddingPhotoContent>() {
        @Override
        public WeddingPhotoContent createFromParcel(Parcel source) {
            return new WeddingPhotoContent(source);
        }

        @Override
        public WeddingPhotoContent[] newArray(int size) {return new WeddingPhotoContent[size];}
    };
}
