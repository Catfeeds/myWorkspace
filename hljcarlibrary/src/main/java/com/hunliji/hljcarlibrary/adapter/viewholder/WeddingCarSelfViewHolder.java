package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerWeddingCarProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2017/4/21.
 */

public class WeddingCarSelfViewHolder extends TrackerWeddingCarProductViewHolder {

    @BindView(R2.id.left_space)
    Space leftSpace;
    @BindView(R2.id.right_space)
    Space rightSpace;
    @BindView(R2.id.iv_cover)
    ImageView ivCover;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.tv_original_price)
    TextView tvOriginalPrice;
    @BindView(R2.id.img_discounts_type)
    ImageView imgDiscountsType;

    private OnItemClickListener<WeddingCarProduct> onItemClickListener;
    private int width;

    public WeddingCarSelfViewHolder(
            ViewGroup parentView, OnItemClickListener<WeddingCarProduct> onItemClickListener) {
        this(LayoutInflater.from(parentView.getContext())
                .inflate(R.layout.wedding_car_self_list_item___car, parentView, false));
        this.onItemClickListener = onItemClickListener;
    }


    public WeddingCarSelfViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvOriginalPrice.getPaint()
                .setAntiAlias(true);
        tvOriginalPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        Point point = CommonUtil.getDeviceSize(itemView.getContext());
        width = Math.round(point.x / 2 - CommonUtil.dp2px(itemView.getContext(), 15));
        ivCover.getLayoutParams().height = Math.round(width * 9 / 16);
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
    protected void setViewData(
            Context mContext, WeddingCarProduct car, int position, int viewType) {
        leftSpace.setVisibility(position % 2 == 0 ? View.VISIBLE : View.GONE);
        rightSpace.setVisibility(position % 2 == 1 ? View.VISIBLE : View.GONE);

        tvTitle.setText(car.getTitle());
        tvPrice.setText(tvPrice.getContext()
                .getString(R.string.label_price5___cv,
                        NumberFormatUtil.formatDouble2String(car.getShowPrice())));
        if (car.getMarketPrice() > 0) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(" 市场价" + tvOriginalPrice.getContext()
                    .getString(R.string.label_price5___cv,
                            NumberFormatUtil.formatDouble2String(car.getMarketPrice())) + " ");
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }
        String coverPath = car.getCoverImage() == null ? null : car.getCoverImage()
                .getImagePath();
        Glide.with(ivCover.getContext())
                .load(ImageUtil.getImagePath(coverPath, width))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(ivCover);
        imgDiscountsType.setVisibility(View.GONE);
    }
}
