package com.hunliji.marrybiz.model.merchantservice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.marrybiz.model.orders.BdProduct;

import org.joda.time.DateTime;

/**
 * 商家服务
 * Created by jinxin on 2018/1/29 0029.
 */

public class MerchantServer implements Parcelable {

    public static final int STATUS_IN_SERVICE = 1;//使用中
    public static final int STATUS_OUT_DATE = 2;//已过期

    long id;
    @SerializedName("merchant_id")
    long merchantId;
    BdProduct product;
    @SerializedName("product_id")
    long productId;//服务id
    @SerializedName("server_end")
    DateTime serverEnd;//服务到期时间
    @SerializedName("server_start")
    DateTime serverStart;//服务开始时间
    int status;//服务状态 1使用中 2已过期
    double price;//续费价格 商家未购买此服务则仅返回价格

    public long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public BdProduct getProduct() {
        return product;
    }

    public long getProductId() {
        return productId;
    }

    public DateTime getServerEnd() {
        return serverEnd;
    }

    public DateTime getServerStart() {
        return serverStart;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeParcelable(this.product, flags);
        dest.writeLong(this.productId);
        HljTimeUtils.writeDateTimeToParcel(dest, this.serverEnd);
        HljTimeUtils.writeDateTimeToParcel(dest, this.serverStart);
        dest.writeInt(this.status);
        dest.writeDouble(this.price);
    }

    public MerchantServer() {}

    protected MerchantServer(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.product = in.readParcelable(BdProduct.class.getClassLoader());
        this.productId = in.readLong();
        this.serverEnd = HljTimeUtils.readDateTimeToParcel(in);
        this.serverStart = HljTimeUtils.readDateTimeToParcel(in);
        this.status = in.readInt();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<MerchantServer> CREATOR = new Parcelable
            .Creator<MerchantServer>() {
        @Override
        public MerchantServer createFromParcel(Parcel source) {return new MerchantServer(source);}

        @Override
        public MerchantServer[] newArray(int size) {return new MerchantServer[size];}
    };
}
