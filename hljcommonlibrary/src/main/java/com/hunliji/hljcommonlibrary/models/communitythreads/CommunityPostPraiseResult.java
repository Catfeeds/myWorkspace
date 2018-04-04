package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/12.
 */

public class CommunityPostPraiseResult implements Parcelable {
    @SerializedName("belong_praise")
    int belongPraise;

    public int getBelongPraise() {
        return belongPraise;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.belongPraise);}

    public CommunityPostPraiseResult() {}

    protected CommunityPostPraiseResult(Parcel in) {this.belongPraise = in.readInt();}

    public static final Parcelable.Creator<CommunityPostPraiseResult> CREATOR = new Parcelable
            .Creator<CommunityPostPraiseResult>() {
        @Override
        public CommunityPostPraiseResult createFromParcel(Parcel source) {
            return new CommunityPostPraiseResult(source);
        }

        @Override
        public CommunityPostPraiseResult[] newArray(int size) {return new CommunityPostPraiseResult[size];}
    };
}
