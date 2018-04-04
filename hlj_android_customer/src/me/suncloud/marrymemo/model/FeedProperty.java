package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 首页feeds tab 用到的mode id是String的property也可以使用
 *
 * @author jinxin 2016/8/18
 */
public class FeedProperty implements Parcelable {
    @SerializedName("id")
    private String idStr;
    private String name;
    private int type;
    @SerializedName(value = "is_shop_product")
    private boolean isShopProduct;//是否是婚品

    public String getName() {
        return name;
    }

    public String getStringId() {
        return idStr;
    }

    public int getType() {
        return type;
    }

    public boolean isShopProduct() {
        return isShopProduct;
    }

    public void setShopProduct(boolean shopProduct) {
        isShopProduct = shopProduct;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FeedProperty(){

    }

    /**
     * 为List之间的equals比较而重写
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FeedProperty) {
            FeedProperty f = (FeedProperty) o;
            return this.idStr.equals(f.getStringId())&&this.name.equals(f.getName());
        }
        return super.equals(o);
    }

    protected FeedProperty(Parcel in) {
        idStr = in.readString();
        name = in.readString();
    }

    public static final Creator<FeedProperty> CREATOR = new Creator<FeedProperty>() {
        @Override
        public FeedProperty createFromParcel(Parcel in) {
            return new FeedProperty(in);
        }

        @Override
        public FeedProperty[] newArray(int size) {
            return new FeedProperty[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idStr);
        parcel.writeString(name);
    }
}
