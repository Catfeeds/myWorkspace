package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 系统标签详情页关注body
 * Created by jinxin on 2017/7/12 0012.
 */

public class FollowBody implements Parcelable {

    String followable_type;//Mark
    long id;

    public void setFollowable_type(String followable_type) {
        this.followable_type = followable_type;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.followable_type);
        dest.writeLong(this.id);
    }

    public FollowBody() {}

    protected FollowBody(Parcel in) {
        this.followable_type = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<FollowBody> CREATOR = new Parcelable
            .Creator<FollowBody>() {
        @Override
        public FollowBody createFromParcel(Parcel source) {return new FollowBody(source);}

        @Override
        public FollowBody[] newArray(int size) {return new FollowBody[size];}
    };
}
