package com.hunliji.hljquestionanswer.models.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/8/26.
 */
public class PostQuestionResult implements Parcelable {

    private long id;
    private String action;
    @SerializedName("is_exist")
    private boolean isExist;

    public boolean isExist() {
        return isExist;
    }

    public long getId() {
        return id;
    }

    protected PostQuestionResult(Parcel in) {
        id = in.readLong();

        action = in.readString();
        isExist = in.readByte() != 0;
    }

    public static final Creator<PostQuestionResult> CREATOR = new Creator<PostQuestionResult>() {
        @Override
        public PostQuestionResult createFromParcel(Parcel in) {
            return new PostQuestionResult(in);
        }

        @Override
        public PostQuestionResult[] newArray(int size) {
            return new PostQuestionResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(action);
        parcel.writeByte((byte) (isExist ? 1 : 0));
    }
}
