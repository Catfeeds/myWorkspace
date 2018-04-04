package me.suncloud.marrymemo.model.topBrand;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 品牌馆model
 * Created by jixnin on 2016/11/15 0015.
 */

public class WeddingBrand implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "top_img")
    String topImg;
    @SerializedName(value = "wedding_photo_title")
    String weddingPhotoTitle;
    @SerializedName(value = "wedding_planner_title")
    String weddingPlannerTitle;
    @SerializedName(value = "wedding_photo_merchants")
    List<BrandHall> weddingPhotoMerchants;
    @SerializedName(value = "wedding_planner_merchants")
    List<BrandHall> weddingPlannerMerchants;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<BrandHall> getWeddingPhotoMerchants() {
        return weddingPhotoMerchants;
    }

    public void setWeddingPhotoMerchants(List<BrandHall> weddingPhotoMerchants) {
        this.weddingPhotoMerchants = weddingPhotoMerchants;
    }

    public List<BrandHall> getWeddingPlannerMerchants() {
        return weddingPlannerMerchants;
    }

    public void setWeddingPlannerMerchants(List<BrandHall> weddingPlannerMerchants) {
        this.weddingPlannerMerchants = weddingPlannerMerchants;
    }

    public String getWeddingPlannerTitle() {
        return weddingPlannerTitle;
    }

    public void setWeddingPlannerTitle(String weddingPlannerTitle) {
        this.weddingPlannerTitle = weddingPlannerTitle;
    }

    public String getWeddingPhotoTitle() {
        return weddingPhotoTitle;
    }

    public void setWeddingPhotoTitle(String weddingPhotoTitle) {
        this.weddingPhotoTitle = weddingPhotoTitle;
    }

    public String getTopImg() {
        return topImg;
    }

    public void setTopImg(String topImg) {
        this.topImg = topImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WeddingBrand() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.topImg);
        dest.writeString(this.weddingPhotoTitle);
        dest.writeString(this.weddingPlannerTitle);
        dest.writeTypedList(this.weddingPhotoMerchants);
        dest.writeTypedList(this.weddingPlannerMerchants);
    }

    protected WeddingBrand(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.topImg = in.readString();
        this.weddingPhotoTitle = in.readString();
        this.weddingPlannerTitle = in.readString();
        this.weddingPhotoMerchants = in.createTypedArrayList(BrandHall.CREATOR);
        this.weddingPlannerMerchants = in.createTypedArrayList(BrandHall.CREATOR);
    }

    public static final Creator<WeddingBrand> CREATOR = new Creator<WeddingBrand>() {
        @Override
        public WeddingBrand createFromParcel(Parcel source) {return new WeddingBrand(source);}

        @Override
        public WeddingBrand[] newArray(int size) {return new WeddingBrand[size];}
    };
}
