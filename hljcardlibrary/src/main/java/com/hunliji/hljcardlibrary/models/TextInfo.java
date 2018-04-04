package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/6/10.
 */

public class TextInfo implements Parcelable {

    @SerializedName("id")
    private long holeId;
    private String content;
    @SerializedName("h5_hole_image_path")
    private String h5ImagePath;
    private String type;

    public TextInfo(TextHole hole){
        this.holeId=hole.getId();
        this.content=hole.getDefaultContent();
        this.type=hole.getType();
        this.h5ImagePath=hole.getDefaultH5ImagePath();
    }

    public String getH5ImagePath() {
        return h5ImagePath;
    }

    public long getHoleId() {
        return holeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setH5ImagePath(String h5ImagePath) {
        this.h5ImagePath = h5ImagePath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.holeId);
        dest.writeString(this.content);
        dest.writeString(this.h5ImagePath);
        dest.writeString(this.type);
    }

    protected TextInfo(Parcel in) {
        this.holeId = in.readLong();
        this.content = in.readString();
        this.h5ImagePath = in.readString();
        this.type = in.readString();
    }

    public static final Creator<TextInfo> CREATOR = new Creator<TextInfo>() {
        @Override
        public TextInfo createFromParcel(Parcel source) {return new TextInfo(source);}

        @Override
        public TextInfo[] newArray(int size) {return new TextInfo[size];}
    };
}
