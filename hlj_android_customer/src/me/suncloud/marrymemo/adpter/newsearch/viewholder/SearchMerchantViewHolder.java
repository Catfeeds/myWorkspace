package me.suncloud.marrymemo.adpter.newsearch.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.view.newsearch.NewSearchResultActivity;

/**
 * Created by hua_rong on 2018/1/16
 * 新版商家结果页
 */

public class SearchMerchantViewHolder extends TrackerMerchantViewHolder {


    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_level_icon)
    ImageView imgLevelIcon;
    @BindView(R.id.img_bond_icon)
    ImageView imgBondIcon;
    @BindView(R.id.img_refund_icon)
    ImageView imgRefundIcon;
    @BindView(R.id.img_promise_icon)
    ImageView imgPromiseIcon;
    @BindView(R.id.img_free_icon)
    ImageView imgFreeIcon;
    @BindView(R.id.img_gift_icon)
    ImageView imgGiftIcon;
    @BindView(R.id.img_exclusive_offer_icon)
    ImageView imgExclusiveOfferIcon;
    @BindView(R.id.img_coupon_icon)
    ImageView imgCouponIcon;
    @BindView(R.id.icons_layout)
    LinearLayout iconsLayout;
    @BindView(R.id.tv_hotel_price)
    TextView tvHotelPrice;
    @BindView(R.id.price_layout)
    LinearLayout priceLayout;
    @BindView(R.id.tv_label1)
    TextView tvLabel1;
    @BindView(R.id.tv_label2)
    TextView tvLabel2;
    @BindView(R.id.tv_label3)
    TextView tvLabel3;
    @BindView(R.id.shop_gift_content)
    TextView shopGiftContent;
    @BindView(R.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    @BindView(R.id.star_rating_bar)
    RatingBar starRatingBar;
    @BindView(R.id.merchant_comment_count)
    TextView merchantCommentCount;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.img_ultimate_tag)
    ImageView imgUltimateTag;
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ll_label)
    LinearLayout llLabel;
    private OnItemClickListener onItemClickListener;

    public SearchMerchantViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public String cpmSource() {
        return NewSearchResultActivity.CPM_SOURCE;
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        ParentArea parentArea = merchant.getShopArea();
        String areaName = parentArea.getName();
        if (parentArea.getLevel() == 3 && parentArea.getParentArea() != null) {
            areaName = parentArea.getParentArea()
                    .getName() + areaName;
        }
        tvCityName.setText(areaName);
        if (merchant.getPriceStart() > 0) {
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(String.format(Locale.getDefault(),
                    "￥%s起",
                    CommonUtil.formatDouble2String(merchant.getPriceStart())));
        } else {
            tvPrice.setVisibility(View.GONE);
        }
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
                        .width(CommonUtil.dp2px(mContext, 72))
                        .height(CommonUtil.dp2px(mContext, 72))
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imgLogo);

        Hotel hotel = merchant.getHotel();
        if (hotel != null) {
            starRatingBar.setVisibility(View.GONE);
            merchantCommentCount.setVisibility(View.GONE);
            imgLevelIcon.setVisibility(View.GONE);
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
            imgExclusiveOfferIcon.setVisibility(CommonUtil.isEmpty(merchant.getExclusiveOffer())
                    ? View.GONE : View.VISIBLE);
            priceLayout.setVisibility(View.VISIBLE);
            tvHotelPrice.setText(hotel.getPriceStr());
            llLabel.setVisibility(View.VISIBLE);
            tvLabel1.setText("容纳" + String.valueOf(hotel.getTableNum()) + "桌");
            tvLabel2.setText(hotel.getArea());
            tvLabel3.setText(hotel.getKind());
        } else {
            //评价星级
            starRatingBar.setVisibility(View.VISIBLE);
            merchantCommentCount.setVisibility(merchant.getCommentCount() == 0 ? View.INVISIBLE :
                    View.VISIBLE);
            starRatingBar.setRating(merchant.getCommentStatistics() == null ? 0 : (float)
                    merchant.getCommentStatistics()
                    .getScore());
            merchantCommentCount.setText(merchant.getCommentCount() == 0 ? "" : mContext.getString(

                    R.string.label_merchant_comment_count___cm, merchant.getCommentCount()));
            iconsLayout.setVisibility(View.VISIBLE);
            imgGiftIcon.setVisibility(View.GONE);
            imgExclusiveOfferIcon.setVisibility(View.GONE);
            priceLayout.setVisibility(View.GONE);
            int levelId = 0;
            if (merchant.getGrade() == 2) {
                levelId = R.mipmap.icon_merchant_level2___cm;
            } else if (merchant.getGrade() == 3) {
                levelId = R.mipmap.icon_merchant_level3___cm;
            } else if (merchant.getGrade() == 4) {
                levelId = R.mipmap.icon_merchant_level4___cm;
            }
            if (levelId != 0) {
                imgLevelIcon.setVisibility(View.VISIBLE);
                imgLevelIcon.setImageResource(levelId);
            } else {
                imgLevelIcon.setVisibility(View.GONE);
            }
            imgBondIcon.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
            imgFreeIcon.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE : View.GONE);
            imgPromiseIcon.setVisibility(merchant.getMerchantPromise() != null && merchant
                    .getMerchantPromise()
                    .size() > 0 ? View.VISIBLE : View.GONE);
            imgRefundIcon.setVisibility(merchant.getChargeBack() != null && merchant.getChargeBack()
                    .size() > 0 ? View.VISIBLE : View.GONE);
            llLabel.setVisibility(View.GONE);
            tvLabel1.setText("套餐 " + String.valueOf(merchant.getActiveWorkCount()));
            tvLabel2.setText("案例 " + String.valueOf(merchant.getActiveCaseCount()));
            tvLabel3.setText("粉丝 " + String.valueOf(merchant.getFansCount()));
        }

        //优惠券图标显示
        imgCouponIcon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
        shopGiftContent.setText(merchant.getShopGift());
        shopGiftLayout.setVisibility(CommonUtil.isEmpty(merchant.getShopGift()) ? View.GONE :
                View.VISIBLE);
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
