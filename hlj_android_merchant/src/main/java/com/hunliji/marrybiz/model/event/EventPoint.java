package com.hunliji.marrybiz.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.ArrayList;

/**
 * 活动点的model
 * Created by chen_bin on 2016/10/10 0010.
 */
public class EventPoint implements Parcelable {
    @SerializedName(value = "hot")
    private ArrayList<Integer> hots;
    @SerializedName(value = "all_points")
    private ArrayList<Integer> points;
    @SerializedName(value = "is_valid")
    private boolean isValid;
    @SerializedName(value = "unit_price")
    private double unitPrice;
    @SerializedName(value = "balance")
    private int balance;
    @SerializedName(value = "num")
    private int num;
    @SerializedName(value = "tip")
    private JsonElement tip;

    private transient String title;
    private transient String content;
    private transient boolean isHot;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public ArrayList<Integer> getHots() {
        if (hots == null) {
            hots = new ArrayList<>();
        }
        return hots;
    }

    public void setHots(ArrayList<Integer> hots) {
        this.hots = hots;
    }

    public ArrayList<Integer> getPoints() {
        if (points == null) {
            points = new ArrayList<>();
        }
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTitle() {
        if (title == null) {
            title = CommonUtil.getAsString(tip, "title");
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if (content == null) {
            content = CommonUtil.getAsString(tip, "content");
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.hots);
        dest.writeList(this.points);
        dest.writeByte(this.isValid ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.unitPrice);
        dest.writeInt(this.balance);
        dest.writeInt(this.num);
        dest.writeString(GsonUtil.getGsonInstance()
                .toJson(this.tip));
    }

    public EventPoint() {}

    protected EventPoint(Parcel in) {
        this.hots = new ArrayList<>();
        this.points = new ArrayList<>();
        in.readList(this.hots, Integer.class.getClassLoader());
        in.readList(this.points, Integer.class.getClassLoader());
        this.isValid = in.readByte() != 0;
        this.unitPrice = in.readDouble();
        this.balance = in.readInt();
        this.num = in.readInt();
        this.tip = GsonUtil.getGsonInstance()
                .fromJson(in.readString(), JsonElement.class);
    }

    public static final Creator<EventPoint> CREATOR = new Creator<EventPoint>() {
        @Override
        public EventPoint createFromParcel(Parcel source) {return new EventPoint(source);}

        @Override
        public EventPoint[] newArray(int size) {return new EventPoint[size];}
    };
}