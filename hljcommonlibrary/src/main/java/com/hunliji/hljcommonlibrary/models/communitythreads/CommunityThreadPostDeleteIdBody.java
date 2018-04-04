package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/13.
 */

public class CommunityThreadPostDeleteIdBody implements Parcelable {
    @SerializedName("thread_id")
    long threadId;
    @SerializedName("post_id")
    long postId;

    public CommunityThreadPostDeleteIdBody(long threadId, long postId) {
        this.threadId = threadId;
        this.postId = postId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.threadId);
        dest.writeLong(this.postId);
    }

    public CommunityThreadPostDeleteIdBody() {}

    protected CommunityThreadPostDeleteIdBody(Parcel in) {
        this.threadId = in.readLong();
        this.postId = in.readLong();
    }

    public static final Parcelable.Creator<CommunityThreadPostDeleteIdBody> CREATOR = new
            Parcelable.Creator<CommunityThreadPostDeleteIdBody>() {
        @Override
        public CommunityThreadPostDeleteIdBody createFromParcel(Parcel source) {
            return new CommunityThreadPostDeleteIdBody(source);
        }

        @Override
        public CommunityThreadPostDeleteIdBody[] newArray(int size) {return new CommunityThreadPostDeleteIdBody[size];}
    };
}
