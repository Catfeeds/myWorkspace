package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Member;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/8/5.
 */
public class QaAuthor extends Author {

    String description;//描述
    Merchant merchant;//商家信息
    @SerializedName(value = "weddingday")//婚期
    DateTime weddingDay;
    @SerializedName(value = "gender")//性别
    boolean gender;
    @SerializedName(value = "is_pending")//婚期 0表示已设置
    int isPending;
    @SerializedName(value = "user_answer_count")//回答数
    int userAnswerCount;
    @SerializedName(value = "user_likes_count")//获得的点赞数
    int userLikesCount;

    public QaAuthor() {}

    public int getUserAnswerCount() {
        return userAnswerCount;
    }

    public int getUserLikesCount() {
        return userLikesCount;
    }

    public String getDescription() {
        return description;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public boolean isGender() {
        return gender;
    }

    public int getIsPending() {
        return isPending;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.description);
        dest.writeParcelable(this.merchant, flags);
        HljTimeUtils.writeDateTimeToParcel(dest,weddingDay);
        dest.writeByte(this.gender ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isPending);
        dest.writeInt(this.userAnswerCount);
        dest.writeInt(this.userLikesCount);
    }

    protected QaAuthor(Parcel in) {
        super(in);
        this.description = in.readString();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.weddingDay = HljTimeUtils.readDateTimeToParcel(in);
        this.gender = in.readByte() != 0;
        this.isPending = in.readInt();
        this.userAnswerCount = in.readInt();
        this.userLikesCount = in.readInt();
    }

    public static final Creator<QaAuthor> CREATOR = new Creator<QaAuthor>() {
        @Override
        public QaAuthor createFromParcel(Parcel source) {return new QaAuthor(source);}

        @Override
        public QaAuthor[] newArray(int size) {return new QaAuthor[size];}
    };
}
