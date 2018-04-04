package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by hua_rong on 2017/8/3.
 * 婚礼摄影
 */

public class WeddingPhotoViewHolder extends TrackerWorkViewHolder {
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.img_bond)
    ImageView imgBond;
    @BindView(R.id.img_refund)
    ImageView imgRefund;
    @BindView(R.id.img_promise)
    ImageView imgPromise;
    @BindView(R.id.img_gift)
    ImageView imgGift;
    @BindView(R.id.img_exclusive_offer)
    ImageView imgExclusiveOffer;
    @BindView(R.id.img_coupon)
    ImageView imgCoupon;
    @BindView(R.id.tv_area_name)
    TextView tvAreaName;
    @BindView(R.id.img_work_cover)
    ImageView imgWorkCover;
    @BindView(R.id.single_cover_layout)
    RelativeLayout singleCoverLayout;
    @BindView(R.id.img_case_cover_1)
    ImageView imgCaseCover1;
    @BindView(R.id.img_case_cover_2)
    ImageView imgCaseCover2;
    @BindView(R.id.img_case_cover_3)
    ImageView imgCaseCover3;
    @BindView(R.id.tv_case_img_count)
    TextView tvCaseImgCount;
    @BindView(R.id.case_cover_layout)
    RelativeLayout caseCoverLayout;
    @BindView(R.id.img_badge)
    ImageView imgBadge;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.show_price_layout)
    LinearLayout showPriceLayout;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_shop_gift)
    TextView tvShopGift;
    @BindView(R.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    private int width;
    private int coverHeight;
    private int bigCoverWidth;
    private int smallCoverSize;
    private int ruleWidth;
    private int ruleHeight;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingPhotoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        Point point = CommonUtil.getDeviceSize(context);
        width = point.x;

        coverHeight = Math.round(width * 5 / 8);
        caseCoverLayout.getLayoutParams().height = coverHeight;
        singleCoverLayout.getLayoutParams().height = coverHeight;
        smallCoverSize = Math.round((coverHeight - CommonUtil.dp2px(itemView.getContext(), 2)) / 2);
        bigCoverWidth = Math.round(width - CommonUtil.dp2px(itemView.getContext(),
                2) - smallCoverSize);
        imgCaseCover1.getLayoutParams().width = bigCoverWidth;
        imgCaseCover2.getLayoutParams().width = smallCoverSize;
        imgCaseCover2.getLayoutParams().height = smallCoverSize;
        imgCaseCover3.getLayoutParams().width = smallCoverSize;
        imgCaseCover3.getLayoutParams().height = smallCoverSize;
        this.ruleWidth = CommonUtil.dp2px(itemView.getContext(), 120);
        this.ruleHeight = CommonUtil.dp2px(itemView.getContext(), 100);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = getItem();
                Context context = v.getContext();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(context, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(Context mContext, Work work, int position, int viewType) {
        if (work == null) {
            return;
        }
        setWorkData(mContext, work);
        setMerchantData(mContext, work);
    }

    private void setWorkData(Context context, Work work) {
        List<WorkMediaItem> mediaItems = work.getMediaItems();
        if (mediaItems != null && mediaItems.size() >= 2) {
            singleCoverLayout.setVisibility(View.GONE);
            caseCoverLayout.setVisibility(View.VISIBLE);
            if (mediaItems.size() - 2 > 99) {
                tvCaseImgCount.setVisibility(View.VISIBLE);
                tvCaseImgCount.setText(String.valueOf(mediaItems.size() - 2));
            } else if (mediaItems.size() - 2 > 0) {
                tvCaseImgCount.setVisibility(View.VISIBLE);
                tvCaseImgCount.setText(String.format("+%s", mediaItems.size() - 2));
            } else {
                tvCaseImgCount.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(bigCoverWidth)
                            .height(coverHeight)
                            .cropPath())
                    .apply(new RequestOptions().override(bigCoverWidth, coverHeight)
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCaseCover1);
            for (int i = 0; i < mediaItems.size(); i++) {
                if (work.getMediaVideosCount() > 0) {
                    mediaItems.remove(i);
                }
            }
            if (mediaItems.size() > 0) {
                Glide.with(context)
                        .load(ImagePath.buildPath(mediaItems.get(0)
                                .getItemCover())
                                .width(smallCoverSize)
                                .height(smallCoverSize)
                                .cropPath())
                        .apply(new RequestOptions().override(smallCoverSize, smallCoverSize)
                                .placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgCaseCover2);
            }
            if (mediaItems.size() > 1) {
                Glide.with(context)
                        .load(ImagePath.buildPath(mediaItems.get(1)
                                .getItemCover())
                                .width(smallCoverSize)
                                .height(smallCoverSize)
                                .cropPath())
                        .apply(new RequestOptions().override(smallCoverSize, smallCoverSize)
                                .placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgCaseCover3);
            }
        } else {
            singleCoverLayout.setVisibility(View.VISIBLE);
            caseCoverLayout.setVisibility(View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(width)
                            .height(coverHeight)
                            .cropPath())
                    .apply(new RequestOptions().override(width, coverHeight)
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgWorkCover);
        }
        WorkRule rule = work.getRule();
        if (rule == null || TextUtils.isEmpty(rule.getBigImg())) {
            imgBadge.setVisibility(View.GONE);
        } else {
            imgBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ImagePath.buildPath(rule.getBigImg())
                            .width(ruleWidth)
                            .height(ruleHeight)
                            .cropPath())
                    .into(imgBadge);
        }
        tvTitle.setText(work.getTitle());
        if (work.getCommodityType() != 0) {
            showPriceLayout.setVisibility(View.GONE);
        } else {
            showPriceLayout.setVisibility(View.VISIBLE);
            tvShowPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
        }
    }

    private void setMerchantData(Context context, Work work) {
        Merchant merchant = work.getMerchant();
        if (merchant == null) {
            return;
        }
        tvMerchantName.setText(merchant.getName());
        int levelId = 0;
        if (merchant.getGrade() == 2) {
            levelId = com.hunliji.hljcommonviewlibrary.R.mipmap.icon_merchant_level2_36_36;
        } else if (merchant.getGrade() == 3) {
            levelId = com.hunliji.hljcommonviewlibrary.R.mipmap.icon_merchant_level3_36_36;
        } else if (merchant.getGrade() == 4) {
            levelId = com.hunliji.hljcommonviewlibrary.R.mipmap.icon_merchant_level4_36_36;
        }
        if (levelId == 0) {
            imgLevel.setVisibility(View.GONE);
        } else {
            imgLevel.setVisibility(View.VISIBLE);
            imgLevel.setImageResource(levelId);
        }
        imgBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        imgRefund.setVisibility(!CommonUtil.isCollectionEmpty(merchant.getChargeBack()) ? View
                .VISIBLE : View.GONE);
        imgPromise.setVisibility(!CommonUtil.isCollectionEmpty(merchant.getMerchantPromise()) ?
                View.VISIBLE : View.GONE);
        imgGift.setVisibility(!CommonUtil.isEmpty(merchant.getPlatformGift()) ? View.VISIBLE :
                View.GONE);
        imgExclusiveOffer.setVisibility(!CommonUtil.isEmpty(merchant.getExclusiveOffer()) ? View
                .VISIBLE : View.GONE);
        imgCoupon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
        tvAreaName.setText(merchant.getShopArea()
                .getName());
        tvCommentCount.setText(context.getString(R.string.label_comment_count2___cv,
                merchant.getCommentCount()));
        if (TextUtils.isEmpty(merchant.getShopGift())) {
            shopGiftLayout.setVisibility(View.GONE);
        } else {
            shopGiftLayout.setVisibility(View.VISIBLE);
            tvShopGift.setText(merchant.getShopGift());
        }
    }
}
