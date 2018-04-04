package com.hunliji.marrybiz.adapter.coupon;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的优惠券列表
 * Created by chen_bin on 2017/4/1 0001.
 */
public class MyCouponRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<CouponInfo> coupons;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private OnItemClickListener onItemClickListener;
    private OnActivateCouponListener onActivateCouponListener;

    public MyCouponRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<CouponInfo> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponInfo> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    public void addCoupons(List<CouponInfo> coupons) {
        if (!CommonUtil.isCollectionEmpty(coupons)) {
            int start = getItemCount() - getFooterViewCount();
            this.coupons.addAll(coupons);
            notifyItemRangeInserted(start, coupons.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnActivateCouponListener(OnActivateCouponListener onActivateCouponListener) {
        this.onActivateCouponListener = onActivateCouponListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(coupons);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new CouponViewHolder((inflater.inflate(R.layout.my_coupon_list_item,
                        parent,
                        false)));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, coupons.get(position), position, viewType);
                break;
        }
    }


    public class CouponViewHolder extends BaseViewHolder<CouponInfo> {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.tv_total_count)
        TextView tvTotalCount;
        @BindView(R.id.tv_provided_count)
        TextView tvProvidedCount;
        @BindView(R.id.tv_online_used_count)
        TextView tvOnlineUsedCount;
        @BindView(R.id.tv_offline_used_count)
        TextView tvOfflineUsedCount;
        @BindView(R.id.tv_useful_life)
        TextView tvUsefulLife;
        @BindView(R.id.btn_edit)
        Button btnEdit;
        @BindView(R.id.btn_activate)
        Button btnActivate;

        public CouponViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
            btnActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActivateCouponListener != null) {
                        onActivateCouponListener.onActivateCoupon(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context context, final CouponInfo couponInfo, final int position, int viewType) {
            if (couponInfo == null) {
                return;
            }
            tvTitle.setText(couponInfo.getTitle());
            tvValue.setText(CommonUtil.formatDouble2String(couponInfo.getValue()));
            if (couponInfo.getType() != 1) {
                tvMoneySill.setVisibility(View.GONE);
            } else {
                tvMoneySill.setVisibility(View.VISIBLE);
                tvMoneySill.setText(context.getString(R.string.label_money_sill,
                        CommonUtil.formatDouble2String(couponInfo.getMoneySill())));
            }
            tvTotalCount.setText(context.getString(R.string.label_total_count,
                    couponInfo.getTotalCount()));
            tvProvidedCount.setText(context.getString(R.string.label_provided_count,
                    couponInfo.getProvidedCount()));
            tvOnlineUsedCount.setText(context.getString(R.string.label_online_used_count,
                    couponInfo.getOnlineUsedCount()));
            tvOfflineUsedCount.setText(context.getString(R.string.label_offline_used_count,
                    couponInfo.getOfflineUsedCount()));
            if (couponInfo.getValidStart() == null || couponInfo.getValidEnd() == null) {
                tvUsefulLife.setText("");
            } else {
                tvUsefulLife.setText(context.getString(R.string.label_useful_life,
                        couponInfo.getValidStart()
                                .toString(context.getString(R.string.format_date_type4,
                                        Locale.getDefault())),
                        couponInfo.getValidEnd()
                                .toString(context.getString(R.string.format_date_type4,
                                        Locale.getDefault()))));
            }
            if (couponInfo.isHidden()) { //已激活
                tvStatus.setText(R.string.label_activated);
                tvStatus.setTextColor(Color.parseColor("#80d089"));
                btnActivate.setText(R.string.label_deactivate);
                btnActivate.setBackgroundResource(R.drawable.sl_r12_stroke_gray_2_solid_light);
                btnActivate.setTextColor(ContextCompat.getColorStateList(context,
                        R.color.stroke_gray_text_color));
            } else {
                tvStatus.setText(R.string.label_not_activated);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                btnActivate.setText(R.string.label_activate);
                btnActivate.setBackgroundResource(R.drawable.sl_r12_stroke_primary_2_solid_light);
                btnActivate.setTextColor(ContextCompat.getColorStateList(context,
                        R.color.stroke_primary_text_color));
            }
        }
    }

    public interface OnActivateCouponListener {
        void onActivateCoupon(int position, CouponInfo couponInfo);
    }

}