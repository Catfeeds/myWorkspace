package me.suncloud.marrymemo.adpter.orders.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.orders.HotelPeriodOrder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 婚宴酒店订单viewHolder
 * Created by chen_bin on 2018/2/28 0028.
 */
public class HotelPeriodOrderViewHolder extends BaseViewHolder<HotelPeriodOrder> {

    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_actual_money)
    TextView tvActualMoney;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.btn_negative_action)
    Button btnNegativeAction;
    @BindView(R.id.btn_positive_action)
    Button btnPositiveAction;
    @BindView(R.id.actions_layout)
    LinearLayout actionsLayout;

    private int coverWidth;

    private OnCancelOrderListener onCancelOrderListener;
    private OnDeleteOrderListener onDeleteOrderListener;
    private OnPayListener onPayListener;
    private OnReorderListener onReorderListener;

    public HotelPeriodOrderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = CommonUtil.dp2px(itemView.getContext(), 62);
        btnNegativeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelPeriodOrder order = getItem();
                if (order == null) {
                    return;
                }
                if (order.getStatus() == HotelPeriodOrder.STATUS_WAITING_FOR_THE_PAYMENT) {
                    onCancelOrder(getAdapterPosition(), getItem());
                } else {
                    onDeleteOrder(getAdapterPosition(), getItem());
                }
            }
        });
        btnPositiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelPeriodOrder order = getItem();
                if (order == null) {
                    return;
                }
                if (order.getStatus() == HotelPeriodOrder.STATUS_WAITING_FOR_THE_PAYMENT) {
                    onPay(getAdapterPosition(), getItem());
                } else {
                    onReorder(getAdapterPosition(), getItem());
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelPeriodOrder order = getItem();
                if (order == null || order.getStatus() != HotelPeriodOrder.STATUS_ORDER_PAID) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), MyBillListActivity.class);
                v.getContext()
                        .startActivity(intent);
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, HotelPeriodOrder order, int position, int viewType) {
        if (order == null) {
            return;
        }
        tvStatus.setText(order.getStatusStr());
        String negativeActionStr = order.getNegativeActionStr();
        String positiveActionStr = order.getPositiveActionStr();
        if (CommonUtil.isEmpty(negativeActionStr) && CommonUtil.isEmpty(positiveActionStr)) {
            actionsLayout.setVisibility(View.GONE);
        } else {
            actionsLayout.setVisibility(View.VISIBLE);
            btnNegativeAction.setText(negativeActionStr);
            btnPositiveAction.setText(positiveActionStr);
        }

        Merchant merchant = order.getMerchant();
        tvMerchantName.setText(merchant.getName());
        Glide.with(mContext)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(coverWidth)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(order.getTitle());
        tvActualMoney.setText(mContext.getString(R.string.label_price4,
                CommonUtil.formatDouble2String(order.getActualMoney())));
        tvTotalMoney.setText(CommonUtil.fromHtml(mContext,
                mContext.getString(R.string.html_total_price,
                        CommonUtil.formatDouble2String(order.getActualMoney()))));
    }

    private void onCancelOrder(int position, HotelPeriodOrder order) {
        if (onCancelOrderListener != null) {
            onCancelOrderListener.onCancelOrder(position, order);
        }
    }

    private void onDeleteOrder(int position, HotelPeriodOrder order) {
        if (onDeleteOrderListener != null) {
            onDeleteOrderListener.onDeleteOrder(position, order);
        }
    }

    private void onPay(int position, HotelPeriodOrder order) {
        if (onPayListener != null) {
            onPayListener.onPay(position, order);
        }
    }

    private void onReorder(int position, HotelPeriodOrder order) {
        if (onReorderListener != null) {
            onReorderListener.onReorder(position, order);
        }
    }

    public interface OnCancelOrderListener {
        void onCancelOrder(int position, HotelPeriodOrder order);
    }

    public interface OnDeleteOrderListener {
        void onDeleteOrder(int position, HotelPeriodOrder order);
    }

    public interface OnPayListener {
        void onPay(int position, HotelPeriodOrder order);
    }

    public interface OnReorderListener {
        void onReorder(int position, HotelPeriodOrder order);
    }

    public void setOnCancelOrderListener(OnCancelOrderListener onCancelOrderListener) {
        this.onCancelOrderListener = onCancelOrderListener;
    }

    public void setOnDeleteOrderListener(OnDeleteOrderListener onDeleteOrderListener) {
        this.onDeleteOrderListener = onDeleteOrderListener;
    }

    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public void setOnReorderListener(OnReorderListener onReorderListener) {
        this.onReorderListener = onReorderListener;
    }

}