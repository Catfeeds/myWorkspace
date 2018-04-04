package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 统筹师
 * Created by jinxin on 2017/3/29 0029.
 */

public class Planner implements Parcelable {

    long id;
    @SerializedName(value = "admin_date")
    DateTime adminDate;
    @SerializedName(value = "admin_user")
    String adminUser;
    @SerializedName(value = "create_at")
    DateTime createAt;
    boolean deleted;
    @SerializedName(value = "fullname")
    String fullName;
    @SerializedName(value = "head_img")
    String headImg;
    List<Photo> img;
    String introduce;
    @SerializedName(value = "likes_count")
    int likesCount;
    @SerializedName(value = "title" )
    int title;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    String weight;
    @SerializedName(value = "is_like")
    boolean isLike;
    @SerializedName(value = "comment_count")
    int commentCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getAdminDate() {
        return adminDate;
    }

    public void setAdminDate(DateTime adminDate) {
        this.adminDate = adminDate;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(DateTime createAt) {
        this.createAt = createAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public List<Photo> getImg() {
        return img;
    }

    public void setImg(List<Photo> img) {
        this.img = img;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.adminDate);
        dest.writeString(this.adminUser);
        dest.writeSerializable(this.createAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.fullName);
        dest.writeString(this.headImg);
        dest.writeTypedList(this.img);
        dest.writeString(this.introduce);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.title);
        dest.writeSerializable(this.updatedAt);
        dest.writeString(this.weight);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.commentCount);
    }

    public Planner() {
    }

    protected Planner(Parcel in) {
        this.id = in.readLong();
        this.adminDate = (DateTime) in.readSerializable();
        this.adminUser = in.readString();
        this.createAt = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.fullName = in.readString();
        this.headImg = in.readString();
        this.img = in.createTypedArrayList(Photo.CREATOR);
        this.introduce = in.readString();
        this.likesCount = in.readInt();
        this.title = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
        this.weight = in.readString();
        this.isLike = in.readByte() != 0;
        this.commentCount = in.readInt();
    }

    public static final Creator<Planner> CREATOR = new Creator<Planner>() {
        @Override
        public Planner createFromParcel(Parcel source) {
            return new Planner(source);
        }

        @Override
        public Planner[] newArray(int size) {
            return new Planner[size];
        }
    };
}


