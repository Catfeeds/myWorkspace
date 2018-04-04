package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchMerchantBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchProductBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.models.wrappers.HljHttpOrderedData;

import java.util.List;

/**
 * 已下单数据
 * Created by chen_bin on 2017/6/28 0028.
 */
public class OrderedListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<Merchant> merchants;
    private List<ShopProduct> products;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_MERCHANT_HEADER = 0;
    private static final int ITEM_TYPE_MERCHANT_LIST = 1;
    private static final int ITEM_TYPE_PRODUCT_HEADER = 2;
    private static final int ITEM_TYPE_PRODUCT_LIST = 3;
    private SearchMerchantBriefInfoViewHolder.OnSelectMerchantListener onSelectMerchantListener;
    private SearchProductBriefInfoViewHolder.OnSelectProductListener onSelectProductListener;

    public OrderedListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOrderedData(HljHttpOrderedData orderedData) {
        this.merchants = orderedData == null ? null : orderedData.getMerchants();
        this.products = orderedData == null ? null : orderedData.getProducts();
        notifyDataSetChanged();
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public List<ShopProduct> getProducts() {
        return products;
    }

    public int getMerchantHeaderViewCount() {
        return !CommonUtil.isCollectionEmpty(merchants) ? 1 : 0;
    }

    public int getProductHeaderViewCount() {
        return !CommonUtil.isCollectionEmpty(products) ? 1 : 0;
    }

    public void setOnSelectMerchantListener(
            SearchMerchantBriefInfoViewHolder.OnSelectMerchantListener onSelectMerchantListener) {
        this.onSelectMerchantListener = onSelectMerchantListener;
    }

    public void setOnSelectProductListener(
            SearchProductBriefInfoViewHolder.OnSelectProductListener onSelectProductListener) {
        this.onSelectProductListener = onSelectProductListener;
    }

    @Override
    public int getItemCount() {
        return getMerchantHeaderViewCount() + CommonUtil.getCollectionSize(merchants) +
                getProductHeaderViewCount() + CommonUtil.getCollectionSize(
                products);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && getMerchantHeaderViewCount() > 0) {
            return ITEM_TYPE_MERCHANT_HEADER;
        } else if (position - getMerchantHeaderViewCount() < CommonUtil.getCollectionSize
                (merchants)) {
            return ITEM_TYPE_MERCHANT_LIST;
        } else if (getProductHeaderViewCount() > 0 && position == getMerchantHeaderViewCount() +
                CommonUtil.getCollectionSize(
                merchants)) {
            return ITEM_TYPE_PRODUCT_HEADER;
        } else {
            return ITEM_TYPE_PRODUCT_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_MERCHANT_HEADER:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout
                                .ordered_merchant_header_item___note,
                        parent,
                        false));
            case ITEM_TYPE_PRODUCT_HEADER:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout
                                .ordered_product_header_item___note,
                        parent,
                        false));
            case ITEM_TYPE_MERCHANT_LIST:
                SearchMerchantBriefInfoViewHolder merchantBriefInfoViewHolder = new
                        SearchMerchantBriefInfoViewHolder(
                        inflater.inflate(R.layout.search_merchant_brief_info_list_item___note,
                                parent,
                                false));
                merchantBriefInfoViewHolder.setOnSelectMerchantListener(onSelectMerchantListener);
                return merchantBriefInfoViewHolder;
            default:
                SearchProductBriefInfoViewHolder productBriefInfoViewHolder = new
                        SearchProductBriefInfoViewHolder(
                        inflater.inflate(R.layout.search_product_brief_info_list_item___note,
                                parent,
                                false));
                productBriefInfoViewHolder.setOnSelectProductListener(onSelectProductListener);
                return productBriefInfoViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_MERCHANT_LIST:
                if (holder instanceof SearchMerchantBriefInfoViewHolder) {
                    SearchMerchantBriefInfoViewHolder merchantBriefInfoViewHolder =
                            (SearchMerchantBriefInfoViewHolder) holder;
                    int index = position - getMerchantHeaderViewCount();
                    merchantBriefInfoViewHolder.setShowBottomLineView(index < CommonUtil
                            .getCollectionSize(
                            merchants) - 1);
                    merchantBriefInfoViewHolder.setView(context,
                            merchants.get(index),
                            index,
                            viewType);
                }
                break;
            case ITEM_TYPE_PRODUCT_LIST:
                if (holder instanceof SearchProductBriefInfoViewHolder) {
                    SearchProductBriefInfoViewHolder productBriefInfoViewHolder =
                            (SearchProductBriefInfoViewHolder) holder;
                    int index = position - CommonUtil.getCollectionSize(merchants) -
                            getMerchantHeaderViewCount() - getProductHeaderViewCount();
                    productBriefInfoViewHolder.setShowBottomLineView(index < CommonUtil
                            .getCollectionSize(
                            products) - 1);
                    productBriefInfoViewHolder.setView(context,
                            products.get(index),
                            index,
                            viewType);
                }
                break;
        }
    }
}