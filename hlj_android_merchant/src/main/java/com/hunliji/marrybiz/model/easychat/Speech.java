package com.hunliji.marrybiz.model.easychat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hua_rong on 2017/7/7.
 */

public class Speech implements Parcelable {

    private String reason;
    private String speech;//问候语
    private int status;//0待审核 1通过 2不通过

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reason);
        dest.writeString(this.speech);
        dest.writeInt(this.status);
    }

    public Speech() {}

    protected Speech(Parcel in) {
        this.reason = in.readString();
        this.speech = in.readString();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<Speech> CREATOR = new Parcelable.Creator<Speech>() {
        @Override
        public Speech createFromParcel(Parcel source) {return new Speech(source);}

        @Override
        public Speech[] newArray(int size) {return new Speech[size];}
    };
}
