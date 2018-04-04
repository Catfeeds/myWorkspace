package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Poster;

/**
 * Created by hua_rong on 2017/11/13
 * 结婚任务 系统模版 poster跳转
 */

public class Template implements Parcelable {

    private String title;//模版名
    private Poster config;//Poster跳转配置

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Poster getConfig() {
        return config;
    }

    public void setConfig(Poster config) {
        this.config = config;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeParcelable(this.config, flags);
    }

    public Template() {}

    protected Template(Parcel in) {
        this.title = in.readString();
        this.config = in.readParcelable(Poster.class.getClassLoader());
    }

    public static final Parcelable.Creator<Template> CREATOR = new Parcelable.Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel source) {return new Template(source);}

        @Override
        public Template[] newArray(int size) {return new Template[size];}
    };
}
