package me.suncloud.marrymemo.adpter.orders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.orders.HotelPeriodOrder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.viewholder.HotelPeriodOrderViewHolder;

/**
 * Created by chen_bin on 2018/2/28 0028.
 */
public class HotelPeriodOrdersAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View footerView;
    private List<HotelPeriodOrder> orders;
    private LayoutInflater inflater;

    private HotelPeriodOrderViewHolder.OnCancelOrderListener onCancelOrderListener;
    private HotelPeriodOrderViewHolder.OnDeleteOrderListener onDeleteOrderListener;
    private HotelPeriodOrderViewHolder.OnPayListener onPayListener;
    private HotelPeriodOrderViewHolder.OnReorderListener onReorderListener;

    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public HotelPeriodOrdersAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<HotelPeriodOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<HotelPeriodOrder> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public void addOrders(List<HotelPeriodOrder> orders) {
        if (!CommonUtil.isCollectionEmpty(orders)) {
            int start = getItemCount() - getFooterViewCount();
            this.orders.addAll(orders);
            notifyItemRangeInserted(start, orders.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnCancelOrderListener(
            HotelPeriodOrderViewHolder.OnCancelOrderListener onCancelOrderListener) {
        this.onCancelOrderListener = onCancelOrderListener;
    }

    public void setOnDeleteOrderListener(
            HotelPeriodOrderViewHolder.OnDeleteOrderListener onDeleteOrderListener) {
        this.onDeleteOrderListener = onDeleteOrderListener;
    }

    public void setOnPayListener(HotelPeriodOrderViewHolder.OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public void setOnReorderListener(
            HotelPeriodOrderViewHolder.OnReorderListener onReorderListener) {
        this.onReorderListener = onReorderListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(orders);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - getFooterViewCount()) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_LIST;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                HotelPeriodOrderViewHolder holder = new HotelPeriodOrderViewHolder(inflater.inflate(
                        R.layout.hotel_period_order_list_item,
                        parent,
                        false));
                holder.setOnCancelOrderListener(onCancelOrderListener);
                holder.setOnDeleteOrderListener(onDeleteOrderListener);
                holder.setOnPayListener(onPayListener);
                holder.setOnReorderListener(onReorderListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, orders.get(position), position, viewType);
                break;
        }
    }

}
