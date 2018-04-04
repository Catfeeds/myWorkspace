package com.hunliji.hljcommonlibrary.models.questionanswer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.modelwrappers.AnswerUsers;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2016/8/17.问题详情
 */
public class Question implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "last_answer_id")
    long lastAnswerId;
    @SerializedName(value = "answer_id")
    long answerId;//回答id 0--未回答
    @SerializedName(value = "by_faker")
    String byFaker;
    @SerializedName(value = "city_code")
    String cityCode;
    @SerializedName(value = "answer_count_faker")
    String answerCountFaker;
    @SerializedName(value = "content")
    String content;//内容
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "title")
    String title;//标题
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "last_answer_time")
    DateTime lastAnswerTime;
    @SerializedName(value = "deleted")
    boolean deleted;
    @SerializedName(value = "is_allow_modify")
    boolean isAllowModify;//修改 是否可编辑 0禁止修改1允许修改
    @SerializedName(value = "is_follow")
    boolean isFollow;//是否关注
    @SerializedName(value = "answer_count")
    int answerCount;
    @SerializedName(value = "status")
    int status;
    @SerializedName(value = "watch_count")
    int watchCount;//浏览数
    @SerializedName(value = "weight")
    int weight;
    @SerializedName(value = "mark")
    ArrayList<Mark> marks;
    @SerializedName(value = "user")
    QaAuthor user;//提问用户
    @SerializedName(value = "user_role")
    int userRole;//当前用户角色 0：路人 1：提问者 2：回答者之一
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @Expose(serialize = false)
    @SerializedName(value = "answer_users")
    AnswerUsers answerUsers;
    @SerializedName(value = "is_questioner")
    boolean isQuestioner;//是否提问者 0:不是 1：是
    @SerializedName(value = "is_answered")
    boolean isAnswered;//商家是否已回答 true已回答 false没有回答
    @SerializedName(value = "appeal_status")
    Integer appealStatus;//申诉状态（没有=未申诉）申诉状态 0待审核 1通过 2未通过
    @SerializedName(value = "follow_count")
    int followCount;//同问数量
    @SerializedName(value = "can_answer")
    boolean canAnswer;
    int type;//区分 社区问答-1 和商家问答(新问答)-2
    Merchant merchant;//新问答 商家信息
    //标签全部问题新增参数
    Answer answer;
    @SerializedName("total_up_count")
    int totalUpCount;//问题总赞数


    public boolean isCanAnswer() {
        return canAnswer;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getLastAnswerId() {
        return lastAnswerId;
    }

    public String getByFaker() {
        return byFaker;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getAnswerCountFaker() {
        return answerCountFaker;
    }

    public String getContent() {
        return content;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public DateTime getLastAnswerTime() {
        return lastAnswerTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isAllowModify() {
        return isAllowModify;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getStatus() {
        return status;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getWeight() {
        return weight;
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }

    public QaAuthor getUser() {
        return user;
    }

    public int getUserRole() {
        return userRole;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public AnswerUsers getAnswerUsers() {
        return answerUsers;
    }

    public boolean isQuestioner() {
        return isQuestioner;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public Answer getAnswer() {
        if (answer == null) {
            answer = new Answer();
        }
        return answer;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public int getType() {
        return type;
    }

    public int getTotalUpCount() {
        return totalUpCount;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Question() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeLong(this.lastAnswerId);
        dest.writeLong(this.answerId);
        dest.writeString(this.byFaker);
        dest.writeString(this.cityCode);
        dest.writeString(this.answerCountFaker);
        dest.writeString(this.content);
        dest.writeString(this.coverPath);
        dest.writeString(this.title);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeSerializable(this.lastAnswerTime);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAllowModify ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFollow ? (byte) 1 : (byte) 0);
        dest.writeInt(this.answerCount);
        dest.writeInt(this.status);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.weight);
        dest.writeTypedList(this.marks);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.userRole);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeParcelable(this.answerUsers, flags);
        dest.writeByte(this.isQuestioner ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnswered ? (byte) 1 : (byte) 0);
        dest.writeValue(this.appealStatus);
        dest.writeInt(this.followCount);
        dest.writeByte(this.canAnswer ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.answer, flags);
        dest.writeInt(this.totalUpCount);
    }

    protected Question(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.lastAnswerId = in.readLong();
        this.answerId = in.readLong();
        this.byFaker = in.readString();
        this.cityCode = in.readString();
        this.answerCountFaker = in.readString();
        this.content = in.readString();
        this.coverPath = in.readString();
        this.title = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.lastAnswerTime = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.isAllowModify = in.readByte() != 0;
        this.isFollow = in.readByte() != 0;
        this.answerCount = in.readInt();
        this.status = in.readInt();
        this.watchCount = in.readInt();
        this.weight = in.readInt();
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.user = in.readParcelable(QaAuthor.class.getClassLoader());
        this.userRole = in.readInt();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.answerUsers = in.readParcelable(AnswerUsers.class.getClassLoader());
        this.isQuestioner = in.readByte() != 0;
        this.isAnswered = in.readByte() != 0;
        this.appealStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.followCount = in.readInt();
        this.canAnswer = in.readByte() != 0;
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        this.totalUpCount = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {return new Question(source);}

        @Override
        public Question[] newArray(int size) {return new Question[size];}
    };
}
