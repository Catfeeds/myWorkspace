package com.hunliji.marrybiz.adapter.event.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.event.RechargeRecord;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/12/27 充扣流水
 */

public class PunchWaterViewHolder extends BaseViewHolder<RechargeRecord> {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_point_count)
    TextView tvPointCount;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.line_layout)
    View lineLayout;

    private List<RechargeRecord> records;

    public void setRecords(List<RechargeRecord> records) {
        this.records = records;
    }

    public PunchWaterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context context, RechargeRecord rechargeRecord, int position, int viewType) {
        lineLayout.setVisibility(position < records.size() - 1 ? View.VISIBLE : View.GONE);
        String title = null;
        int type = rechargeRecord.getType();
        int value = Math.abs(rechargeRecord.getValue());
        int num = Math.abs(rechargeRecord.getNum());
        String valueCount = null;
        tvPrice.setVisibility(View.GONE);
        switch (type) {
            case RechargeRecord.TYPE_BUSINESS_POINT_RECHARGE:
            case RechargeRecord.TYPE_OPERATE_POINT_RECHARGE:
                title = "活动点充值";
                valueCount = value + "点";
                tvPrice.setVisibility(View.VISIBLE);
                tvPrice.setText(String.format(Locale.getDefault(),
                        "金额%s",
                        CommonUtil.formatDouble2String(rechargeRecord.getPrice())));
                break;
            case RechargeRecord.TYPE_POINT_CONSUMPTION:
                title = "活动点消费";
                valueCount = "-" + value + "点";
                break;
            case RechargeRecord.TYPE_COUPON_RECHARGE:
                switch (rechargeRecord.getSubtype()) {
                    case RechargeRecord.SubType.TYPE_EXCHANGE:
                        title = "活动点兑换活动券";
                        tvPrice.setVisibility(View.VISIBLE);
                        tvPrice.setText("-" + value + "点");
                        valueCount = num + "张";
                        break;
                    case RechargeRecord.SubType.TYPE_RETURN:
                    case RechargeRecord.SubType.TYPE_RECHARGE:
                        title = "活动券充值";
                        valueCount = num + "张";
                        break;
                    default:
                        break;
                }
                break;
            case RechargeRecord.TYPE_COUPON_CONSUMPTION:
                title = "活动券消费";
                valueCount = "-" + num + "张";
                break;
            default:
                break;
        }
        tvTitle.setText(title);
        tvPointCount.setText(valueCount);
        DateTime dateTime = rechargeRecord.getCreatedAt();
        if (dateTime != null) {
            tvTime.setText(dateTime.toString(context.getString(R.string.format_date_type11)));
        }

    }
}
