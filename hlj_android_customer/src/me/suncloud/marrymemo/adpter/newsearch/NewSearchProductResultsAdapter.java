package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by werther on 16/12/6.
 * 婚品结果页
 */

public class NewSearchProductResultsAdapter extends NewBaseSearchResultAdapter {

    private static final int ITEM_TYPE_LIST_RATIO_1_TO_1 = 11;
    private static final int ITEM_TYPE_LIST_RATIO_2_TO_3 = 12;

    public NewSearchProductResultsAdapter(Context context, ArrayList<? extends Object> data) {
        super(context, data);
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
                CommonProductViewHolder productViewHolder = new CommonProductViewHolder(
                        layoutInflater.inflate(R.layout.common_product_list_item___cv,
                                parent,
                                false),
                        style);
                productViewHolder.setOnItemClickListener(onItemClickListener);
                return productViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            default:
                int index = getItemIndex(position);
                if (holder instanceof CommonProductViewHolder) {
                    CommonProductViewHolder viewHolder = (CommonProductViewHolder) holder;
                    viewHolder.setView(context, (ShopProduct) data.get(index), index, viewType);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headSize = (headerView == null ? 0 : 1);
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else {
            int index = position - headSize;
            ShopProduct product = (ShopProduct) data.get(index);
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
}
