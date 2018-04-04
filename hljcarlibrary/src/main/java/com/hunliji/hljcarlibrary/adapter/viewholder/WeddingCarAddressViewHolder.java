package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarDetailComment;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jinxin on 2017/12/28 0028.
 */

public class WeddingCarAddressViewHolder extends BaseViewHolder<WeddingCarProduct> {

    @BindView(R2.id.tv_address)
    TextView tvAddress;
    @BindView(R2.id.tv_order_car)
    TextView tvOrderCar;

    private Context mContext;
    private onWeddingCarAddressClickListener onWeddingCarAddressClickListener;

    public WeddingCarAddressViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setOnWeddingCarAddressClickListener(WeddingCarAddressViewHolder
                                                            .onWeddingCarAddressClickListener
                                                            onWeddingCarAddressClickListener) {
        this.onWeddingCarAddressClickListener = onWeddingCarAddressClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarProduct carProduct, int position, int viewType) {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        WeddingCarDetailComment carDetailComment = carProduct.getMerchantComment();
        tvAddress.setText(carDetailComment.getAddress());
    }
    
    @OnClick(R2.id.tv_order_car)
    void onOrderCar(){
        if(onWeddingCarAddressClickListener != null){
            onWeddingCarAddressClickListener.onOrderCar();
        }
    }

    public interface  onWeddingCarAddressClickListener{
        void onOrderCar();
    }
}
