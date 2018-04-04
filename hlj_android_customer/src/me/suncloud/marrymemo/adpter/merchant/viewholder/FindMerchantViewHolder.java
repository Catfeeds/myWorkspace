package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.merchant.MerchantListFragment;
import me.suncloud.marrymemo.model.City;

/**
 * Created by hua_rong on 2018/3/9
 * 找商家
 */

public class FindMerchantViewHolder extends TrackerMerchantViewHolder {

    @BindView(R.id.merchant_view)
    RelativeLayout merchantView;
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
    @BindView(R.id.shop_gift_content)
    TextView shopGiftContent;
    @BindView(R.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    @BindView(R.id.cost_effective_content)
    TextView costEffectiveContent;
    @BindView(R.id.cost_effective_layout)
    LinearLayout costEffectiveLayout;
    @BindView(R.id.star_rating_bar)
    RatingBar starRatingBar;
    @BindView(R.id.merchant_comment_count)
    TextView merchantCommentCount;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.img_ultimate_tag)
    ImageView imgUltimateTag;
    @BindView(R.id.tv_area_name)
    TextView tvAreaName;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.ll_shop_photo)
    LinearLayout llShopPhoto;
    @BindView(R.id.shop_photo_1)
    ImageView shopPhoto1;
    @BindView(R.id.shop_photo_2)
    ImageView shopPhoto2;
    @BindView(R.id.shop_photo_3)
    ImageView shopPhoto3;
    @BindView(R.id.vertical_line)
    View verticalLine;
    @BindView(R.id.btn_contact_merchant)
    Button btnContactMerchant;

    private OnItemClickListener onItemClickListener;
    private int imageWidth;
    private int imageHeight;
    private Location location;
    private City city;
    private String cpmSource;


    public FindMerchantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        location = LocationSession.getInstance()
                .getLocation(context);
        Point point = CommonUtil.getDeviceSize(context);
        imageWidth = Math.round((point.x - CommonUtil.dp2px(context, 36)) / 3);
        imageHeight = Math.round(imageWidth * 3 / 4);
        shopPhoto1.getLayoutParams().width = imageWidth;
        shopPhoto2.getLayoutParams().width = imageWidth;
        shopPhoto3.getLayoutParams().width = imageWidth;
        shopPhoto1.getLayoutParams().height = imageHeight;
        shopPhoto2.getLayoutParams().height = imageHeight;
        shopPhoto3.getLayoutParams().height = imageHeight;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem() == null) {
                    return;
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getItemPosition(), getItem());
                }
            }
        });
    }

    private void initMessageTracker() {
        try {
            HljVTTagger.buildTagger(btnContactMerchant)
                    .tagName(HljTaggerName.FIND_MERCHANT_CONTACT_BTN)
                    .dataId(getItem().getId())
                    .atPosition(getItemPosition())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_MERCHANT)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCity(City city) {
        this.city = city;
    }

    @OnClick(R.id.btn_contact_merchant)
    void onContactMerchant(View view) {
        Merchant merchant = getItem();
        if (!AuthUtil.loginBindCheck(view.getContext())) {
            return;
        }
        if (merchant != null && merchant.getId() > 0) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                            .WS_CUSTOMER_CHAT_ACTIVITY)
                    .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                            merchant.getUserId())
                    .navigation(view.getContext());
        }
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
            Context mContext, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        initMessageTracker();
        tvDistance.setVisibility(View.GONE);
        if (city != null && city.getId() > 0 && location != null && !TextUtils.isEmpty(location
                .getCity()) && !TextUtils.isEmpty(
                city.getName()) && location.getCity()
                .startsWith(city.getName())) {
            LatLng start = new LatLng(merchant.getLatitude(), merchant.getLongitude());
            LatLng end = new LatLng(location.getLatitude(), location.getLongitude());
            float distance = AMapUtils.calculateLineDistance(start, end);
            if (distance > 0 && distance < 1000) {
                tvDistance.setVisibility(View.VISIBLE);
                tvDistance.setText("小于1km");
            } else if (distance >= 1000 && distance <= 100 * 1000) {
                tvDistance.setVisibility(View.VISIBLE);
                tvDistance.setText(Math.round(distance / 1000.0f) + "km");
            }
        }
        String shopArea = null;
        ParentArea parentArea = merchant.getShopArea();
        if (city == null || city.getId() == 0) {
            if (parentArea.getParentArea() != null) {
                shopArea = parentArea.getParentArea()
                        .getName();
            }
        } else {
            shopArea = parentArea.getName();
        }
        tvAreaName.setText(shopArea);
        if (TextUtils.isEmpty(shopArea) || tvDistance.getVisibility() == View.GONE) {
            verticalLine.setVisibility(View.GONE);
        } else {
            verticalLine.setVisibility(View.VISIBLE);
        }
        List<Work> works = merchant.getNewRecommendMeals();
        int workSize = CommonUtil.getCollectionSize(works);
        if (workSize < 2) {
            llShopPhoto.setVisibility(View.GONE);
        } else {
            llShopPhoto.setVisibility(View.VISIBLE);
            if (workSize > 0 && works.get(0) != null) {
                Glide.with(mContext)
                        .load(getCropPath(works.get(0)))
                        .into(shopPhoto1);
                shopPhoto1.setVisibility(View.VISIBLE);
            } else {
                shopPhoto1.setVisibility(View.GONE);
            }
            if (workSize > 1 && works.get(1) != null) {
                Glide.with(mContext)
                        .load(getCropPath(works.get(1)))
                        .into(shopPhoto2);
                shopPhoto2.setVisibility(View.VISIBLE);
            } else {
                shopPhoto2.setVisibility(View.GONE);
            }
            if (workSize > 2 && works.get(2) != null) {
                Glide.with(mContext)
                        .load(getCropPath(works.get(2)))
                        .into(shopPhoto3);
                shopPhoto3.setVisibility(View.VISIBLE);
            } else {
                shopPhoto3.setVisibility(View.GONE);
            }
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
                        .width(CommonUtil.dp2px(mContext, 50))
                        .height(CommonUtil.dp2px(mContext, 50))
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imgLogo);
        //评价星级
        starRatingBar.setVisibility(View.VISIBLE);
        merchantCommentCount.setVisibility(merchant.getCommentCount() == 0 ? View.INVISIBLE :
                View.VISIBLE);
        starRatingBar.setRating(merchant.getCommentStatistics() == null ? 0 : (float) merchant
                .getCommentStatistics()
                .getScore());
        merchantCommentCount.setText(merchant.getCommentCount() == 0 ? "" : mContext.getString(R
                        .string.label_merchant_comment_count___cm,
                merchant.getCommentCount()));
        iconsLayout.setVisibility(View.VISIBLE);
        imgGiftIcon.setVisibility(View.GONE);
        imgExclusiveOfferIcon.setVisibility(View.GONE);

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
        //优惠券图标显示
        imgCouponIcon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
        shopGiftContent.setText(merchant.getShopGift());
        shopGiftLayout.setVisibility(CommonUtil.isEmpty(merchant.getShopGift()) ? View.GONE :
                View.VISIBLE);
    }

    private String getCropPath(Work work) {
        return ImagePath.buildPath(work.getCoverPath())
                .width(imageWidth)
                .height(imageHeight)
                .cropPath();
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

