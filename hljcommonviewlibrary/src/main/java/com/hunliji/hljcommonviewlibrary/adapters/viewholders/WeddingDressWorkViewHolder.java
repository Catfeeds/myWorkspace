package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 婚纱礼服列表
 * Created by mo_yu on 2017/8/5
 * .
 */
public class WeddingDressWorkViewHolder extends TrackerWorkViewHolder {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_badge)
    ImageView imgBadge;
    @BindView(R2.id.cover_layout)
    RelativeLayout coverLayout;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.tv_area_tag)
    TextView tvAreaTag;
    @BindView(R2.id.tv_free_trial)
    TextView tvFreeTrial;
    @BindView(R2.id.tv_shop_gift)
    TextView tvShopGift;

    public int imageWidth;
    public int imageHeight;
    private int ruleWidth;
    private int ruleHeight;
    public OnItemClickListener onItemClickListener;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingDressWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                8)) / 2;
        this.imageHeight = Math.round(imageWidth * 4 / 3f);
        this.ruleWidth = CommonUtil.dp2px(itemView.getContext(), 60);
        this.ruleHeight = CommonUtil.dp2px(itemView.getContext(), 50);
        this.imgCover.getLayoutParams().width = imageWidth;
        this.imgCover.getLayoutParams().height = imageHeight;
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
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            final Context context, final Work work, final int position, int viewType) {
        if (work == null) {
            return;
        }
        String coverPath;
        if (!TextUtils.isEmpty(work.getVerticalImage())) {
            coverPath = ImagePath.buildPath(work.getVerticalImage())
                    .width(imageWidth)
                    .height(imageHeight)
                    .cropPath();
        } else {
            coverPath = ImagePath.buildPath(work.getCoverPath())
                    .width(imageWidth)
                    .height(imageHeight)
                    .cropPath();
        }
        Glide.with(context)
                .load(coverPath)
                .apply(new RequestOptions().override(imageWidth, imageHeight)
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        WorkRule rule = work.getRule();
        if (rule != null && !TextUtils.isEmpty(rule.getShowImg())) {
            String rulePath = ImagePath.buildPath(rule.getShowImg())
                    .width(ruleWidth)
                    .height(ruleHeight)
                    .cropPath();
            imgBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(rulePath)
                    .into(imgBadge);
        } else {
            imgBadge.setVisibility(View.GONE);
        }
        tvTitle.setText(work.getTitle());
        tvShowPrice.setText(String.valueOf((int) work.getShowPrice()));
        tvCommentCount.setText(context.getString(R.string.label_comment_count2___cv,
                work.getMerchant()
                        .getCommentCount()));
        tvFreeTrial.setVisibility(work.isFreeTrialYarn() ? View.VISIBLE : View.GONE);
        Merchant merchant = work.getMerchant();
        if (merchant.getId() > 0) {
            if (!TextUtils.isEmpty(merchant.getShopArea()
                    .getName())) {
                tvAreaTag.setVisibility(View.VISIBLE);
                tvAreaTag.setText(merchant.getShopArea()
                        .getName());
            } else {
                tvAreaTag.setVisibility(View.GONE);
            }
            tvShopGift.setVisibility(!CommonUtil.isEmpty(merchant.getShopGift()) ? View.VISIBLE :
                    View.GONE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}