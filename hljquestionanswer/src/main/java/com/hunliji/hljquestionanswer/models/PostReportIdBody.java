package com.hunliji.hljquestionanswer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 16/8/22.
 */
public class PostReportIdBody implements Parcelable{
    @SerializedName("id")
    long id;
    @SerializedName("kind")
    String kind;
    @SerializedName("message")
    String message;

    public PostReportIdBody(){
    }

    protected PostReportIdBody(Parcel in) {
        id = in.readLong();
        kind = in.readString();
        message = in.readString();
    }

    public static final Creator<PostReportIdBody> CREATOR = new Creator<PostReportIdBody>() {
        @Override
        public PostReportIdBody createFromParcel(Parcel in) {
            return new PostReportIdBody(in);
        }

        @Override
        public PostReportIdBody[] newArray(int size) {
            return new PostReportIdBody[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(kind);
        parcel.writeString(message);
    }
}
