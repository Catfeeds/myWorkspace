package com.hunliji.hljcardlibrary.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by wangtao on 2017/6/10.
 */

public class Theme implements Parcelable {

    private long id;
    @SerializedName("thumb_path")
    private String thumbPath;
    @SerializedName("preview_link")
    private String previewLink;

    @SerializedName("front_page")
    private Template frontPage;
    @SerializedName("speech_page")
    private Template speechPage;


    @SerializedName("is_locked")
    private boolean isLocked;
    @SerializedName("is_member")
    private boolean isMember;

    public boolean isSupportVideo() {
        return supportVideo;
    }

    public void setSupportVideo(boolean supportVideo) {
        this.supportVideo = supportVideo;
    }

    @SerializedName(value = "support_video")
    private boolean supportVideo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public Template getFrontPage() {
        return frontPage;
    }

    public void setFrontPage(Template frontPage) {
        this.frontPage = frontPage;
    }

    public Template getSpeechPage() {
        return speechPage;
    }

    public void setSpeechPage(Template speechPage) {
        this.speechPage = speechPage;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.thumbPath);
        dest.writeString(this.previewLink);
        dest.writeParcelable(this.frontPage, flags);
        dest.writeParcelable(this.speechPage, flags);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMember ? (byte) 1 : (byte) 0);
        dest.writeByte(this.supportVideo ? (byte) 1 : (byte) 0);
    }

    public Theme() {}

    protected Theme(Parcel in) {
        this.id = in.readLong();
        this.thumbPath = in.readString();
        this.previewLink = in.readString();
        this.frontPage = in.readParcelable(Template.class.getClassLoader());
        this.speechPage = in.readParcelable(Template.class.getClassLoader());
        this.isLocked = in.readByte() != 0;
        this.isMember = in.readByte() != 0;
        this.supportVideo = in.readByte() != 0;
    }

    public static final Creator<Theme> CREATOR = new Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel source) {return new Theme(source);}

        @Override
        public Theme[] newArray(int size) {return new Theme[size];}
    };



    public ArrayList<String> getImagePaths(Context context) {
        ArrayList<String> images = new ArrayList<>();
        if (frontPage != null) {
            for (String string : frontPage.getImagePaths(context)) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
        }
        if (speechPage != null) {
            for (String string : speechPage.getImagePaths(context)) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
        }
        return images;
    }

    public ArrayList<String> getFontPaths(Context context) {
        ArrayList<String> fonts = new ArrayList<>();
        if (frontPage != null) {
            for (String string : frontPage.getFontPaths(context)) {
                if (!fonts.contains(string)) {
                    fonts.add(string);
                }
            }
        }
        if (speechPage != null) {
            for (String string : speechPage.getFontPaths(context)) {
                if (!fonts.contains(string)) {
                    fonts.add(string);
                }
            }
        }
        return fonts;
    }
}
