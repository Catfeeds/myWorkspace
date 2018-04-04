package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;

/**
 * Created by wangtao on 2017/8/22.
 */

public class WSTrack implements Parcelable {
    public static final int WORK = 1;
    public static final int CASE = 2;
    public static final int MERCHANT = 3;
    public static final int SHOW_WINDOW = 4;
    public static final int PRODUCT = 5;
    public static final int WEDDING_CAR = 6;

    @SerializedName("detail")
    private String detail;
    @SerializedName("action")
    private int action;
    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("meal")
    private TrackWork work;
    @SerializedName("case")
    private TrackCase aCase;
    @SerializedName("product")
    private TrackProduct product;
    @SerializedName("merchant")
    private TrackMerchant merchant;
    @SerializedName("car_product")
    private TrackWeddingCarProduct carProduct;

    public int getAction() {
        return action;
    }

    public WSTrack(String detail) {
        this.detail = detail;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setWork(TrackWork work) {
        this.work = work;
    }

    public void setCase(TrackCase aCase) {
        this.aCase = aCase;
    }

    public void setProduct(TrackProduct product) {
        this.product = product;
    }

    public void setMerchant(TrackMerchant merchant) {
        this.merchant = merchant;
    }

    public String getDetail() {
        return detail;
    }

    public String getImagePath() {
        return imagePath;
    }

    public TrackWork getWork() {
        return work;
    }

    public TrackCase getCase() {
        return aCase;
    }

    public TrackProduct getProduct() {
        return product;
    }

    public TrackMerchant getMerchant() {
        return merchant;
    }

    public TrackWeddingCarProduct getCarProduct() {
        return carProduct;
    }

    public void setCarProduct(TrackWeddingCarProduct carProduct) {
        this.carProduct = carProduct;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.detail);
        dest.writeInt(this.action);
        dest.writeString(this.imagePath);
        dest.writeParcelable(this.work, flags);
        dest.writeParcelable(this.aCase, flags);
        dest.writeParcelable(this.product, flags);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.carProduct, flags);
    }

    protected WSTrack(Parcel in) {
        this.detail = in.readString();
        this.action = in.readInt();
        this.imagePath = in.readString();
        this.work = in.readParcelable(TrackWork.class.getClassLoader());
        this.aCase = in.readParcelable(TrackCase.class.getClassLoader());
        this.product = in.readParcelable(TrackProduct.class.getClassLoader());
        this.merchant = in.readParcelable(TrackMerchant.class.getClassLoader());
        this.carProduct = in.readParcelable(WeddingCarProduct.class.getClassLoader());
    }

    public static final Creator<WSTrack> CREATOR = new Creator<WSTrack>() {
        @Override
        public WSTrack createFromParcel(Parcel source) {return new WSTrack(source);}

        @Override
        public WSTrack[] newArray(int size) {return new WSTrack[size];}
    };
}
