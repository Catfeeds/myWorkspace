package com.hunliji.marrybiz.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/12/14.商家订单筛选
 */

public class MerchantOrderFilter implements Parcelable {

    long id;
    @SerializedName("filter_title")
    String filterTitle;// 筛选条件名称
    @SerializedName("filter_key")
    String filterKey;// 对应列表筛选字段
    @SerializedName("filter_options")
    List<Label> filterOptions;//筛选项list


    public long getId() {
        return id;
    }

    public String getFilterTitle() {
        return filterTitle;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public List<Label> getFilterOptions() {
        return filterOptions;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.filterTitle);
        dest.writeString(this.filterKey);
        dest.writeTypedList(this.filterOptions);
    }

    public MerchantOrderFilter() {}

    protected MerchantOrderFilter(Parcel in) {
        this.id = in.readLong();
        this.filterTitle = in.readString();
        this.filterKey = in.readString();
        this.filterOptions = in.createTypedArrayList(Label.CREATOR);
    }

    public static final Creator<MerchantOrderFilter> CREATOR = new Creator<MerchantOrderFilter>() {
        @Override
        public MerchantOrderFilter createFromParcel(Parcel source) {
            return new MerchantOrderFilter(source);
        }

        @Override
        public MerchantOrderFilter[] newArray(int size) {return new MerchantOrderFilter[size];}
    };
}
