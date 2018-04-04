package com.example.suncloud.hljweblibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class JsInfo implements Parcelable {

    @SerializedName("js_cdn_md5")
    private String jsCdnMd5;
    @SerializedName("js_cdn_url")
    private String jsCdnUrl;

    public String getJsCdnMd5() {
        return jsCdnMd5;
    }

    public String getJsCdnUrl() {
        return jsCdnUrl;
    }

    protected JsInfo(Parcel in) {
        jsCdnMd5 = in.readString();
        jsCdnUrl = in.readString();
    }

    public static final Creator<JsInfo> CREATOR = new Creator<JsInfo>() {
        @Override
        public JsInfo createFromParcel(Parcel in) {
            return new JsInfo(in);
        }

        @Override
        public JsInfo[] newArray(int size) {
            return new JsInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jsCdnMd5);
        parcel.writeString(jsCdnUrl);
    }
}
