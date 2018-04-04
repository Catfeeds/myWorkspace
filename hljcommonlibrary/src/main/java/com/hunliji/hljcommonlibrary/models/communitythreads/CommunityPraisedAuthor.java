package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/8/1.
 * 社区点赞用户
 */
public class CommunityPraisedAuthor implements Parcelable {
    long id;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "user")
    Author author;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public Author getAuthor() {
        return author;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeSerializable(this.createdAt);
        dest.writeParcelable(this.author, flags);
    }

    public CommunityPraisedAuthor() {}

    protected CommunityPraisedAuthor(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.author = in.readParcelable(Author.class.getClassLoader());
    }

    public static final Creator<CommunityPraisedAuthor> CREATOR = new
            Creator<CommunityPraisedAuthor>() {
        @Override
        public CommunityPraisedAuthor createFromParcel(Parcel source) {
            return new CommunityPraisedAuthor(source);
        }

        @Override
        public CommunityPraisedAuthor[] newArray(int size) {return new CommunityPraisedAuthor[size];}
    };
}
