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
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders.DebtViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Debt;

import java.util.List;

/**
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<Debt> debts;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public DebtListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
        notifyDataSetChanged();
    }

    public void addDebts(List<Debt> debts) {
        if (!CommonUtil.isCollectionEmpty(debts)) {
            int start = getItemCount() - getFooterViewCount();
            this.debts.addAll(debts);
            notifyItemRangeInserted(start, debts.size());
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
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(debts);
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
                return new DebtViewHolder(inflater.inflate(R.layout.debt_list_item___pay,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof DebtViewHolder) {
                    DebtViewHolder debtViewHolder = (DebtViewHolder) holder;
                    int index = position - getHeaderViewCount();
                    debtViewHolder.setTopLineView(index > 0);
                    debtViewHolder.setView(context, debts.get(index), index, viewType);
                }
                break;
        }
    }

}
