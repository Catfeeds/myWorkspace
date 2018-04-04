package com.hunliji.marrybiz.adapter.weddingcar.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jinxin on 2018/1/5 0005.
 */

public class WeddingCarOrderHeaderViewHolder extends BaseViewHolder<WeddingCarOrderDetail> {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.img_sms)
    ImageView imgSms;
    @BindView(R.id.tv_user_address)
    TextView tvUserAddress;
    @BindView(R.id.tv_user_time)
    TextView tvUserTime;

    private Context mContext;
    private onWeddingCarOrderHeaderClickListener onWeddingCarOrderHeaderClickListener;
    private WeddingCarOrderDetail orderDetail;

    public WeddingCarOrderHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setOnWeddingCarOrderHeaderClickListener(
            WeddingCarOrderHeaderViewHolder.onWeddingCarOrderHeaderClickListener
                    onWeddingCarOrderHeaderClickListener) {
        this.onWeddingCarOrderHeaderClickListener = onWeddingCarOrderHeaderClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarOrderDetail orderDetail, int position, int viewType) {
        if (orderDetail == null) {
            return;
        }

        this.orderDetail = orderDetail;
        tvName.setText(orderDetail.getBuyerName());
        tvPhone.setText(orderDetail.getBuyerPhone());
        tvUserAddress.setText(orderDetail.getAddressDetail());
        tvUserTime.setText(orderDetail.getStartAt() == null ? null : orderDetail.getStartAt()
                .toString(HljCommon.DateFormat.DATE_FORMAT_LONG));
        int imgSmsRes = orderDetail.getUserId() > 0 ? R.drawable.icon_chat_bubble_black_46_44 : R
                .drawable.icon_chat_bubble_gray_46_44;
        imgSms.setImageResource(imgSmsRes);
    }

    @OnClick(R.id.img_call)
    void onCall() {
        if (onWeddingCarOrderHeaderClickListener != null) {
            onWeddingCarOrderHeaderClickListener.onCall(orderDetail);
        }
    }

    @OnClick(R.id.img_sms)
    void onSms() {
        if (onWeddingCarOrderHeaderClickListener != null) {
            onWeddingCarOrderHeaderClickListener.onSms(orderDetail);
        }
    }

    public interface onWeddingCarOrderHeaderClickListener {
        void onCall(WeddingCarOrderDetail orderDetail);

        void onSms(WeddingCarOrderDetail orderDetail);
    }
}
