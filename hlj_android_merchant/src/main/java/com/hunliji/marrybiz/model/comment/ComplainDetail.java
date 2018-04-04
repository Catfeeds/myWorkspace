package com.hunliji.marrybiz.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by hua_rong on 2017/6/14.
 * 申诉详情
 */

public class ComplainDetail implements Parcelable {

    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_QUESTION = 2;

    public static final String TYPE = "type";

    private long id;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    private int status;
    private String reason;
    @SerializedName(value = "order_comment_id")
    private long orderCommentId;
    private ArrayList<Photo> photos;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;//申诉时间
    private String content;//申诉内容
    @SerializedName(value = "community_comment_id")
    private long communityCommentId;
    @SerializedName(value = "order_comment")
    private ServiceComment orderComment;
    @SerializedName(value = "community_comment")
    private RepliedComment repliedComment;
    @SerializedName(value = "appeal_status")
    private int appealStatus;//1:申诉成功 0：申诉失败

    //一下为问答参数
    private int type;//申诉类型 1问题申诉 2回答申诉
    @SerializedName(value = "qa_answer")
    private Answer qaAnswer;
    @SerializedName(value = "qa_question")
    private Question qaQuestion;

    public Answer getQaAnswer() {
        return qaAnswer;
    }

    public Question getQaQuestion() {
        return qaQuestion;
    }

    public int getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getOrderCommentId() {
        return orderCommentId;
    }

    public void setOrderCommentId(long orderCommentId) {
        this.orderCommentId = orderCommentId;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCommunityCommentId() {
        return communityCommentId;
    }

    public void setCommunityCommentId(long communityCommentId) {
        this.communityCommentId = communityCommentId;
    }

    public ServiceComment getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(ServiceComment orderComment) {
        this.orderComment = orderComment;
    }

    public RepliedComment getRepliedComment() {
        return repliedComment;
    }

    public void setRepliedComment(RepliedComment repliedComment) {
        this.repliedComment = repliedComment;
    }

    public int getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(int appealStatus) {
        this.appealStatus = appealStatus;
    }

    public ComplainDetail() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.updatedAt);
        dest.writeInt(this.status);
        dest.writeString(this.reason);
        dest.writeLong(this.orderCommentId);
        dest.writeTypedList(this.photos);
        dest.writeLong(this.merchantId);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.content);
        dest.writeLong(this.communityCommentId);
        dest.writeParcelable(this.orderComment, flags);
        dest.writeParcelable(this.repliedComment, flags);
        dest.writeInt(this.appealStatus);
        dest.writeInt(this.type);
        dest.writeParcelable(this.qaAnswer, flags);
        dest.writeParcelable(this.qaQuestion, flags);
    }

    protected ComplainDetail(Parcel in) {
        this.id = in.readLong();
        this.updatedAt = (DateTime) in.readSerializable();
        this.status = in.readInt();
        this.reason = in.readString();
        this.orderCommentId = in.readLong();
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.merchantId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.content = in.readString();
        this.communityCommentId = in.readLong();
        this.orderComment = in.readParcelable(ServiceComment.class.getClassLoader());
        this.repliedComment = in.readParcelable(RepliedComment.class.getClassLoader());
        this.appealStatus = in.readInt();
        this.type = in.readInt();
        this.qaAnswer = in.readParcelable(Answer.class.getClassLoader());
        this.qaQuestion = in.readParcelable(Question.class.getClassLoader());
    }

    public static final Creator<ComplainDetail> CREATOR = new Creator<ComplainDetail>() {
        @Override
        public ComplainDetail createFromParcel(Parcel source) {return new ComplainDetail(source);}

        @Override
        public ComplainDetail[] newArray(int size) {return new ComplainDetail[size];}
    };
}
