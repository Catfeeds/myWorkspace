package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * 发现-专题model
 * Created by chen_bin on 2016/9/13 0013.
 */
public class TopicUrl implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "category")
    private long categoryId;
    @SerializedName(value = "begin_at")
    private DateTime beginAt;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "good_title")
    private String goodTitle;
    @SerializedName(value = "summary")
    private String summary;
    @SerializedName(value = "img_title")
    private String img;
    @SerializedName(value = "list_img")
    private String listImg;
    @SerializedName(value = "img_share")
    private String imgShare;
    @SerializedName(value = "html")
    private String html;
    @SerializedName(value = "category_name")
    private String categoryName;
    @SerializedName(value = "is_like")
    private boolean isPraised;
    @SerializedName(value = "is_followed")
    private boolean isCollected;
    @SerializedName(value = "likes_count")
    private int praiseCount;
    @SerializedName(value = "comment_count")
    private int commentCount;
    @SerializedName(value = "watch_count")
    private int watchCount;
    @SerializedName(value = "type")
    private int type;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    transient boolean isAnimEnd;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.categoryId);
        dest.writeSerializable(this.beginAt);
        dest.writeString(this.title);
        dest.writeString(this.goodTitle);
        dest.writeString(this.summary);
        dest.writeString(this.img);
        dest.writeString(this.listImg);
        dest.writeString(this.imgShare);
        dest.writeString(this.html);
        dest.writeString(this.categoryName);
        dest.writeByte(this.isPraised ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.praiseCount);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.type);
        dest.writeParcelable(this.shareInfo, flags);
    }

    public TopicUrl() {}

    protected TopicUrl(Parcel in) {
        this.id = in.readLong();
        this.categoryId = in.readLong();
        this.beginAt = (DateTime) in.readSerializable();
        this.title = in.readString();
        this.goodTitle = in.readString();
        this.summary = in.readString();
        this.img = in.readString();
        this.listImg = in.readString();
        this.imgShare = in.readString();
        this.html = in.readString();
        this.categoryName = in.readString();
        this.isPraised = in.readByte() != 0;
        this.isCollected = in.readByte() != 0;
        this.praiseCount = in.readInt();
        this.commentCount = in.readInt();
        this.watchCount = in.readInt();
        this.type = in.readInt();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
    }

    public static final Creator<TopicUrl> CREATOR = new Creator<TopicUrl>() {
        @Override
        public TopicUrl createFromParcel(Parcel source) {return new TopicUrl(source);}

        @Override
        public TopicUrl[] newArray(int size) {return new TopicUrl[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public DateTime getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(DateTime beginAt) {
        this.beginAt = beginAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodTitle() {
        return TextUtils.isEmpty(goodTitle) ? title : goodTitle;
    }

    public void setGoodTitle(String goodTitle) {
        this.goodTitle = goodTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getListImg() {
        return listImg;
    }

    public void setListImg(String listImg) {
        this.listImg = listImg;
    }

    public String getImgShare() {
        return imgShare;
    }

    public void setImgShare(String imgShare) {
        this.imgShare = imgShare;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public boolean isAnimEnd() {
        return isAnimEnd;
    }

    public void setAnimEnd(boolean animEnd) {
        isAnimEnd = animEnd;
    }
}