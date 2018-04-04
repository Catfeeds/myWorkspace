package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Label;

import java.util.ArrayList;

/**
 * Created by Suncloud on 2015/10/9.
 */
public class MenuAdapter extends BaseAdapter {

    private ArrayList<Label> mData;
    private Context mContext;

    public MenuAdapter(Context mContext) {
        this.mData = new ArrayList<>();
        this.mContext = mContext;
    }

    public void setData(ArrayList<Label> mData) {
        if (mData != null) {
            this.mData.clear();
            this.mData.addAll(mData);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Label getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.menu_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) convertView
                    .findViewById(R.id.text);
            convertView.setTag(holder);
        }
        Label label = mData.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.textView.setText(label.getName());
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}