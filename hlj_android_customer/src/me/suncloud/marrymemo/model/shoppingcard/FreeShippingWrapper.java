package me.suncloud.marrymemo.model.shoppingcard;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.product.FreeShipping;

/**
 * 用来描述每一个购物车商品包邮运费模板等信息
 * Created by jinxin on 2017/10/20 0020.
 */

public class FreeShippingWrapper implements Parcelable {

    public long type;//运费模板的id 包邮和其他也认为是一种运费模板
    public boolean isFree;//是否包邮
    public boolean isAchieve;//是否达到满减条件
    public FreeShipping freeShipping;//用来记录运费模板信息
    public int collectPosition;//对应这个商品在 排序后模板里面的位置
    public boolean isShowShipping;//是否显示满什么包邮的
    public int size;//对应这个运费模板下面商品的个数
    public ShoppingCartItem cartItem;//对应购物车的商品

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.type);
        dest.writeByte(this.isFree ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAchieve ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.freeShipping, flags);
        dest.writeInt(this.collectPosition);
        dest.writeByte(this.isShowShipping ? (byte) 1 : (byte) 0);
        dest.writeInt(this.size);
        dest.writeParcelable(this.cartItem, flags);
    }

    public FreeShippingWrapper() {}

    protected FreeShippingWrapper(Parcel in) {
        this.type = in.readLong();
        this.isFree = in.readByte() != 0;
        this.isAchieve = in.readByte() != 0;
        this.freeShipping = in.readParcelable(FreeShipping.class.getClassLoader());
        this.collectPosition = in.readInt();
        this.isShowShipping = in.readByte() != 0;
        this.size = in.readInt();
        this.cartItem = in.readParcelable(ShoppingCartItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<FreeShippingWrapper> CREATOR = new Parcelable
            .Creator<FreeShippingWrapper>() {
        @Override
        public FreeShippingWrapper createFromParcel(Parcel source) {
            return new FreeShippingWrapper(source);
        }

        @Override
        public FreeShippingWrapper[] newArray(int size) {return new FreeShippingWrapper[size];}
    };
}
