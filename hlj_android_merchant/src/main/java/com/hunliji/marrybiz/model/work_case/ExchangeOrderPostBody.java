package com.hunliji.marrybiz.model.work_case;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 套餐案例排序
 * Created by chen_bin on 2017/2/6 0006.
 */
public class ExchangeOrderPostBody implements Parcelable {
    @SerializedName(value = "set_meal_ids")
    String setMealIds;

    public ExchangeOrderPostBody() {}

    public ExchangeOrderPostBody(String setMealIds) {
        this.setMealIds = setMealIds;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.setMealIds);}

    protected ExchangeOrderPostBody(Parcel in) {this.setMealIds = in.readString();}

    public static final Creator<ExchangeOrderPostBody> CREATOR = new
            Creator<ExchangeOrderPostBody>() {
        @Override
        public ExchangeOrderPostBody createFromParcel(Parcel source) {
            return new ExchangeOrderPostBody(source);
        }

        @Override
        public ExchangeOrderPostBody[] newArray(int size) {return new ExchangeOrderPostBody[size];}
    };
}
