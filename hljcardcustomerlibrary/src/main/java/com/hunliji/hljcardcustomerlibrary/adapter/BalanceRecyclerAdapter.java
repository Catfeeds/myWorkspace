package com.hunliji.hljcardcustomerlibrary.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.Balance;
import com.hunliji.hljcardcustomerlibrary.models.WithdrawData;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljkefulibrary.moudles.Support;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/7.余额明细列表
 */

public class BalanceRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE = 1;
    private final static int FOOTER_TYPE = 2;

    private Context context;
    private ArrayList<Balance> list;
    private View footerView;
    private boolean isShowMore;
    private int count;
    private OnCheckWithdrawSettingListener onCheckWithdrawSettingListener;

    public BalanceRecyclerAdapter(Context context, ArrayList<Balance> balances) {
        this.context = context;
        this.list = balances;
        this.isShowMore = false;
    }

    public void setOnCheckWithdrawSettingListener(
            OnCheckWithdrawSettingListener onCheckWithdrawSettingListener) {
        this.onCheckWithdrawSettingListener = onCheckWithdrawSettingListener;
    }

    public boolean isShowMore() {
        return isShowMore;
    }

    public void setShowMore(boolean showMore) {
        isShowMore = showMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isShowMore && list.size() > 5 && position == 5) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_balance_list_item, parent, false), isShowMore);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM_TYPE) {
            holder.setView(context, list.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        if ((list.size() > 5 && isShowMore) || list.size() <= 5) {
            count = list.size();
        } else {
            count = 5;
        }
        return count + (list.size() > 5 && !isShowMore && footerView != null ? 1 : 0);
    }

    public class ViewHolder extends BaseViewHolder<Balance> {
        @BindView(R2.id.tv_balance_title)
        TextView tvBalanceTitle;
        @BindView(R2.id.tv_gift_time)
        TextView tvGiftTime;
        @BindView(R2.id.tv_price_tag)
        TextView tvPriceTag;
        @BindView(R2.id.tv_gift_price)
        TextView tvGiftPrice;
        @BindView(R2.id.line_layout)
        View lineLayout;
        boolean isShowMore;

        View view;

        ViewHolder(View view, boolean isShowMore) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            this.isShowMore = isShowMore;
        }

        @Override
        protected void setViewData(
                Context mContext, final Balance item, int position, int viewType) {
            if (position == count - 1) {
                lineLayout.setVisibility(View.GONE);
            } else {
                lineLayout.setVisibility(View.VISIBLE);
            }

            if (item.getWithDrawData() != null && item.getWithDrawData()
                    .getUpdatedAt() != null) {
                tvGiftTime.setText(item.getWithDrawData()
                        .getUpdatedAt()
                        .toString("yyyy-MM-dd HH:mm:ss"));
            } else if (item.getCreatedAt() != null) {
                tvGiftTime.setText(item.getCreatedAt()
                        .toString("yyyy-MM-dd HH:mm:ss"));
            }

            tvBalanceTitle.setText(item.getTypeMsg());
            if (item.getWithDrawData() != null && item.getWithDrawData()
                    .getStatus() == WithdrawData.STATUS_PROCCING) {
                // 提现中
                tvBalanceTitle.setTextColor(ContextCompat.getColor(context, R.color.colorLink));
                Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.icon_info_blue);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvBalanceTitle.setCompoundDrawables(null, null, drawable, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWithdrawingDlg();
                    }
                });

                setPrice(item);
            } else if (item.getWithDrawData() != null && item.getWithDrawData()
                    .getStatus() == WithdrawData.STATUS_FIAL) {
                // 提现失败
                tvBalanceTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.icon_info_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvBalanceTitle.setCompoundDrawables(null, null, drawable, null);

                tvGiftPrice.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                tvGiftPrice.setText("已退回至余额");
                tvGiftPrice.setTextSize(14);
                tvPriceTag.setVisibility(View.GONE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWithdrawFailDlg(item);
                    }
                });
            } else {
                tvBalanceTitle.setCompoundDrawables(null, null, null, null);
                view.setOnClickListener(null);
                tvBalanceTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
                setPrice(item);
            }
        }

        private void setPrice(Balance item) {
            tvGiftPrice.setTextSize(18);
            tvGiftPrice.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
            if (item.getValue() >= 0) {
                tvPriceTag.setText("+");
            } else {
                tvPriceTag.setText("");
            }

            DecimalFormat df = new DecimalFormat("#####0.00");
            String value = df.format(item.getValue());
            if (value.length() > 2) {
                Spannable span = new SpannableString(value);
                span.setSpan(new AbsoluteSizeSpan(CommonUtil.dp2px(context, 14)),
                        span.length() - 2,
                        span.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvGiftPrice.setText(span);
            }
        }

        private void showWithdrawingDlg() {
            final Dialog dialog = DialogUtil.createSingleButtonDialog(context,
                    "提现中",
                    "",
                    "好的",
                    null);

            String msgStr = "提现金额将在3个工作日内转入您的账户，请注意查收。如有疑问，点击联系客服";
            String linkStr = "点击联系客服";
            TextView tvMsg = dialog.findViewById(R.id.tv_alert_msg);
            SpannableString ss = new SpannableString(msgStr);
            int startIndex = msgStr.indexOf(linkStr);
            int endIndex = startIndex + linkStr.length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    SupportJumpService supportJumpService = (SupportJumpService) ARouter
                            .getInstance()
                            .build(RouterPath.ServicePath.GO_TO_SUPPORT)
                            .navigation();
                    if (supportJumpService != null) {
                        supportJumpService.gotoSupport(context, Support.SUPPORT_KIND_DEFAULT_ROBOT);
                        dialog.cancel();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMsg.setText(ss);
            tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
            tvMsg.setLinkTextColor(ContextCompat.getColor(context,
                    com.hunliji.hljchatlibrary.R.color.colorLink));

            dialog.show();
        }

        private void showWithdrawFailDlg(final Balance item) {
            final Dialog dialog = DialogUtil.createDoubleButtonDialog(context,
                    "提现失败",
                    "",
                    "查看提现设置",
                    "取消",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 查看提现设置
                            if (item.getWithDrawData()
                                    .getCardId() > 0 && onCheckWithdrawSettingListener != null) {
                                onCheckWithdrawSettingListener.onWithdrawSettingCheck(item
                                        .getWithDrawData()
                                        .getCardId());
                            }
                        }
                    },
                    null);

            String msgStr = "提现金额已退回至余额，请检查提现设置中的姓名、银行卡等信息是否正确。如有疑问，点击联系客服";
            String linkStr = "点击联系客服";
            TextView tvMsg = dialog.findViewById(R.id.tv_alert_msg);
            SpannableString ss = new SpannableString(msgStr);
            int startIndex = msgStr.indexOf(linkStr);
            int endIndex = startIndex + linkStr.length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    SupportJumpService supportJumpService = (SupportJumpService) ARouter
                            .getInstance()
                            .build(RouterPath.ServicePath.GO_TO_SUPPORT)
                            .navigation();
                    if (supportJumpService != null) {
                        supportJumpService.gotoSupport(context, Support.SUPPORT_KIND_DEFAULT_ROBOT);
                        dialog.cancel();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMsg.setText(ss);
            tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
            tvMsg.setLinkTextColor(ContextCompat.getColor(context,
                    com.hunliji.hljchatlibrary.R.color.colorLink));

            dialog.show();
        }
    }

    public interface OnCheckWithdrawSettingListener {
        void onWithdrawSettingCheck(long cardId);
    }

}

