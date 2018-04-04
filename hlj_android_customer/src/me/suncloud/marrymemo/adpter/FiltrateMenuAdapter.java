package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;

/**
 * Created by LuoHanlin on 2015/3/19.
 */
public class FiltrateMenuAdapter extends BaseAdapter {

    private ArrayList<Label> mData;
    private Context mContext;
    private int type;
    private int listItemResourceId;

    public FiltrateMenuAdapter(Context mContext) {
        this.mData = new ArrayList<>();
        this.mContext = mContext;
    }

    public FiltrateMenuAdapter(Context mContext, int listItemResourceId) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        this.listItemResourceId = listItemResourceId;
    }

    public void setData(ArrayList<Label> mData, int type) {
        if (mData != null) {
            this.mData.clear();
            this.mData.addAll(mData);
        }
        this.type = type;
        notifyDataSetChanged();
    }

    public void setData(List<? extends Label> mData) {
        if (mData != null) {
            this.mData.clear();
            this.mData.addAll(mData);
        } else {
            this.mData.clear();
        }
        notifyDataSetChanged();
    }

    public int getType() {
        return type;
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
            ViewHolder holder = new ViewHolder();
            if (listItemResourceId != 0) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(listItemResourceId, parent, false);
                holder.line = convertView.findViewById(R.id.line);
                holder.imgNext = (ImageView) convertView.findViewById(R.id.img_next);
            } else {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.menu_list_item, parent, false);
            }
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            holder.checkableLinearLayout2 = (CheckableLinearLayout2) convertView;
            convertView.setTag(holder);
        }
        Label menuItem = mData.get(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        if (!JSONUtil.isEmpty(menuItem.getName())) {
            holder.textView.setText(menuItem.getName());
        }
        if (menuItem instanceof MerchantProperty && holder.imgNext != null) {
            MerchantProperty property = (MerchantProperty) menuItem;
            holder.imgNext.setVisibility(property.getChildren() == null || property.getChildren()
                    .isEmpty() ? View.GONE : View.VISIBLE);
        }
        holder.checkableLinearLayout2.setOnCheckedChangeListener(new CheckableLinearLayout2
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (holder.line != null) {
                    holder.line.setVisibility(checked ? View.GONE : View.VISIBLE);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CheckableLinearLayout2 checkableLinearLayout2;
        TextView textView;
        ImageView imgNext;
        View line;
    }
}
