package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangtao on 2017/6/10.
 */

public class TransInfo implements Parcelable {

    public static final String DEFAULT="1,0,0,1,0,0";


    protected TransInfo(Parcel in) {
    }

    public static final Creator<TransInfo> CREATOR = new Creator<TransInfo>() {
        @Override
        public TransInfo createFromParcel(Parcel in) {
            return new TransInfo(in);
        }

        @Override
        public TransInfo[] newArray(int size) {
            return new TransInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
