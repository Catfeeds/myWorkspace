package me.suncloud.marrymemo.model.bindpartner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;

/**
 * Created by werther on 16/8/22.
 */
public class PartnerBindResult implements Parcelable {
    String action;
    long id;
    @SerializedName(value = "share")
    ShareInfo shareInfo;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeLong(this.id);
        dest.writeParcelable(this.shareInfo, flags);
    }

    public PartnerBindResult() {}

    protected PartnerBindResult(Parcel in) {
        this.action = in.readString();
        this.id = in.readLong();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<PartnerBindResult> CREATOR = new Parcelable
            .Creator<PartnerBindResult>() {
        @Override
        public PartnerBindResult createFromParcel(Parcel source) {
            return new PartnerBindResult(source);
        }

        @Override
        public PartnerBindResult[] newArray(int size) {return new PartnerBindResult[size];}
    };

    public String getAction() {
        return action;
    }

    public long getId() {
        return id;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }
}
