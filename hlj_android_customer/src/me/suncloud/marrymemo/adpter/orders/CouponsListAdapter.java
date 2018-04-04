package me.suncloud.marrymemo.adpter.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.util.Util;

/**
 * 与RedPacketsListAdapter是一样的作用，只不过是针对新的Http请求方式的model而修改的新版
 */
public class CouponsListAdapter extends BaseAdapter {
    private ArrayList<CouponRecord> mData;

    public CouponsListAdapter(ArrayList<CouponRecord> data, Context context) {
        this.mData = data;
    }

    public CouponsListAdapter(ArrayList<CouponRecord> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData == null ? -1 : mData.get(position)
                .getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.red_packet_item_view, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.ctv = (CheckedTextView) convertView.findViewById(R.id.ctv_amount);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder != null) {
            CouponRecord couponRecord = mData.get(position);
            if (couponRecord.getId() < 0) {
                holder.ctv.setText(R.string.label_use_no_coupon);
            } else {
                String moneySillStr;
                if (couponRecord.getMoneySill() > 0) {
                    moneySillStr = "（套餐价格满" + Util.formatDouble2String(couponRecord.getMoneySill
                            ()) + "元使用）";
                } else {
                    moneySillStr = "（无金额门槛限制）";
                }
                holder.ctv.setText(Util.formatDouble2String(couponRecord.getValue()) +
                        moneySillStr);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        CheckedTextView ctv;
    }
}
