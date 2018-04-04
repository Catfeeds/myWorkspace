package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by luohanlin on 2018/3/2.
 */

public class ProductParameter implements Parcelable {
    @SerializedName("key_name")
    private String keyName;
    private String value;

    public ProductParameter(String keyName, String value) {
        this.keyName = keyName;
        this.value = value;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.keyName);
        dest.writeString(this.value);
    }

    protected ProductParameter(Parcel in) {
        this.keyName = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<ProductParameter> CREATOR = new Parcelable
            .Creator<ProductParameter>() {
        @Override
        public ProductParameter createFromParcel(Parcel source) {return new ProductParameter(source);}

        @Override
        public ProductParameter[] newArray(int size) {return new ProductParameter[size];}
    };
}
