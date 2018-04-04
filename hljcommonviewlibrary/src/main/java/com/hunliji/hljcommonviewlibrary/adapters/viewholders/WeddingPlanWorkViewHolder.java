package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 婚礼策划套餐viewHolder
 * Created by chen_bin on 2017/7/28 0028.
 */
public class WeddingPlanWorkViewHolder extends TrackerWorkViewHolder {

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_badge)
    ImageView imgBadge;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_area_name)
    TextView tvAreaName;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.show_price_layout)
    LinearLayout showPriceLayout;

    private int imageWidth;
    private int imageHeight;
    private int ruleWidth;
    private int ruleHeight;
    private int logoSize;
    private OnItemClickListener onItemClickListener;

    @Override
    public String cpmSource() {
        return "merchant_serve_channel";
    }

    public WeddingPlanWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
        this.imageHeight = Math.round(imageWidth * 10.0f / 16.0f);
        this.ruleWidth = CommonUtil.dp2px(itemView.getContext(), 120);
        this.ruleHeight = CommonUtil.dp2px(itemView.getContext(), 100);
        this.logoSize = CommonUtil.dp2px(itemView.getContext(), 20);
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
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
        Glide.with(context)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
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

    private void setMerchant(Context context, Work work) {
        Merchant merchant = work.getMerchant();
        if (merchant == null) {
            return;
        }
        tvMerchantName.setText(merchant.getName());
        tvAreaName.setText(getAreaName(work));
    }

    private String getAreaName(Work work) {
        String areaName = null;
        ParentArea parentArea = work.getMerchant().getShopArea();
        if (parentArea != null) {
            areaName = parentArea.getName();
            if (work.isLvPai()) {
                parentArea = parentArea.getParentArea();
                if (parentArea != null) {
                    areaName = parentArea.getName();
                }
            }
        }
        return areaName;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}