package me.suncloud.marrymemo.model.bindpartner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/9/5.
 */
public class BindAgreePostBody implements Parcelable {
    @SerializedName(value = "partner_user_id")
    long partnerUserId;

    public long getPartnerUserId() {
        return partnerUserId;
    }

    public BindAgreePostBody(long partnerUserId) {
        this.partnerUserId = partnerUserId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeLong(this.partnerUserId);}

    public BindAgreePostBody() {}

    protected BindAgreePostBody(Parcel in) {this.partnerUserId = in.readLong();}

    public static final Parcelable.Creator<BindAgreePostBody> CREATOR = new Parcelable
            .Creator<BindAgreePostBody>() {
        @Override
        public BindAgreePostBody createFromParcel(Parcel source) {
            return new BindAgreePostBody(source);
        }

        @Override
        public BindAgreePostBody[] newArray(int size) {return new BindAgreePostBody[size];}
    };
}
