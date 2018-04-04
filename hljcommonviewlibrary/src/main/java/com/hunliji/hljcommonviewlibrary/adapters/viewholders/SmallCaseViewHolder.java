package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerCaseViewHolder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by werther on 16/12/1.
 */

public class SmallCaseViewHolder extends TrackerCaseViewHolder {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_hot_tag)
    ImageView imgHotTag;
    @BindView(R2.id.img_cover_layout)
    RelativeLayout imgCoverLayout;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_describe)
    TextView tvDescribe;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.tv_merchant_property)
    public TextView tvMerchantProperty;
    @BindView(R2.id.tv_city_name)
    TextView tvCityName;

    public int workImgWidth;
    private OnItemClickListener onItemClickListener;

    public static final int CASE_SMALL_COMMON_1 = 1; // 小图通用样式1
    public static final int CASE_SMALL_COMMON_2 = 2; // 小图通用样式2

    private int style;
    private boolean showPropertyTag; // 是否显示分类标识

    public SmallCaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.workImgWidth = CommonUtil.dp2px(itemView.getContext(), 120);
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, final Work item, final int position, int viewType) {
        if (item == null) {
            return;
        }

        Glide.with(mContext)
                .load(ImageUtil.getImagePath(item.getCoverPath(), workImgWidth))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        String title = TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle();
        tvTitle.setText(title);

        //收藏数
        tvCollectCount.setVisibility(View.VISIBLE);
        tvCollectCount.setText(mContext.getString(R.string.label_collect_count___cv,
                String.valueOf(item.getCollectorsCount())));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            }
        });
        if (style == CASE_SMALL_COMMON_1) {
            // 标题双行，不显示描述，有商家名
            tvDescribe.setVisibility(View.GONE);
            tvMerchantName.setVisibility(View.VISIBLE);
            tvMerchantName.setText(item.getMerchant()
                    .getName());
            tvTitle.setMaxLines(2);
        } else {
            tvDescribe.setVisibility(View.VISIBLE);
            tvMerchantName.setVisibility(View.GONE);
            tvDescribe.setText(item.getDescribe());
            tvTitle.setMaxLines(1);
        }
        //商家类别
        if (showPropertyTag && !TextUtils.isEmpty(item.getMerchant()
                .getProperty()
                .getName())) {
            tvMerchantProperty.setVisibility(View.VISIBLE);
            tvMerchantProperty.setText(item.getMerchant()
                    .getProperty()
                    .getName());
        } else {
            tvMerchantProperty.setVisibility(View.GONE);
        }
    }

    public void setShowPropertyTag(boolean showPropertyTag) {
        this.showPropertyTag = showPropertyTag;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setCityName(String cityName) {
        tvCityName.setVisibility(View.VISIBLE);
        tvCityName.setText(cityName);
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
