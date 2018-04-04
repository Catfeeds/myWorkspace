package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/12.
 */

public class CommunityThreadIdBody implements Parcelable {
    @SerializedName("thread_id")
    long threadId;

    public CommunityThreadIdBody(long threadId) {
        this.threadId = threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeLong(this.threadId);}

    public CommunityThreadIdBody() {}

    protected CommunityThreadIdBody(Parcel in) {this.threadId = in.readLong();}

    public static final Parcelable.Creator<CommunityThreadIdBody> CREATOR = new Parcelable
            .Creator<CommunityThreadIdBody>() {
        @Override
        public CommunityThreadIdBody createFromParcel(Parcel source) {
            return new CommunityThreadIdBody(source);
        }

        @Override
        public CommunityThreadIdBody[] newArray(int size) {return new CommunityThreadIdBody[size];}
    };
}
