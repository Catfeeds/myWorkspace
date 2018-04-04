package me.suncloud.marrymemo.model.card;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangtao on 2017/4/1.
 */

public class ShareLink implements Parcelable {

    private String msg;
    private String link;

    public String getMsg() {
        return msg;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeString(this.link);
    }

    public ShareLink() {}

    protected ShareLink(Parcel in) {
        this.msg = in.readString();
        this.link = in.readString();
    }

    public static final Creator<ShareLink> CREATOR = new Creator<ShareLink>() {
        @Override
        public ShareLink createFromParcel(Parcel source) {return new ShareLink(source);}

        @Override
        public ShareLink[] newArray(int size) {return new ShareLink[size];}
    };
}
