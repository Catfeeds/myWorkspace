package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

/**
 * Created by jinxin on 2017/7/10 0010.
 */

public class NotePoster implements Parcelable {

    long id;
    @SerializedName(value = "bride_name")
    String brideName;
    String content;
    Media cover;
    @SerializedName(value = "groom_name")
    String groomName;
    @SerializedName(value = "poster_text")
    List<String> posterText;
    String qrcode;
    String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Media getCover() {
        return cover;
    }

    public void setCover(Media cover) {
        this.cover = cover;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public List<String> getPosterText() {
        return posterText;
    }

    public void setPosterText(List<String> posterText) {
        this.posterText = posterText;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NotePoster() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.brideName);
        dest.writeString(this.content);
        dest.writeParcelable(this.cover, flags);
        dest.writeString(this.groomName);
        dest.writeStringList(this.posterText);
        dest.writeString(this.qrcode);
        dest.writeString(this.title);
    }

    protected NotePoster(Parcel in) {
        this.id = in.readLong();
        this.brideName = in.readString();
        this.content = in.readString();
        this.cover = in.readParcelable(Media.class.getClassLoader());
        this.groomName = in.readString();
        this.posterText = in.createStringArrayList();
        this.qrcode = in.readString();
        this.title = in.readString();
    }

    public static final Creator<NotePoster> CREATOR = new Creator<NotePoster>() {
        @Override
        public NotePoster createFromParcel(Parcel source) {return new NotePoster(source);}

        @Override
        public NotePoster[] newArray(int size) {return new NotePoster[size];}
    };
}
