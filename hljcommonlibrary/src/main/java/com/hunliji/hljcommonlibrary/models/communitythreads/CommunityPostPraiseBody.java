package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/12.
 */

public class CommunityPostPraiseBody implements Parcelable {
    @SerializedName("post_id")
    long postId;

    public CommunityPostPraiseBody(long postId) {
        this.postId = postId;
    }

    public long getPostId() {
        return postId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeLong(this.postId);}

    protected CommunityPostPraiseBody(Parcel in) {this.postId = in.readLong();}

    public static final Parcelable.Creator<CommunityPostPraiseBody> CREATOR = new Parcelable
            .Creator<CommunityPostPraiseBody>() {
        @Override
        public CommunityPostPraiseBody createFromParcel(Parcel source) {
            return new CommunityPostPraiseBody(source);
        }

        @Override
        public CommunityPostPraiseBody[] newArray(int size) {return new CommunityPostPraiseBody[size];}
    };
}
