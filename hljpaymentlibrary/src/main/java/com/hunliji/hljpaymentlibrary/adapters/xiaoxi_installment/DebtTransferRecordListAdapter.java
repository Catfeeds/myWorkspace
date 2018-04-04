package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .DebtTransferRecordViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtTransferRecord;

import java.util.List;

/**
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtTransferRecordListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<DebtTransferRecord> records;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public DebtTransferRecordListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<DebtTransferRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DebtTransferRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    public void addRecords(List<DebtTransferRecord> records) {
        if (!CommonUtil.isCollectionEmpty(records)) {
            int start = getItemCount() - getFooterViewCount();
            this.records.addAll(records);
            notifyItemRangeInserted(start, records.size());
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
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(records);
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
                return new DebtTransferRecordViewHolder(inflater.inflate(R.layout
                                .debt_transfer_record_list_item___pay,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof DebtTransferRecordViewHolder) {
                    DebtTransferRecordViewHolder recordViewHolder =
                            (DebtTransferRecordViewHolder) holder;
                    int index = position - getHeaderViewCount();
                    recordViewHolder.setTopLineView(index > 0);
                    recordViewHolder.setView(context, records.get(index), index, viewType);
                }
                break;
        }
    }

}
