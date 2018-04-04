package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.marrybiz.model.Privilege;

import java.util.List;

/**
 * 商家特权记录(到店礼 优惠卷 商家承诺 等等)
 * Created by jixnin on 2017/2/9 0009.
 */

public class MerchantPrivilegeRecord implements Parcelable {
    @SerializedName(value = "list")
    List<Privilege> records;
    @SerializedName(value = "hot_tag_useful_count")
    int hotTagUsefulCount;
    private String title;


    public String getTitle() {
        return title;
    }

    public MerchantPrivilegeRecord(String title) {
        this.title = title;
    }

    public int getHotTagUsefulCount() {
        return hotTagUsefulCount;
    }

    public void setHotTagUsefulCount(int hotTagUsefulCount) {
        this.hotTagUsefulCount = hotTagUsefulCount;
    }

    public List<Privilege> getRecords() {
        return records;
    }

    public void setRecords(List<Privilege> records) {
        this.records = records;
    }

    public MerchantPrivilegeRecord() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.records);
        dest.writeInt(this.hotTagUsefulCount);
    }

    protected MerchantPrivilegeRecord(Parcel in) {
        this.records = in.createTypedArrayList(Privilege.CREATOR);
        this.hotTagUsefulCount = in.readInt();
    }

    public static final Creator<MerchantPrivilegeRecord> CREATOR = new
            Creator<MerchantPrivilegeRecord>() {
        @Override
        public MerchantPrivilegeRecord createFromParcel(Parcel source) {
            return new MerchantPrivilegeRecord(source);
        }

        @Override
        public MerchantPrivilegeRecord[] newArray(int size) {return new MerchantPrivilegeRecord[size];}
    };
}
