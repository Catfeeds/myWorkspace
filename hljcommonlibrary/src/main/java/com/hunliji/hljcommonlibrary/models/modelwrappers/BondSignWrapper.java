package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.IconSign;

/**
 * Created by werther on 16/7/27.
 * Gson反序列化Wrapper,没有Entity意义,只用于包装数据方便反序列化
 */
public class BondSignWrapper implements Parcelable {

    @SerializedName(value = "bond_sign_url")
    String bondSignUrl;
    @SerializedName(value = "bond_sign")
    IconSign bondSign;

    public String getBondSignUrl() {
        return bondSignUrl;
    }

    public IconSign getBondSign() {
        return bondSign;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bondSignUrl);
        dest.writeParcelable(this.bondSign, flags);
    }

    public BondSignWrapper() {}

    protected BondSignWrapper(Parcel in) {
        this.bondSignUrl = in.readString();
        this.bondSign = in.readParcelable(IconSign.class.getClassLoader());
    }

    public static final Parcelable.Creator<BondSignWrapper> CREATOR = new Parcelable
            .Creator<BondSignWrapper>() {
        @Override
        public BondSignWrapper createFromParcel(Parcel source) {return new BondSignWrapper(source);}

        @Override
        public BondSignWrapper[] newArray(int size) {return new BondSignWrapper[size];}
    };
}
