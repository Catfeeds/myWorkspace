package me.suncloud.marrymemo.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingGuest implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "wedding_table_id")
    private long tableId;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "fullname")
    private String fullName;
    @SerializedName(value = "first_char")
    private String firstChar;
    @SerializedName(value = "num")
    private int num;
    @SerializedName(value = "wedding_table")
    private WeddingTable table;

    private transient String pinyin;
    private transient boolean isSelected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public WeddingTable getTable() {
        if (table == null) {
            table = new WeddingTable();
        }
        return table;
    }

    public void setTable(WeddingTable table) {
        this.table = table;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.tableId);
        dest.writeLong(this.userId);
        dest.writeString(this.fullName);
        dest.writeString(this.firstChar);
        dest.writeInt(this.num);
        dest.writeParcelable(this.table, flags);
    }

    public WeddingGuest() {}

    protected WeddingGuest(Parcel in) {
        this.id = in.readLong();
        this.tableId = in.readLong();
        this.userId = in.readLong();
        this.fullName = in.readString();
        this.firstChar = in.readString();
        this.num = in.readInt();
        this.table = in.readParcelable(WeddingTable.class.getClassLoader());
    }

    public static final Creator<WeddingGuest> CREATOR = new Creator<WeddingGuest>() {
        @Override
        public WeddingGuest createFromParcel(Parcel source) {return new WeddingGuest(source);}

        @Override
        public WeddingGuest[] newArray(int size) {return new WeddingGuest[size];}
    };

}
