package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客照
 * Created by jinxin on 2018/2/1 0001.
 */

public class CasePhoto implements Parcelable {

    private long id;
    @SerializedName("cover_path")
    private String coverPath;//封面
    @SerializedName("mark_content")
    private String markContent;//标签文字
    @SerializedName("shooting_time")
    private String shootingTime;//拍摄时间
    @SerializedName(value = "media_items")
    private List<WorkMediaItem> detailPhotos;//详细图片
    @SerializedName(value = "music_url")
    private String musicUrl;//背景音乐URL
    @SerializedName(value = "is_collected")
    private boolean isCollected;
    private Merchant merchant;
    private ShareInfo share;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getMarkContent() {
        return markContent;
    }

    public void setMarkContent(String markContent) {
        this.markContent = markContent;
    }

    public String getShootingTime() {
        return shootingTime;
    }

    public List<WorkMediaItem> getDetailPhotos() {
        if (detailPhotos == null) {
            detailPhotos = new ArrayList<>();
        }
        return detailPhotos;
    }

    //清除后台返回的脏数据
    public void setDetailPhotos(List<WorkMediaItem> detailPhotos) {
        if (detailPhotos == null) {
            detailPhotos = new ArrayList<>();
        }
        Iterator<WorkMediaItem> photoIterator = detailPhotos.iterator();
        while (photoIterator.hasNext()) {
            WorkMediaItem photo = photoIterator.next();
            if (photo == null || photo.getKind() == 2 || TextUtils.isEmpty(photo.getMediaPath())
                    || photo.getMediaPath()
                    .toLowerCase()
                    .endsWith(".mp4")) {
                photoIterator.remove();
            }
        }
        this.detailPhotos = detailPhotos;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public ShareInfo getShare() {
        return share;
    }

    public void setShare(ShareInfo share) {
        this.share = share;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.coverPath);
        dest.writeString(this.markContent);
        dest.writeString(this.shootingTime);
        dest.writeTypedList(this.detailPhotos);
        dest.writeString(this.musicUrl);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.share, flags);
    }

    public CasePhoto() {}

    protected CasePhoto(Parcel in) {
        this.id = in.readLong();
        this.coverPath = in.readString();
        this.markContent = in.readString();
        this.shootingTime = in.readString();
        this.detailPhotos = in.createTypedArrayList(WorkMediaItem.CREATOR);
        this.musicUrl = in.readString();
        this.isCollected = in.readByte() != 0;
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.share = in.readParcelable(ShareInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<CasePhoto> CREATOR = new Parcelable.Creator<CasePhoto>
            () {
        @Override
        public CasePhoto createFromParcel(Parcel source) {return new CasePhoto(source);}

        @Override
        public CasePhoto[] newArray(int size) {return new CasePhoto[size];}
    };
}

