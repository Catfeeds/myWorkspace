package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by werther on 16/12/5.
 */

public class HotelMenuSet implements Parcelable {
    long id;
    String name;
    @SerializedName(value = "list_names", alternate = "food_name")
    ArrayList<String> listNames;
//    @SerializedName("hotel_menu_series")
//    ArrayList<HotelMenuSetItem> hotelMenuSetItems;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getListNames() {
        if(listNames==null){
            listNames=new ArrayList<>();
        }
        return listNames;
    }

    public ArrayList<HotelMenuSetItem> getHotelMenuSetItems() {
        return null;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeStringList(this.listNames);
//        dest.writeTypedList(this.hotelMenuSetItems);
    }

    public HotelMenuSet() {}

    protected HotelMenuSet(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.listNames = in.createStringArrayList();
//        this.hotelMenuSetItems = in.createTypedArrayList(HotelMenuSetItem.CREATOR);
    }

    public static final Parcelable.Creator<HotelMenuSet> CREATOR = new Parcelable
            .Creator<HotelMenuSet>() {
        @Override
        public HotelMenuSet createFromParcel(Parcel source) {return new HotelMenuSet(source);}

        @Override
        public HotelMenuSet[] newArray(int size) {return new HotelMenuSet[size];}
    };
}
