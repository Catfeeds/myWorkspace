package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchMerchantViewHolder;

/**
 * Created by hua_rong on 2018/1/16.
 * 新版 商家结果页
 */

public class NewSearchMerchantResultsAdapter extends NewBaseSearchResultAdapter {

    private static final int ITEM_TYPE_AD_TIP = 3;//cpm结尾 广告提示
    private static final int ITEM_TYPE_CPM = 4;

    public NewSearchMerchantResultsAdapter(Context context, ArrayList<? extends Object> data) {
        super(context, data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            case ITEM_TYPE_AD_TIP:
                View adView = layoutInflater.inflate(R.layout.search_cpm_ad_divider, parent, false);
                return new ExtraBaseViewHolder(adView);
            default:
                View view = layoutInflater.inflate(R.layout.new_small_common_merchant_item___cv,
                        parent,
                        false);
                SearchMerchantViewHolder holder = new SearchMerchantViewHolder(view);
                holder.setOnItemClickListener(onItemClickListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                SearchMerchantViewHolder merchantViewHolder = (SearchMerchantViewHolder) holder;
                merchantViewHolder.setShowBottomLineView(index < data.size()-1);
                merchantViewHolder.setView(context, (Merchant) data.get(index), index, viewType);
                break;
            case ITEM_TYPE_CPM:
                int cpmIndex = getCpmIndex(position);
                SearchMerchantViewHolder cpmViewHolder = (SearchMerchantViewHolder) holder;
                cpmViewHolder.setShowBottomLineView(cpmIndex < getCpmCount() - 1);
                Merchant cpmFeed = (Merchant) getCPMFeed(cpmIndex);
                cpmViewHolder.setView(context, cpmFeed, cpmIndex, viewType);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int cpmSize = getCpmCount();
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else if (getCpmIndex(position) < cpmSize) {
            return ITEM_TYPE_CPM;
        } else if (!isCpmEmpty() && getCpmIndex(position) == cpmSize) {
            return ITEM_TYPE_AD_TIP;
        } else {
            return ITEM_TYPE_ITEM;
        }
    }

}
