package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;

/**
 * Created by werther on 16/12/5.
 * 酒店
 */

public class Hotel implements Parcelable {
    long id;
    @SerializedName("city_name")
    String cityName;
    String area;
    String kind;
    @SerializedName("price_start")
    double priceStart;
    @SerializedName("price_end")
    double priceEnd;
    @SerializedName("table_num")
    int tableNum;
    @SerializedName("table_min")
    int tableMin;
    @SerializedName("table_max")
    int tableMax;

    @SerializedName("hotel_halls")
    ArrayList<HotelHall> hotelHalls;
    @SerializedName("hotel_menus")
    ArrayList<HotelMenu> hotelMenus;
    @SerializedName("is_adv")
    boolean isAdv;

    public long getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getArea() {
        return area;
    }

    public String getKind() {
        return kind;
    }

    public double getPriceStart() {
        return priceStart;
    }

    public double getPriceEnd() {
        return priceEnd;
    }

    public int getTableNum() {
        return tableNum;
    }

    public int getTableMin() {
        return tableMin;
    }

    public int getTableMax() {
        return tableMax;
    }

    public String getTableStr() {
        if (tableMin < tableMax) {
            return TextUtils.concat(CommonUtil.formatDouble2String(tableMin),
                    "-",
                    CommonUtil.formatDouble2String(tableMax))
                    .toString();
        }
        return CommonUtil.formatDouble2String(tableMax);
    }

    public ArrayList<HotelHall> getHotelHalls() {
        return hotelHalls;
    }

    public ArrayList<HotelMenu> getHotelMenus() {
        return hotelMenus;
    }

    public boolean isAdv() {
        return isAdv;
    }

    public String getPriceStr() {
        if (priceStart < priceEnd) {
            return TextUtils.concat(CommonUtil.formatDouble2String(priceStart),
                    "-",
                    CommonUtil.formatDouble2String(priceEnd))
                    .toString();
        }
        return CommonUtil.formatDouble2String(priceEnd);
    }

    public Hotel() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.cityName);
        dest.writeString(this.area);
        dest.writeString(this.kind);
        dest.writeDouble(this.priceStart);
        dest.writeDouble(this.priceEnd);
        dest.writeInt(this.tableNum);
        dest.writeInt(this.tableMin);
        dest.writeInt(this.tableMax);
        dest.writeTypedList(this.hotelHalls);
        dest.writeTypedList(this.hotelMenus);
        dest.writeByte(this.isAdv ? (byte) 1 : (byte) 0);
    }

    protected Hotel(Parcel in) {
        this.id = in.readLong();
        this.cityName = in.readString();
        this.area = in.readString();
        this.kind = in.readString();
        this.priceStart = in.readDouble();
        this.priceEnd = in.readDouble();
        this.tableNum = in.readInt();
        this.tableMin = in.readInt();
        this.tableMax = in.readInt();
        this.hotelHalls = in.createTypedArrayList(HotelHall.CREATOR);
        this.hotelMenus = in.createTypedArrayList(HotelMenu.CREATOR);
        this.isAdv = in.readByte() != 0;
    }

    public static final Creator<Hotel> CREATOR = new Creator<Hotel>() {
        @Override
        public Hotel createFromParcel(Parcel source) {return new Hotel(source);}

        @Override
        public Hotel[] newArray(int size) {return new Hotel[size];}
    };
}
