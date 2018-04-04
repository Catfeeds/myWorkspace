package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/7.收到礼物列表
 */

public class ReceiveCashRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE = 1;
    private final static int HEADER_TYPE = 2;

    private Context context;
    private ArrayList<UserGift> list;
    private View headerView;
    private long cashLastId;
    private OnReplyGiftCashClickListener onReplyGiftCashClickListener;
    private OnHiddenClickListener onHiddenClickListener;

    public interface OnReplyGiftCashClickListener {
        void onReply(UserGift userGift, int position);
    }

    public void setOnReplyGiftCashClickListener(
            OnReplyGiftCashClickListener onReplyGiftCashClickListener) {
        this.onReplyGiftCashClickListener = onReplyGiftCashClickListener;
    }

    public interface OnHiddenClickListener {
        void onHidden(UserGift userGift, int position);
    }

    public void setOnHiddenClickListener(OnHiddenClickListener onHiddenClickListener) {
        this.onHiddenClickListener = onHiddenClickListener;
    }

    public ReceiveCashRecyclerAdapter(Context context, ArrayList<UserGift> cardGifts) {
        this.context = context;
        this.list = cardGifts;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setCashLastId(long cashLastId) {
        this.cashLastId = cashLastId;
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
            holder.setView(context, getItem(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (headerView != null ? 1 : 0);
    }

    public UserGift getItem(int position) {
        int index = headerView == null ? position : position - 1;
        return list.get(index);
    }

    public class ViewHolder extends BaseViewHolder<UserGift> {
        @BindView(R2.id.tv_gift_name)
        TextView tvGiftName;
        @BindView(R2.id.tv_gift_desc)
        TextView tvGiftDesc;
        @BindView(R2.id.tv_price_tag)
        TextView tvPriceTag;
        @BindView(R2.id.tv_gift_price)
        TextView tvGiftPrice;
        @BindView(R2.id.tv_gift_price_end)
        TextView tvGiftPriceEnd;
        @BindView(R2.id.tv_gift_time)
        TextView tvGiftTime;
        @BindView(R2.id.cash_bg_layout)
        RelativeLayout cashBgLayout;
        @BindView(R2.id.line_layout)
        View lineLayout;
        @BindView(R2.id.img_new_tag)
        ImageView imgNewTag;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext, final UserGift item, final int position, int viewType) {
            if (position == getItemCount() - 1) {
                lineLayout.setVisibility(View.GONE);
            } else {
                lineLayout.setVisibility(View.VISIBLE);
            }
            //当前id比最后浏览的id大时表示该礼金记录未被浏览
            if (item.getId() > cashLastId) {
                cashBgLayout.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.transparent_primary5));
                imgNewTag.setVisibility(View.VISIBLE);
            } else {
                cashBgLayout.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.colorWhite));
                imgNewTag.setVisibility(View.GONE);
            }
            tvGiftName.setText(item.getGiverName());
            tvGiftTime.setText(HljTimeUtils.getShowTime(mContext, item.getCreatedAt()));
            DecimalFormat df = new DecimalFormat("#####0.00");
            String value = df.format(item.getPrice());
            if (value.length() > 2) {
                Spannable span = new SpannableString(value);
                span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(mContext, 15)),
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
                tvGiftDesc.setText(mContext.getString(R.string.label_cash_title___card,
                        !TextUtils.isEmpty(item.getTitle()) ? item.getTitle() : "礼金"));
            }

            cashBgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(mContext, item, position);
                }
            });
        }

        private void showMenu(
                final Context mContext, final UserGift item, final int position) {
            DialogUtil.createBottomMenuDialog(mContext,
                    new LinkedHashMap<String, View.OnClickListener>() {
                        {
                            put("回复", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onReplyItem(mContext, item, position);
                                }
                            });
                            put(item.isHidden() ? "恢复请帖显示" : "不在请帖显示", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onHidden(mContext, item, position);
                                }
                            });
                        }
                    },
                    null)
                    .show();
        }

        private void onReplyItem(
                final Context mContext, final UserGift item, final int position) {
            if (item.getCardUserReply() == null) {
                ToastUtil.showToast(mContext, "抱歉，该宾客未允许接收消息", 0);
            } else if (item.getCardUserReply()
                    .getStatus() == 1 || item.getCardUserReply()
                    .getStatus() == 2) {
                ToastUtil.showToast(mContext, "抱歉，您已给该宾客发过了", 0);
            } else if (onReplyGiftCashClickListener != null) {
                onReplyGiftCashClickListener.onReply(item, position);
            }
        }

        private void onHidden(final Context mContext, final UserGift item, final int position) {
            onHiddenClickListener.onHidden(item, position);
        }
    }
}
