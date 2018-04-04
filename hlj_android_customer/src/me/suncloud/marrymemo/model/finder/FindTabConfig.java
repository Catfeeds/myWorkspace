package me.suncloud.marrymemo.model.finder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luohanlin on 2018/3/22.
 */

public class FindTabConfig implements Parcelable {
    boolean show;
    int index;
    String title;
    String url;

    public boolean isShow() {
        return show;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest, int flags) {
        dest.writeByte(this.show ? (byte) 1 : (byte) 0);
        dest.writeInt(this.index);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public FindTabConfig() {}

    protected FindTabConfig(Parcel in) {
        this.show = in.readByte() != 0;
        this.index = in.readInt();
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<FindTabConfig> CREATOR = new Parcelable
            .Creator<FindTabConfig>() {
        @Override
        public FindTabConfig createFromParcel(Parcel source) {return new FindTabConfig(source);}

        @Override
        public FindTabConfig[] newArray(int size) {return new FindTabConfig[size];}
    };
}
