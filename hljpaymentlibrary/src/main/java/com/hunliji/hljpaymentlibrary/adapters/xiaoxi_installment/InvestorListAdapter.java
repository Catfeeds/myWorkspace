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
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders.InvestorViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Investor;

import java.util.List;

/**
 * 出借人列表adapter
 * Created by chen_bin on 2017/12/13 0013.
 */
public class InvestorListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private List<Investor> investors;
    private String assetOrderId;
    private LayoutInflater inflater;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;

    public InvestorListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setInvestors(List<Investor> investors) {
        this.investors = investors;
        notifyDataSetChanged();
    }

    public void setAssetOrderId(String assetOrderId) {
        this.assetOrderId = assetOrderId;
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        return ITEM_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + CommonUtil.getCollectionSize(investors);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                InvestorViewHolder holder = new InvestorViewHolder(inflater.inflate(R.layout
                                .investor_list_item___pay,
                        parent,
                        false));
                holder.setAssetOrderId(assetOrderId);
                return holder;

        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof InvestorViewHolder) {
                    InvestorViewHolder vh = (InvestorViewHolder) holder;
                    int index = position - getHeaderViewCount();
                    vh.setShowTopLineView(index != 0);
                    vh.setView(context, investors.get(index), index, viewType);
                }
                break;
        }
    }

}
