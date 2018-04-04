package me.suncloud.marrymemo.adpter.filter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.MerchantProperty;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;

/**
 * Created by hua_rong on 2018/3/13 0013
 */
public class NewFiltrateMenuAdapter extends BaseAdapter {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private ArrayList<Label> mData;
    private int type;
    private LayoutInflater layoutInflater;

    public NewFiltrateMenuAdapter(Context mContext, int type) {
        this.type = type;
        this.mData = new ArrayList<>();
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<? extends Label> mData) {
        this.mData.clear();
        if (mData != null) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ViewHolder holder = new ViewHolder();
            convertView = layoutInflater.inflate(type == LEFT ? R.layout.filtrate_menu_list_item
                            : R.layout.menu_list_item,
                    parent,
                    false);
            holder.line = convertView.findViewById(R.id.line);
            holder.imgNext = convertView.findViewById(R.id.img_next);
            holder.textView = convertView.findViewById(R.id.text);
            holder.checkableLinearLayout2 = (CheckableLinearLayout2) convertView;
            convertView.setTag(holder);
        }
        Label menuItem = mData.get(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(menuItem.getName())) {
            holder.textView.setText(menuItem.getName());
        }
        if (menuItem instanceof MerchantProperty && holder.imgNext != null) {
            MerchantProperty property = (MerchantProperty) menuItem;
            holder.imgNext.setVisibility(CommonUtil.isCollectionEmpty(property.getChildren()) ?
                    View.GONE : View.VISIBLE);
        }
        holder.checkableLinearLayout2.setOnCheckedChangeListener(new CheckableLinearLayout2.OnCheckedChangeListener() {
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

