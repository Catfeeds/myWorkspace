package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by hua_rong on 2017/11/7
 * 结婚账本 列表model
 * http://doc.hunliji.com/workspace/myWorkspace.do?projectId=13#4034
 */

public class MarryBook implements Parcelable {

    public static final int TYPE_MARRY_BOOK = 0;
    public static final int TYPE_GIFT_INCOME = 1;

    private long id;
    private double money;
    private String remark;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    private RecordBook type;
    private ArrayList<Photo> images;
    @SerializedName(value = "type_id")
    private long typeId;
    private boolean isParent;//本地属性
    private boolean isFirstLine;//本地属性
    private double parentPrice;//每一项的价格

    public double getParentPrice() {
        return parentPrice;
    }

    public void setParentPrice(double parentPrice) {
        this.parentPrice = parentPrice;
    }


    public boolean isFirstLine() {
        return isFirstLine;
    }

    public void setFirstLine(boolean isFirstLine) {
        this.isFirstLine = isFirstLine;
    }

    public ArrayList<Photo> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(ArrayList<Photo> images) {
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public RecordBook getType() {
        if (type == null) {
            return new RecordBook();
        }
        return type;
    }


    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public void setType(RecordBook type) {
        this.type = type;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        this.isParent = parent;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.money);
        dest.writeString(this.remark);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeParcelable(this.type, flags);
        dest.writeTypedList(this.images);
        dest.writeByte(this.isParent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFirstLine ? (byte) 1 : (byte) 0);
        dest.writeLong(this.typeId);
    }

    public MarryBook() {}

    protected MarryBook(Parcel in) {
        this.id = in.readLong();
        this.money = in.readDouble();
        this.remark = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.type = in.readParcelable(RecordBook.class.getClassLoader());
        this.images = in.createTypedArrayList(Photo.CREATOR);
        this.isParent = in.readByte() != 0;
        this.isFirstLine = in.readByte() != 0;
        this.typeId = in.readLong();
    }

    public static final Creator<MarryBook> CREATOR = new Creator<MarryBook>() {
        @Override
        public MarryBook createFromParcel(Parcel source) {return new MarryBook(source);}

        @Override
        public MarryBook[] newArray(int size) {return new MarryBook[size];}
    };
}
