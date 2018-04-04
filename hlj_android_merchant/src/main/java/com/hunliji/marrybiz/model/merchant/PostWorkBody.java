package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/4.套餐提交参数
 */

public class PostWorkBody implements Parcelable {

    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "seat")
    int seat;//位置 仅支持 1 2 3
    @SerializedName(value = "set_meal_id")
    long setMealId;//套餐id

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.seat);
        dest.writeLong(this.setMealId);
    }

    public PostWorkBody(long id) {
        this.id = id;
    }

    public PostWorkBody(int seat, long setMealId) {
        this.seat = seat;
        this.setMealId = setMealId;
    }

    protected PostWorkBody(Parcel in) {
        this.id = in.readLong();
        this.seat = in.readInt();
        this.setMealId = in.readLong();
    }

    public static final Creator<PostWorkBody> CREATOR = new Creator<PostWorkBody>() {
        @Override
        public PostWorkBody createFromParcel(Parcel source) {return new PostWorkBody(source);}

        @Override
        public PostWorkBody[] newArray(int size) {return new PostWorkBody[size];}
    };
}
