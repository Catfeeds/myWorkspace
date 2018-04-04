package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerWeddingCarProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2018/1/12
 * 婚车结果页
 */

public class SmallWeddingCarViewHolder extends TrackerWeddingCarProductViewHolder {

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_hot_tag)
    ImageView imgHotTag;
    @BindView(R2.id.tv_merchant_property)
    TextView tvMerchantProperty;
    @BindView(R2.id.tv_city_name)
    TextView tvCityName;
    @BindView(R2.id.tv_head_car)
    TextView tvHeadCar;
    @BindView(R2.id.tv_head_car_name)
    TextView tvHeadCarName;
    @BindView(R2.id.ll_head_car)
    LinearLayout llHeadCar;
    @BindView(R2.id.tv_follow_car)
    TextView tvFollowCar;
    @BindView(R2.id.tv_follow_car_name)
    TextView tvFollowCarName;
    @BindView(R2.id.ll_follow_car)
    LinearLayout llFollowCar;
    @BindView(R2.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R2.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R2.id.ll_market_average_price)
    LinearLayout llMarketAveragePrice;
    @BindView(R2.id.bottom_line_layout)
    View bottomLineLayout;
    private int width;
    private int height;

    private OnItemClickListener onItemClickListener;

    public SmallWeddingCarViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        width = CommonUtil.dp2px(context, 120);
        height = CommonUtil.dp2px(context, 75);
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

    public void setShowBottomLineView(boolean isShow) {
        bottomLineLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void setViewData(
            Context context, WeddingCarProduct car, int position, int viewType) {
        if (car != null) {
            itemView.setVisibility(View.VISIBLE);
            int carType = car.getType();
            llFollowCar.setVisibility(View.GONE);
            tvHeadCar.setVisibility(View.GONE);
            switch (carType) {
                case WeddingCarProduct.TYPE_WORK:
                    tvHeadCarName.setText(car.getMainCar());
                    tvFollowCarName.setText(car.getShowSubCarTitle());
                    llFollowCar.setVisibility(View.VISIBLE);
                    tvHeadCar.setVisibility(View.VISIBLE);
                    break;
                case WeddingCarProduct.TYPE_SELF:
                    tvHeadCarName.setText(car.getTitle());
                    break;
                default:
                    break;
            }
            if (car.getCity() != null) {
                tvCityName.setText(car.getCity()
                        .getName());
            }
            tvShowPrice.setText(CommonUtil.formatDouble2String(car.getShowPrice()));
            if (car.getMarketPrice() > 0) {
                llMarketAveragePrice.setVisibility(View.VISIBLE);
                tvMarketPrice.setText(CommonUtil.formatDouble2String(car.getMarketPrice()));
                tvMarketPrice.getPaint()
                        .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else {
                llMarketAveragePrice.setVisibility(View.GONE);
            }
            String coverPath = car.getCoverImage() == null ? null : car.getCoverImage()
                    .getImagePath();
            Glide.with(context)
                    .load(ImagePath.buildPath(coverPath)
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCover);
            imgHotTag.setVisibility(View.GONE);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
