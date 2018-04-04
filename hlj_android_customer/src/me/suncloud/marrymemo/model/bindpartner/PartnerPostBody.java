package me.suncloud.marrymemo.model.bindpartner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/8/22.
 */
public class PartnerPostBody implements Parcelable {
    int gender;
    @SerializedName(value = "partner_phone")
    String partnerPhone;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.gender);
        dest.writeString(this.partnerPhone);
    }

    public PartnerPostBody() {}

    protected PartnerPostBody(Parcel in) {
        this.gender = in.readInt();
        this.partnerPhone = in.readString();
    }

    public static final Parcelable.Creator<PartnerPostBody> CREATOR = new Parcelable
            .Creator<PartnerPostBody>() {
        @Override
        public PartnerPostBody createFromParcel(Parcel source) {return new PartnerPostBody(source);}

        @Override
        public PartnerPostBody[] newArray(int size) {return new PartnerPostBody[size];}
    };

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }
}
