package com.hunliji.marrybiz.adapter.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.model.orders.MerchantOrderSub;
import com.hunliji.marrybiz.view.orders.MerchantOrderDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/12.支付订单列表
 */

public class MerchantOrderListRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int ITEM_TYPE = 0;
    private static final int FOOTER_TYPE = 1;
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<MerchantOrder> list;
    private View footerView;

    public MerchantOrderListRecyclerAdapter(Context context) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(ArrayList<MerchantOrder> list) {
        this.list = list;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE:
                return new MerchantOrderViewHolder(inflater.inflate(R.layout
                                .merchant_order_list_item,
                        parent,
                        false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == ITEM_TYPE) {
            holder.setView(mContext, getItem(position), position, itemType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    private MerchantOrder getItem(int position) {
        return list.get(position);
    }


    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0) + (footerView != null ? 1 : 0);
    }

    static class MerchantOrderViewHolder extends BaseViewHolder<MerchantOrder> {
        @BindView(R.id.tv_order_no)
        TextView tvOrderNo;
        @BindView(R.id.tv_order_state)
        TextView tvOrderState;
        @BindView(R.id.order_sub_name_layout)
        LinearLayout orderSubNameLayout;
        @BindView(R.id.tv_order_amount)
        TextView tvOrderAmount;

        MerchantOrderViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(view.getContext(), MerchantOrderDetailActivity.class);
                    intent.putExtra(MerchantOrderDetailActivity.ARG_ORDER_ID, getItem().getId());
                    view.getContext()
                            .startActivity(intent);
                    Activity activity = (Activity) view.getContext();
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, MerchantOrder item, int position, int viewType) {
            String orderAmountStr = mContext.getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat2(item.getActualMoney()));
            Spannable span = new SpannableString(orderAmountStr);
            span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(mContext, 18)),
                    1,
                    orderAmountStr.length() - 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvOrderAmount.setText(span);
            tvOrderNo.setText(item.getTradeNo());
            switch (item.getStatus()) {
                case MerchantOrder.ORDER_WAIT_FOR_PAY:
                    tvOrderState.setVisibility(View.VISIBLE);
                    tvOrderState.setText("待支付");
                    tvOrderState.setBackgroundResource(R.drawable.sp_r2_solid_accent);
                    break;
                case MerchantOrder.ORDER_PAY_CLOSED:
                    tvOrderState.setVisibility(View.VISIBLE);
                    tvOrderState.setText("已关闭");
                    tvOrderState.setBackgroundResource(R.drawable.sp_r2_solid_gray2);
                    break;
                default:
                    tvOrderState.setVisibility(View.GONE);
                    break;
            }

            orderSubNameLayout.removeAllViews();
            for (MerchantOrderSub merchantOrderSub : item.getMerchantOrderSubs()) {
                View view = View.inflate(mContext, R.layout.merchant_order_name_item, null);
                TextView tvOrderSubName = view.findViewById(R.id.tv_order_sub_name);
                tvOrderSubName.setText(merchantOrderSub.getProduct()
                        .getTitle());
                orderSubNameLayout.addView(view);
            }
        }
    }
}
