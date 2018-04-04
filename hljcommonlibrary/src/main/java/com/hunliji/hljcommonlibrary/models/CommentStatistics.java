package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 近期评价概况
 * Created by chen_bin on 2017/4/15 0015.
 */
public class CommentStatistics implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "good_rate")
    private double goodRate;
    @SerializedName(value = "score")
    private double score;
    @SerializedName(value = "count_1star")
    private int count1Star;
    @SerializedName(value = "count_2star")
    private int count2Star;
    @SerializedName(value = "count_3star")
    private int count3Star;
    @SerializedName(value = "count_4star")
    private int count4Star;
    @SerializedName(value = "count_5star")
    private int count5Star;
    @SerializedName(value = "ninety_bad_count")
    private int ninetyBadCount;
    @SerializedName(value = "ninety_medium_count")
    private int ninetyMediumCount;
    @SerializedName(value = "ninety_good_count")
    private int ninetyGoodCount;
    @SerializedName(value = "thirty_bad_count")
    private int thirtyBadCount;
    @SerializedName(value = "thirty_medium_count")
    private int thirtyMediumCount;
    @SerializedName(value = "thirty_good_count")
    private int thirtyGoodCount;
    @SerializedName(value = "total_bad_count")
    private int totalBadCount;
    @SerializedName(value = "total_medium_count")
    private int totalMediumCount;
    @SerializedName(value = "total_good_count")
    private int totalGoodCount;
    @SerializedName(value = "has_photos_count")
    private int hasPhotosCount;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.goodRate);
        dest.writeDouble(this.score);
        dest.writeInt(this.count1Star);
        dest.writeInt(this.count2Star);
        dest.writeInt(this.count3Star);
        dest.writeInt(this.count4Star);
        dest.writeInt(this.count5Star);
        dest.writeInt(this.ninetyBadCount);
        dest.writeInt(this.ninetyMediumCount);
        dest.writeInt(this.ninetyGoodCount);
        dest.writeInt(this.thirtyBadCount);
        dest.writeInt(this.thirtyMediumCount);
        dest.writeInt(this.thirtyGoodCount);
        dest.writeInt(this.totalBadCount);
        dest.writeInt(this.totalMediumCount);
        dest.writeInt(this.totalGoodCount);
        dest.writeInt(this.hasPhotosCount);
    }

    public CommentStatistics() {}

    protected CommentStatistics(Parcel in) {
        this.id = in.readLong();
        this.goodRate = in.readDouble();
        this.score = in.readDouble();
        this.count1Star = in.readInt();
        this.count2Star = in.readInt();
        this.count3Star = in.readInt();
        this.count4Star = in.readInt();
        this.count5Star = in.readInt();
        this.ninetyBadCount = in.readInt();
        this.ninetyMediumCount = in.readInt();
        this.ninetyGoodCount = in.readInt();
        this.thirtyBadCount = in.readInt();
        this.thirtyMediumCount = in.readInt();
        this.thirtyGoodCount = in.readInt();
        this.totalBadCount = in.readInt();
        this.totalMediumCount = in.readInt();
        this.totalGoodCount = in.readInt();
        this.hasPhotosCount = in.readInt();
    }

    public static final Creator<CommentStatistics> CREATOR = new Creator<CommentStatistics>() {
        @Override
        public CommentStatistics createFromParcel(Parcel source) {
            return new CommentStatistics(source);
        }

        @Override
        public CommentStatistics[] newArray(int size) {return new CommentStatistics[size];}
    };

    public long getId() {
        return id;
    }

    public double getGoodRate() {
        return goodRate;
    }

    public double getScore() {
        return score;
    }

    public int getCount1Star() {
        return count1Star;
    }

    public int getCount2Star() {
        return count2Star;
    }

    public int getCount3Star() {
        return count3Star;
    }

    public int getCount4Star() {
        return count4Star;
    }

    public int getCount5Star() {
        return count5Star;
    }

    public int getCommentCount() {
        return count5Star + count4Star + count3Star + count2Star + count1Star;
    }

    public int getNinetyBadCount() {
        return ninetyBadCount;
    }

    public int getNinetyMediumCount() {
        return ninetyMediumCount;
    }

    public int getNinetyGoodCount() {
        return ninetyGoodCount;
    }

    public int getThirtyBadCount() {
        return thirtyBadCount;
    }

    public int getThirtyMediumCount() {
        return thirtyMediumCount;
    }

    public int getThirtyGoodCount() {
        return thirtyGoodCount;
    }

    public int getTotalBadCount() {
        return totalBadCount;
    }

    public int getTotalMediumCount() {
        return totalMediumCount;
    }

    public int getTotalGoodCount() {
        return totalGoodCount;
    }

    public int getHasPhotosCount() {
        return hasPhotosCount;
    }
}
