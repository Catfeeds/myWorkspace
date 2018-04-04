package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by luohanlin on 2018/2/5.
 */

public class WithdrawData implements Parcelable {

    public static final int STATUS_PROCCING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FIAL = 2;

    @SerializedName("card_id")
    long cardId; // 如果有多个请帖，则没有id，否则返回对应的请帖ID
    int status; // 0待确认，1成功 2失败
    @SerializedName("updated_at")
    DateTime updatedAt;


    public long getCardId() {
        return cardId;
    }

    public int getStatus() {
        return status;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cardId);
        dest.writeInt(this.status);
        dest.writeSerializable(this.updatedAt);
    }

    public WithdrawData() {}

    protected WithdrawData(Parcel in) {
        this.cardId = in.readLong();
        this.status = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<WithdrawData> CREATOR = new Parcelable
            .Creator<WithdrawData>() {
        @Override
        public WithdrawData createFromParcel(Parcel source) {return new WithdrawData(source);}

        @Override
        public WithdrawData[] newArray(int size) {return new WithdrawData[size];}
    };
}
