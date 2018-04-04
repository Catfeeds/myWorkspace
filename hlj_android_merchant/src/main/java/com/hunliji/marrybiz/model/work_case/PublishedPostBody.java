package com.hunliji.marrybiz.model.work_case;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 定制套餐上下架
 * Created by chen_bin on 2017/2/6 0006.
 */
public class PublishedPostBody implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "is_published")
    int isPublished;

    public PublishedPostBody(long id, int isPublished) {
        this.id = id;
        this.isPublished = isPublished;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.isPublished);
    }

    public PublishedPostBody() {}

    protected PublishedPostBody(Parcel in) {
        this.id = in.readLong();
        this.isPublished = in.readInt();
    }

    public static final Creator<PublishedPostBody> CREATOR = new Creator<PublishedPostBody>() {
        @Override
        public PublishedPostBody createFromParcel(Parcel source) {
            return new PublishedPostBody(source);
        }

        @Override
        public PublishedPostBody[] newArray(int size) {return new PublishedPostBody[size];}
    };
}
