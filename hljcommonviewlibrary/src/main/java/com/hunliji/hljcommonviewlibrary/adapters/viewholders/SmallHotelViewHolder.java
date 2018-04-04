package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 17/9/8.
 * 酒店商家列表小视图item
 */

public class SmallHotelViewHolder extends TrackerMerchantViewHolder {

    @BindView(R2.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R2.id.img_level_icon)
    ImageView imgLevelIcon;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_label1)
    TextView tvLabel1;
    @BindView(R2.id.tv_label3)
    TextView tvLabel3;
    @BindView(R2.id.img_bond_icon)
    ImageView imgBondIcon;
    @BindView(R2.id.img_refund_icon)
    ImageView imgRefundIcon;
    @BindView(R2.id.img_promise_icon)
    ImageView imgPromiseIcon;
    @BindView(R2.id.img_free_icon)
    ImageView imgFreeIcon;
    @BindView(R2.id.img_gift_icon)
    ImageView imgGiftIcon;
    @BindView(R2.id.img_exclusive_offer_icon)
    ImageView imgExclusiveOfferIcon;
    @BindView(R2.id.img_coupon_icon)
    ImageView imgCouponIcon;
    @BindView(R2.id.icons_layout)
    LinearLayout iconsLayout;
    @BindView(R2.id.tv_hotel_area)
    TextView tvHotelArea;
    @BindView(R2.id.tv_hotel_price)
    TextView tvHotelPrice;
    @BindView(R2.id.price_layout)
    LinearLayout priceLayout;
    @BindView(R2.id.tv_shop_gift_content)
    TextView tvShopGiftContent;
    @BindView(R2.id.tv_shop_gift_subtitle)
    TextView tvShopGiftSubtitle;
    @BindView(R2.id.tv_shop_gift_name)
    TextView tvShopGiftName;
    @BindView(R2.id.img_shop_gift)
    ImageView imgShopGift;
    @BindView(R2.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    @BindView(R2.id.cost_effective_content)
    TextView costEffectiveContent;
    @BindView(R2.id.cost_effective_layout)
    LinearLayout costEffectiveLayout;
    @BindView(R2.id.merchant_info_view)
    LinearLayout merchantInfoView;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.img_ultimate_tag)
    ImageView imgUltimateTag;
    @BindView(R2.id.merchant_view)
    RelativeLayout merchantView;
    @BindView(R2.id.feature_view)
    LinearLayout featureView;
    @BindView(R2.id.tv_feature_title)
    TextView tvFeatureTitle;
    @BindView(R2.id.tv_order_count)
    TextView tvOrderCount;
    private String cpmSource;

    private int logoWidth;

    private OnItemClickListener onItemClickListener;

    public SmallHotelViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        logoWidth = CommonUtil.dp2px(itemView.getContext(), 100);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    public void setCpmSource(String cpmSource) {
        this.cpmSource = cpmSource;
    }

    @Override
    public String cpmSource() {
        return cpmSource;
    }

    @Override
    protected void setViewData(
            Context mContext, final Merchant merchant, final int position, int viewType) {
        if (merchant == null) {
            return;
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, merchant);
                }
            }
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvName
                .getLayoutParams();
        if (merchant.getIsPro() == Merchant.MERCHANT_ULTIMATE) {
            imgUltimateTag.setVisibility(View.VISIBLE);
            layoutParams.rightMargin = CommonUtil.dp2px(mContext, 24);
        } else {
            imgUltimateTag.setVisibility(View.GONE);
            layoutParams.rightMargin = 0;
        }
        tvName.setText(merchant.getName());
        Glide.with(mContext)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoWidth)
                        .height(logoWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imgLogo);

        Hotel hotel = merchant.getHotel();
        if (hotel != null) {
            if (hotel.isAdv()) {
                //订单数
                tvOrderCount.setVisibility(View.VISIBLE);
                tvOrderCount.setText(mContext.getString(R.string.label_order_count___cv,
                        merchant.getHotelOrderViewCount()));
                imgLevelIcon.setVisibility(View.VISIBLE);
                imgLevelIcon.setImageResource(R.mipmap.icon_optimization_yellow_64_32);
            } else {
                tvOrderCount.setVisibility(View.INVISIBLE);
                imgLevelIcon.setVisibility(View.GONE);
            }
            imgBondIcon.setVisibility(View.GONE);
            imgPromiseIcon.setVisibility(View.GONE);
            imgRefundIcon.setVisibility(View.GONE);
            boolean isIconShow = !CommonUtil.isEmpty(merchant.getFreeOrder()) || !CommonUtil
                    .isEmpty(
                    merchant.getPlatformGift()) || !CommonUtil.isEmpty(merchant.getExclusiveOffer
                    ());

            iconsLayout.setVisibility(isIconShow ? View.VISIBLE : View.GONE);
            imgFreeIcon.setVisibility(CommonUtil.isEmpty(merchant.getFreeOrder()) ? View.GONE :
                    View.VISIBLE);
            imgGiftIcon.setVisibility(CommonUtil.isEmpty(merchant.getPlatformGift()) ? View.GONE
                    : View.VISIBLE);
            imgExclusiveOfferIcon.setVisibility(View.GONE);
            tvHotelPrice.setText(CommonUtil.formatDouble2String(hotel.getPriceStart()));
            tvLabel1.setText(hotel.getTableStr() + "桌");
            tvHotelArea.setText(hotel.getArea());
            tvLabel3.setText(hotel.getKind());
        }
        imgCouponIcon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
        if (!CommonUtil.isEmpty(merchant.getShopGift())) {
            //到店礼
            imgShopGift.setVisibility(View.GONE);
            shopGiftLayout.setVisibility(View.VISIBLE);
            tvShopGiftSubtitle.setVisibility(View.VISIBLE);
            tvShopGiftName.setVisibility(View.VISIBLE);
            tvShopGiftContent.setText(merchant.getShopGift());
        } else if (!TextUtils.isEmpty(merchant.getExclusiveOffer())) {
            //普通优惠
            imgShopGift.setVisibility(View.VISIBLE);
            shopGiftLayout.setVisibility(View.VISIBLE);
            tvShopGiftSubtitle.setVisibility(View.GONE);
            tvShopGiftName.setVisibility(View.GONE);
            tvShopGiftContent.setText(merchant.getExclusiveContent());
        } else {
            shopGiftLayout.setVisibility(View.GONE);
        }

        //酒店特色优惠（个性化）
        if (!TextUtils.isEmpty(merchant.getPrivilegeFeature())) {
            featureView.setVisibility(View.VISIBLE);
            tvFeatureTitle.setText(merchant.getPrivilegeFeature());
        } else {
            featureView.setVisibility(View.GONE);
            tvFeatureTitle.setText("");
        }
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setShowPriceView(boolean showPriceView) {
        priceLayout.setVisibility(showPriceView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
