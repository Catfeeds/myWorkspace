package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/8/26.
 * 话题所属小组,老版本中的小组分类,新版本中可用于将话题分组
 */
public class CommunityGroup implements Parcelable {
    long id;
    String title;
    boolean closed;
    boolean hidden;
    @SerializedName(value = "support_local")
    boolean supportLocal;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeByte(this.closed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeByte(this.supportLocal ? (byte) 1 : (byte) 0);
    }

    public CommunityGroup() {}

    protected CommunityGroup(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.closed = in.readByte() != 0;
        this.hidden = in.readByte() != 0;
        this.supportLocal = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CommunityGroup> CREATOR = new Parcelable
            .Creator<CommunityGroup>() {
        @Override
        public CommunityGroup createFromParcel(Parcel source) {return new CommunityGroup(source);}

        @Override
        public CommunityGroup[] newArray(int size) {return new CommunityGroup[size];}
    };

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isSupportLocal() {
        return supportLocal;
    }
}
