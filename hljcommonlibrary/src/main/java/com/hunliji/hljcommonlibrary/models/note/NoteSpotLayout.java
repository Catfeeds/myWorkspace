package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/6/27 0027.
 */
public class NoteSpotLayout implements Parcelable {
    @SerializedName(value = "x")
    private float x;
    @SerializedName(value = "y")
    private float y;
    @SerializedName(value = "type")
    private int type;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeInt(this.type);
    }

    public NoteSpotLayout() {}

    public NoteSpotLayout(float x, float y) {
        this.x = x;
        this.y = y;
    }

    protected NoteSpotLayout(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.type = in.readInt();
    }

    public static final Creator<NoteSpotLayout> CREATOR = new Creator<NoteSpotLayout>() {
        @Override
        public NoteSpotLayout createFromParcel(Parcel source) {return new NoteSpotLayout(source);}

        @Override
        public NoteSpotLayout[] newArray(int size) {return new NoteSpotLayout[size];}
    };
}
