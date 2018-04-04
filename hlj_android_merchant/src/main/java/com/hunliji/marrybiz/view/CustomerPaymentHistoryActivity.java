package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.model.OrderPayHistory;
import com.hunliji.marrybiz.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * 定制套餐付款记录页
 * Created by jinxin on 2016/2/1.
 */
public class CustomerPaymentHistoryActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<OrderPayHistory> {

    private TextView customerAllPrice;
    private TextView customerRedPacketPrice;
    private TextView customerBuyerHasPaid;
    private TextView customerBuyerNeedPaid;
    private PullToRefreshListView paymentRecords;

    private CustomSetmealOrder order;
    private List<OrderPayHistory> payHistorys;
    private ObjectBindAdapter<OrderPayHistory> adapter;
    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        paymentRecords = (PullToRefreshListView) findViewById(R.id.list_view);
        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        header = getLayoutInflater().inflate(R.layout.customer_payment_history_header, null);

        customerAllPrice = (TextView) header.findViewById(R.id.customer_all_price);
        customerRedPacketPrice = (TextView) header.findViewById(R.id.customer_red_packet_price);
        customerBuyerHasPaid = (TextView) header.findViewById(R.id.customer_buyer_has_paid);
        customerBuyerNeedPaid = (TextView) header.findViewById(R.id.customer_buyer_need_paid);

        payHistorys = new ArrayList<>();

        if (order != null && (order.getPayHistories() != null && order.getPayHistories()
                .size() > 0)) {
            payHistorys.addAll(order.getPayHistories());
            customerAllPrice.setText(String.format(getString(R.string.label_price5),
                    Util.roundUpDouble2String(order.getActualPrice())));
            int redPacket = (int) (Math.min(order.getActualPrice() - order.getPaidMoney(),
                    order.getRedPacketMoney()));
            customerRedPacketPrice.setText(String.format(getString(R.string.label_price6),
                    redPacket));
            customerBuyerHasPaid.setText(String.format(getString(R.string.label_price6),
                    Util.roundUpDouble2String(order.getPaidMoney())));
            customerBuyerNeedPaid.setText(Util.roundUpDouble2String(Math.max(0,
                    order.getActualPrice() - order.getRedPacketMoney() - order.getPaidMoney())));

            adapter = new ObjectBindAdapter<>(this,
                    payHistorys,
                    R.layout.custom_set_meal_payhistory_item);
            adapter.setViewBinder(this);
            paymentRecords.getRefreshableView()
                    .addHeaderView(header);
            paymentRecords.getRefreshableView()
                    .setAdapter(adapter);
            paymentRecords.setMode(PullToRefreshBase.Mode.DISABLED);
        } else {
            showEmptyView();
        }
        hideOkButton();
    }

    private void showEmptyView() {
        View emptyView = findViewById(R.id.empty_hint_layout);
        View emptyImage = emptyView.findViewById(R.id.img_empty_hint);
        TextView textEmptyView = (TextView) emptyView.findViewById(R.id.text_empty_hint);
        textEmptyView.setText(getString(R.string.no_item));
        emptyView.setVisibility(View.VISIBLE);
        emptyImage.setVisibility(View.VISIBLE);
        textEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void setViewValue(View view, OrderPayHistory orderPayHistory, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.paymentHistoryTime = (TextView) view.findViewById(R.id.payment_history_time);
            holder.paymentHistoryMoney = (TextView) view.findViewById(R.id.payment_history_money);
            holder.paymentHistoryEvent = (TextView) view.findViewById(R.id.payment_history_event);
            view.setTag(holder);
        }

        OrderPayHistory payHistory = payHistorys.get(position);
        holder.paymentHistoryEvent.setText(payHistory.getEventStr());
        holder.paymentHistoryTime.setText(String.format(getString(R.string.label_paid_time2),
                payHistory.getCreatedAt()
                        .toString(Constants.DATE_FORMAT_LONG)));
        holder.paymentHistoryMoney.setText(Util.formatDouble2String(payHistory.getMoney()));
    }


    class ViewHolder {
        TextView paymentHistoryMoney;
        TextView paymentHistoryEvent;
        TextView paymentHistoryTime;
    }
}
