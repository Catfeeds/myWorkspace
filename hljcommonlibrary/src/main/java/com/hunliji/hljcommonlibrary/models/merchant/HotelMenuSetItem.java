package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by werther on 16/12/5.
 */

public class HotelMenuSetItem implements Parcelable {
    long id;
    String name;
    @SerializedName("list_names")
    ArrayList<String> listNames;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getListNames() {
        return listNames;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeStringList(this.listNames);
    }

    public HotelMenuSetItem() {}

    protected HotelMenuSetItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.listNames = in.createStringArrayList();
    }

    public static final Parcelable.Creator<HotelMenuSetItem> CREATOR = new Parcelable
            .Creator<HotelMenuSetItem>() {
        @Override
        public HotelMenuSetItem createFromParcel(Parcel source) {return new HotelMenuSetItem
                (source);}

        @Override
        public HotelMenuSetItem[] newArray(int size) {return new HotelMenuSetItem[size];}
    };
}
