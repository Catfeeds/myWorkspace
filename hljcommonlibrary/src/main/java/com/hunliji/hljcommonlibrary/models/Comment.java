package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by werther on 16/7/27.
 */
public class Comment implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "comment_photos", alternate = {"imgs", "photos"})
    private ArrayList<Photo> photos;
    @SerializedName(value = "praised_users")
    private ArrayList<Author> praisedUsers; //点赞列表
    @SerializedName(value = "author", alternate = "user")
    private Author author;
    @SerializedName(value = "content")
    private String content;
    @SerializedName(value = "is_like")
    private boolean isLike; //是否点赞
    @SerializedName(value = "rating")
    private int rating; //评分
    @SerializedName(value = "likes_count")
    private int likesCount;
    @SerializedName(value = "comment_count")
    private int commentCount;

    private transient boolean isExpanded; //评论内容是否展开

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public ArrayList<Photo> getPhotos() {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<Author> getPraisedUsers() {
        if (praisedUsers == null) {
            praisedUsers = new ArrayList<>();
        }
        return praisedUsers;
    }

    public void setPraisedUsers(ArrayList<Author> praisedUsers) {
        this.praisedUsers = praisedUsers;
    }

    public Author getAuthor() {
        if (author == null) {
            author = new Author();
        }
        return author;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "" : content.trim();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getLikesCount() {
        return likesCount < 0 ? 0 : likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentCount() {
        return commentCount < 0 ? 0 : commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {this.isExpanded = expanded;}

    public String getGrade(int rating) {
        String grade = null;
        switch (rating) {
            case 1:
                grade = "有待改善";
                break;
            case 2:
                grade = "体验一般";
                break;
            case 3:
                grade = "基本满意";
                break;
            case 4:
                grade = "比较满意";
                break;
            case 5:
                grade = "服务超预期";
                break;
        }
        return grade;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeTypedList(this.photos);
        dest.writeTypedList(this.praisedUsers);
        dest.writeParcelable(this.author, flags);
        dest.writeString(this.content);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.rating);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.commentCount);
    }

    public Comment() {}

    protected Comment(Parcel in) {
        this.id = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.praisedUsers = in.createTypedArrayList(Author.CREATOR);
        this.author = in.readParcelable(Author.class.getClassLoader());
        this.content = in.readString();
        this.isLike = in.readByte() != 0;
        this.rating = in.readInt();
        this.likesCount = in.readInt();
        this.commentCount = in.readInt();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {return new Comment(source);}

        @Override
        public Comment[] newArray(int size) {return new Comment[size];}
    };
}