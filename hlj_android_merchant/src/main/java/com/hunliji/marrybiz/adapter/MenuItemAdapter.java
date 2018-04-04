package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Label;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Label> mData;
    private long id;

    public MenuItemAdapter(Context mContext) {
        this.mContext = mContext;
        mData = new ArrayList<Label>();
    }

    public void setLabels(List<Label> arrayList, long id) {
        if (arrayList != null) {
            this.id = id;
            mData.clear();
            mData.addAll(arrayList);
            notifyDataSetChanged();
        }

    }

    public void setLabelId(long id) {
        this.id = id;
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
        return mData == null ? 0 : mData.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.label_list_item, parent,
                    false);
            ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.label_item);
            holder.textView.setGravity(Gravity.CENTER);
            convertView.setTag(holder);
        }
        Label label = mData.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (label != null) {
            holder.textView.setText(label.getName());
            if (label.getId().equals(id)) {
                holder.textView.setBackgroundColor(mContext.getResources().getColor(R.color
                        .colorPrimary));
                holder.textView.setTextColor(mContext.getResources().getColor(android.R.color
                        .white));
            } else {
                holder.textView.setBackgroundColor(mContext.getResources().getColor(android.R
                        .color.transparent));
                holder.textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }

}