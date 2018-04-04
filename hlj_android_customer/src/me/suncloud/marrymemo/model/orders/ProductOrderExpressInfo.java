package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 17/1/4.
 * 婚品订单物流信息
 */

public class ProductOrderExpressInfo implements Parcelable {
    long id;
    @SerializedName("tracking_no")
    String trackingNo;//物流快递单号
    @SerializedName("type_name")
    String typeName;//物流公司名称
    @SerializedName("type_code")
    String typeCode;//物流公司英文名称
    @SerializedName("details")
    List<ShippingStatus> shippingStatusList;//物流信息
    int status;//状态 int类型表示
    String state;//状态 String类型表示

    public long getId() {
        return id;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public List<ShippingStatus> getShippingStatusList() {
        if (shippingStatusList == null) {
            return new ArrayList<>();
        }
        return shippingStatusList;
    }

    public int getStatus() {
        return status;
    }

    public String getState() {
        return state;
    }

    public ProductOrderExpressInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.trackingNo);
        dest.writeString(this.typeName);
        dest.writeString(this.typeCode);
        dest.writeTypedList(this.shippingStatusList);
        dest.writeInt(this.status);
        dest.writeString(this.state);
    }

    protected ProductOrderExpressInfo(Parcel in) {
        this.id = in.readLong();
        this.trackingNo = in.readString();
        this.typeName = in.readString();
        this.typeCode = in.readString();
        this.shippingStatusList = in.createTypedArrayList(ShippingStatus.CREATOR);
        this.status = in.readInt();
        this.state = in.readString();
    }

    public static final Creator<ProductOrderExpressInfo> CREATOR = new
            Creator<ProductOrderExpressInfo>() {
        @Override
        public ProductOrderExpressInfo createFromParcel(Parcel source) {
            return new ProductOrderExpressInfo(source);
        }

        @Override
        public ProductOrderExpressInfo[] newArray(int size) {return new ProductOrderExpressInfo[size];}
    };
}
