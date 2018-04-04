package com.hunliji.hljquestionanswer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2016/9/18.问答全部列表特殊model
 */
public class LatestQuestion implements Parcelable {

    long id;
    String title;
    @SerializedName(value = "answer_count")
    int answerCount;
    Answer answer;
    @SerializedName(value = "last_answer_time")
    DateTime lastAnswerTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public Answer getAnswer() {
        if (answer == null) {
            answer = new Answer();
        }
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public DateTime getLastAnswerTime() {
        return lastAnswerTime;
    }

    public LatestQuestion() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.answerCount);
        dest.writeParcelable(this.answer, flags);
        dest.writeSerializable(this.lastAnswerTime);
    }

    protected LatestQuestion(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.answerCount = in.readInt();
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        this.lastAnswerTime = (DateTime) in.readSerializable();
    }

    public static final Creator<LatestQuestion> CREATOR = new
            Creator<LatestQuestion>() {
        @Override
        public LatestQuestion createFromParcel(Parcel source) {
            return new LatestQuestion(source);
        }

        @Override
        public LatestQuestion[] newArray(int size) {return new LatestQuestion[size];}
    };
}
