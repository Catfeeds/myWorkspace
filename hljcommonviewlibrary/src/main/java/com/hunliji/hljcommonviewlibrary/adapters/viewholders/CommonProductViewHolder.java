package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 婚品列表
 * Created by mo_yu on 2016/11/10
 * .
 */
public class CommonProductViewHolder extends TrackerProductViewHolder {
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_badge)
    ImageView imgBadge;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R2.id.content_layout)
    View contentLayout;
    public int imageWidth;
    public int imageHeight;
    public int badgeSize;
    public OnItemClickListener onItemClickListener;
    public final static int STYLE_RATIO_1_TO_1 = 0; //1:1比例
    public final static int STYLE_RATIO_2_TO_3 = 1; //1:1.5比例

    public CommonProductViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.badgeSize = CommonUtil.dp2px(itemView.getContext(), 40);
        this.imageWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                8)) / 2;
        switch (style) {
            case STYLE_RATIO_1_TO_1:
                this.imageHeight = imageWidth;
                break;
            case STYLE_RATIO_2_TO_3:
                this.imageHeight = Math.round(imageWidth * 1.5f);
                break;
        }
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
            final Context context, final ShopProduct product, final int position, int viewType) {
        if (product == null) {
            return;
        }

        Glide.with(context)
                .load(ImagePath.buildPath(product.getCoverPath())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().override(imageWidth, imageHeight)
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(product.getTitle());
        tvShowPrice.setText(NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getShowPrice
                ()));
        tvCollectCount.setText(context.getString(R.string.label_collect_count2___cv,
                String.valueOf(product.getCollectCount())));
        if (product.getRule() == null || TextUtils.isEmpty(product.getRule()
                .getShowImg())) {
            imgBadge.setVisibility(View.GONE);
        } else {
            imgBadge.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ImageUtil.getImagePath2(product.getRule()
                            .getShowImg(), badgeSize))
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgBadge);
        }
        if (TextUtils.isEmpty(product.getTitle())) {
            tvTitle.setText("");
        } else {
            boolean isNew = false;
            StringBuffer sb = new StringBuffer();
            if (product.getCreatedAt() != null) {
                isNew = (HljTimeUtils.getServerCurrentTimeMillis() - product.getCreatedAt()
                        .getMillis()) / (24 * 60 * 60 * 1000) <= 15;
                if (isNew) {
                    sb.append(context.getString(R.string.label_new_product___cv));
                }
            }
            if (product.getShipingFee() <= 0) {
                if (isNew) {
                    sb.append(" ");
                }
                sb.append(context.getString(R.string.label_free_shipping___cv));
            }
            if (TextUtils.isEmpty(sb)) {
                tvTitle.setText(product.getTitle());
            } else {
                sb.append(" ");
                SpannableStringBuilder builder = new SpannableStringBuilder(sb.toString() +
                        product.getTitle());
                if (isNew) {
                    Drawable drawable = ContextCompat.getDrawable(context,
                            R.mipmap.icon_split_line_10_24);
                    drawable.setBounds(0,
                            0,
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    builder.setSpan(new HljImageSpan(drawable),
                            2,
                            3,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff4a65")),
                        0,
                        sb.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}