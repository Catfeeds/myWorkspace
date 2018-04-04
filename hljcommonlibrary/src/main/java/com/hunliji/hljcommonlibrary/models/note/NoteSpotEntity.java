package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/6/27 0027.
 */
public class NoteSpotEntity implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "type")
    private String type;

    public transient static final String TYPE_MERCHANT = "Merchant";
    public transient static final String TYPE_SHOP_PRODUCT = "ShopProduct";

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.type);
    }

    public NoteSpotEntity() {}

    protected NoteSpotEntity(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.type = in.readString();
    }

    public static final Creator<NoteSpotEntity> CREATOR = new Creator<NoteSpotEntity>() {
        @Override
        public NoteSpotEntity createFromParcel(Parcel source) {return new NoteSpotEntity(source);}

        @Override
        public NoteSpotEntity[] newArray(int size) {return new NoteSpotEntity[size];}
    };
}
