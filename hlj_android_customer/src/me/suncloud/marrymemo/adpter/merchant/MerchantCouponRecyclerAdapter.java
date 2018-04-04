package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 商家端主页的优惠券列表
 * Created by chen_bin on 2016/11/3 0003.
 */
public class MerchantCouponRecyclerAdapter extends RecyclerView
        .Adapter<BaseViewHolder<CouponInfo>> {
    private Context context;
    private List<CouponInfo> coupons;
    private LayoutInflater inflater;
    private OnReceiveCouponListener onReceiveCouponListener;

    public MerchantCouponRecyclerAdapter(Context context) {
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

    public void setOnReceiveCouponListener(OnReceiveCouponListener onReceiveCouponListener) {
        this.onReceiveCouponListener = onReceiveCouponListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(coupons);
    }

    @Override
    public BaseViewHolder<CouponInfo> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.merchant_coupon_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CouponInfo> holder, int position) {
        holder.setView(context, coupons.get(position), position, getItemViewType(position));
    }

    public class ViewHolder extends BaseViewHolder<CouponInfo> {
        @BindView(R.id.receive_layout)
        RelativeLayout receiveLayout;
        @BindView(R.id.value_layout)
        LinearLayout valueLayout;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.word_layout)
        LinearLayout wordLayout;
        @BindView(R.id.tv_word)
        TextView tvWord;
        @BindView(R.id.tv_coupon_date)
        TextView tvCouponDate;
        @BindView(R.id.img_coupon_tag)
        ImageView imgCouponTag;
        int colorPrimary;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            colorPrimary = ThemeUtil.getAttrColor(itemView.getContext(),
                    R.attr.hljCouponColor,
                    ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
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
            tvValue.setText(CommonUtil.formatDouble2String(couponInfo.getValue()));
            tvMoneySill.setText(couponInfo.getMoneySill() > 0 ? context.getString(R.string
                            .label_money_sill_available,
                    CommonUtil.formatDouble2String(couponInfo.getMoneySill())) : context
                    .getString(R.string.label_money_sill_empty2));
            tvCouponDate.setText("有效期 " + couponInfo.getValidStart()
                    .toString("yyyy.MM.dd") + " - " + couponInfo.getValidEnd()
                    .toString("yyyy.MM.dd"));
            if (couponInfo.isUsed()) {
                tvWord.setText("已领取  ");
                tvValue.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                tvMoneySill.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                tvValue.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                tvRmb.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                receiveLayout.setBackgroundResource(R.drawable.image_bg_coupon_receive_gray);
            } else {
                tvWord.setText(R.string.label_immediately_receive);
                tvValue.setTextColor(colorPrimary);
                tvMoneySill.setTextColor(colorPrimary);
                tvValue.setTextColor(colorPrimary);
                tvRmb.setTextColor(colorPrimary);
                receiveLayout.setBackgroundResource(ThemeUtil.getAttrResourceId(receiveLayout
                                .getContext(),
                        R.attr.hljIconMerchantCoupon,
                        R.drawable.image_bg_coupon_receive_red));
            }
        }
    }

    public interface OnReceiveCouponListener {
        void onReceiveCoupon(int position, CouponInfo couponInfo);
    }
}