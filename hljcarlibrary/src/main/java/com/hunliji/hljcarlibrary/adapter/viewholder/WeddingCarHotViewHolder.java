package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerWeddingCarProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/27.
 */

public class WeddingCarHotViewHolder extends TrackerWeddingCarProductViewHolder {

    @BindView(R2.id.iv_cover)
    ImageView ivCover;
    @BindView(R2.id.tv_lead_car)
    TextView tvLeadCar;
    @BindView(R2.id.tv_follow_car)
    TextView tvFollowCar;
    @BindView(R2.id.tv_price_hint)
    TextView tvPriceHint;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.img_discounts_type)
    ImageView imgDiscountsType;
    @BindView(R2.id.tv_original_price)
    TextView tvOriginalPrice;
    private OnItemClickListener<WeddingCarProduct> onItemClickListener;

    private int width;

    public WeddingCarHotViewHolder(
            ViewGroup parentView, OnItemClickListener<WeddingCarProduct> onItemClickListener) {
        this(LayoutInflater.from(parentView.getContext())
                .inflate(R.layout.wedding_car_hot_list_item___car, parentView, false));
        this.onItemClickListener = onItemClickListener;
    }

    private WeddingCarHotViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvOriginalPrice.getPaint()
                .setAntiAlias(true);
        tvOriginalPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        width = CommonUtil.getDeviceSize(itemView.getContext()).x;
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
        tvLeadCar.setText(!CommonUtil.isEmpty(car.getMainCar()) ? car.getMainCar() : car.getTitle
                ());
        tvFollowCar.setText(car.getShowSubCarTitle());
        tvPrice.setText(NumberFormatUtil.formatDouble2String(car.getShowPrice()));
        if (car.getMarketPrice() > 0) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText("市场价" + tvOriginalPrice.getContext()
                    .getString(R.string.label_price5___cv,
                            NumberFormatUtil.formatDouble2String(car.getMarketPrice())));
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }
        String coverPath = car.getCoverImage() == null ? null : car.getCoverImage()
                .getImagePath();
        Glide.with(ivCover.getContext())
                .load(ImagePath.buildPath(coverPath)
                        .width(width)
                        .path())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(ivCover);
        imgDiscountsType.setVisibility(View.GONE);
    }
}
