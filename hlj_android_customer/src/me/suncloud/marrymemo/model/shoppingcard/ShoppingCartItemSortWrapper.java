package me.suncloud.marrymemo.model.shoppingcard;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.product.FreeShipping;

import java.util.List;


/**
 * 用来添加包邮条件排序的model
 * Created by jinxin on 2017/10/20 0020.
 */

public class ShoppingCartItemSortWrapper implements Parcelable {

    public static final long TYPE_FREE_SHIPPING = -1L;//包邮
    public static final long TYPE_OTHERS = -2L;//其他

    public long type;//运费模板的id 包邮和其他也认为是一种运费模板
    public List<ShoppingCartItem> cartItemList;
    public boolean isAchieve;//是否达到满减条件
    public boolean isFree;//免费
    public FreeShipping freeShipping;//用来记录运费模板信息

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.type);
        dest.writeTypedList(this.cartItemList);
        dest.writeByte(this.isAchieve ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFree ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.freeShipping, flags);
    }

    public ShoppingCartItemSortWrapper() {}

    protected ShoppingCartItemSortWrapper(Parcel in) {
        this.type = in.readLong();
        this.cartItemList = in.createTypedArrayList(ShoppingCartItem.CREATOR);
        this.isAchieve = in.readByte() != 0;
        this.isFree = in.readByte() != 0;
        this.freeShipping = in.readParcelable(FreeShipping.class.getClassLoader());
    }

    public static final Parcelable.Creator<ShoppingCartItemSortWrapper> CREATOR = new Parcelable
            .Creator<ShoppingCartItemSortWrapper>() {
        @Override
        public ShoppingCartItemSortWrapper createFromParcel(Parcel source) {
            return new ShoppingCartItemSortWrapper(source);
        }

        @Override
        public ShoppingCartItemSortWrapper[] newArray(int size) {return new ShoppingCartItemSortWrapper[size];}
    };
}
