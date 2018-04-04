package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 第三方绑定Model
 * Created by jinxin on 2017/1/7 0007.
 */

public class ThirdBind implements Parcelable {
    long id;
    String type;//qq,sina,weixin

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        dest.writeString(this.type);
    }

    public ThirdBind() {}

    protected ThirdBind(Parcel in) {
        this.id = in.readLong();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<ThirdBind> CREATOR = new Parcelable.Creator<ThirdBind>
            () {
        @Override
        public ThirdBind createFromParcel(Parcel source) {return new ThirdBind(source);}

        @Override
        public ThirdBind[] newArray(int size) {return new ThirdBind[size];}
    };
}
