package me.suncloud.marrymemo.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingTable implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "table_name")
    private String tableName;
    @SerializedName(value = "guests_total_num")
    private int totalNum;
    @SerializedName(value = "max_num")
    private int maxNum;
    @SerializedName(value = "table_no")
    private int tableNo;
    @SerializedName(value = "guests")
    private List<WeddingGuest> guests;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public List<WeddingGuest> getGuests() {
        if (guests == null) {
            guests = new ArrayList<>();
        }
        return guests;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.tableName);
        dest.writeInt(this.totalNum);
        dest.writeInt(this.maxNum);
        dest.writeInt(this.tableNo);
        dest.writeTypedList(this.guests);
    }

    public WeddingTable() {}

    protected WeddingTable(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.tableName = in.readString();
        this.totalNum = in.readInt();
        this.maxNum = in.readInt();
        this.tableNo = in.readInt();
        this.guests = in.createTypedArrayList(WeddingGuest.CREATOR);
    }

    public static final Creator<WeddingTable> CREATOR = new Creator<WeddingTable>() {
        @Override
        public WeddingTable createFromParcel(Parcel source) {return new WeddingTable(source);}

        @Override
        public WeddingTable[] newArray(int size) {return new WeddingTable[size];}
    };
}
