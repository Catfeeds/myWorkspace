package com.hunliji.hljcommonlibrary.models.merchant_feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.ShareInfo;

/**
 * Created by chen_bin on 2017/3/17 0017.
 */
public class PublishMerchantFeedResponse implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.shareInfo, flags);
    }

    public PublishMerchantFeedResponse() {}

    protected PublishMerchantFeedResponse(Parcel in) {
        this.id = in.readLong();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
    }

    public static final Creator<PublishMerchantFeedResponse> CREATOR = new
            Creator<PublishMerchantFeedResponse>() {
        @Override
        public PublishMerchantFeedResponse createFromParcel(Parcel source) {
            return new PublishMerchantFeedResponse(source);
        }

        @Override
        public PublishMerchantFeedResponse[] newArray(int size) {return new
                PublishMerchantFeedResponse[size];}
    };
}