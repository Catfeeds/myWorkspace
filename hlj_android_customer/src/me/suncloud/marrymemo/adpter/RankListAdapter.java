package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Label;

/**
 * Created by luohanlin on 15/3/26.
 */
public class RankListAdapter extends BaseAdapter{

    private ArrayList<Label> mData;
    private Context mContext;
    private int selectedItem;

    public RankListAdapter(Context context, ArrayList<? extends Label> data) {
        this.mData =new ArrayList<>(data);
        this.mContext = context;
    }

    public void setmData(ArrayList<? extends Label> data) {
        if (this.mData != null) {
            this.mData.clear();
            this.mData.addAll(data);
            this.notifyDataSetChanged();
        }
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_list_item, parent,
                    false);
            ViewHolder holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.label.setText(mData.get(position).getName());
        if (position == selectedItem) {
            holder.label.setPressed(true);
        }else{
            holder.label.setPressed(false);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView label;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }
}
