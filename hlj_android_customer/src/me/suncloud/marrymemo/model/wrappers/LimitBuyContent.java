package me.suncloud.marrymemo.model.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/9/6.旅拍限时团购
 */

public class LimitBuyContent extends Work {

    public static final String MEAL = "Meal";
    public static final String PIC = "Pic";

    private String type;//Meal:套餐；Pic:单张图片，只取pic字段
    @SerializedName("pic")
    private String pic;


    public String getType() {
        return type;
    }

    public String getPic() {
        return pic;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(this.type);
        dest.writeString(this.pic);
    }

    public LimitBuyContent() {}

    protected LimitBuyContent(Parcel in) {
        super(in);
        this.type = in.readString();
        this.pic = in.readString();
    }

    public static final Parcelable.Creator<LimitBuyContent> CREATOR = new Parcelable.Creator<LimitBuyContent>() {
        @Override
        public LimitBuyContent createFromParcel(Parcel source) {return new LimitBuyContent(source);}

        @Override
        public LimitBuyContent[] newArray(int size) {return new LimitBuyContent[size];}
    };
}
