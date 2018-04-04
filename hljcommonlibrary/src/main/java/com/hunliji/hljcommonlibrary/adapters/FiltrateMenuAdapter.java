package com.hunliji.hljcommonlibrary.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.MerchantProperty;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chen_bin on 2017/3/31.
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

    public void setItems(ArrayList<Label> items, int type) {
        this.type = type;
        this.mData.clear();
        if (mData != null) {
            this.mData.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setItems(List<? extends Label> items) {
        this.mData.clear();
        if (items != null) {
            this.mData.addAll(items);
        }
        notifyDataSetChanged();
    }

    public int getType() {
        return type;
    }

    @Override
    public int getCount() {
        return CommonUtil.isCollectionEmpty(mData) ? 0 : mData.size();
    }

    @Override
    public Label getItem(int position) {
        return CommonUtil.isCollectionEmpty(mData) ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ViewHolder holder = new ViewHolder();
            if (listItemResourceId != 0) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(listItemResourceId, parent, false);
                holder.lineLayout = convertView.findViewById(R.id.line_layout);
                holder.imgNext = (ImageView) convertView.findViewById(R.id.img_next);
            } else {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.filtrate_menu_list_item2___cm, parent, false);
            }
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.itemView = (CheckableLinearLayout) convertView;
            convertView.setTag(holder);
        }
        Label menuItem = mData.get(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(menuItem.getName())) {
            holder.tvTitle.setText(menuItem.getName());
        }
        if (menuItem instanceof MerchantProperty && holder.imgNext != null) {
            MerchantProperty property = (MerchantProperty) menuItem;
            holder.imgNext.setVisibility(CommonUtil.isCollectionEmpty(property.getChildren()) ?
                    View.GONE : View.VISIBLE);
        } else if (menuItem instanceof ShopCategory && holder.imgNext != null) {
            ShopCategory shopCategory = (ShopCategory) menuItem;
            holder.imgNext.setVisibility(CommonUtil.isCollectionEmpty(shopCategory.getChildren())
                    ? View.GONE : View.VISIBLE);
        }
        holder.itemView.setOnCheckedChangeListener(new CheckableLinearLayout
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (holder.lineLayout != null) {
                    holder.lineLayout.setVisibility(checked ? View.GONE : View.VISIBLE);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CheckableLinearLayout itemView;
        TextView tvTitle;
        ImageView imgNext;
        View lineLayout;
    }
}
