package com.hunliji.hljquestionanswer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 16/8/23.
 */
public class AnswerCommentResponse implements Parcelable {
    long id;
    @SerializedName(value = "feed_comment_count")
    int totalCount;
    AnswerComment comment;

    public int getTotalCount() {
        return totalCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AnswerComment getComment() {
        return comment;
    }

    public void setComment(AnswerComment comment) {
        this.comment = comment;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.totalCount);
        dest.writeParcelable(this.comment, flags);
    }

    protected AnswerCommentResponse(Parcel in) {
        this.id = in.readLong();
        this.totalCount = in.readInt();
        this.comment = in.readParcelable(AnswerComment.class.getClassLoader());
    }

    public static final Creator<AnswerCommentResponse> CREATOR = new 
            Creator<AnswerCommentResponse>() {
        @Override
        public AnswerCommentResponse createFromParcel(Parcel source) {
            return new AnswerCommentResponse(source);
        }

        @Override
        public AnswerCommentResponse[] newArray(int size) {return new AnswerCommentResponse[size];}
    };
}
