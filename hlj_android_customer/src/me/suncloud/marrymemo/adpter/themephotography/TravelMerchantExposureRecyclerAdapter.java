package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.viewholder.TravelMerchantExposureViewHolder;
import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;

/**
 * 旅拍品牌专场频道页
 * Created by chen_bin on 2017/5/13 0013.
 */
public class TravelMerchantExposureRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<TravelMerchantExposure> exposures;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public TravelMerchantExposureRecyclerAdapter(
            Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<TravelMerchantExposure> getExposures() {
        return exposures;
    }

    public void setExposures(List<TravelMerchantExposure> exposures) {
        this.exposures = exposures;
        notifyDataSetChanged();
    }

    public void addExposures(List<TravelMerchantExposure> exposures) {
        if (!CommonUtil.isCollectionEmpty(exposures)) {
            int start = getItemCount() - getFooterViewCount();
            this.exposures.addAll(exposures);
            notifyItemRangeInserted(start, exposures.size());
            if (start - getHeaderViewCount() > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize
                (exposures);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new TravelMerchantExposureViewHolder(inflater.inflate(R.layout
                                .travel_merchant_exposure_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof TravelMerchantExposureViewHolder) {
                    TravelMerchantExposureViewHolder merchantExposureViewHolder =
                            (TravelMerchantExposureViewHolder) holder;
                    int index = position - getHeaderViewCount();
                    merchantExposureViewHolder.setView(context,
                            exposures.get(index),
                            index,
                            viewType);
                }
                break;
        }
    }
}
