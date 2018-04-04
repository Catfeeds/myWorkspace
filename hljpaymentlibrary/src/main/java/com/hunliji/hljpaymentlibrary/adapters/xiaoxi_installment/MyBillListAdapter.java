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
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders.MyBillViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;

import java.util.List;

/**
 * 我的账单adapter
 * Created by chen_bin on 2017/8/17 0017.
 */
public class MyBillListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private List<XiaoxiInstallmentOrder> orders;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;

    public MyBillListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOrders(List<XiaoxiInstallmentOrder> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    private int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + CommonUtil.getCollectionSize(orders);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                return new MyBillViewHolder(inflater.inflate(R.layout.my_bill_list_item___pay,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, orders.get(index), index, viewType);
                break;
        }
    }

}
