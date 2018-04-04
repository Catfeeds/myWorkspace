package com.hunliji.hljquestionanswer.models.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2016/11/10.推荐问答列表
 */

public class RecQaWrappers implements Parcelable {

    @SerializedName(value = "answer_count")
    int answerCount;
    long id;
    @SerializedName(value = "mark")
    ArrayList<Mark> markList;
    String title;
    @SerializedName(value = "watch_count")
    int watchCount;
    Answer answer;
    @SerializedName(value = "last_answer_time")
    DateTime lastAnswerTime;

    public int getAnswerCount() {
        return answerCount;
    }

    public long getId() {
        return id;
    }

    public ArrayList<Mark> getMarkList() {
        return markList;
    }

    public String getTitle() {
        return title;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public Answer getAnswer() {
        return answer;
    }

    public DateTime getLastAnswerTime() {
        return lastAnswerTime;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.answerCount);
        dest.writeLong(this.id);
        dest.writeTypedList(this.markList);
        dest.writeString(this.title);
        dest.writeInt(this.watchCount);
        dest.writeParcelable(this.answer, flags);
        dest.writeSerializable(this.lastAnswerTime);
    }

    public RecQaWrappers() {}

    protected RecQaWrappers(Parcel in) {
        this.answerCount = in.readInt();
        this.id = in.readLong();
        this.markList = in.createTypedArrayList(Mark.CREATOR);
        this.title = in.readString();
        this.watchCount = in.readInt();
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        this.lastAnswerTime = (DateTime) in.readSerializable();
    }

    public static final Creator<RecQaWrappers> CREATOR = new Creator<RecQaWrappers>() {
        @Override
        public RecQaWrappers createFromParcel(Parcel source) {return new RecQaWrappers(source);}

        @Override
        public RecQaWrappers[] newArray(int size) {return new RecQaWrappers[size];}
    };
}
