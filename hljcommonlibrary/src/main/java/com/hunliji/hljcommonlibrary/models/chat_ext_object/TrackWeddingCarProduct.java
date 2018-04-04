package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Rule;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by jinxin on 2018/1/4 0004.
 */

public class TrackWeddingCarProduct implements Parcelable {

    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName(value = "cover_image")
    Photo coverImage;
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("show_price")
    private double showPrice;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "rule")
    private Rule rule;
    @SerializedName(value = "main_car")
    private String mainCar;//头车,主婚车
    @SerializedName(value = "sub_car")
    String subCar;//副婚车

    transient String showSubCarTitle;

    public TrackWeddingCarProduct(
            long id,
            String title,
            String mainCar,
            String subCar,
            Photo coverPath,
            double actualPrice,
            double marketPrice,
            double showPrice,
            Rule rule) {
        this.id = id;
        this.title = title;
        this.mainCar = mainCar;
        this.subCar = subCar;
        this.coverImage = coverPath;
        this.actualPrice = actualPrice;
        this.marketPrice = marketPrice;
        this.showPrice = showPrice;
        this.rule = rule;
    }

    public TrackWeddingCarProduct(WeddingCarProduct carProduct){
        if(carProduct != null){
            this.id = carProduct.getId();
            this.title = carProduct.getTitle();
            this.mainCar = carProduct.getMainCar();
            this.subCar = carProduct.getShowSubCarTitle();
            this.coverImage = carProduct.getCoverImage();
            this.actualPrice = carProduct.getActualPrice();
            this.marketPrice = carProduct.getMarketPrice();
            this.showPrice = carProduct.getShowPrice();
            this.rule = carProduct.getRule();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getMainCar() {
        return mainCar;
    }

    public void setMainCar(String mainCar) {
        this.mainCar = mainCar;
    }

    public String getSubCar() {
        return subCar;
    }

    public void setSubCar(String subCar) {
        this.subCar = subCar;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Photo getCoverImage() {
        return coverImage;
    }

    public void setShowSubCarTitle(String showSubCarTitle) {
        this.showSubCarTitle = showSubCarTitle;
    }

    public String getShowSubCarTitle() {
        if (CommonUtil.isEmpty(showSubCarTitle)) {
            String a = subCar.replace("/", "x");
            showSubCarTitle = a.replace("or", "/");
        }
        return showSubCarTitle;
    }

    public void setCoverImage(Photo coverImage) {
        this.coverImage = coverImage;
    }

    public TrackWeddingCarProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeParcelable(this.coverImage, flags);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.showPrice);
        dest.writeDouble(this.actualPrice);
        dest.writeParcelable(this.rule, flags);
        dest.writeString(this.mainCar);
        dest.writeString(this.subCar);
    }

    protected TrackWeddingCarProduct(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.coverImage = in.readParcelable(Photo.class.getClassLoader());
        this.marketPrice = in.readDouble();
        this.showPrice = in.readDouble();
        this.actualPrice = in.readDouble();
        this.rule = in.readParcelable(Rule.class.getClassLoader());
        this.mainCar = in.readString();
        this.subCar = in.readString();
    }

    public static final Creator<TrackWeddingCarProduct> CREATOR = new
            Creator<TrackWeddingCarProduct>() {
        @Override
        public TrackWeddingCarProduct createFromParcel(Parcel source) {
            return new TrackWeddingCarProduct(source);
        }

        @Override
        public TrackWeddingCarProduct[] newArray(int size) {return new TrackWeddingCarProduct[size];}
    };
}
