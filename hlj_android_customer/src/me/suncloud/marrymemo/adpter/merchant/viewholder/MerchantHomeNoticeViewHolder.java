package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/9/28.
 */

public class MerchantHomeNoticeViewHolder extends BaseViewHolder<Merchant> {

    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.notice_layout)
    LinearLayout noticeLayout;
    @BindView(R.id.notice_bottom_line_layout)
    View noticeBottomLineLayout;
    @BindView(R.id.tv_shop_gift)
    TextView tvShopGift;
    @BindView(R.id.tv_shop_gift_count)
    TextView tvShopGiftCount;
    @BindView(R.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    @BindView(R.id.shop_gift_bottom_line_layout)
    View shopGiftBottomLineLayout;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.coupon_layout)
    LinearLayout couponLayout;
    @BindView(R.id.tv_exclusive_offer)
    TextView tvExclusiveOffer;
    @BindView(R.id.exclusive_offer_layout)
    LinearLayout exclusiveOfferLayout;
    @BindView(R.id.subscribe_hotel_top_line_layout)
    View subscribeHotelTopLineLayout;
    @BindView(R.id.subscribe_hotel_layout)
    LinearLayout subscribeHotelLayout;

    private NoticeClickListener listener;

    public MerchantHomeNoticeViewHolder(ViewGroup parent, NoticeClickListener listener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_notice_shop_gift_coupon_layout, parent, false));
        this.listener = listener;
    }

    private MerchantHomeNoticeViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        noticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onNoticeClick();
                }
            }
        });
        shopGiftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onGiftClick();
                }
            }
        });
        couponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCouponClick();
                }

            }
        });
        exclusiveOfferLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onExclusiveOfferClick();
                }

            }
        });
        subscribeHotelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSubscribeHotelClick();
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, Merchant merchant, int position, int viewType) {
        boolean showNotice = false;
        boolean showShopGift = false;
        boolean showCoupon = false;
        boolean showExclusiveOffer = false;
        boolean showSubscribeHotel = merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL;
        if (TextUtils.isEmpty(merchant.getNoticeStr())) {
            noticeLayout.setVisibility(View.GONE);
        } else {
            showNotice = true;
            noticeLayout.setVisibility(View.VISIBLE);
            tvNotice.setText(merchant.getNoticeStr());
        }
        if (TextUtils.isEmpty(merchant.getShopGift())) {
            shopGiftLayout.setVisibility(View.GONE);
        } else {
            showShopGift = true;
            shopGiftLayout.setVisibility(View.VISIBLE);
            tvShopGift.setText(merchant.getShopGift());
            tvShopGiftCount.setText(tvShopGiftCount.getContext()
                    .getString(R.string.label_shop_gift_received_count,
                            merchant.getShopGiftCount()));
        }
        if (!merchant.isCoupon() || CommonUtil.isCollectionEmpty(merchant.getCoupons())) {
            couponLayout.setVisibility(View.GONE);
        } else {
            showCoupon = true;
            couponLayout.setVisibility(View.VISIBLE);
            tvCoupon.setText(tvCoupon.getContext()
                    .getString(R.string.label_coupon_count2,
                            merchant.getCoupons()
                                    .size()));
        }

        //酒店优惠
        if (TextUtils.isEmpty(merchant.getExclusiveOffer())) {
            exclusiveOfferLayout.setVisibility(View.GONE);
        } else {
            showExclusiveOffer = true;
            exclusiveOfferLayout.setVisibility(View.VISIBLE);
            tvExclusiveOffer.setText(merchant.getExclusiveContent());
        }
        //订阅酒店优惠
        subscribeHotelLayout.setVisibility(showSubscribeHotel ? View.VISIBLE : View.GONE);

        noticeBottomLineLayout.setVisibility(showNotice && (showShopGift || showCoupon ||
                showExclusiveOffer) ? View.VISIBLE : View.GONE);
        shopGiftBottomLineLayout.setVisibility(showShopGift && showCoupon ? View.VISIBLE : View
                .GONE);
        subscribeHotelTopLineLayout.setVisibility(showSubscribeHotel && (showNotice ||
                showShopGift || showExclusiveOffer) ? View.VISIBLE : View.GONE);
    }

    public interface NoticeClickListener {
        void onNoticeClick();

        void onGiftClick();

        void onCouponClick();

        void onExclusiveOfferClick();

        void onSubscribeHotelClick();
    }
}
