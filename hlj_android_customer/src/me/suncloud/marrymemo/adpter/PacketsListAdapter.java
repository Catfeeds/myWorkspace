package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by werther on 1/26/16.
 */
public class PacketsListAdapter extends BaseAdapter {
    private ArrayList<RedPacket> mData;

    public PacketsListAdapter(ArrayList<RedPacket> data, Context context) {
        this.mData = data;
    }

    public PacketsListAdapter(ArrayList<RedPacket> data) {
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
            RedPacket redPacket = mData.get(position);
            if (redPacket.getId() < 0) {
                holder.ctv.setText(R.string.label_use_not_red_enve2);
            } else {
                holder.ctv.setText(holder.ctv.getContext()
                        .getString(R.string.label_red_packet_item,
                                Util.formatDouble2String(redPacket.getAmount()),
                                redPacket.getRedPacketName()));
            }
        }

        return convertView;
    }

    private class ViewHolder {
        CheckedTextView ctv;
    }
}
