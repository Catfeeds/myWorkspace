package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * 编辑笔记本body
 * Created by jinxin on 2017/7/20 0020.
 */

public class NoteBookEditBody implements Parcelable {

    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "title")
    String title;
    @SerializedName(value = "desc")
    String desc;
    @SerializedName(value = "photo")
    Photo photo;

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeParcelable(this.photo, flags);
    }

    public NoteBookEditBody() {}

    protected NoteBookEditBody(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.desc = in.readString();
        this.photo = in.readParcelable(Photo.class.getClassLoader());
    }

    public static final Parcelable.Creator<NoteBookEditBody> CREATOR = new Parcelable
            .Creator<NoteBookEditBody>() {
        @Override
        public NoteBookEditBody createFromParcel(Parcel source) {return new NoteBookEditBody(source);}

        @Override
        public NoteBookEditBody[] newArray(int size) {return new NoteBookEditBody[size];}
    };
}
