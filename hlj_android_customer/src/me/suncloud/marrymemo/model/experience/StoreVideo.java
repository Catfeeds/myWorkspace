package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2017/3/29 0029.
 */

public class StoreVideo implements Parcelable {

    String intro;//导读
    String path;//路径
    String title;//标题
    String vframe;//封面


    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVframe() {
        return vframe;
    }

    public void setVframe(String vframe) {
        this.vframe = vframe;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.intro);
        dest.writeString(this.path);
        dest.writeString(this.title);
        dest.writeString(this.vframe);
    }

    public StoreVideo() {}

    protected StoreVideo(Parcel in) {
        this.intro = in.readString();
        this.path = in.readString();
        this.title = in.readString();
        this.vframe = in.readString();
    }

    public static final Parcelable.Creator<StoreVideo> CREATOR = new Parcelable
            .Creator<StoreVideo>() {
        @Override
        public StoreVideo createFromParcel(Parcel source) {return new StoreVideo(source);}

        @Override
        public StoreVideo[] newArray(int size) {return new StoreVideo[size];}
    };
}
