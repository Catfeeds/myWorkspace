package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Mark;

/**
 * note_mark
 * Created by chen_bin on 2017/7/6 0006.
 */
public class NoteMark extends Mark implements Parcelable {
    @SerializedName(value = "note_count")
    private int noteCount;
    @SerializedName(value = "shop_product_count")
    private int productCount;
    @SerializedName(value = "set_meal_count")
    private int setMealCount;
    @SerializedName(value = "is_follow")
    private boolean isFollow;

    public int getNoteCount() {
        return noteCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getSetMealCount() {
        return setMealCount;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.noteCount);
        dest.writeInt(this.productCount);
        dest.writeInt(this.setMealCount);
        dest.writeByte(this.isFollow ? (byte) 1 : (byte) 0);
    }

    public NoteMark() {}

    protected NoteMark(Parcel in) {
        super(in);
        this.noteCount = in.readInt();
        this.productCount = in.readInt();
        this.setMealCount = in.readInt();
        this.isFollow = in.readByte() != 0;
    }

    public static final Creator<NoteMark> CREATOR = new Creator<NoteMark>() {
        @Override
        public NoteMark createFromParcel(Parcel source) {return new NoteMark(source);}

        @Override
        public NoteMark[] newArray(int size) {return new NoteMark[size];}
    };
}
