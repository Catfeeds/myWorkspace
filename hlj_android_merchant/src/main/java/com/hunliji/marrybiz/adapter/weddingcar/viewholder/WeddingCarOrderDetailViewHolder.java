package com.hunliji.marrybiz.adapter.weddingcar.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSkuItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderSub;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2018/1/5 0005.
 */

public class WeddingCarOrderDetailViewHolder extends BaseViewHolder<WeddingCarOrderSub> {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_main_car_hint)
    TextView tvMainCarHint;
    @BindView(R.id.tv_main_car)
    TextView tvMainCar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_follow_car_hint)
    TextView tvFollowCarHint;
    @BindView(R.id.tv_follow_car)
    TextView tvFollowCar;
    @BindView(R.id.layout_follow_car)
    LinearLayout layoutFollowCar;
    @BindView(R.id.tv_sku)
    TextView tvSku;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_car_count)
    TextView tvCarCount;
    @BindView(R.id.layout_sku)
    LinearLayout layoutSku;

    private Context mContext;
    private int imgWidth;
    private int imgHeight;


    public WeddingCarOrderDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        imgWidth = CommonUtil.dp2px(mContext, 100);
        imgHeight = CommonUtil.dp2px(mContext, 62);
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarOrderSub carOrderSub, int position, int viewType) {
        if (carOrderSub == null) {
            return;
        }
        WeddingCarProduct carProduct = carOrderSub.getProduct();
        if (carProduct != null) {
            Glide.with(mContext)
                    .load(ImagePath.buildPath(carProduct.getCoverImage() == null ? null :
                            carProduct.getCoverImage()
                            .getImagePath())
                            .width(imgWidth)
                            .height(imgHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCover);
            int type = carProduct.getType();
            if (type == WeddingCarProduct.TYPE_SELF) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(carProduct.getTitle());
                tvMainCar.setVisibility(View.GONE);
                tvMainCarHint.setVisibility(View.GONE);
                layoutFollowCar.setVisibility(View.GONE);
            } else if (type == WeddingCarProduct.TYPE_WORK) {
                tvTitle.setVisibility(View.GONE);
                tvMainCar.setText(carProduct.getMainCar());
                tvFollowCar.setText(carProduct.getShowSubCarTitle());
                tvMainCar.setVisibility(View.VISIBLE);
                tvMainCarHint.setVisibility(View.VISIBLE);
                layoutFollowCar.setVisibility(View.VISIBLE);
            }

            tvPrice.setText(mContext.getString(R.string.label_price5,
                    NumberFormatUtil.formatDouble2String(carProduct.getShowPrice())));
        }
        WeddingCarSku sku = carOrderSub.getSku();
        StringBuilder builder = new StringBuilder();
        if (sku != null) {
            List<WeddingCarSkuItem> skuItemList = sku.getSkuItem();
            if (skuItemList != null) {
                for (WeddingCarSkuItem item : skuItemList) {
                    if (item != null) {
                        builder.append(item.getValue())
                                .append("；");
                    }
                }
            }
        }
        if (builder.lastIndexOf("；") == builder.length() - 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        tvSku.setText(builder.toString());
        tvCarCount.setText(mContext.getString(R.string.label_wedding_car_count,
                String.valueOf(carOrderSub.getQuantity())));
    }
}
