package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/12/28.婚车筛选
 */

public class CarFilter implements Parcelable {

    public static final String MEAL_TAB = "meal";
    public static final String OPTIONAL_TAB = "optional";

    @SerializedName("brand")
    private List<Label> brandList;//款型
    @SerializedName("color")
    private List<Label> colorList;//颜色
    @SerializedName("product_brand")
    private List<Label> productBrandList;//	品牌
    @SerializedName("sort")
    private List<Label> sortList;//排序字段


    public List<Label> getBrandList() {
        return brandList;
    }

    public List<Label> getColorList() {
        return colorList;
    }

    public List<Label> getProductBrandList() {
        return productBrandList;
    }

    public List<Label> getSortList() {
        return sortList;
    }


    public CarFilter() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.brandList);
        dest.writeTypedList(this.colorList);
        dest.writeTypedList(this.productBrandList);
        dest.writeTypedList(this.sortList);
    }

    protected CarFilter(Parcel in) {
        this.brandList = in.createTypedArrayList(Label.CREATOR);
        this.colorList = in.createTypedArrayList(Label.CREATOR);
        this.productBrandList = in.createTypedArrayList(Label.CREATOR);
        this.sortList = in.createTypedArrayList(Label.CREATOR);
    }

    public static final Creator<CarFilter> CREATOR = new Creator<CarFilter>() {
        @Override
        public CarFilter createFromParcel(Parcel source) {return new CarFilter(source);}

        @Override
        public CarFilter[] newArray(int size) {return new CarFilter[size];}
    };
}
