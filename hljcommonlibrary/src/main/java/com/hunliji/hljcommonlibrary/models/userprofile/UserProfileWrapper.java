package com.hunliji.hljcommonlibrary.models.userprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/8/30.
 */
public class UserProfileWrapper implements Parcelable {
    @SerializedName(value = "user")
    UserProfile userProfile;
    @SerializedName(value = "story_count")
    int storyCount;
    @SerializedName(value = "answer_count")
    int answerCount;
    @SerializedName(value = "comment_count")
    int commentCount;
    @SerializedName(value = "community_thread_count")
    int communityThreadCount;
    @SerializedName(value = "note_count")
    int noteCount;//笔记数

    public UserProfileWrapper() {}

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getCommunityThreadCount() {
        return communityThreadCount;
    }

    public int getStoryCount() {
        return storyCount;
    }

    public int getNoteCount() {
        return noteCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userProfile, flags);
        dest.writeInt(this.storyCount);
        dest.writeInt(this.answerCount);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.communityThreadCount);
        dest.writeInt(this.noteCount);
    }

    protected UserProfileWrapper(Parcel in) {
        this.userProfile = in.readParcelable(UserProfile.class.getClassLoader());
        this.storyCount = in.readInt();
        this.answerCount = in.readInt();
        this.commentCount = in.readInt();
        this.communityThreadCount = in.readInt();
        this.noteCount = in.readInt();
    }

    public static final Creator<UserProfileWrapper> CREATOR = new Creator<UserProfileWrapper>() {
        @Override
        public UserProfileWrapper createFromParcel(Parcel source) {
            return new UserProfileWrapper(source);
        }

        @Override
        public UserProfileWrapper[] newArray(int size) {return new UserProfileWrapper[size];}
    };
}
