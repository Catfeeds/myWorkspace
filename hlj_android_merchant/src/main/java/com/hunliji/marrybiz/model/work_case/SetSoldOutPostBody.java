package com.hunliji.marrybiz.model.work_case;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 套餐、案例上下架
 * Created by chen_bin on 2017/2/6 0006.
 */
public class SetSoldOutPostBody implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "sold_out")
    int soldOut;

    public SetSoldOutPostBody(long id, int soldOut) {
        this.id = id;
        this.soldOut = soldOut;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.soldOut);
    }

    public SetSoldOutPostBody() {}

    protected SetSoldOutPostBody(Parcel in) {
        this.id = in.readLong();
        this.soldOut = in.readInt();
    }

    public static final Creator<SetSoldOutPostBody> CREATOR = new Creator<SetSoldOutPostBody>() {
        @Override
        public SetSoldOutPostBody createFromParcel(Parcel source) {
            return new SetSoldOutPostBody(source);
        }

        @Override
        public SetSoldOutPostBody[] newArray(int size) {return new SetSoldOutPostBody[size];}
    };
}
