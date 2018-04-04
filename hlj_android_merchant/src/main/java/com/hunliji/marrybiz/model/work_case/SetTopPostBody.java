package com.hunliji.marrybiz.model.work_case;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 置顶提交
 * Created by chen_bin on 2017/2/7 0007.
 */
public class SetTopPostBody implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "is_top")
    int isTop;

    public SetTopPostBody(long id, int isTop) {
        this.id = id;
        this.isTop = isTop;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.isTop);
    }

    public SetTopPostBody() {}

    protected SetTopPostBody(Parcel in) {
        this.id = in.readLong();
        this.isTop = in.readInt();
    }

    public static final Creator<SetTopPostBody> CREATOR = new Creator<SetTopPostBody>() {
        @Override
        public SetTopPostBody createFromParcel(Parcel source) {return new SetTopPostBody(source);}

        @Override
        public SetTopPostBody[] newArray(int size) {return new SetTopPostBody[size];}
    };
}