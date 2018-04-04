package me.suncloud.marrymemo.adpter.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import me.suncloud.marrymemo.util.Util;

/**
 * 与RedPacketsListAdapter是一样的作用，只不过是针对新的Http请求方式的model而修改的新版
 */
public class RedPacketsListAdapter extends BaseAdapter {
    private ArrayList<RedPacket> mData;

    public RedPacketsListAdapter(ArrayList<RedPacket> data, Context context) {
        this.mData = data;
    }

    public RedPacketsListAdapter(ArrayList<RedPacket> data) {
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
                    .inflate(R.layout.red_packet_item_view3, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.tvMoneyAmount = (TextView) convertView.findViewById(R.id.tv_money_amount);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.tvUseNone = (TextView) convertView.findViewById(R.id.tv_use_none);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder != null) {
            RedPacket redPacket = mData.get(position);
            if (redPacket.getId() < 0) {
                holder.tvUseNone.setVisibility(View.VISIBLE);
                holder.tvMoneyAmount.setVisibility(View.GONE);
                holder.tvDesc.setVisibility(View.GONE);
            } else {
                holder.tvUseNone.setVisibility(View.GONE);
                holder.tvMoneyAmount.setVisibility(View.VISIBLE);
                holder.tvDesc.setVisibility(View.VISIBLE);
                holder.tvMoneyAmount.setText(Util.formatDouble2String(redPacket.getAmount()) + "" +
                        "(" + redPacket.getRedPacketName() + ")");
                String moneySillStr;
                if (redPacket.getMoneySill() > 0) {
                    moneySillStr = "实付金额满" + Util.formatDouble2String(redPacket.getMoneySill()) +
                            "元可用";
                } else {
                    moneySillStr = "无金额门槛限制";
                }
                holder.tvDesc.setText(moneySillStr);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvMoneyAmount;
        TextView tvDesc;
        TextView tvUseNone;
    }
}
