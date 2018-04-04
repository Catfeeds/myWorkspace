package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2016/10/31.
 */

public class ExperienceResverationResult implements Parcelable {
    long id;
    String action;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.action);
    }

    public ExperienceResverationResult() {
    }

    protected ExperienceResverationResult(Parcel in) {
        this.id = in.readLong();
        this.action = in.readString();
    }

    public static final Parcelable.Creator<ExperienceResverationResult> CREATOR = new Parcelable.Creator<ExperienceResverationResult>() {
        @Override
        public ExperienceResverationResult createFromParcel(Parcel source) {
            return new ExperienceResverationResult(source);
        }

        @Override
        public ExperienceResverationResult[] newArray(int size) {
            return new ExperienceResverationResult[size];
        }
    };
}
