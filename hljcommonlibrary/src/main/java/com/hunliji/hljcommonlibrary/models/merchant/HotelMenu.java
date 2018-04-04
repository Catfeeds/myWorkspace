package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by werther on 16/12/5.
 */

public class HotelMenu implements Parcelable {
    long id;
    double price;
    @SerializedName("price_unit")
    String priceUnit;
    String addition;
    String name;
    @SerializedName("service_fee")
    double serviceFee;
    @SerializedName("series")
    ArrayList<HotelMenuSet> hotelMenuSets;

    transient boolean isNoExpand = true;

    public void setNoExpand(boolean noExpand) {
        isNoExpand = noExpand;
    }

    public long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public String getAddition() {
        return addition;
    }

    public String getName() {
        return name;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public ArrayList<HotelMenuSet> getHotelMenuSets() {
        if(hotelMenuSets==null){
            hotelMenuSets=new ArrayList<>();
        }
        return hotelMenuSets;
    }

    public boolean isNoExpand() {
        return isNoExpand;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.price);
        dest.writeString(this.priceUnit);
        dest.writeString(this.addition);
        dest.writeString(this.name);
        dest.writeDouble(this.serviceFee);
        dest.writeTypedList(this.hotelMenuSets);
    }

    public HotelMenu() {}

    protected HotelMenu(Parcel in) {
        this.id = in.readLong();
        this.price = in.readDouble();
        this.priceUnit = in.readString();
        this.addition = in.readString();
        this.name = in.readString();
        this.serviceFee = in.readDouble();
        this.hotelMenuSets = in.createTypedArrayList(HotelMenuSet.CREATOR);
    }

    public static final Parcelable.Creator<HotelMenu> CREATOR = new Parcelable.Creator<HotelMenu>
            () {
        @Override
        public HotelMenu createFromParcel(Parcel source) {return new HotelMenu(source);}

        @Override
        public HotelMenu[] newArray(int size) {return new HotelMenu[size];}
    };
}
