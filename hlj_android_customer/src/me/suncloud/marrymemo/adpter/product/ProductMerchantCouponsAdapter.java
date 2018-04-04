package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by luohanlin on 2017/12/6.
 */

public class ProductMerchantCouponsAdapter extends RecyclerView
        .Adapter<BaseViewHolder<CouponInfo>> {


    private Context context;
    private List<CouponInfo> coupons;
    private LayoutInflater inflater;
    private OnReceiveCouponListener onReceiveCouponListener;

    public ProductMerchantCouponsAdapter(Context context, List<CouponInfo> couponInfos) {
        this.context = context;
        this.coupons = couponInfos;
        this.inflater = LayoutInflater.from(context);
    }

    public List<CouponInfo> getCoupons() {
        return coupons;
    }

    public void setOnReceiveCouponListener(OnReceiveCouponListener onReceiveCouponListener) {
        this.onReceiveCouponListener = onReceiveCouponListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(coupons);
    }

    @Override
    public BaseViewHolder<CouponInfo> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.product_merchant_coupon_list_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CouponInfo> holder, int position) {
        holder.setView(context, coupons.get(position), position, getItemViewType(position));
    }

    public class ViewHolder extends BaseViewHolder<CouponInfo> {
        @BindView(R.id.receive_layout)
        LinearLayout receiveLayout;
        @BindView(R.id.value_layout)
        LinearLayout valueLayout;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.word_layout)
        LinearLayout wordLayout;
        @BindView(R.id.tv_word)
        TextView tvWord;
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.end_padding)
        View endPadding;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            receiveLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CouponInfo couponInfo = getItem();
                    if (onReceiveCouponListener != null && !couponInfo.isUsed()) {
                        onReceiveCouponListener.onReceiveCoupon(getAdapterPosition(), couponInfo);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context mContext, final CouponInfo couponInfo, int position, int viewType) {
            if (couponInfo == null) {
                return;
            }
            if (position == 0) {
                startPadding.setVisibility(View.VISIBLE);
            } else {
                startPadding.setVisibility(View.GONE);
            }
            if (position == coupons.size() - 1) {
                endPadding.setVisibility(View.VISIBLE);
            } else {
                endPadding.setVisibility(View.GONE);
            }

            tvValue.setText(context.getString(R.string.label_price,
                    CommonUtil.formatDouble2String(couponInfo.getValue())));
            tvMoneySill.setText(couponInfo.getType() == 1 ? context.getString(R.string
                            .label_money_sill_available,
                    CommonUtil.formatDouble2String(couponInfo.getMoneySill())) : context
                    .getString(R.string.label_money_sill_empty2));
            if (couponInfo.isUsed()) {
                valueLayout.setBackgroundResource(R.drawable.image_bg_coupon_received_left_gray);
                wordLayout.setBackgroundResource(R.drawable.image_bg_coupon_received_right_gray);
                tvWord.setText(R.string.label_already);
            } else {
                valueLayout.setBackgroundResource(ThemeUtil.getAttrResourceId(valueLayout
                                .getContext(),
                        R.attr.hljIconMerchantCouponLeft,
                        R.mipmap.image_bg_coupon_receive_left_red));
                wordLayout.setBackgroundResource(ThemeUtil.getAttrResourceId(wordLayout
                                .getContext(),
                        R.attr.hljIconMerchantCouponRight,
                        R.mipmap.image_bg_coupon_receive_right_red));
                tvWord.setText(R.string.label_immediately);
            }
        }
    }

    public interface OnReceiveCouponListener {
        void onReceiveCoupon(int position, CouponInfo couponInfo);
    }
}
