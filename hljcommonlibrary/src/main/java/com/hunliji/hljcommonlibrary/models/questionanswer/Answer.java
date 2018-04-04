package com.hunliji.hljcommonlibrary.models.questionanswer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2016/8/17.回答详情
 */
public class Answer implements Parcelable {

    @SerializedName(value = "id")
    long id;//回答id
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "question_id")
    long questionId;
    @SerializedName(value = "by_faker")
    String byFaker;
    @SerializedName(value = "city_code")
    String cityCode;
    @SerializedName(value = "comment_count_faker")
    String commentCountFaker;
    @SerializedName(value = "content")
    String content;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "summary")
    String summary;//摘要
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "deleted")
    boolean deleted;
    @SerializedName(value = "is_allow_modify")
    boolean isAllowModify;//修改 是否可编辑 0禁止修改1允许修改
    @SerializedName(value = "is_follow")
    boolean isFollow;//是否收藏
    @SerializedName(value = "is_top")
    boolean isTop;//是否优质回答
    @SerializedName(value = "is_like")
    int likeType;//是否点赞 1表示赞同，-1表示反对,0表示无状态
    @SerializedName(value = "comment_count")
    int commentCount;//评论数
    @SerializedName(value = "down_count")
    int downCount;//反对数
    @SerializedName(value = "score")
    int score;
    @SerializedName(value = "status")
    int status;
    @SerializedName(value = "up_count")
    int upCount;//点赞数
    @SerializedName(value = "watch_count")
    int watchCount;//浏览数
    @SerializedName(value = "likes_count")
    int likesCount;//收藏数
    @SerializedName(value = "weight")
    int weight;
    @SerializedName(value = "user")
    QaAuthor user;
    @SerializedName(value = "question")
    Question question;
    @SerializedName(value = "comments")
    ArrayList<Comment> comments;
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @SerializedName(value = "is_rewrite_style")
    boolean isRewriteStyle;//是否为精编问答
    @SerializedName(value = "appeal_status")
    Integer appealStatus;//null代表未申诉 申诉状态 0待审核 1通过 2未通过

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public int getExpandedState() {
        return expandedState;
    }

    public void setExpandedState(int expandedState) {
        this.expandedState = expandedState;
    }

    private transient int expandedState;

    public boolean isRewriteStyle() {
        return isRewriteStyle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getByFaker() {
        return byFaker;
    }

    public void setByFaker(String byFaker) {
        this.byFaker = byFaker;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCommentCountFaker() {
        return commentCountFaker;
    }

    public void setCommentCountFaker(String commentCountFaker) {
        this.commentCountFaker = commentCountFaker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAllowModify() {
        return isAllowModify;
    }

    public void setAllowModify(boolean allowModify) {
        isAllowModify = allowModify;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getLikeType() {
        return likeType;
    }

    public void setLikeType(int likeType) {
        this.likeType = likeType;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setUser(QaAuthor user) {
        this.user = user;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public QaAuthor getUser() {
        return user;
    }

    public Question getQuestion() {
        if (this.question == null) {
            this.question = new Question();
        }
        return question;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public boolean isTop() {
        return isTop;
    }

    public Answer() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeLong(this.questionId);
        dest.writeString(this.byFaker);
        dest.writeString(this.cityCode);
        dest.writeString(this.commentCountFaker);
        dest.writeString(this.content);
        dest.writeString(this.coverPath);
        dest.writeString(this.summary);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAllowModify ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFollow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likeType);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.downCount);
        dest.writeInt(this.score);
        dest.writeInt(this.status);
        dest.writeInt(this.upCount);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.likesCount);
        dest.writeInt(this.weight);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.question, flags);
        dest.writeTypedList(this.comments);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeByte(this.isRewriteStyle ? (byte) 1 : (byte) 0);
        dest.writeValue(this.appealStatus);
    }

    protected Answer(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.questionId = in.readLong();
        this.byFaker = in.readString();
        this.cityCode = in.readString();
        this.commentCountFaker = in.readString();
        this.content = in.readString();
        this.coverPath = in.readString();
        this.summary = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.isAllowModify = in.readByte() != 0;
        this.isFollow = in.readByte() != 0;
        this.isTop = in.readByte() != 0;
        this.likeType = in.readInt();
        this.commentCount = in.readInt();
        this.downCount = in.readInt();
        this.score = in.readInt();
        this.status = in.readInt();
        this.upCount = in.readInt();
        this.watchCount = in.readInt();
        this.likesCount = in.readInt();
        this.weight = in.readInt();
        this.user = in.readParcelable(QaAuthor.class.getClassLoader());
        this.question = in.readParcelable(Question.class.getClassLoader());
        this.comments = in.createTypedArrayList(Comment.CREATOR);
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.isRewriteStyle = in.readByte() != 0;
        this.appealStatus = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {return new Answer(source);}

        @Override
        public Answer[] newArray(int size) {return new Answer[size];}
    };
}
