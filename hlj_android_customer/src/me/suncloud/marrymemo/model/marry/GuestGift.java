package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.PinyinUtil;

/**
 * Created by hua_rong on 2017/12/12
 */

public class GuestGift implements Parcelable {

    @SerializedName(value = "guest_name")
    private String guestName;
    private double money;
    private boolean selected;

    public String getFirstChar() {
        if (!TextUtils.isEmpty(guestName)) {
            String pinyin = getPinyin();
            if (!TextUtils.isEmpty(pinyin)) {
                String firstChar = pinyin.substring(0, 1);
                if (firstChar.matches("[A-Z]")) {
                    return firstChar;
                } else {
                    return "#";
                }
            }
        }
        return "";
    }

    public String getPinyin() {
        String pinyin = PinyinUtil.getPinyin(guestName);
        if (TextUtils.isEmpty(pinyin)) {
            return "";
        }
        return pinyin;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guestName);
        dest.writeDouble(this.money);
    }

    public GuestGift() {}

    protected GuestGift(Parcel in) {
        this.guestName = in.readString();
        this.money = in.readDouble();
    }

    public static final Creator<GuestGift> CREATOR = new Creator<GuestGift>() {
        @Override
        public GuestGift createFromParcel(Parcel source) {return new GuestGift(source);}

        @Override
        public GuestGift[] newArray(int size) {return new GuestGift[size];}
    };
}
