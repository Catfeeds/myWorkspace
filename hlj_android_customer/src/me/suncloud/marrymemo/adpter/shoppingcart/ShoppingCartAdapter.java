package me.suncloud.marrymemo.adpter.shoppingcart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonviewlibrary.adapters.GroupRecyclerAdapter;

import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.shoppingcart.viewholder.ShoppingCartDividerViewHolder;
import me.suncloud.marrymemo.adpter.shoppingcart.viewholder.ShoppingCartEmptyViewHolder;
import me.suncloud.marrymemo.adpter.shoppingcart.viewholder.ShoppingCartGroupFooterViewHolder;
import me.suncloud.marrymemo.adpter.shoppingcart.viewholder.ShoppingCartGroupHeaderViewHolder;
import me.suncloud.marrymemo.adpter.shoppingcart.viewholder.ShoppingCartGroupItemViewHolder;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartItem;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.viewholder.shoppingcart.ShoppingCartGridViewHolder;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartAdapter extends GroupRecyclerAdapter<BaseViewHolder> implements
        ShoppingCartGroupHeaderViewHolder.OnGroupHeaderClickListener, ShoppingCartEmptyViewHolder
        .OnEmptyClickListener, ShoppingCartDividerViewHolder.OnCartDividerClickListener,
        ShoppingCartGroupFooterViewHolder.onCartGroupFooterClickListener,
        ShoppingCartGroupItemViewHolder.onCartGroupItemClickListener,
        OnItemClickListener<ShopProduct> {


    public static class GroupType {
        public static final int TYPE_EMPTY = 1001;
        public static final int TYPE_GROUP = 1;
        public static final int TYPE_DIVIDER = 1002;
        public static final int TYPE_PRODUCT = 1003;
    }

    public static class ItemType {
        public static final int TYPE_EMPTY = 1;
        public static final int TYPE_GROUP_HEADER = 2;
        public static final int TYPE_GROUP_ITEM = 3;
        public static final int TYPE_GROUP_FOOTER = 4;
        public static final int TYPE_GROUP_FOOTER_INVALID = 5;//失效的分组
        public static final int TYPE_DIVIDER = 6;
        public static final int TYPE_PRODUCT_ITEM = 7;
        public static final int TYPE_PRODUCT_FOOTER = 8;
    }

    private List<ShoppingCartGroup> groupList;
    private List<ShopProduct> productList;
    private Context mContext;
    private LayoutInflater inflater;
    private OnShoppingCartAdapterListener onShoppingCartAdapterListener;

    public ShoppingCartAdapter(
            Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void setData(List<ShoppingCartGroup> groupList, List<ShopProduct> productList) {
        resetGroup();
        setGroupList(groupList);
        setDivider();
        setProductList(productList);
    }

    private void setGroupList(List<ShoppingCartGroup> groupList) {
        this.groupList = groupList;
        removeNoCartItemGroup();
        if (groupList == null || groupList.isEmpty()) {
            setEmpty();
        } else {
            for (int i = 0, size = this.groupList.size(); i < size; i++) {
                setGroup(i,
                        i,
                        this.groupList.get(i)
                                .getCartList()
                                .size());
            }
        }
    }

    private void setEmpty() {
        if (groupList == null || groupList.isEmpty()) {
            setGroup(0, GroupType.TYPE_EMPTY, 1);
        }
    }

    private void setDivider() {
        int index = (groupList == null || groupList.isEmpty()) ? 1 : groupList.size();
        setGroup(index, GroupType.TYPE_DIVIDER, 1);
    }

    private void setProductList(List<ShopProduct> productList) {
        this.productList = productList;
        if (this.productList == null || this.productList.isEmpty()) {
            return;
        }
        int groupSize = (groupList == null || groupList.isEmpty()) ? 1 : groupList.size();
        //加上divider的位置
        groupSize++;
        setGroup(groupSize, GroupType.TYPE_PRODUCT, productList.size());
    }

    //去除婚品列表是空的商家
    private void removeNoCartItemGroup() {
        if (groupList == null) {
            return;
        }
        Iterator<ShoppingCartGroup> groupIterator = groupList.iterator();
        while (groupIterator.hasNext()) {
            ShoppingCartGroup group = groupIterator.next();
            if (group.getCartList() == null || group.getCartList()
                    .isEmpty()) {
                groupIterator.remove();
            }
        }
    }

    @Override
    public boolean hasGroupHeader(int groupType) {
        if (groupType < GroupType.TYPE_EMPTY) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasGroupFooter(int groupType) {
        if (groupType < GroupType.TYPE_EMPTY || groupType == GroupType.TYPE_PRODUCT) {
            return true;
        }
        return false;
    }

    @Override
    public int getGroupHeaderType(int groupType) {
        if (groupType < GroupType.TYPE_EMPTY) {
            return ItemType.TYPE_GROUP_HEADER;
        }
        return 0;
    }

    @Override
    public int getGroupFooterType(int groupType) {
        if (groupType < GroupType.TYPE_EMPTY) {
            ShoppingCartGroup group = groupList.get(groupType);
            if (group != null && !group.isInvalidGroup()) {
                return ItemType.TYPE_GROUP_FOOTER;
            } else {
                return ItemType.TYPE_GROUP_FOOTER_INVALID;
            }
        } else if (groupType == GroupType.TYPE_PRODUCT) {
            return ItemType.TYPE_PRODUCT_FOOTER;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int groupType, int childPosition) {
        if (groupType < GroupType.TYPE_EMPTY) {
            return ItemType.TYPE_GROUP_ITEM;
        } else if (groupType == GroupType.TYPE_EMPTY) {
            return ItemType.TYPE_EMPTY;
        } else if (groupType == GroupType.TYPE_DIVIDER) {
            return ItemType.TYPE_DIVIDER;
        } else if (groupType == GroupType.TYPE_PRODUCT) {
            return ItemType.TYPE_PRODUCT_ITEM;
        }
        return 0;
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupType, int childPosition) {
        if (holder instanceof ShoppingCartEmptyViewHolder) {
            holder.setView(mContext,
                    productList == null || productList.isEmpty(),
                    groupType,
                    childPosition);
        } else if (holder instanceof ShoppingCartGroupItemViewHolder) {
            if (groupList.isEmpty() || groupList.get(groupType)
                    .getSortCartItemList()
                    .isEmpty()) {
                return;
            }
            ((ShoppingCartGroupItemViewHolder) holder).setCartItem(groupList.get(groupType),
                    groupList.get(groupType)
                            .getSortCartItemList()
                            .get(childPosition),
                    childPosition,
                    groupList.get(groupType)
                            .getSortCartItemList()
                            .size());
        } else if (holder instanceof ShoppingCartGridViewHolder) {
            ((ShoppingCartGridViewHolder) holder).itemView.findViewById(R.id.layout_product_divider)
                    .setVisibility(!(childPosition <= 1) ? View.VISIBLE : View.GONE);
            holder.setView(mContext, productList.get(childPosition), childPosition, groupType);
        } else if (holder instanceof ShoppingCartDividerViewHolder) {
            ((ShoppingCartDividerViewHolder) holder).setDivider(hasInvalidGroup(),
                    (productList == null || productList.isEmpty()));
        }
    }

    private boolean hasInvalidGroup() {
        if (groupList == null) {
            return false;
        }
        for (ShoppingCartGroup group : groupList) {
            if (group.isInvalidGroup()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindGroupHeaderViewHolder(BaseViewHolder holder, int groupType) {
        //groupType 就是CartGroup中的position
        if (holder instanceof ShoppingCartGroupHeaderViewHolder) {
            holder.setView(mContext, groupList.get(groupType), groupList.size(), groupType);
        }
    }

    @Override
    public void onBindGroupFooterViewHolder(BaseViewHolder holder, int groupType) {
        //groupType 就是CartGroup中的position
        if (holder instanceof ShoppingCartGroupFooterViewHolder) {
            holder.setView(mContext, groupList.get(groupType), groupList.size(), groupType);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case ItemType.TYPE_EMPTY:
                itemView = inflater.inflate(R.layout.shopping_cart_empty_view, parent, false);
                ShoppingCartEmptyViewHolder emptyViewHolder = new ShoppingCartEmptyViewHolder(
                        itemView);
                emptyViewHolder.setOnEmptyClickListener(this);
                return emptyViewHolder;
            case ItemType.TYPE_GROUP_HEADER:
                itemView = inflater.inflate(R.layout.shopping_cart_group_header, parent, false);
                ShoppingCartGroupHeaderViewHolder shoppingCartGroupHeaderViewHolder = new
                        ShoppingCartGroupHeaderViewHolder(
                        itemView);
                shoppingCartGroupHeaderViewHolder.setOnGroupHeaderClickListener(this);
                return shoppingCartGroupHeaderViewHolder;
            case ItemType.TYPE_GROUP_FOOTER:
                itemView = inflater.inflate(R.layout.shopping_cart_group_footer, parent, false);
                ShoppingCartGroupFooterViewHolder groupFooterViewHolder = new
                        ShoppingCartGroupFooterViewHolder(
                        itemView);
                groupFooterViewHolder.setOnCartGroupFooterClickListener(this);
                return groupFooterViewHolder;
            case ItemType.TYPE_GROUP_FOOTER_INVALID:
                itemView = new View(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT,
                        0);
                itemView.setLayoutParams(params);
                return new ExtraBaseViewHolder(itemView);
            case ItemType.TYPE_GROUP_ITEM:
                itemView = inflater.inflate(R.layout.shopping_group_item, parent, false);
                ShoppingCartGroupItemViewHolder groupItemViewHolder = new
                        ShoppingCartGroupItemViewHolder(
                        itemView);
                groupItemViewHolder.setOnCartGroupItemClickListener(this);
                return groupItemViewHolder;
            case ItemType.TYPE_DIVIDER:
                itemView = inflater.inflate(R.layout.shopping_cart_divider, parent, false);
                ShoppingCartDividerViewHolder dividerViewHolder = new ShoppingCartDividerViewHolder(
                        itemView);
                dividerViewHolder.setOnCartDividerClickListener(this);
                return dividerViewHolder;
            case ItemType.TYPE_PRODUCT_ITEM:
                itemView = inflater.inflate(R.layout.shopping_cart_product_item, parent, false);
                ShoppingCartGridViewHolder shoppingCartGridViewHolder = new
                        ShoppingCartGridViewHolder(
                        itemView);
                shoppingCartGridViewHolder.setOnItemClickListener(this);
                return shoppingCartGridViewHolder;
            case ItemType.TYPE_PRODUCT_FOOTER:
                itemView = inflater.inflate(R.layout.hlj_product_no_more_footer___cv,
                        parent,
                        false);
                itemView.findViewById(R.id.no_more_hint)
                        .setVisibility(View.VISIBLE);
                itemView.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.colorBackground));
                return new ExtraBaseViewHolder(itemView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onCbMerchantClick(ShoppingCartGroup cartGroup) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onCbMerchantClick(cartGroup);
        }
    }

    @Override
    public void onEmptyClick() {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onEmptyClick();
        }
    }

    @Override
    public void onClear() {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onClear();
        }
    }

    @Override
    public void onAddOn(ShoppingCartGroup cartGroup) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onAddOn(cartGroup);
        }
    }

    @Override
    public void onCbItemClick(ShoppingCartGroup shoppingCartGroup) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onCbItemClick(shoppingCartGroup);
        }
    }

    @Override
    public void onDelete(
            ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onDelete(shoppingCartGroup, item);
        }
    }

    @Override
    public void onPlus(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onPlus(shoppingCartGroup, item);
        }
    }

    @Override
    public void onSubtract(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onSubtract(shoppingCartGroup, item);
        }
    }

    @Override
    public void addOn(long merchantId, long expressTemplateId) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.addOn(merchantId, expressTemplateId);
        }
    }

    @Override
    public void onGetCoupon(ShoppingCartGroup cartGroup) {
        if (onShoppingCartAdapterListener != null) {
            onShoppingCartAdapterListener.onGetCoupon(cartGroup);
        }
    }

    public void setOnShoppingCartAdapterListener(
            OnShoppingCartAdapterListener onShoppingCartAdapterListener) {
        this.onShoppingCartAdapterListener = onShoppingCartAdapterListener;
    }

    @Override
    public void onItemClick(
            int position, ShopProduct product) {
        if (product != null && product.getId() > 0) {
            Intent intent = new Intent(mContext, ShopProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            mContext.startActivity(intent);
        }
    }


    public interface OnShoppingCartAdapterListener {
        void onCbMerchantClick(ShoppingCartGroup cartGroup);

        void onEmptyClick();

        void onClear();

        void onAddOn(ShoppingCartGroup cartGroup);

        void onCbItemClick(ShoppingCartGroup shoppingCartGroup);

        void onDelete(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void onPlus(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void onSubtract(ShoppingCartGroup shoppingCartGroup, ShoppingCartItem item);

        void addOn(long merchantId, long expressTemplateId);

        void onGetCoupon(ShoppingCartGroup group);
    }
}
