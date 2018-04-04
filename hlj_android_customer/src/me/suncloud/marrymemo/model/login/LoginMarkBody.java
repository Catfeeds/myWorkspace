package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2016/9/1.
 */
public class LoginMarkBody implements Parcelable {
    long[] marks;

    public long[] getMarks() {
        return marks;
    }

    public void setMarks(long[] marks) {
        this.marks = marks;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {dest.writeLongArray(this.marks);}

    public LoginMarkBody() {}

    protected LoginMarkBody(Parcel in) {this.marks = in.createLongArray();}

    public static final Parcelable.Creator<LoginMarkBody> CREATOR = new
            Parcelable.Creator<LoginMarkBody>() {
        @Override
        public LoginMarkBody createFromParcel(Parcel source) {
            return new LoginMarkBody(source);
        }

        @Override
        public LoginMarkBody[] newArray(int size) {return new LoginMarkBody[size];}
    };
}
