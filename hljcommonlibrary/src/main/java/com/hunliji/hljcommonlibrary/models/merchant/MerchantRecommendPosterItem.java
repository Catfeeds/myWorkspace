package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;

import java.util.ArrayList;

/**
 * 仅旗舰版配置大图模式商家 返回数据
 * Created by chen_bin on 2017/5/19 0019.
 */
public class MerchantRecommendPosterItem implements Parcelable {
    @SerializedName(value = "posters")
    private ArrayList<Poster> posters;
    @SerializedName(value = "show_type")
    private int showType; //1双图 2竖图 3 横图

    public transient static final int DOUBLE_IMAGE = 1;
    public transient static final int SIMPLE_VERTICAL_IMAGE = 2;
    public transient static final int SIMPLE_HORIZONTAL_IMAGE = 3;

    public ArrayList<Poster> getPosters() {
        return posters;
    }

    public int getShowType() {
        return showType;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.posters);
        dest.writeInt(this.showType);
    }

    public MerchantRecommendPosterItem() {}

    protected MerchantRecommendPosterItem(Parcel in) {
        this.posters = in.createTypedArrayList(Poster.CREATOR);
        this.showType = in.readInt();
    }

    public static final Creator<MerchantRecommendPosterItem> CREATOR = new
            Creator<MerchantRecommendPosterItem>() {
        @Override
        public MerchantRecommendPosterItem createFromParcel(Parcel source) {
            return new MerchantRecommendPosterItem(source);
        }

        @Override
        public MerchantRecommendPosterItem[] newArray(int size) {
            return new MerchantRecommendPosterItem[size];
        }
    };
}
