package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;


public abstract class NewBaseSearchResultAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public Context context;
    public View headerView;
    public View footerView;
    public List<? extends Object> data;
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_ITEM = 1;
    public static final int ITEM_TYPE_FOOTER = 2;


    public List<? extends Object> cpmFeeds;//cpm广告 包含店铺 和 套餐
    public Merchant merchant;//cpm店铺广告
    public OnItemClickListener onItemClickListener;
    public LayoutInflater layoutInflater;

    public NewBaseSearchResultAdapter(Context context, List<? extends Object> data) {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setCpmFeeds(List<? extends Object> cpmFeeds) {
        this.cpmFeeds = cpmFeeds;
    }

    public List<? extends Object> getCpmFeeds() {
        return cpmFeeds;
    }

    public int getCpmIndex(int position) {
        return position - getHeaderViewCount() - getShopCpmCount();
    }

    public int getItemIndex(int position) {
        return getCpmIndex(position) - getCpmCount() - (isCpmEmpty() ? 0 : 1);
    }

    public Object getCPMFeed(int index) {
        return cpmFeeds.get(index);
    }

    public void setShopCpm(Merchant merchant) {
        this.merchant = merchant;
    }

    public boolean isAllEmpty() {
        return merchant == null && CommonUtil.isCollectionEmpty(cpmFeeds) && CommonUtil
                .isCollectionEmpty(
                data);
    }

    public void clearAll() {
        merchant = null;
        if (!CommonUtil.isCollectionEmpty(cpmFeeds)) {
            cpmFeeds.clear();
        }
        if (!CommonUtil.isCollectionEmpty(data)) {
            data.clear();
        }
    }

    public int getCpmCount() {
        return CommonUtil.getCollectionSize(cpmFeeds);
    }

    public void setData(List<? extends Object> data) {
        this.data = data;
    }

    public void addData(List data) {
        if (CommonUtil.isCollectionEmpty(data)) {
            return;
        }
        int index = getItemCount();
        if (footerView != null) {
            index--;
        }
        this.data.addAll(data);
        if (index > 0) {
            notifyItemChanged(index - 1);
        }
        notifyItemRangeInserted(index, data.size());
    }

    public int getHeaderViewCount() {
        return headerView == null ? 0 : 1;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView == null ? 0 : 1;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += getHeaderViewCount();
        count += getShopCpmCount();
        count += CommonUtil.getCollectionSize(data);
        count += footerView == null ? 0 : 1;
        count += getCpmCount();
        count += isCpmEmpty() ? 0 : 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_ITEM;
        }
    }

    /**
     * 如果cpm店铺广告和cpm广告都为null 则不显示推广等提示
     *
     * @return
     */
    public boolean isCpmEmpty() {
        return merchant == null && CommonUtil.isCollectionEmpty(cpmFeeds);
    }

    public int getShopCpmCount() {
        return merchant == null ? 0 : 1;
    }


}
