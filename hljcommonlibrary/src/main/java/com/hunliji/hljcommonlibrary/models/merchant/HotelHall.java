package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;

/**
 * Created by werther on 16/12/5.
 * 酒店大厅数据
 */

public class HotelHall implements Parcelable {
    long id;
    String name;
    @SerializedName("cover_url")
    String coverUrl;
    @SerializedName("cover_width")
    long coverWidth;
    @SerializedName("cover_height")
    long coverHeight;
    double height;
    @SerializedName("max_table_num")
    long maxTableNum;
    @SerializedName("min_table_num")
    long minTableNum;
    @SerializedName("table_num")
    long tableNum;
    double area;
    String pillar;
    String shape;
    @SerializedName("min_purchasing")
    String minPurchasing;

    String other;
    @SerializedName("best_table_num")
    int bestTableNum;
    ArrayList<Photo> items;


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        if (TextUtils.isEmpty(coverUrl)) {
            if (!CommonUtil.isCollectionEmpty(items)) {
                coverUrl = items.get(0)
                        .getImagePath();
            }
        }
        return coverUrl;
    }

    public long getCoverWidth() {
        return coverWidth;
    }

    public long getCoverHeight() {
        return coverHeight;
    }

    public double getHeight() {
        return height;
    }

    public long getMaxTableNum() {
        return maxTableNum;
    }

    public long getMinTableNum() {
        return minTableNum;
    }

    public long getTableNum() {
        return tableNum;
    }

    public int getBestTableNum() {
        return bestTableNum;
    }

    public double getArea() {
        return area;
    }

    public String getPillar() {
        return pillar;
    }

    public String getShape() {
        return shape;
    }

    public String getMinPurchasing() {
        return minPurchasing;
    }

    public double getMinPurchasingPrice() {
        try {
            return Double.valueOf(minPurchasing);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getOther() {
        return other;
    }

    public ArrayList<Photo> getItems() {
        return items;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.coverUrl);
        dest.writeLong(this.coverWidth);
        dest.writeLong(this.coverHeight);
        dest.writeDouble(this.height);
        dest.writeLong(this.maxTableNum);
        dest.writeLong(this.minTableNum);
        dest.writeLong(this.tableNum);
        dest.writeDouble(this.area);
        dest.writeString(this.pillar);
        dest.writeString(this.shape);
        dest.writeString(this.minPurchasing);
        dest.writeString(this.other);
        dest.writeTypedList(this.items);
    }

    public HotelHall() {}

    protected HotelHall(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.coverUrl = in.readString();
        this.coverWidth = in.readLong();
        this.coverHeight = in.readLong();
        this.height = in.readDouble();
        this.maxTableNum = in.readLong();
        this.minTableNum = in.readLong();
        this.tableNum = in.readLong();
        this.area = in.readDouble();
        this.pillar = in.readString();
        this.shape = in.readString();
        this.minPurchasing = in.readString();
        this.other = in.readString();
        this.items = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Parcelable.Creator<HotelHall> CREATOR = new Parcelable.Creator<HotelHall>
            () {
        @Override
        public HotelHall createFromParcel(Parcel source) {return new HotelHall(source);}

        @Override
        public HotelHall[] newArray(int size) {return new HotelHall[size];}
    };
}
