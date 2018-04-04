package me.suncloud.marrymemo.api.bindbank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2017/4/5 0005.
 */

public class BindBankPostBody implements Parcelable {

    String acc_no;//卡号
    String id_holder;//姓名
    long cid;//开户行所在地

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public String getId_holder() {
        return id_holder;
    }

    public void setId_holder(String id_holder) {
        this.id_holder = id_holder;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public BindBankPostBody() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.acc_no);
        dest.writeString(this.id_holder);
        dest.writeLong(this.cid);
    }

    protected BindBankPostBody(Parcel in) {
        this.acc_no = in.readString();
        this.id_holder = in.readString();
        this.cid = in.readLong();
    }

    public static final Creator<BindBankPostBody> CREATOR = new Creator<BindBankPostBody>() {
        @Override
        public BindBankPostBody createFromParcel(Parcel source) {return new BindBankPostBody(source);}

        @Override
        public BindBankPostBody[] newArray(int size) {return new BindBankPostBody[size];}
    };
}
