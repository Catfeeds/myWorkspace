package com.hunliji.hljcommonlibrary.models.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

/**
 * Created by chen_bin on 2017/9/12 0012.
 */
public class ServiceOrderSub implements Parcelable {
    @SerializedName("product")
    private Work work;

    public Work getWork() {
        if (work == null) {
            work = new Work();
        }
        return work;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.work, flags);}

    public ServiceOrderSub() {}

    protected ServiceOrderSub(Parcel in) {
        this.work = in.readParcelable(Work.class.getClassLoader());
    }

    public static final Creator<ServiceOrderSub> CREATOR = new Creator<ServiceOrderSub>() {
        @Override
        public ServiceOrderSub createFromParcel(Parcel source) {return new ServiceOrderSub(source);}

        @Override
        public ServiceOrderSub[] newArray(int size) {return new ServiceOrderSub[size];}
    };
}
