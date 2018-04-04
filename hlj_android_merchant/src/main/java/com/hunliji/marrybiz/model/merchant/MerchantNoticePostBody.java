package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 商家公告post body
 * Created by jinxin on 2017/2/6 0006.
 */

public class MerchantNoticePostBody implements Parcelable {

    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.content);}

    public MerchantNoticePostBody() {}

    protected MerchantNoticePostBody(Parcel in) {this.content = in.readString();}

    public static final Parcelable.Creator<MerchantNoticePostBody> CREATOR = new Parcelable
            .Creator<MerchantNoticePostBody>() {
        @Override
        public MerchantNoticePostBody createFromParcel(Parcel source) {
            return new MerchantNoticePostBody(source);
        }

        @Override
        public MerchantNoticePostBody[] newArray(int size) {return new
                MerchantNoticePostBody[size];}
    };
}
