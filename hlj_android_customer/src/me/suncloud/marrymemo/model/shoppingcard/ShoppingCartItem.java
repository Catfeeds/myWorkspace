package me.suncloud.marrymemo.model.shoppingcard;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.Sku;

import org.joda.time.DateTime;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartItem implements Parcelable {

    long id;
    @SerializedName(value = "sku_id")
    long skuId;
    @SerializedName(value = "product_id")
    long productId;
    @SerializedName(value = "merchant_id")
    long merchantId;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    ShopProduct product;
    Sku sku;
    @SerializedName(value = "is_valid")
    boolean isValid;
    int quantity;
    String reason;

    private boolean checked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggle() {
        this.checked = !checked;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
        }
    }

    public ShoppingCartItem() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.skuId);
        dest.writeLong(this.productId);
        dest.writeLong(this.merchantId);
        dest.writeSerializable(this.createdAt);
        dest.writeParcelable(this.product, flags);
        dest.writeParcelable(this.sku, flags);
        dest.writeByte(this.isValid ? (byte) 1 : (byte) 0);
        dest.writeInt(this.quantity);
        dest.writeString(this.reason);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    protected ShoppingCartItem(Parcel in) {
        this.id = in.readLong();
        this.skuId = in.readLong();
        this.productId = in.readLong();
        this.merchantId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.product = in.readParcelable(ShopProduct.class.getClassLoader());
        this.sku = in.readParcelable(Sku.class.getClassLoader());
        this.isValid = in.readByte() != 0;
        this.quantity = in.readInt();
        this.reason = in.readString();
        this.checked = in.readByte() != 0;
    }

    public static final Creator<ShoppingCartItem> CREATOR = new Creator<ShoppingCartItem>() {
        @Override
        public ShoppingCartItem createFromParcel(Parcel source) {
            return new ShoppingCartItem(source);
        }

        @Override
        public ShoppingCartItem[] newArray(int size) {return new ShoppingCartItem[size];}
    };
}
