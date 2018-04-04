package com.hunliji.hljcommonviewlibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 婚品通用瀑布流Adapter
 * Created by jixnin on 2016/7/6.
 */
public class ProductRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private ArrayList<ShopProduct> products;
    private View headerView;
    private View footerView;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_LIST_RATIO_1_TO_1 = 1; //1:1比例的图片类型
    private static final int ITEM_TYPE_LIST_RATIO_2_TO_3 = 2;//1:1.5比例的图片类型
    private static final int ITEM_TYPE_FOOTER = 3;

    public ProductRecyclerAdapter(Context context, ArrayList<ShopProduct> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(List<ShopProduct> items) {
        this.products.clear();
        if (!CommonUtil.isCollectionEmpty(items)) {
            this.products.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<ShopProduct> items) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            int start = getItemCount() - (footerView != null ? 1 : 0);
            this.products.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }
    }

    public void clearItems() {
        this.products.clear();
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return (headerView != null ? 1 : 0) + products.size() + (footerView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (footerView != null && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            ShopProduct product = products.get(position - (headerView != null ? 1 : 0));
            if (product.getCoverImage() == null) {
                return ITEM_TYPE_LIST_RATIO_1_TO_1;
            } else {
                int width = product.getCoverImage()
                        .getWidth();
                int height = product.getCoverImage()
                        .getHeight();
                return width == 0 || height * 1.0f / width < 1.2f ? ITEM_TYPE_LIST_RATIO_1_TO_1 :
                        ITEM_TYPE_LIST_RATIO_2_TO_3;
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                ExtraBaseViewHolder headerViewHolder = new ExtraBaseViewHolder(headerView);
                headerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return headerViewHolder;
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            default:
                int style;
                if (viewType == ITEM_TYPE_LIST_RATIO_1_TO_1) {
                    style = CommonProductViewHolder.STYLE_RATIO_1_TO_1;
                } else {
                    style = CommonProductViewHolder.STYLE_RATIO_2_TO_3;
                }
                CommonProductViewHolder productViewHolder = new CommonProductViewHolder(inflater
                        .inflate(
                        R.layout.common_product_list_item___cv,
                        parent,
                        false), style);
                productViewHolder.setOnItemClickListener(new OnItemClickListener<ShopProduct>() {
                    @Override
                    public void onItemClick(int position, ShopProduct product) {
                        if (product != null && product.getId() > 0) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                                    .withLong("id", product.getId())
                                    .withTransition(R.anim.slide_in_up,
                                            R.anim.activity_anim_default)
                                    .navigation(context);
                        }
                    }
                });
                return productViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            case ITEM_TYPE_LIST_RATIO_1_TO_1:
            case ITEM_TYPE_LIST_RATIO_2_TO_3:
                int index = position - (headerView != null ? 1 : 0);
                holder.setView(context, products.get(index), position, viewType);
                break;
        }
    }

}
