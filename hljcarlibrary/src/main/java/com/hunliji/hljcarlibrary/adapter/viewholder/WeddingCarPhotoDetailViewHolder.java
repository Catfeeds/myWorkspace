package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2018/1/19 0019.
 */

public class WeddingCarPhotoDetailViewHolder extends BaseViewHolder<WeddingCarProduct> {

    @BindView(R2.id.tv_photo_detail)
    TextView tvPhotoDetail;

    public WeddingCarPhotoDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarProduct carProduct, int position, int viewType) {
        if(carProduct == null){
            return;
        }

        tvPhotoDetail.setText(carProduct.getDescribe());
    }
}
