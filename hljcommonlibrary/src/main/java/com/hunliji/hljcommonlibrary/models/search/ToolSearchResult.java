package com.hunliji.hljcommonlibrary.models.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/12/1.
 */

public class ToolSearchResult implements Parcelable {
    long id;
    String name;
    String description;
    @SerializedName("target_type")
    int targetType;
    @SerializedName("target_id")
    long targetId;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetType() {
        return targetType;
    }

    public long getTargetId() {
        return targetId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.targetType);
        dest.writeLong(this.targetId);
    }

    public ToolSearchResult() {}

    protected ToolSearchResult(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.description = in.readString();
        this.targetType = in.readInt();
        this.targetId = in.readLong();
    }

    public static final Creator<ToolSearchResult> CREATOR = new Creator<ToolSearchResult>() {
        @Override
        public ToolSearchResult createFromParcel(Parcel source) {
            return new ToolSearchResult(source);
        }

        @Override
        public ToolSearchResult[] newArray(int size) {return new ToolSearchResult[size];}
    };
}
