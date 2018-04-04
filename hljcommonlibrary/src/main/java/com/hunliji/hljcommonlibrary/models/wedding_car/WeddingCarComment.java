package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.CommonOrderInfo;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;

import java.util.ArrayList;
import java.util.List;

/**
 * 婚车评论
 * Created by jinxin on 2017/12/27 0027.
 */

public class WeddingCarComment extends Comment {

    @SerializedName(value = "reply_comments")
    ArrayList<RepliedComment> repliedComments; //评论的回复
    @SerializedName(value = "merchant_info", alternate = "merchant")
    Merchant merchant;
    @SerializedName(value = "watch_count")
    int watchCount;//浏览数
    @SerializedName(value = "order_info")
    CommonOrderInfo commonOrderInfo;

    private transient int contentStatus;

    public ArrayList<RepliedComment> getRepliedComments() {
        return repliedComments;
    }

    public void setRepliedComments(ArrayList<RepliedComment> repliedComments) {
        this.repliedComments = repliedComments;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public CommonOrderInfo getCommonOrderInfo() {
        return commonOrderInfo;
    }

    public void setCommonOrderInfo(CommonOrderInfo commonOrderInfo) {
        this.commonOrderInfo = commonOrderInfo;
    }

    public int getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.repliedComments);
        dest.writeParcelable(this.merchant, flags);
        dest.writeInt(this.watchCount);
        dest.writeParcelable(this.commonOrderInfo, flags);
    }

    public WeddingCarComment() {}

    protected WeddingCarComment(Parcel in) {
        super(in);
        this.repliedComments = in.createTypedArrayList(RepliedComment.CREATOR);
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.watchCount = in.readInt();
        this.commonOrderInfo = in.readParcelable(CommonOrderInfo.class.getClassLoader());
    }

    public static final Creator<WeddingCarComment> CREATOR = new Creator<WeddingCarComment>() {
        @Override
        public WeddingCarComment createFromParcel(Parcel source) {
            return new WeddingCarComment(source);
        }

        @Override
        public WeddingCarComment[] newArray(int size) {return new WeddingCarComment[size];}
    };
}
