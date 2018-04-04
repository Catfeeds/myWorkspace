package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/7.收到礼物列表
 */

public class ReceiveGiftRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE = 1;
    private final static int HEADER_TYPE = 2;

    private Context context;
    private ArrayList<UserGift> list;
    private View headerView;

    public ReceiveGiftRecyclerAdapter(Context context, ArrayList<UserGift> cardGifts) {
        this.context = context;
        this.list = cardGifts;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_cash_list_item___card, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            int index = headerView == null ? position : position - 1;
            holder.setView(context, list.get(index), index, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (headerView != null ? 1 : 0);
    }

    public class ViewHolder extends BaseViewHolder<UserGift> {
        @BindView(R2.id.tv_gift_name)
        TextView tvGiftName;
        @BindView(R2.id.tv_gift_desc)
        TextView tvGiftDesc;
        @BindView(R2.id.tv_gift_time)
        TextView tvGiftTime;
        @BindView(R2.id.tv_price_tag)
        TextView tvPriceTag;
        @BindView(R2.id.tv_gift_price)
        TextView tvGiftPrice;
        @BindView(R2.id.line_layout)
        View lineLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, UserGift item, int position, int viewType) {
            if (position == list.size() - 1) {
                lineLayout.setVisibility(View.GONE);
            } else {
                lineLayout.setVisibility(View.VISIBLE);
            }

            tvGiftName.setText(item.getGiverName());
            tvGiftTime.setText(HljTimeUtils.getShowTime(mContext, item.getCreatedAt()));
            DecimalFormat df = new DecimalFormat("#####0.00");
            String value = df.format(item.getPrice());
            if (value.length() > 2) {
                Spannable span = new SpannableString(value);
                span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(mContext, 14)),
                        span.length() - 2,
                        span.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvGiftPrice.setText(span);
            }
            if (item.getCardGift() != null && !TextUtils.isEmpty(item.getCardGift()
                    .getTitle())) {
                tvGiftDesc.setText(mContext.getString(R.string.label_gift_title___card,
                        item.getCardGift()
                                .getTitle()));
            } else {
                tvGiftDesc.setText(mContext.getString(R.string.label_cash_title___card, item.getTitle()));
            }
        }
    }
}
