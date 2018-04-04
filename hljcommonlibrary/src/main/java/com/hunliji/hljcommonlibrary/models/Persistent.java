package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/3/13 0013.
 */
public class Persistent implements Parcelable {
    @SerializedName(value = "iphone")
    String iphone;
    @SerializedName(value = "ipad")
    String ipad;
    @SerializedName(value = "flash")
    String flash;
    @SerializedName(value = "m3u8_640_480")
    String streamingPhone;
    @SerializedName(value = "m3u8_1024_768")
    String streamingPad;
    @SerializedName(value = "vframe")
    String vframe;
    @SerializedName(value = "domain")
    String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIphone() {
        return generatePathWithDomain(iphone);
    }

    public String getStreamingPhone() {
        return generatePathWithDomain(streamingPhone);
    }

    public String getScreenShot() {
        return generatePathWithDomain(vframe);
    }

    private String generatePathWithDomain(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        } else {
            if (path.toLowerCase()
                    .startsWith("http") || path.toLowerCase()
                    .startsWith("https")) {
                return path;
            } else {
                return domain + path;
            }
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iphone);
        dest.writeString(this.ipad);
        dest.writeString(this.flash);
        dest.writeString(this.streamingPhone);
        dest.writeString(this.streamingPad);
        dest.writeString(this.vframe);
        dest.writeString(this.domain);
    }

    public Persistent() {}

    protected Persistent(Parcel in) {
        this.iphone = in.readString();
        this.ipad = in.readString();
        this.flash = in.readString();
        this.streamingPhone = in.readString();
        this.streamingPad = in.readString();
        this.vframe = in.readString();
        this.domain = in.readString();
    }

    public static final Creator<Persistent> CREATOR = new Creator<Persistent>() {
        @Override
        public Persistent createFromParcel(Parcel source) {return new Persistent(source);}

        @Override
        public Persistent[] newArray(int size) {return new Persistent[size];}
    };
}
