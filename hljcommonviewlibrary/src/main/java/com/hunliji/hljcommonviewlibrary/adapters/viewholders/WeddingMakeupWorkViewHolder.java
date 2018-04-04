package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 婚礼跟妆套餐viewHolder
 * Created by chen_bin on 2017/7/28 0028.
 */
public class WeddingMakeupWorkViewHolder extends TrackerWorkViewHolder {
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.img_level)
    ImageView imgLevel;
    @BindView(R2.id.img_bond)
    ImageView imgBond;
    @BindView(R2.id.img_refund)
    ImageView imgRefund;
    @BindView(R2.id.img_promise)
    ImageView imgPromise;
    @BindView(R2.id.img_gift)
    ImageView imgGift;
    @BindView(R2.id.img_exclusive_offer)
    ImageView imgExclusiveOffer;
    @BindView(R2.id.img_coupon)
    ImageView imgCoupon;
    @BindView(R2.id.tv_area_name)
    TextView tvAreaName;
    @BindView(R2.id.images_layout)
    LinearLayout imagesLayout;
    @BindView(R2.id.img_badge)
    ImageView imgBadge;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.show_price_layout)
    LinearLayout showPriceLayout;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.tv_shop_gift)
    TextView tvShopGift;
    @BindView(R2.id.shop_gift_layout)
    LinearLayout shopGiftLayout;
    private int imageSpace;
    private int imageWidth;
    private int imageHeight;
    private int ruleWidth;
    private int ruleHeight;
    private OnItemClickListener onItemClickListener;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingMakeupWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageSpace = CommonUtil.dp2px(itemView.getContext(), 2);
        this.imageWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - 2 * imageSpace) / 3;
        this.imageHeight = Math.round(imageWidth * 3.0f / 4.0f);
        this.ruleWidth = CommonUtil.dp2px(itemView.getContext(), 60);
        this.ruleHeight = CommonUtil.dp2px(itemView.getContext(), 50);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
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
        setWork(mContext, work);
        setMerchant(mContext, work);
    }

    private void setWork(Context context, Work work) {
        if (CommonUtil.isCollectionEmpty(work.getMediaItems())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            ArrayList<WorkMediaItem> mediaItems = new ArrayList<>();
            for (WorkMediaItem mediaItem : work.getMediaItems()) {
                if (mediaItem.getKind() == WorkMediaItem.MEDIA_KIND_PHOTO) {
                    mediaItems.add(mediaItem);
                }
                if (mediaItems.size() == 3) {
                    break;
                }
            }
            if (mediaItems.size() == 0) {
                imagesLayout.setVisibility(View.GONE);
            } else {
                imagesLayout.setVisibility(View.VISIBLE);
                int size = mediaItems.size();
                int count = imagesLayout.getChildCount();
                if (count > size) {
                    imagesLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    View view = null;
                    final WorkMediaItem mediaItem = mediaItems.get(i);
                    if (count > i) {
                        view = imagesLayout.getChildAt(i);
                    }
                    if (view == null) {
                        View.inflate(context, R.layout.image_item___cm, imagesLayout);
                        view = imagesLayout.getChildAt(imagesLayout.getChildCount() - 1);
                    }
                    ImageView imgCover = view.findViewById(R.id.image);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgCover
                            .getLayoutParams();
                    params.width = imageWidth;
                    params.height = imageHeight;
                    params.leftMargin = i == 0 ? 0 : imageSpace;
                    Glide.with(context)
                            .load(ImagePath.buildPath(mediaItem.getItemCover())
                                    .width(imageWidth)
                                    .height(imageHeight)
                                    .cropPath())
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(imgCover);
                }
            }
        }
        WorkRule rule = work.getRule();
        if (rule == null || TextUtils.isEmpty(rule.getShowImg())) {
            imgBadge.setVisibility(View.GONE);
        } else {
            imgBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ImagePath.buildPath(rule.getShowImg())
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

    private void setMerchant(Context context, Work work) {
        Merchant merchant = work.getMerchant();
        if (merchant == null) {
            return;
        }
        tvMerchantName.setText(merchant.getName());
        int levelId = 0;
        if (merchant.getGrade() == 2) {
            levelId = R.mipmap.icon_merchant_level2_36_36;
        } else if (merchant.getGrade() == 3) {
            levelId = R.mipmap.icon_merchant_level3_36_36;
        } else if (merchant.getGrade() == 4) {
            levelId = R.mipmap.icon_merchant_level4_36_36;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
