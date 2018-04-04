package me.suncloud.marrymemo.adpter.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.wallet.MyRedPacketListActivity;

/**
 * 领券中心adapter
 * Created by chen_bin on 2016/11/30 0030.
 */
public class CertificateCenterRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private List<RedPacket> redPackets;
    private List<CouponInfo> coupons;
    private int logoSize;
    private static final int ITEM_TYPE_RED_PACKET = 0;
    private static final int ITEM_TYPE_COUPON = 1;
    private static final int ITEM_TYPE_FOOTER = 2;
    private static final int ITEM_TYPE_HEADER = 3;
    private LayoutInflater inflater;
    private OnReceiveListener onReceiveListener;

    public CertificateCenterRecyclerAdapter(
            Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.logoSize = CommonUtil.dp2px(context, 48);
    }

    public void setData(List<RedPacket> redPackets, List<CouponInfo> coupons) {
        this.redPackets = redPackets;
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    //顶部会员引导入口（文案的金额定死）
    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return !CommonUtil.isCollectionEmpty(redPackets) || !CommonUtil.isCollectionEmpty
                (coupons) ? 1 : 0;
    }

    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize
                (redPackets) + CommonUtil.getCollectionSize(
                coupons);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (position < CommonUtil.getCollectionSize(redPackets) + getHeaderViewCount()) {
            return ITEM_TYPE_RED_PACKET;
        } else {
            return ITEM_TYPE_COUPON;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout
                                .certificate_center_list_footer,
                        parent,
                        false));
            case ITEM_TYPE_RED_PACKET:
                return new RedPacketViewHolder(inflater.inflate(R.layout
                                .certificate_center_red_packet_list_item,
                        parent,
                        false));
            default:
                return new CouponViewHolder(inflater.inflate(R.layout
                                .certificate_center_coupon_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_RED_PACKET:
                int index = position - getHeaderViewCount();
                holder.setView(context, redPackets.get(index), index, viewType);
                break;
            case ITEM_TYPE_COUPON:
                index = position - getHeaderViewCount() - CommonUtil.getCollectionSize(redPackets);
                holder.setView(context, coupons.get(index), index, viewType);
                break;
        }
    }

    //红包的viewHolder
    public class RedPacketViewHolder extends BaseViewHolder<RedPacket> {
        @BindView(R.id.img_edge)
        ImageView imgEdge;
        @BindView(R.id.img_red_packet)
        ImageView imgRedPacket;
        @BindView(R.id.img_status)
        ImageView imgStatus;
        @BindView(R.id.tv_red_packet_name)
        TextView tvRedPacketName;
        @BindView(R.id.tv_category_type)
        TextView tvCategoryType;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.btn_receive)
        Button btnReceive;
        @BindView(R.id.btn_go_see)
        Button btnGoSee;
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;

        public RedPacketViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnGoSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, MyRedPacketListActivity.class));
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
            btnReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onReceiveListener != null) {
                        onReceiveListener.onReceiveRedPacket(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final RedPacket redPacket,
                final int position,
                int viewType) {
            //1待领取 2可领取 3抢光 4已领取
            if (redPacket.getGetStatus() == RedPacket.GET_STATUS_WAITING_RECEIVE && redPacket
                    .getProvideStartTime() != null) {
                tvStartTime.setVisibility(View.VISIBLE);
                imgStatus.setVisibility(View.GONE);
                btnReceive.setVisibility(View.GONE);
                btnGoSee.setVisibility(View.GONE);
                String str;
                switch (HljTimeUtils.getSurplusDay(redPacket.getProvideStartTime())) {
                    case 1:
                        str = context.getString(R.string.format_date_type_today);
                        break;
                    case 2:
                        str = context.getString(R.string.format_date_type_tomorrow);
                        break;
                    default:
                        str = redPacket.getProvideStartTime()
                                .toString(context.getString(R.string.format_date_type11));
                        break;
                }
                tvStartTime.setText(CommonUtil.fromHtml(context,
                        context.getString(R.string.label_red_packet_start_time,
                                str,
                                redPacket.getProvideStartTime()
                                        .toString(context.getString(R.string.format_date_type14)))));
            } else if (redPacket.getGetStatus() == RedPacket.GET_STATUS_CAN_RECEIVE) {
                btnReceive.setVisibility(View.VISIBLE);
                imgStatus.setVisibility(View.GONE);
                btnGoSee.setVisibility(View.GONE);
                tvStartTime.setVisibility(View.GONE);
            } else if (redPacket.getGetStatus() == RedPacket.GET_STATUS_LOOT_ALL) {
                imgStatus.setVisibility(View.VISIBLE);
                btnReceive.setVisibility(View.GONE);
                btnGoSee.setVisibility(View.GONE);
                tvStartTime.setVisibility(View.GONE);
                imgEdge.setBackgroundResource(R.drawable.bg_red_packet_edge_gray);
                imgRedPacket.setImageResource(R.drawable.icon_red_packet_gray_68_80);
                tvRmb.setTextColor(ContextCompat.getColor(context, R.color.colorGray3));
                tvValue.setTextColor(ContextCompat.getColor(context, R.color.colorGray3));
                imgStatus.setImageResource(R.drawable.icon_out_of_coupon);
            } else if (redPacket.getGetStatus() == RedPacket.GET_STATUS_RECEIVED) {
                btnGoSee.setVisibility(View.VISIBLE);
                imgStatus.setVisibility(View.VISIBLE);
                btnReceive.setVisibility(View.GONE);
                tvStartTime.setVisibility(View.GONE);
                imgStatus.setImageResource(R.drawable.icon_received_primary);
            }
            imgEdge.setBackgroundResource(R.drawable.bg_red_packet_edge_red);
            imgRedPacket.setImageResource(R.drawable.icon_red_packet_red_68_80);
            tvRmb.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tvValue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tvRedPacketName.setText(redPacket.getRedPacketName());
            tvCategoryType.setText(redPacket.getCategoryType());
            tvValue.setText(CommonUtil.formatDouble2String(redPacket.getAmount()));
            tvMoneySill.setText(redPacket.getMoneySill() == 0 ? context.getString(R.string
                    .label_money_sill_empty) : context.getString(
                    R.string.label_money_sill_available,
                    CommonUtil.formatDouble2String(redPacket.getMoneySill())));
        }
    }

    //优惠券的viewHolder
    public class CouponViewHolder extends BaseViewHolder<CouponInfo> {
        @BindView(R.id.top_layout)
        LinearLayout topLayout;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_money_sill)
        TextView tvMoneySill;
        @BindView(R.id.receive_layout)
        FrameLayout receiveLayout;
        @BindView(R.id.word_layout)
        LinearLayout wordLayout;
        @BindView(R.id.btn_go_use)
        Button btnGoUse;
        @BindView(R.id.img_received)
        ImageView imgReceived;

        public CouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Drawable drawable = ContextCompat.getDrawable(context,
                    R.drawable.image_bg_merchant_property);
            if (drawable != null) {
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                tvProperty.getPaint()
                        .setAntiAlias(true);
                tvProperty.getPaint()
                        .setFlags(Paint.ANTI_ALIAS_FLAG);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProperty
                        .getLayoutParams();
                params.topMargin = (int) (Math.sqrt(0.5 * width * width) - height);
                tvProperty.setPivotY(height);
            }
            btnGoUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CouponInfo couponInfo = getItem();
                    if (couponInfo != null && couponInfo.getMerchantId() > 0) {
                        Intent intent = new Intent(context, MerchantDetailActivity.class);
                        intent.putExtra("id", couponInfo.getMerchantId());
                        intent.putExtra("tab", 1); //点击优惠券的去使用要跳转到商家主页-套餐tab
                        context.startActivity(intent);
                    }
                }
            });
            receiveLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onReceiveListener != null) {
                        onReceiveListener.onReceiveCoupon(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final CouponInfo couponInfo,
                final int position,
                int viewType) {
            topLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            //1待领取 2可领取 3抢光 4已领取
            if (couponInfo.getGetStatus() == CouponInfo.GET_STATUS_CAN_RECEIVE) {
                wordLayout.setVisibility(View.VISIBLE);
                btnGoUse.setVisibility(View.GONE);
                imgReceived.setVisibility(View.GONE);
            } else if (couponInfo.getGetStatus() == CouponInfo.GET_STATUS_RECEIVED) {
                btnGoUse.setVisibility(View.VISIBLE);
                imgReceived.setVisibility(View.VISIBLE);
                wordLayout.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(couponInfo.getMerchant()
                            .getLogoPath())
                            .width(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgAvatar);
            if (TextUtils.isEmpty(couponInfo.getMerchant()
                    .getProperty()
                    .getName())) {
                tvProperty.setVisibility(View.GONE);
            } else {
                tvProperty.setVisibility(View.VISIBLE);
                tvProperty.setText(couponInfo.getMerchant()
                        .getProperty()
                        .getName());
            }
            tvMerchantName.setText(couponInfo.getMerchant()
                    .getName());
            tvValue.setText(CommonUtil.formatDouble2String(couponInfo.getValue()));
            tvMoneySill.setText(couponInfo.getMoneySill() == 0 ? context.getString(R.string
                    .label_money_sill_empty) : context.getString(
                    R.string.label_money_sill_available,
                    CommonUtil.formatDouble2String(couponInfo.getMoneySill())));
        }
    }

    public interface OnReceiveListener {

        void onReceiveRedPacket(int position, RedPacket redPacket);

        void onReceiveCoupon(int position, CouponInfo couponInfo);
    }

}