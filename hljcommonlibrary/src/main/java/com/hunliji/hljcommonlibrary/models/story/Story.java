package com.hunliji.hljcommonlibrary.models.story;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;

import java.util.ArrayList;

/**
 * Created by werther on 16/8/31.
 */
public class Story implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "praise_count", alternate = "praises_count")
    int praiseCount;
    @SerializedName(value = "collect_count", alternate = "collects_count")
    int collectCount;
    @SerializedName(value = "comment_count", alternate = "comments_count")
    int commentCount;
    @SerializedName(value = "cover_path")
    String coverPath;
    String description;
    String version;
    @SerializedName(value = "is_praised")
    boolean isPraised;
    @SerializedName(value = "collected")
    boolean isCollected;
    Author user;
    @SerializedName(value = "opened")
    boolean isOpened;
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @SerializedName(value = "images")
    ArrayList<Photo> photos;
    Mark mark;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public Author getUser() {
        return user;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.praiseCount);
        dest.writeInt(this.collectCount);
        dest.writeInt(this.commentCount);
        dest.writeString(this.coverPath);
        dest.writeString(this.description);
        dest.writeString(this.version);
        dest.writeByte(this.isPraised ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
        dest.writeByte(this.isOpened ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeTypedList(this.photos);
        dest.writeParcelable(this.mark, flags);
    }

    public Story() {}

    protected Story(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.praiseCount = in.readInt();
        this.collectCount = in.readInt();
        this.commentCount = in.readInt();
        this.coverPath = in.readString();
        this.description = in.readString();
        this.version = in.readString();
        this.isPraised = in.readByte() != 0;
        this.isCollected = in.readByte() != 0;
        this.user = in.readParcelable(Author.class.getClassLoader());
        this.isOpened = in.readByte() != 0;
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.mark = in.readParcelable(Mark.class.getClassLoader());
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {return new Story(source);}

        @Override
        public Story[] newArray(int size) {return new Story[size];}
    };
}
