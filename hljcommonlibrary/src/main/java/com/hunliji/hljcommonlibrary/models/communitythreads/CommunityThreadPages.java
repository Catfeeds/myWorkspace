package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;

/**
 * Created by werther on 16/8/26.
 * 话题专用富文本
 */
public class CommunityThreadPages implements Parcelable {
    @SerializedName(value = "cid", alternate = "id")
    long id;
    String title;
    String content;
    @SerializedName(value = "img_path")
    String imgPath;
    @SerializedName(value = "sub_title")
    String subTitle;
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @SerializedName(value = "join_count")
    int joinCount;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.imgPath);
        dest.writeString(this.subTitle);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeInt(this.joinCount);
    }

    public CommunityThreadPages() {}

    protected CommunityThreadPages(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.content = in.readString();
        this.imgPath = in.readString();
        this.subTitle = in.readString();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.joinCount = in.readInt();
    }

    public static final Parcelable.Creator<CommunityThreadPages> CREATOR = new Parcelable
            .Creator<CommunityThreadPages>() {
        @Override
        public CommunityThreadPages createFromParcel(Parcel source) {
            return new CommunityThreadPages(source);
        }

        @Override
        public CommunityThreadPages[] newArray(int size) {return new CommunityThreadPages[size];}
    };

    public long getId() {
        return id;
    }

    public String getTitle() {
        return TextUtils.isEmpty(title) ? "" : title;
    }

    public String getContent() {
        return content;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public int getJoinCount() {
        return joinCount;
    }
}
