package com.hunliji.hljhttplibrary.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Mark;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/7/18.
 * 婚礼纪Http接口返回的真正有用的数据结构
 * 多一个count参数
 */
public class HljHttpCountData<T> extends HljHttpData<T> implements Parcelable {
    @SerializedName(value = "top_count")
    int topCount;
    @SerializedName(value = "current_count")
    int currentCount;
    @SerializedName(value = "max_channel_id")
    int maxChannelId;
    @SerializedName(value = "fix_mark")
    Mark localMark;
    @SerializedName(value = "total_money")
    double totalMoney;//礼物总价
    @SerializedName(value = "balance")
    double balance;//礼金余额
    @SerializedName(value = "gift_balance")
    double giftBalance;//礼物余额
    @SerializedName(value = "cash_balance")
    double cashBalance;//余额
    @SerializedName(value = "last_withdraw_at")
    DateTime lastWithdrawAt;//最后提现时间

    public DateTime getLastWithdrawAt() {
        return lastWithdrawAt;
    }

    public double getBalance() {
        return balance;
    }

    public double getGiftBalance() {
        return giftBalance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public int getTopCount() {
        return topCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getMaxChannelId() {
        return maxChannelId;
    }

    public void setMaxChannelId(int maxChannelId) {
        this.maxChannelId = maxChannelId;
    }

    public Mark getLocalMark() {
        return localMark;
    }

    public void setLocalMark(Mark localMark) {
        this.localMark = localMark;
    }

    public HljHttpCountData() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.topCount);
        dest.writeInt(this.currentCount);
        dest.writeInt(this.maxChannelId);
        dest.writeParcelable(this.localMark, flags);
        dest.writeDouble(this.totalMoney);
        dest.writeDouble(this.balance);
        dest.writeDouble(this.giftBalance);
        dest.writeDouble(this.cashBalance);
        dest.writeSerializable(this.lastWithdrawAt);
    }

    protected HljHttpCountData(Parcel in) {
        this.topCount = in.readInt();
        this.currentCount = in.readInt();
        this.maxChannelId = in.readInt();
        this.localMark = in.readParcelable(Mark.class.getClassLoader());
        this.totalMoney = in.readDouble();
        this.balance = in.readDouble();
        this.giftBalance = in.readDouble();
        this.cashBalance = in.readDouble();
        this.lastWithdrawAt = (DateTime) in.readSerializable();
    }

    public static final Creator<HljHttpCountData> CREATOR = new Creator<HljHttpCountData>() {
        @Override
        public HljHttpCountData createFromParcel(Parcel source) {return new HljHttpCountData(source);}

        @Override
        public HljHttpCountData[] newArray(int size) {return new HljHttpCountData[size];}
    };
}
