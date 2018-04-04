package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商家信息viewHolder
 * Created by chen_bin on 2017/5/11 0011.
 */
public class MerchantBriefInfoViewHolder extends BaseViewHolder<Merchant> {
    @BindView(R2.id.merchant_header_view)
    RelativeLayout merchantHeaderView;
    @BindView(R2.id.tv_merchant_header_title)
    TextView tvMerchantHeaderTitle;
    @BindView(R2.id.merchant_view)
    LinearLayout merchantView;
    @BindView(R2.id.img_merchant_logo)
    RoundedImageView imgMerchantLogo;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.img_level)
    ImageView imgLevel;
    @BindView(R2.id.img_bond)
    ImageView imgBond;
    @BindView(R2.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.tv_address)
    TextView tvAddress;
    @BindView(R2.id.tv_work_count)
    TextView tvWorkCount;
    @BindView(R2.id.tv_fan_count)
    TextView tvFanCount;
    @BindView(R2.id.img_arrow)
    ImageView imgArrow;
    @BindView(R2.id.bottom_thin_line_layout)
    View bottomThinLineLayout;
    @BindView(R2.id.bottom_thick_line_layout)
    View bottomThickLineLayout;
    @BindView(R2.id.header_tag)
    View headerTag;
    private int logoSize;
    private OnItemClickListener onItemClickListener;

    public MerchantBriefInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        logoSize = CommonUtil.dp2px(itemView.getContext(), 60);
        merchantView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context context, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        Glide.with(context)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgMerchantLogo);
        int res = 0;
        switch (merchant.getGrade()) {
            case 2:
                res = R.mipmap.icon_merchant_level2_32_32;
                break;
            case 3:
                res = R.mipmap.icon_merchant_level3_32_32;
                break;
            case 4:
                res = R.mipmap.icon_merchant_level4_32_32;
                break;
            default:
                break;
        }
        if (res != 0) {
            imgLevel.setVisibility(View.VISIBLE);
            imgLevel.setImageResource(res);
        } else {
            imgLevel.setVisibility(View.GONE);
        }
        imgBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE : View.GONE);
        tvMerchantName.setText(merchant.getName());
        if (merchant.getShopType() == Merchant.SHOP_TYPE_PRODUCT) { //婚品商家
            ratingBar.setVisibility(View.GONE);
            tvCommentCount.setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
            tvWorkCount.setVisibility(View.VISIBLE);
            tvFanCount.setVisibility(View.VISIBLE);
            tvWorkCount.setText(context.getString(R.string.label_work_count___cv,
                    merchant.getWorksCount()));
            tvFanCount.setText(context.getString(R.string.label_fan_count___cv,
                    merchant.getFansCount()));
        } else {
            tvAddress.setVisibility(View.VISIBLE);
            tvCommentCount.setVisibility(View.VISIBLE);
            tvWorkCount.setVisibility(View.GONE);
            tvFanCount.setVisibility(View.GONE);
            tvAddress.setText(merchant.getAddress());
            if (merchant.getCommentStatistics() != null) {
                ratingBar.setRating((float) merchant.getCommentStatistics()
                        .getScore());
            }
            if (merchant.getCommentCount() <= 0) {
                tvCommentCount.setText(R.string.label_no_comment___cv);
            } else {
                tvCommentCount.setText(context.getString(R.string.label_comment_count___cv,
                        merchant.getCommentCount()));
            }
        }
    }

    public void setShowMerchantHeaderView(boolean showMerchantHeaderView) {
        merchantHeaderView.setVisibility(showMerchantHeaderView ? View.VISIBLE : View.GONE);
    }

    //服务商家左边的红线标志，笔记详情中显示
    public void showHeaderTag(boolean isShow) {
        headerTag.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setMerchantHeaderTitle(String merchantHeaderTitle) {
        tvMerchantHeaderTitle.setText(merchantHeaderTitle);
    }

    public void setShowBottomThinLineView(boolean showBottomThinLineView) {
        bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
        bottomThickLineLayout.setVisibility(View.GONE);
    }

    public void setShowBottomThickLineView(boolean showBottomThickLineView) {
        bottomThinLineLayout.setVisibility(View.GONE);
        bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}