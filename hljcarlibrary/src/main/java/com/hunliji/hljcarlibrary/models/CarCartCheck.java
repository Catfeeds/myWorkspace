package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by Suncloud on 2015/10/15.
 */
public class CarCartCheck implements Parcelable {

    private long id;
    @SerializedName("show_num")
    private int showNum=-1;
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("show_price")
    private double showPrice;
    @SerializedName("is_published")
    private boolean isPublished;

    public CarCartCheck(){

    }

    public CarCartCheck(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            showNum=jsonObject.optInt("show_num", -1);
            marketPrice=jsonObject.optDouble("market_price", 0);
            showPrice=jsonObject.optDouble("show_price",0);
            isPublished=jsonObject.optInt("is_published")>0;
        }
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public int getShowNum() {
        return showNum;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.showNum);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.showPrice);
        dest.writeByte(this.isPublished ? (byte) 1 : (byte) 0);
    }

    protected CarCartCheck(Parcel in) {
        this.id = in.readLong();
        this.showNum = in.readInt();
        this.marketPrice = in.readDouble();
        this.showPrice = in.readDouble();
        this.isPublished = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CarCartCheck> CREATOR = new Parcelable
            .Creator<CarCartCheck>() {
        @Override
        public CarCartCheck createFromParcel(Parcel source) {return new CarCartCheck(source);}

        @Override
        public CarCartCheck[] newArray(int size) {return new CarCartCheck[size];}
    };
}
