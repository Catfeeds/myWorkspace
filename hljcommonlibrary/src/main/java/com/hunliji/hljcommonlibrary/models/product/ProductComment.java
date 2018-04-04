package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Comment;

/**
 * Created by mo_yu on 2016/10/28.婚品订单评论
 */

public class ProductComment extends Comment {
    @SerializedName(value = "sku")
    private Sku sku;
    @SerializedName(value = "shop_product")
    private ShopProduct product;

    private transient long subOrderId; //婚品子订单orderId

    public transient final static int POSITIVE_RATING = 1; //好评
    public transient final static int NEUTRAL_RATING = 0; //中评
    public transient final static int NEGATIVE_RATING = -1; //差评
    public transient final static int INIT_RATING = -2; //默认不选中评价等级

    public Sku getSku() {
        if (sku == null) {
            sku = new Sku();
        }
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public ShopProduct getProduct() {
        if (product == null) {
            product = new ShopProduct();
        }
        return product;
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
    }

    public long getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(long subOrderId) {
        this.subOrderId = subOrderId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.sku, flags);
        dest.writeParcelable(this.product, flags);
    }

    public ProductComment() {}

    protected ProductComment(Parcel in) {
        super(in);
        this.sku = in.readParcelable(Sku.class.getClassLoader());
        this.product = in.readParcelable(ShopProduct.class.getClassLoader());
    }

    public static final Creator<ProductComment> CREATOR = new Creator<ProductComment>() {
        @Override
        public ProductComment createFromParcel(Parcel source) {
            return new ProductComment(source);
        }

        @Override
        public ProductComment[] newArray(int size) {return new ProductComment[size];}
    };
}
