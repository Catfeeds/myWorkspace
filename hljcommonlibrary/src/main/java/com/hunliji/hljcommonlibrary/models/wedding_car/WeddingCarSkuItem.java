package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2017/12/27 0027.
 */

public class WeddingCarSkuItem implements Parcelable {

    long id;
    String property;
    @SerializedName(value = "property_id")
    long propertyId;
    @SerializedName(value = "sku_id")
    long skuId;
    String value;
    @SerializedName(value = "value_id")
    long valueId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getValueId() {
        return valueId;
    }

    public void setValueId(long valueId) {
        this.valueId = valueId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.property);
        dest.writeLong(this.propertyId);
        dest.writeLong(this.skuId);
        dest.writeString(this.value);
        dest.writeLong(this.valueId);
    }

    public WeddingCarSkuItem() {}

    protected WeddingCarSkuItem(Parcel in) {
        this.id = in.readLong();
        this.property = in.readString();
        this.propertyId = in.readLong();
        this.skuId = in.readLong();
        this.value = in.readString();
        this.valueId = in.readLong();
    }

    public static final Parcelable.Creator<WeddingCarSkuItem> CREATOR = new Parcelable
            .Creator<WeddingCarSkuItem>() {
        @Override
        public WeddingCarSkuItem createFromParcel(Parcel source) {
            return new WeddingCarSkuItem(source);
        }

        @Override
        public WeddingCarSkuItem[] newArray(int size) {return new WeddingCarSkuItem[size];}
    };
}
