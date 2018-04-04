package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by mo_yu on 2016/9/6.
 */
public class PosterData implements Parcelable{

    long id;
    String name;
    JsonObject floors;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JsonObject getFloors() {
        return floors;
    }

    public void setFloors(JsonObject floors) {
        this.floors = floors;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.floors.toString());
    }

    public PosterData() {}

    protected PosterData(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.floors = (JsonObject) new JsonParser().parse(in.readString());
    }

    public static final Creator<PosterData> CREATOR = new Creator<PosterData>() {
        @Override
        public PosterData createFromParcel(Parcel source) {return new PosterData(source);}

        @Override
        public PosterData[] newArray(int size) {return new PosterData[size];}
    };
}
