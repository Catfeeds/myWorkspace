package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallProductViewHolder;
import com.hunliji.hljnotelibrary.R;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2017/6/29.笔记相关婚品
 */

public class NoteProductRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int FOOTER_TYPE = 0;
    private final static int ITEM_TYPE = 1;

    private Context context;
    private ArrayList<ShopProduct> products;
    private View footerView;
    private LayoutInflater inflater;
    private int itemWidth;

    public NoteProductRecyclerAdapter(Context context, ArrayList<ShopProduct> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
        this.itemWidth = CommonUtil.dp2px(context, 110);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                SmallProductViewHolder productViewHolder = new SmallProductViewHolder(inflater
                        .inflate(
                        R.layout.common_product_list_item___cv,
                        parent,
                        false), SmallProductViewHolder.STYLE_RATIO_1_TO_1, itemWidth);
                productViewHolder.setOnItemClickListener(new OnItemClickListener<ShopProduct>() {
                    @Override
                    public void onItemClick(int position, ShopProduct product) {
                        if (product != null && product.getId() > 0) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                                    .withLong("id", product.getId())
                                    .navigation(context);
                        }
                    }
                });
                productViewHolder.itemView.getLayoutParams().width = itemWidth;
                return productViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE:
                holder.setView(context, products.get(position), position, viewType);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return products.size() + (footerView != null ? 1 : 0);
    }
}
