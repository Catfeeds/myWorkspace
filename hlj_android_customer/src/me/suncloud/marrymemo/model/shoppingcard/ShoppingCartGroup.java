package me.suncloud.marrymemo.model.shoppingcard;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.LongSparseArray;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.product.FreeShipping;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.suncloud.marrymemo.model.wallet.CouponRecord;

/**
 * Created by werther on 15/8/7.
 * 对应购物车列表中的一个商家(和商家下的商品列表)
 */
public class ShoppingCartGroup implements Parcelable {

    Merchant merchant;
    @SerializedName(value = "cart_list")
    List<ShoppingCartItem> cartList;
    @SerializedName(value = "coupon_list")
    List<CouponInfo> couponList;

    private String leaveMessage; // 留言
    private boolean isInvalidGroup;//是否是失效商品的商家
    private LongSparseArray<ShoppingCartItemSortWrapper> sortMap;//用来排序购物车里的婚品
    private List<FreeShippingWrapper> sortCartItemList;//排序好的购物车商品列表
    private CouponRecord couponRecord;
    private double shippingFee; // 这个组里面的商品合集对应的邮费

    // 构造虚拟的分组,按照商家分组,初始化购物车列表
    public ShoppingCartGroup() {
        this.sortMap = new LongSparseArray<>();
        this.sortCartItemList = new ArrayList<>();
    }

    public List<FreeShippingWrapper> getSortCartItemList() {
        return sortCartItemList;
    }

    public void addItem(ShoppingCartItem item) {
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        cartList.add(item);
    }

    public void toggleProduct(long pid) {
        if (cartList == null) {
            return;
        }
        for (ShoppingCartItem item : cartList) {
            if (item.getId() == pid) {
                item.toggle();
            }
        }
    }

    public boolean isProductItemChecked(long id) {
        if (cartList == null) {
            return false;
        }
        for (ShoppingCartItem item : cartList) {
            if (item.getId() == id) {
                return item.isChecked() && item.isValid();
            }
        }
        return false;
    }

    public boolean isAllMerchantProductChecked() {
        if (cartList == null) {
            return false;
        }
        if (cartList.isEmpty() || !cartList.get(0)
                .isValid()) {
            //如果这是个失效的婚品 就默认为都是选中的
            return true;
        }
        for (ShoppingCartItem item : cartList) {
            if (!item.isChecked() && item.isValid()) {
                return false;
            }
        }

        return true;
    }

    public boolean isAllMerchantProductInvalid() {
        if (cartList == null) {
            return false;
        }
        for (ShoppingCartItem item : cartList) {
            if (item.isValid()) {
                return false;
            }
        }

        return true;
    }

    public boolean isAllInvalid() {
        if (cartList == null) {
            return false;
        }
        for (ShoppingCartItem item : cartList) {
            if (item.isValid()) {
                return false;
            }
        }
        return true;
    }

    public void toggleGroup() {
        boolean isAll = isAllMerchantProductChecked();
        for (ShoppingCartItem item : cartList) {
            item.setChecked(!isAll);
        }
    }

    public void selectAll() {
        for (ShoppingCartItem item : cartList) {
            if (item.isValid()) {
                item.setChecked(true);
            }
        }
    }

    public void deSelectAll() {
        for (ShoppingCartItem item : cartList) {
            if (item.isValid()) {
                item.setChecked(false);
            }
        }
    }

    //计算当前商家下面所有婚品的总钱数
    public double getCurrentMoney() {
        double money = 0D;
        if (cartList == null) {
            return money;
        }
        for (ShoppingCartItem cartItem : cartList) {
            money += cartItem.getSku()
                    .getShowPrice() * cartItem.getQuantity();
        }
        return money;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public boolean isInvalidGroup() {
        return isInvalidGroup;
    }

    public void setInvalidGroup(boolean invalidGroup) {
        isInvalidGroup = invalidGroup;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public List<ShoppingCartItem> getCartList() {
        return cartList;
    }

    public void setCartList(List<ShoppingCartItem> cartList) {
        this.cartList = cartList;
    }

    public List<CouponInfo> getCouponList() {
        return couponList;
    }

    public LongSparseArray<ShoppingCartItemSortWrapper> getSortMap() {
        return sortMap;
    }

    //降去除无效婚品后的婚品列表排序排序
    public void sortCartItemList() {
        if (cartList == null) {
            return;
        }
        Collections.sort(cartList, new Comparator<ShoppingCartItem>() {
            @Override
            public int compare(
                    ShoppingCartItem lhs, ShoppingCartItem rhs) {
                if (lhs.isValid() && rhs.isValid()) {
                    return lhs.getCreatedAt()
                            .isAfter(rhs.getCreatedAt()) ? -1 : 1;
                } else if (lhs.isValid() && !rhs.isValid()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    //根据运费模板来排序婚品
    public void sortCartItemByFreeShipping() {
        sortMap.clear();
        //将婚品分组
        for (int i = 0, size = cartList.size(); i < size; i++) {
            ShoppingCartItem cartItem = cartList.get(i);
            if (cartItem == null) {
                continue;
            }

            ShopProduct product = cartItem.getProduct();
            if (product == null) {
                continue;
            }
            FreeShipping freeShipping = product.getFreeShipping();
            if (product.getShipingFee() == 0) {
                //包邮
                sortFree(cartItem);
            } else if (freeShipping != null) {
                //有运费模板
                sortFreeShipping(cartItem);
            } else {
                //其他
                sortOther(cartItem);
            }
        }

        calculateArrive();
        regroupSortMap();
    }

    private List<FreeShippingWrapper> getFreeShippingWrapperList
            (List<ShoppingCartItemSortWrapper> wrapperList) {
        List<FreeShippingWrapper> cartItemList = new ArrayList<>();
        if (wrapperList == null) {
            return cartItemList;
        }
        for (int i = 0, size = wrapperList.size(); i < size; i++) {
            ShoppingCartItemSortWrapper wrapper = wrapperList.get(i);
            for (int j = 0, itemSize = wrapper.cartItemList.size(); j < itemSize; j++) {
                FreeShippingWrapper shippingWrapper = new FreeShippingWrapper();
                shippingWrapper.freeShipping = wrapper.freeShipping;
                shippingWrapper.type = wrapper.type;
                shippingWrapper.cartItem = wrapper.cartItemList.get(j);
                shippingWrapper.isAchieve = wrapper.isAchieve;
                shippingWrapper.isFree = wrapper.isFree;
                shippingWrapper.collectPosition = j;
                if (wrapper.freeShipping != null || wrapper.isFree) {
                    shippingWrapper.isShowShipping = j == itemSize - 1;
                } else {
                    shippingWrapper.isShowShipping = false;
                }
                shippingWrapper.size = itemSize;
                cartItemList.add(shippingWrapper);
            }
        }
        return cartItemList;
    }

    //婚品按时间降序排序
    private void sortCartItemDesc(List<ShoppingCartItem> itemList) {
        if (itemList == null) {
            return;
        }
        Collections.sort(itemList, new Comparator<ShoppingCartItem>() {
            @Override
            public int compare(
                    ShoppingCartItem lhs, ShoppingCartItem rhs) {
                if (lhs.isValid() && rhs.isValid()) {
                    return lhs.getCreatedAt()
                            .isAfter(rhs.getCreatedAt()) ? -1 : 1;
                } else if (lhs.isValid() && !rhs.isValid()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    //包邮和达到的类别排序
    private void sortShoppingWrapperDesc(List<ShoppingCartItemSortWrapper> wrapperList) {
        if (wrapperList == null) {
            return;
        }
        Collections.sort(wrapperList, new Comparator<ShoppingCartItemSortWrapper>() {
            @Override
            public int compare(
                    ShoppingCartItemSortWrapper wrapper1, ShoppingCartItemSortWrapper wrapper2) {
                if (wrapper1.cartItemList.isEmpty() || wrapper2.cartItemList.isEmpty()) {
                    return 0;
                }
                ShoppingCartItem lhs = wrapper1.cartItemList.get(0);
                ShoppingCartItem rhs = wrapper2.cartItemList.get(0);
                if (lhs.isValid() && rhs.isValid()) {
                    return lhs.getCreatedAt()
                            .isAfter(rhs.getCreatedAt()) ? -1 : 1;
                } else if (lhs.isValid() && !rhs.isValid()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    //计算运费是否已达到满减条件
    private void calculateArrive() {
        for (int i = 0, size = sortMap.size(); i < size; i++) {
            ShoppingCartItemSortWrapper wrapper = sortMap.valueAt(i);
            if (wrapper.type == ShoppingCartItemSortWrapper.TYPE_FREE_SHIPPING || wrapper.type ==
                    ShoppingCartItemSortWrapper.TYPE_OTHERS) {
                continue;
            }
            wrapper.isAchieve = false;
            wrapper.isFree = false;
            FreeShipping freeShipping = null;
            //计算是否已经满XXX减XX 类0金额 1件数
            double money = 0D;
            int count = 0;
            for (int j = 0, itemSize = wrapper.cartItemList.size(); j < itemSize; j++) {
                ShoppingCartItem item = wrapper.cartItemList.get(j);
                if (freeShipping == null) {
                    freeShipping = item.getProduct()
                            .getFreeShipping();
                }
                money += item.getSku()
                        .getShowPrice() * item.getQuantity();
                count += item.getQuantity();
            }

            if (freeShipping == null) {
                continue;
            }
            int freeType = freeShipping.getType();
            if (freeType == 0) {
                if (money >= wrapper.freeShipping.getMoney()) {
                    //满多少金额包邮
                    wrapper.isAchieve = true;
                } else {
                    //尚未达到满多少包邮条件
                    wrapper.isAchieve = false;
                }
            } else if (freeType == 1) {
                if (count >= wrapper.freeShipping.getNum()) {
                    //满多少件包邮
                    wrapper.isAchieve = true;
                } else {
                    //尚未达到满多少包邮条件
                    wrapper.isAchieve = false;
                }
            }
        }
    }

    //已经分组好的数据，按照包邮 已达到包邮条件 未达到包邮条件 其他 排序
    private void regroupSortMap() {
        List<ShoppingCartItemSortWrapper> arrive = new ArrayList<>();
        List<ShoppingCartItemSortWrapper> notArrive = new ArrayList<>();
        List<ShoppingCartItemSortWrapper> other = new ArrayList<>();
        //完成 未完成 其他
        for (int i = 0, size = sortMap.size(); i < size; i++) {
            long key = sortMap.keyAt(i);
            ShoppingCartItemSortWrapper wrapper = sortMap.get(key);
            if (wrapper.isFree || wrapper.isAchieve) {
                arrive.add(wrapper);
            } else if (wrapper.freeShipping != null) {
                //未达到
                notArrive.add(wrapper);
            } else {
                other.add(wrapper);
            }
        }

        //arrive 将最后一个添加的婚品放到第一位
        for (ShoppingCartItemSortWrapper wrapper : arrive) {
            sortCartItemDesc(wrapper.cartItemList);
        }
        sortShoppingWrapperDesc(arrive);

        //not arrive 将最后一个添加的婚品放到第一位
        for (ShoppingCartItemSortWrapper wrapper : notArrive) {
            sortCartItemDesc(wrapper.cartItemList);
        }
        sortShoppingWrapperDesc(notArrive);

        //其他
        if (!other.isEmpty()) {
            ShoppingCartItemSortWrapper otherWrapper = other.get(0);
            sortCartItemDesc(otherWrapper.cartItemList);
        }

        //将排序好的婚品 重新组成以为数组
        sortCartItemList.clear();
        sortCartItemList.addAll(getFreeShippingWrapperList(arrive));
        sortCartItemList.addAll(getFreeShippingWrapperList(notArrive));
        sortCartItemList.addAll(getFreeShippingWrapperList(other));
    }

    private void sortFree(ShoppingCartItem cartItem) {
        ShoppingCartItemSortWrapper wrapper = sortMap.get(ShoppingCartItemSortWrapper
                .TYPE_FREE_SHIPPING);
        if (wrapper == null) {
            wrapper = new ShoppingCartItemSortWrapper();
            wrapper.type = ShoppingCartItemSortWrapper.TYPE_FREE_SHIPPING;
            wrapper.isFree = true;
            wrapper.isAchieve = false;
            List<ShoppingCartItem> cartItemList = new ArrayList<>();
            wrapper.cartItemList = cartItemList;
            sortMap.put(ShoppingCartItemSortWrapper.TYPE_FREE_SHIPPING, wrapper);
        }
        wrapper.cartItemList.add(cartItem);
    }

    private void sortOther(ShoppingCartItem cartItem) {
        ShoppingCartItemSortWrapper wrapper = sortMap.get(ShoppingCartItemSortWrapper.TYPE_OTHERS);
        if (wrapper == null) {
            wrapper = new ShoppingCartItemSortWrapper();
            wrapper.type = ShoppingCartItemSortWrapper.TYPE_OTHERS;
            wrapper.isFree = false;
            wrapper.isAchieve = false;
            List<ShoppingCartItem> cartItemList = new ArrayList<>();
            wrapper.cartItemList = cartItemList;
            sortMap.put(ShoppingCartItemSortWrapper.TYPE_OTHERS, wrapper);
        }
        wrapper.cartItemList.add(cartItem);
    }

    private void sortFreeShipping(ShoppingCartItem cartItem) {
        FreeShipping freeShipping = cartItem.getProduct()
                .getFreeShipping();
        if (freeShipping == null) {
            return;
        }
        long type = freeShipping.getId();
        ShoppingCartItemSortWrapper wrapper = sortMap.get(type);
        if (wrapper == null) {
            wrapper = new ShoppingCartItemSortWrapper();
            wrapper.type = type;
            List<ShoppingCartItem> cartItemList = new ArrayList<>();
            wrapper.isFree = false;
            wrapper.isAchieve = false;
            wrapper.cartItemList = cartItemList;
            wrapper.freeShipping = freeShipping;
            sortMap.put(type, wrapper);
        }
        wrapper.cartItemList.add(cartItem);
    }

    public void setCouponRecord(CouponRecord couponRecord) {
        this.couponRecord = couponRecord;
    }

    public CouponRecord getCouponRecord() {
        return couponRecord;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.merchant, flags);
        dest.writeTypedList(this.cartList);
        dest.writeTypedList(this.couponList);
        dest.writeString(this.leaveMessage);
    }

    protected ShoppingCartGroup(Parcel in) {
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.cartList = in.createTypedArrayList(ShoppingCartItem.CREATOR);
        this.couponList = in.createTypedArrayList(CouponInfo.CREATOR);
        this.leaveMessage = in.readString();
    }

    public static final Creator<ShoppingCartGroup> CREATOR = new Creator<ShoppingCartGroup>
            () {
        @Override
        public ShoppingCartGroup createFromParcel(Parcel source) {
            return new ShoppingCartGroup(source);
        }

        @Override
        public ShoppingCartGroup[] newArray(int size) {return new ShoppingCartGroup[size];}
    };
}

