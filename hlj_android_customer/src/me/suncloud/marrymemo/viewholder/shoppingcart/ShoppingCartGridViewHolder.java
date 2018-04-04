package me.suncloud.marrymemo.viewholder.shoppingcart;

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
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
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
public class ShoppingCartGridViewHolder extends TrackerProductViewHolder {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_badge)
    ImageView imgBadge;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;
    private int imageWidth;
    private int badgeSize;
    private OnItemClickListener onItemClickListener;

    public ShoppingCartGridViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.badgeSize = CommonUtil.dp2px(itemView.getContext(), 40);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x / 2 - CommonUtil.dp2px(
                itemView.getContext(),
                4);
        this.imgCover.getLayoutParams().width = imageWidth;
        this.imgCover.getLayoutParams().height = imageWidth;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
                        .height(imageWidth)
                        .cropPath())
                .apply(new RequestOptions().override(imageWidth, imageWidth)
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
}