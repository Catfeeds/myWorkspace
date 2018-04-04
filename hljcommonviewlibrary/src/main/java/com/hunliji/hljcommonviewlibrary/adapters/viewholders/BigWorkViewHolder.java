package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 套餐(带活动)大图样式公用viewholder
 * Created by jinxin on 2016/12/7 0007.
 */

public class BigWorkViewHolder extends TrackerWorkViewHolder {
    View itemView;
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_installment)
    ImageView imgInstallment;
    @BindView(R2.id.badge)
    ImageView badge;
    @BindView(R2.id.re_cover)
    RelativeLayout reCover;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_money)
    TextView tvMoney;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.tv_collect_hint)
    TextView tvCollectHint;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R2.id.li_price)
    LinearLayout liPrice;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.img_level)
    ImageView imgLevel;
    @BindView(R2.id.img_bond)
    ImageView imgBond;
    @BindView(R2.id.img_refund)
    ImageView imgRefund;
    @BindView(R2.id.img_promise)
    ImageView imgPromise;
    @BindView(R2.id.img_free)
    ImageView imgFree;
    @BindView(R2.id.li_merchant_icon)
    LinearLayout liMerchantIcon;
    @BindView(R2.id.li_merchant)
    LinearLayout liMerchant;
    @BindView(R2.id.img_coupon)
    ImageView imgCoupon;
    @BindView(R2.id.tv_property)
    TextView tvProperty;
    @BindView(R2.id.line_layout)
    View lineLayout;
    private int imgHeight;
    private int imgWidth;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int ruleWidth;
    private boolean isShowMerchantProperty;//是否显示商家类别

    public BigWorkViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.itemView.setTag(this);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        tvPrice.getPaint()
                .setAntiAlias(true);
        tvPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        Point point = CommonUtil.getDeviceSize(itemView.getContext());
        imgWidth = point.x;
        imgHeight = Math.round(imgWidth * 10.0f / 16);
        ruleWidth = Math.round(dm.density *120);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, final Work work, final int position, int viewType) {
        if (work == null) {
            return;
        }

        String imagePath = ImageUtil.getImagePath(work.getCoverPath(), imgWidth);
        imgCover.getLayoutParams().height = imgHeight;
        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
        } else {
            Glide.with(context)
                    .clear(imgCover);
            imgCover.setImageResource(R.mipmap.icon_empty_image);
        }
        tvTitle.setText(work.getTitle());
        if (work.getCommodityType() == 0) {
            liPrice.setVisibility(View.VISIBLE);
            tvMoney.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
        } else {
            liPrice.setVisibility(View.GONE);
        }
        if (work.getMarketPrice() > 0) {
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(context.getString(R.string.label_price9___cv,
                    CommonUtil.formatDouble2String(work.getMarketPrice())));
        } else {
            tvPrice.setVisibility(View.GONE);
        }
        tvCollectCount.setText(String.valueOf(work.getCollectorsCount()));
        imgInstallment.setVisibility(View.GONE);
        WorkRule rule = work.getRule();
        if (rule != null && !TextUtils.isEmpty(rule.getBigImg())) {
            badge.setVisibility(View.VISIBLE);
            String rulePath = ImageUtil.getImagePath(rule.getBigImg(), ruleWidth);
            Glide.with(mContext)
                    .load(rulePath)
                    .into(badge);
        } else {
            badge.setVisibility(View.GONE);
            Glide.with(mContext)
                    .clear(badge);
        }

        Merchant merchant = work.getMerchant();
        if (merchant.getId() > 0) {
            if (isShowMerchantProperty) {
                tvProperty.setVisibility(View.VISIBLE);
                tvProperty.setText(merchant.getPropertyName());
            } else {
                tvProperty.setVisibility(View.GONE);
            }
            liMerchant.setVisibility(View.VISIBLE);
            tvName.setText(merchant.getName());
            imgBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
            imgFree.setVisibility(merchant.getActiveWorkCount() > 0 ? View.VISIBLE : View.GONE);
            imgPromise.setVisibility(merchant.getMerchantPromise() != null && merchant
                    .getMerchantPromise()
                    .size() > 0 ? View.VISIBLE : View.GONE);
            imgRefund.setVisibility(merchant.getChargeBack() != null && merchant.getChargeBack()
                    .size() > 0 ? View.VISIBLE : View.GONE);
            imgCoupon.setVisibility(merchant.isCoupon() ? View.VISIBLE : View.GONE);
            int levelId = 0;
            if (merchant.getGrade() == 2) {
                levelId = R.mipmap.icon_merchant_level2_32_32;
            } else if (merchant.getGrade() == 3) {
                levelId = R.mipmap.icon_merchant_level3_32_32;
            } else if (merchant.getGrade() == 4) {
                levelId = R.mipmap.icon_merchant_level4_32_32;
            }
            if (levelId != 0) {
                imgLevel.setVisibility(View.VISIBLE);
                imgLevel.setImageResource(levelId);
            } else {
                imgLevel.setVisibility(View.GONE);
            }
        } else {
            liMerchant.setVisibility(View.GONE);
            imgBond.setVisibility(View.GONE);
            imgFree.setVisibility(View.GONE);
            imgPromise.setVisibility(View.GONE);
            imgRefund.setVisibility(View.GONE);
            imgLevel.setVisibility(View.GONE);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, work);
                }
            }
        });
    }

    public void setShowMerchantProperty(boolean showMerchantProperty) {
        this.isShowMerchantProperty = showMerchantProperty;
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
