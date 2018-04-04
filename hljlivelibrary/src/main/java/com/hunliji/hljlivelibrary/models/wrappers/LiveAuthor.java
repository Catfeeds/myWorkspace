package com.hunliji.hljlivelibrary.models.wrappers;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Member;

/**
 * Created by Suncloud on 2016/11/4.
 */

public class LiveAuthor extends Author {
    @SerializedName("live_role")
    private int liveRole; //直播用户类型 1 主播，2 嘉宾，3 用户

    public void setLiveRole(int liveRole) {
        this.liveRole = liveRole;
    }

    public int getLiveRole() {
        return liveRole;
    }


    public LiveAuthor() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.liveRole);
    }

    protected LiveAuthor(Parcel in) {
        super(in);
        this.liveRole = in.readInt();
    }

    public static final Creator<LiveAuthor> CREATOR = new Creator<LiveAuthor>() {
        @Override
        public LiveAuthor createFromParcel(Parcel source) {return new LiveAuthor(source);}

        @Override
        public LiveAuthor[] newArray(int size) {return new LiveAuthor[size];}
    };
}
