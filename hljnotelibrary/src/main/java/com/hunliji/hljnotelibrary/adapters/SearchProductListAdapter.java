package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchProductBriefInfoViewHolder;

import java.util.List;

/**
 * 婚品搜索结果adapter
 * Created by chen_bin on 2017/6/29 0029.
 */
public class SearchProductListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View customView;
    private View footerView;
    private List<ShopProduct> products;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_CUSTOM = 1;
    private final static int ITEM_TYPE_FOOTER = 2;
    private SearchProductBriefInfoViewHolder.OnSelectProductListener onSelectProductListener;

    public SearchProductListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setProducts(List<ShopProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void addProducts(List<ShopProduct> products) {
        if (!CommonUtil.isCollectionEmpty(products)) {
            int start = getItemCount() - getCustomViewCount() - getFooterViewCount();
            this.products.addAll(products);
            notifyItemRangeInserted(start, products.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getCustomViewCount() {
        return customView != null ? 1 : 0;
    }

    public void setCustomView(View customView) {
        this.customView = customView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnSelectProductListener(
            SearchProductBriefInfoViewHolder.OnSelectProductListener onSelectProductListener) {
        this.onSelectProductListener = onSelectProductListener;
    }

    @Override
    public int getItemCount() {
        return getCustomViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(products);
    }

    @Override
    public int getItemViewType(int position) {
        if (getCustomViewCount() > 0 && position == getItemCount() - 1 - getFooterViewCount()) {
            return ITEM_TYPE_CUSTOM;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE_CUSTOM:
                return new ExtraBaseViewHolder(customView);
            default:
                SearchProductBriefInfoViewHolder productViewHolder = new
                        SearchProductBriefInfoViewHolder(
                        inflater.inflate(R.layout.search_product_brief_info_list_item___note,
                                parent,
                                false));
                productViewHolder.setOnSelectProductListener(onSelectProductListener);
                return productViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof SearchProductBriefInfoViewHolder) {
                    SearchProductBriefInfoViewHolder productViewHolder =
                            (SearchProductBriefInfoViewHolder) holder;
                    productViewHolder.setShowBottomLineView(position < products.size() - 1);
                    productViewHolder.setView(context, products.get(position), position, viewType);
                }
                break;
        }
    }

}
