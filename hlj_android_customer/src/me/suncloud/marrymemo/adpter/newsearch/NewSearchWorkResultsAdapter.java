package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchCaseViewHolder;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchHeaderCpmMerchantViewHolder;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchHeaderCpmWorkCaseViewHolder;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchWorkViewHolder;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by werther on 16/12/1.
 * 套餐和案例结果页
 */

public class NewSearchWorkResultsAdapter extends NewBaseSearchResultAdapter {

    private static final int ITEM_TYPE_AD_TIP = 3;//cpm结尾 广告提示
    private static final int ITEM_TYPE_CPM = 4;
    private static final int ITEM_TYPE_SHOP_CPM = 5;//店铺广告cpm
    private static final int ITEM_TYPE_WORK_CPM_FIRST = 6;//套餐如果没有店铺cpm 取普通cpm的第一条数据放大显示 如果无cpm 则不显示

    private NewSearchApi.SearchType searchType;

    public NewSearchWorkResultsAdapter(
            Context context, ArrayList<? extends Object> data, NewSearchApi.SearchType searchType) {
        super(context, data);
        this.searchType = searchType;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
            case ITEM_TYPE_HEADER:
                return super.onCreateViewHolder(parent, viewType);
            case ITEM_TYPE_AD_TIP:
                View view = layoutInflater.inflate(R.layout.search_cpm_ad_divider, parent, false);
                return new ExtraBaseViewHolder(view);
            case ITEM_TYPE_SHOP_CPM:
                SearchHeaderCpmMerchantViewHolder shopCpmViewHolder = new
                        SearchHeaderCpmMerchantViewHolder(
                        layoutInflater.inflate(R.layout.search_cpm_layout___cv, parent, false));
                shopCpmViewHolder.setOnItemClickListener(new OnItemClickListener<Merchant>() {
                    @Override
                    public void onItemClick(
                            int position, Merchant merchant) {
                        if (merchant != null && merchant.getId() > 0) {
                            Intent intent = new Intent(context, MerchantDetailActivity.class);
                            intent.putExtra(MerchantDetailActivity.ARG_ID, merchant.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return shopCpmViewHolder;
            case ITEM_TYPE_WORK_CPM_FIRST:
                SearchHeaderCpmWorkCaseViewHolder workCpmViewHolder = new
                        SearchHeaderCpmWorkCaseViewHolder(
                        layoutInflater.inflate(R.layout.search_work_cpm_layout___cv,
                                parent,
                                false));
                workCpmViewHolder.setItemClickListener(onItemClickListener);
                return workCpmViewHolder;
            case ITEM_TYPE_CPM:
            case ITEM_TYPE_ITEM:
                if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK) {
                    SearchWorkViewHolder holder = new SearchWorkViewHolder(layoutInflater.inflate
                            (R.layout.small_common_work_item___cv,
                            parent,
                            false));
                    holder.setStyle(SearchWorkViewHolder.STYLE_SEARCH);
                    holder.setOnItemClickListener(onItemClickListener);
                    return holder;
                } else if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_CASE) {
                    SearchCaseViewHolder holder = new SearchCaseViewHolder(layoutInflater.inflate
                            (R.layout.small_common_case_item___cv,
                            parent,
                            false));
                    holder.setShowPropertyTag(true);
                    holder.setStyle(SearchCaseViewHolder.CASE_SMALL_COMMON_1);
                    holder.setOnItemClickListener(onItemClickListener);
                    return holder;
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_SHOP_CPM:
                if (holder instanceof SearchHeaderCpmMerchantViewHolder) {
                    SearchHeaderCpmMerchantViewHolder shopCpmViewHolder =
                            (SearchHeaderCpmMerchantViewHolder) holder;
                    shopCpmViewHolder.setView(context,
                            merchant,
                            position - getHeaderViewCount(),
                            viewType);
                    if (getShopCpmCount() == 1 && getCpmCount() == 0) {
                        shopCpmViewHolder.hideViewBottom();
                    }
                }
                break;
            case ITEM_TYPE_WORK_CPM_FIRST:
                int firstCpmIndex = position - getHeaderViewCount();
                if (holder instanceof SearchHeaderCpmWorkCaseViewHolder) {
                    SearchHeaderCpmWorkCaseViewHolder cpmViewHolder =
                            (SearchHeaderCpmWorkCaseViewHolder) holder;
                    cpmViewHolder.setView(context,
                            (Work) getCPMFeed(firstCpmIndex),
                            firstCpmIndex,
                            viewType);
                    if (getShopCpmCount() == 0 && getCpmCount() == 1) {
                        cpmViewHolder.hideViewBottom();
                    }
                }
                break;
            case ITEM_TYPE_CPM:
                int cpmIndex = getCpmIndex(position);
                Work cpmWork = (Work) getCPMFeed(cpmIndex);
                setWorkViewHolder(holder, cpmWork, cpmIndex, viewType, true);
                break;
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                setWorkViewHolder(holder, (Work) data.get(index), index, viewType, false);
                break;
        }
    }

    private void setWorkViewHolder(
            BaseViewHolder holder, Work work, int index, int viewType, boolean isCpm) {
        if (work == null) {
            return;
        }
        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK) {
            SearchWorkViewHolder workViewHolder = (SearchWorkViewHolder) holder;
            if (isCpm) {
                workViewHolder.setShowBottomThinLineView(index < getCpmCount() - 1);
            } else {
                workViewHolder.setShowBottomThinLineView(index < data.size() - 1);
            }
            workViewHolder.setView(context, work, index, viewType);
            workViewHolder.setCityName(work.getCity());
        } else if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_CASE) {
            SearchCaseViewHolder caseViewHolder = (SearchCaseViewHolder) holder;
            if (isCpm) {
                caseViewHolder.setShowBottomLineView(index < getCpmCount() - 1);
            } else {
                caseViewHolder.setShowBottomLineView(index < data.size() - 1);
            }
            caseViewHolder.setView(context, work, index, viewType);
            caseViewHolder.setCityName(work.getCity());
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headSize = getHeaderViewCount();//当前headerSize=0
        int cpmSize = getCpmCount();
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else if (merchant != null && position == headSize) {
            return ITEM_TYPE_SHOP_CPM;
        } else if (merchant == null && searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK &&
                cpmSize > 0 && position == headSize) {
            return ITEM_TYPE_WORK_CPM_FIRST;
        } else if (getCpmIndex(position) < cpmSize) {
            return ITEM_TYPE_CPM;
        } else if (!isCpmEmpty() && getCpmIndex(position) == cpmSize) {
            return ITEM_TYPE_AD_TIP;
        } else {
            return ITEM_TYPE_ITEM;
        }
    }

}
