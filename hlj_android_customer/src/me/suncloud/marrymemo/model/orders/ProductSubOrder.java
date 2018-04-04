package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.Sku;

/**
 * Created by werther on 17/1/3.
 * 婚品订单子订单model，与原来的model数据格式一致
 */

public class ProductSubOrder implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName("product_id")
    private long productId;
    @SerializedName("red_packet_money")
    private double redPacketMoney;
    @SerializedName(value = "quantity")
    private int quantity;
    @SerializedName("is_gift")
    private boolean isGift;
    @SerializedName(value = "product")
    private ShopProduct product;
    @SerializedName(value = "sku")
    private Sku sku;
    @SerializedName("actual_money")
    private double actualMoney;
    @SerializedName("activity_status")
    private int activityStatus;
    @SerializedName("refund_status")
    private int refundStatus;
    @SerializedName("refund")
    private ProductOrderRefundInfo refundInfo;
    @SerializedName(value = "is_comment")
    private boolean isCommented; //子订单是否已评价

    public long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isGift() {
        return isGift;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public Sku getSku() {
        return sku;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public ProductOrderRefundInfo getRefundInfo() {
        return refundInfo;
    }

    public boolean isCommented() {
        return isCommented;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.productId);
        dest.writeDouble(this.redPacketMoney);
        dest.writeInt(this.quantity);
        dest.writeByte(this.isGift ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.product, flags);
        dest.writeParcelable(this.sku, flags);
        dest.writeDouble(this.actualMoney);
        dest.writeInt(this.activityStatus);
        dest.writeInt(this.refundStatus);
        dest.writeParcelable(this.refundInfo, flags);
        dest.writeByte(this.isCommented ? (byte) 1 : (byte) 0);
    }

    public ProductSubOrder() {}

    protected ProductSubOrder(Parcel in) {
        this.id = in.readLong();
        this.productId = in.readLong();
        this.redPacketMoney = in.readDouble();
        this.quantity = in.readInt();
        this.isGift = in.readByte() != 0;
        this.product = in.readParcelable(ShopProduct.class.getClassLoader());
        this.sku = in.readParcelable(Sku.class.getClassLoader());
        this.actualMoney = in.readDouble();
        this.activityStatus = in.readInt();
        this.refundStatus = in.readInt();
        this.refundInfo = in.readParcelable(ProductOrderRefundInfo.class.getClassLoader());
        this.isCommented = in.readByte() != 0;
    }

    public static final Creator<ProductSubOrder> CREATOR = new Creator<ProductSubOrder>() {
        @Override
        public ProductSubOrder createFromParcel(Parcel source) {return new ProductSubOrder(source);}

        @Override
        public ProductSubOrder[] newArray(int size) {return new ProductSubOrder[size];}
    };
}
