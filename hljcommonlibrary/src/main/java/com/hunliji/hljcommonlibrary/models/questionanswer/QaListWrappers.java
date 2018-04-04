package com.hunliji.hljcommonlibrary.models.questionanswer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.modelwrappers.AnswerUsers;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2016/9/18.问答列表特殊model 包含热门回答，最新问题和标签问题三种类型
 * 类型混杂后有冗余字段
 */
public class QaListWrappers implements Parcelable {

    long id;
    /**
     * 最新问题
     */
    String title;
    @SerializedName(value = "answer_count")
    int answerCount;
    Answer answer;
    @SerializedName(value = "last_answer_time")
    DateTime lastAnswerTime;
    @SerializedName(value = "answer_users")
    AnswerUsers answerUsers;
    @SerializedName(value = "watch_count")
    int watchCount;//浏览数

    /**
     * 热门回答
     *
     */
    @SerializedName(value = "comment_count")
    int commentCount;//评论数
    @SerializedName(value = "cover_path")
    String coverPath;//封面图
    @SerializedName(value = "is_like")
    boolean isLike;//是否赞同 未登录默认0
    @SerializedName(value = "up_count")
    int upCount;//赞同数
    @SerializedName(value = "summary")
    String summary;//回答摘要
    @SerializedName(value = "user")
    QaAuthor user;
    @SerializedName(value = "question")
    Question question;
    @SerializedName(value = "is_rewrite_style")
    boolean isRewriteStyle;

    public boolean isRewriteStyle() {
        return isRewriteStyle;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public Answer getAnswer() {
        return answer;
    }

    public DateTime getLastAnswerTime() {
        return lastAnswerTime;
    }

    public AnswerUsers getAnswerUsers() {
        return answerUsers;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isLike() {
        return isLike;
    }

    public int getUpCount() {
        return upCount;
    }

    public String getSummary() {
        return summary;
    }

    public QaAuthor getUser() {
        return user;
    }

    public Question getQuestion() {
        return question;
    }

    public QaListWrappers() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.answerCount);
        dest.writeParcelable(this.answer, flags);
        dest.writeSerializable(this.lastAnswerTime);
        dest.writeParcelable(this.answerUsers, flags);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.commentCount);
        dest.writeString(this.coverPath);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.upCount);
        dest.writeString(this.summary);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.question, flags);
        dest.writeByte(this.isRewriteStyle ? (byte) 1 : (byte) 0);
    }

    protected QaListWrappers(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.answerCount = in.readInt();
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        this.lastAnswerTime = (DateTime) in.readSerializable();
        this.answerUsers = in.readParcelable(AnswerUsers.class.getClassLoader());
        this.watchCount = in.readInt();
        this.commentCount = in.readInt();
        this.coverPath = in.readString();
        this.isLike = in.readByte() != 0;
        this.upCount = in.readInt();
        this.summary = in.readString();
        this.user = in.readParcelable(QaAuthor.class.getClassLoader());
        this.question = in.readParcelable(Question.class.getClassLoader());
        this.isRewriteStyle = in.readByte() != 0;
    }

    public static final Creator<QaListWrappers> CREATOR = new Creator<QaListWrappers>() {
        @Override
        public QaListWrappers createFromParcel(Parcel source) {return new QaListWrappers(source);}

        @Override
        public QaListWrappers[] newArray(int size) {return new QaListWrappers[size];}
    };
}
