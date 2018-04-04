package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/15.
 */

public class WeddingPhotoPraiseBody implements Parcelable {
    @SerializedName("entity_type")
    String entityType;
    long id;
    int type;

    public WeddingPhotoPraiseBody(long id) {
        entityType = "CommunityThreadItem";
        this.id = id;
        type = 8;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.entityType);
        dest.writeLong(this.id);
        dest.writeInt(this.type);
    }

    protected WeddingPhotoPraiseBody(Parcel in) {
        this.entityType = in.readString();
        this.id = in.readLong();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<WeddingPhotoPraiseBody> CREATOR = new Parcelable
            .Creator<WeddingPhotoPraiseBody>() {
        @Override
        public WeddingPhotoPraiseBody createFromParcel(Parcel source) {
            return new WeddingPhotoPraiseBody(source);
        }

        @Override
        public WeddingPhotoPraiseBody[] newArray(int size) {
            return new WeddingPhotoPraiseBody[size];
        }
    };
}
