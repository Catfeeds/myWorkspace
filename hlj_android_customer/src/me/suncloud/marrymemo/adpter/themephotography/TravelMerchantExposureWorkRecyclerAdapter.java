package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.viewholder
        .TravelMerchantExposureWorkViewHolder;

/**
 * 旅拍品牌专场频道页套餐
 * Created by chen_bin on 2017/5/31 0031.
 */
public class TravelMerchantExposureWorkRecyclerAdapter extends RecyclerView
        .Adapter<BaseViewHolder> {
    private Context context;
    private List<Work> works;
    private LayoutInflater inflater;
    private OnLoadMoreListener onLoadMoreListener;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public TravelMerchantExposureWorkRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public int getFooterViewCount() {
        return CommonUtil.getCollectionSize(works) >= 3 ? 1 : 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(works);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder moreViewHolder = new ExtraBaseViewHolder(inflater.inflate(R
                                .layout.travel_merchant_exposure_work_footer,
                        parent,
                        false));
                moreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                });
                return moreViewHolder;
            default:
                return new TravelMerchantExposureWorkViewHolder(inflater.inflate(R.layout
                                .travel_merchant_exposure_work_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, works.get(position), position, viewType);
                break;
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}