package com.hunliji.hljquestionanswer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 16/8/22.
 */
public class PostPraiseIdBody implements Parcelable{
    @SerializedName("id")
    long id;
    @SerializedName("value")
    int value;

    public PostPraiseIdBody(){
    }

    protected PostPraiseIdBody(Parcel in) {
        id = in.readLong();
        value = in.readInt();
    }

    public static final Creator<PostPraiseIdBody> CREATOR = new Creator<PostPraiseIdBody>() {
        @Override
        public PostPraiseIdBody createFromParcel(Parcel in) {
            return new PostPraiseIdBody(in);
        }

        @Override
        public PostPraiseIdBody[] newArray(int size) {
            return new PostPraiseIdBody[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(value);
    }
}
