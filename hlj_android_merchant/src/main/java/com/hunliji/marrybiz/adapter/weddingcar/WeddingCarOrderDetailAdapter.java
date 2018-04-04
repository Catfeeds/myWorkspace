package com.hunliji.marrybiz.adapter.weddingcar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.weddingcar.viewholder.WeddingCarOrderDetailViewHolder;
import com.hunliji.marrybiz.adapter.weddingcar.viewholder.WeddingCarOrderFooterViewHolder;
import com.hunliji.marrybiz.adapter.weddingcar.viewholder.WeddingCarOrderHeaderViewHolder;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderSub;

import java.util.List;

/**
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarOrderDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        WeddingCarOrderHeaderViewHolder.onWeddingCarOrderHeaderClickListener {

    private final int ITEM_HEADER = 12;
    private final int ITEM_ORDER_DETAIL = 13;
    private final int ITEM_FOOTER = 14;

    private Context mContext;
    private LayoutInflater inflater;
    private WeddingCarOrderDetail orderDetail;
    private int itemCount;
    private onWeddingCarOrderDetailAdapterListener onWeddingCarOrderDetailAdapterListener;

    public WeddingCarOrderDetailAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void setOrderDetail(WeddingCarOrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public void setOnWeddingCarOrderDetailAdapterListener(WeddingCarOrderDetailAdapter
                                                                  .onWeddingCarOrderDetailAdapterListener onWeddingCarOrderDetailAdapterListener) {
        this.onWeddingCarOrderDetailAdapterListener = onWeddingCarOrderDetailAdapterListener;
    }

    public int calculateItemCount() {
        int count = 0;
        if (orderDetail != null) {
            count++;
            if (orderDetail.getOrderSubs() != null && !orderDetail.getOrderSubs()
                    .isEmpty()) {
                count += orderDetail.getOrderSubs()
                        .size();
            }
            count++;
        }
        itemCount = count;
        return count;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case ITEM_HEADER:
                itemView = inflater.inflate(R.layout.wedding_car_order_detail_header,
                        parent,
                        false);
                WeddingCarOrderHeaderViewHolder headerViewHolder = new
                        WeddingCarOrderHeaderViewHolder(
                        itemView);
                headerViewHolder.setOnWeddingCarOrderHeaderClickListener(this);
                return headerViewHolder;
            case ITEM_ORDER_DETAIL:
                itemView = inflater.inflate(R.layout.wedding_car_order_detail_item, parent, false);
                WeddingCarOrderDetailViewHolder orderDetailViewHolder = new
                        WeddingCarOrderDetailViewHolder(
                        itemView);
                return orderDetailViewHolder;
            case ITEM_FOOTER:
                itemView = inflater.inflate(R.layout.wedding_car_order_detail_footer,
                        parent,
                        false);
                return new WeddingCarOrderFooterViewHolder(itemView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_HEADER:
                holder.setView(mContext, orderDetail, position, ITEM_HEADER);
                break;
            case ITEM_ORDER_DETAIL:
                List<WeddingCarOrderSub> orderSubs = orderDetail.getOrderSubs();
                position--;
                holder.setView(mContext, orderSubs.get(position), position, ITEM_ORDER_DETAIL);
                break;
            case ITEM_FOOTER:
                holder.setView(mContext, orderDetail, position, ITEM_HEADER);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_HEADER;
        } else if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_ORDER_DETAIL;
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public void onCall(WeddingCarOrderDetail orderDetail) {
        if(onWeddingCarOrderDetailAdapterListener != null){
            onWeddingCarOrderDetailAdapterListener.onCall(orderDetail);
        }
    }

    @Override
    public void onSms(WeddingCarOrderDetail orderDetail) {
        if(onWeddingCarOrderDetailAdapterListener != null){
            onWeddingCarOrderDetailAdapterListener.onSms(orderDetail);
        }
    }

    public interface onWeddingCarOrderDetailAdapterListener{
        void onCall(WeddingCarOrderDetail orderDetail);
        void onSms(WeddingCarOrderDetail orderDetail);
    }
}
