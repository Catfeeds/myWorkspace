package com.hunliji.marrybiz.adapter.experience;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.experience.viewholder.AdvDetailHeaderViewHolder;
import com.hunliji.marrybiz.adapter.experience.viewholder.AdvOrderDetailItemViewHolder;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.ShowHistory;

import java.util.List;

/**
 * Created by jinxin on 2017/12/19 0019.
 */

public class AdvDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        AdvDetailHeaderViewHolder.OnAdvDetailHeaderClickListener {

    private final int ITEM_HEADER = 11;
    private final int ITEM_ITEM = 12;

    private Context mContext;
    private LayoutInflater inflater;
    private List<ShowHistory> showHistoryList;
    private AdvDetail shopOrder;
    private int advType;
    private OnAdvDetailAdapterClickListener onAdvDetailClickListener;

    public AdvDetailAdapter(Context mContext, int type) {
        this.mContext = mContext;
        this.advType = type;
        inflater = LayoutInflater.from(mContext);
    }

    public void setShowHistory(List<ShowHistory> showHistoryList) {
        this.showHistoryList = showHistoryList;
    }

    public void setShopOrder(AdvDetail shopOrder) {
        this.shopOrder = shopOrder;
    }

    public void setOnAdvDetailClickListener(
            OnAdvDetailAdapterClickListener onAdvDetailClickListener) {
        this.onAdvDetailClickListener = onAdvDetailClickListener;
    }

    public void setSaleName(String name) {
        if (this.shopOrder == null) {
            return;
        }
        this.shopOrder.setExpSaler(name);
    }

    public void setArriveShop(boolean isArrive) {
        if (this.shopOrder == null) {
            return;
        }
        this.shopOrder.setCome(isArrive);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_HEADER:
                itemView = inflater.inflate(R.layout.experience_shop_recommend_detai_header,
                        parent,
                        false);
                AdvDetailHeaderViewHolder headerViewHolder = new AdvDetailHeaderViewHolder(
                        itemView,
                        advType);
                headerViewHolder.setOnAdvDetailHeaderClickListener(this);
                return headerViewHolder;
            case ITEM_ITEM:
                itemView = inflater.inflate(R.layout.experience_shop_recommend_detail_item,
                        parent,
                        false);
                return new AdvOrderDetailItemViewHolder(itemView, advType);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_HEADER) {
            holder.setView(mContext, shopOrder, position, viewType);
        } else {
            position -= (getShopOrderHeaderCount() > 0 ? 1 : 0);
            holder.setView(mContext,
                    showHistoryList.get(position),
                    position,
                    showHistoryList.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_HEADER;
        } else {
            return ITEM_ITEM;
        }
    }

    private int getShowHistoryListItemCount() {
        return CommonUtil.isCollectionEmpty(showHistoryList) ? 0 : showHistoryList.size();
    }

    private int getShopOrderHeaderCount() {
        return shopOrder == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return getShowHistoryListItemCount() + getShopOrderHeaderCount();
    }

    @Override
    public void onCall(String phone) {
        if (onAdvDetailClickListener != null) {
            onAdvDetailClickListener.onCall(phone);
        }
    }

    @Override
    public void onSms(AdvDetail detail) {
        if (onAdvDetailClickListener != null) {
            onAdvDetailClickListener.onSms(detail);
        }
    }

    @Override
    public void onSale(String name, TextView tvName) {
        if (onAdvDetailClickListener != null) {
            onAdvDetailClickListener.onSale(name, tvName);
        }
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        if (onAdvDetailClickListener != null) {
            onAdvDetailClickListener.onCheckedChanged(isChecked);
        }
    }

    public interface OnAdvDetailAdapterClickListener {

        void onCall(String phone);

        void onSms(AdvDetail detail);

        void onSale(String name, TextView tvName);

        void onCheckedChanged(boolean isChecked);
    }
}
