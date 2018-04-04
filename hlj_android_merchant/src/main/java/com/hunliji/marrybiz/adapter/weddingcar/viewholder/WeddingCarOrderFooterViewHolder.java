package com.hunliji.marrybiz.adapter.weddingcar.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2018/1/5 0005.
 */

public class WeddingCarOrderFooterViewHolder extends BaseViewHolder<WeddingCarOrderDetail> {

    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_order_account)
    TextView tvOrderAccount;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_order_money)
    TextView tvOrderMoney;
    @BindView(R.id.tv_release_money)
    TextView tvReleaseMoney;
    @BindView(R.id.tv_work_price)
    TextView tvWorkPrice;
    @BindView(R.id.tv_red_packet)
    TextView tvRedPacket;
    @BindView(R.id.tv_fact_money)
    TextView tvFactMoney;
    @BindView(R.id.tv_fact_in_money)
    TextView tvFactInMoney;
    @BindView(R.id.tv_red_packet_name)
    TextView tvRedPacketName;
    @BindView(R.id.layout_deposit_money)
    LinearLayout layoutDepositMoney;
    @BindView(R.id.tv_pay_money)
    TextView tvPayMoney;
    @BindView(R.id.layout_all_money)
    LinearLayout layoutAllMoney;
    @BindView(R.id.layout_red_packet)
    LinearLayout layoutRedPacket;
    @BindView(R.id.tv_order_money_status)
    TextView tvOrderMoneyStatus;
    @BindView(R.id.tv_release_money_status)
    TextView tvReleaseMoneyStatus;
    @BindView(R.id.tv_pay_money_status)
    TextView tvPayMoneyStatus;
    @BindView(R.id.layout_release)
    LinearLayout layoutRelease;
    @BindView(R.id.tv_offline_time)
    TextView tvOfflineTime;
    @BindView(R.id.layout_offline)
    LinearLayout layoutOffline;

    private Context mContext;

    public WeddingCarOrderFooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCarOrderDetail orderDetail, int position, int viewType) {
        if (orderDetail == null) {
            return;
        }

        tvOrderMoney.setText(mContext.getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(orderDetail.getDepositMoney())));
        double factInMoney = 0D;
        switch (orderDetail.getStauts()) {
            case WeddingCarOrderDetail.WAIT_TAKE_ORDER:
            case WeddingCarOrderDetail.WAIT_FOR_PAY:
            case WeddingCarOrderDetail.BUYER_PAY_DEPOSIT:
            case WeddingCarOrderDetail.SELLER_REFUSE_ORDER:
            case WeddingCarOrderDetail.BUYER_CANCEL:
            case WeddingCarOrderDetail.SYSTEM_AUTO_CLOSE:
                //显示全款 未付
                layoutDepositMoney.setVisibility(View.GONE);
                layoutAllMoney.setVisibility(View.VISIBLE);
                tvPayMoney.setText(mContext.getString(R.string.label_price,
                        NumberFormatUtil.formatDouble2String(orderDetail.getActualMoney())));
                tvPayMoneyStatus.setText("未付");
                factInMoney = 0D;
                break;
            default:
                if (orderDetail.getIsPayAll() == WeddingCarOrderDetail.TAKER_ORDER_MONEY) {
                    //定金
                    layoutAllMoney.setVisibility(View.GONE);
                    layoutDepositMoney.setVisibility(View.VISIBLE);
                    tvOrderMoney.setText(mContext.getString(R.string.label_price,
                            NumberFormatUtil.formatDouble2String(orderDetail.getDepositMoney())));
                    double releaseMoney = orderDetail.getActualMoney() - orderDetail
                            .getDepositMoney() - orderDetail.getRedPacketMoney();
                    tvReleaseMoney.setText(mContext.getString(R.string.label_price,
                            NumberFormatUtil.formatDouble2String(releaseMoney)));
                    tvReleaseMoneyStatus.setText("未付");
                    factInMoney = orderDetail.getDepositMoney();
                } else if (orderDetail.getIsPayAll() == WeddingCarOrderDetail.ALL_PAY_MONEY) {
                    //全款
                    if (orderDetail.getDepositMoney() > 0) {
                        layoutAllMoney.setVisibility(View.GONE);
                        layoutDepositMoney.setVisibility(View.VISIBLE);
                        tvOrderMoney.setText(mContext.getString(R.string.label_price,
                                NumberFormatUtil.formatDouble2String(orderDetail.getDepositMoney
                                        ())));
                        if (orderDetail.isOfflinePay()) {
                            //线下支付
                            layoutOffline.setVisibility(View.VISIBLE);
                            layoutRelease.setVisibility(View.GONE);
                            tvOfflineTime.setText(orderDetail.getOffLinePayTime() == null ? null
                                    : orderDetail.getOffLinePayTime()
                                    .toString(HljCommon.DateFormat.DATE_FORMAT_LONG2));
                        } else {
                            layoutOffline.setVisibility(View.GONE);
                            layoutRelease.setVisibility(View.VISIBLE);
                            double releaseMoney = orderDetail.getActualMoney() - orderDetail
                                    .getDepositMoney() - orderDetail.getRedPacketMoney();
                            tvReleaseMoney.setText(mContext.getString(R.string.label_price,
                                NumberFormatUtil.formatDouble2String(releaseMoney)));
                            tvReleaseMoneyStatus.setText("已付");
                        }
                    } else {
                        layoutDepositMoney.setVisibility(View.GONE);
                        layoutAllMoney.setVisibility(View.VISIBLE);
                        tvPayMoney.setText(mContext.getString(R.string.label_price,
                                NumberFormatUtil.formatDouble2String(orderDetail.getPaidMoney())));
                        tvPayMoneyStatus.setText("已付");
                    }
                    if(orderDetail.isOfflinePay()){
                        factInMoney = orderDetail.getPaidMoney();
                    }else{
                        factInMoney = orderDetail.getActualMoney();
                    }
                }
                break;
        }

        if (orderDetail.getRedPacketMoney() > 0) {
            layoutRedPacket.setVisibility(View.VISIBLE);
            tvRedPacketName.setText(orderDetail.getRedPacketName());
            tvRedPacket.setText(mContext.getString(R.string.label_release_price,
                    NumberFormatUtil.formatDouble2String(orderDetail.getRedPacketMoney())));
        } else {
            layoutRedPacket.setVisibility(View.GONE);
        }

        tvFactMoney.setText(mContext.getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(orderDetail.getPaidMoney())));
        tvFactInMoney.setText(mContext.getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(factInMoney)));
        tvWorkPrice.setText(mContext.getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(orderDetail.getActualMoney())));

        tvMoney.setText(mContext.getString(R.string.label_price5,
                NumberFormatUtil.formatDouble2String(orderDetail.getActualMoney())));
        tvOrderNumber.setText(orderDetail.getOrderNo());
        tvOrderAccount.setText(orderDetail.getUser() == null ? null : orderDetail.getUser()
                .getNick());
        tvOrderTime.setText(orderDetail.getCreatedAt() == null ? null : orderDetail.getCreatedAt()
                .toString(HljCommon.DateFormat.DATE_FORMAT_LONG2));
    }
}
