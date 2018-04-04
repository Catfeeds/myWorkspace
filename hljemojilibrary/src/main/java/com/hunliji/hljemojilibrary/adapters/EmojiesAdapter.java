package com.hunliji.hljemojilibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.R;

import java.util.ArrayList;
import java.util.List;

public class EmojiesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mData;
    private int width;

    public EmojiesAdapter(Context mContext, int width) {
        this.mContext = mContext;
        this.mData = new ArrayList<String>();
        this.width = width;
    }

    public void setTags(List<String> arrayList) {
        if (arrayList != null) {
            mData.clear();
            mData.addAll(arrayList);
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return 21;
    }

    @Override
    public Object getItem(int position) {
        if (position == 20) {
            return "delete";
        } else if (position < mData.size()) {
            return mData == null ? null : mData.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.faces_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.imageView.setLayoutParams(new GridView.LayoutParams(width, width));
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (mData.size() > position) {
            holder.imageView.setImageResource(EmojiUtil.getImageResFromTag(mData.get(position),
                    mContext));
        } else if (position == 20) {
            holder.imageView.setImageResource(R.mipmap.ic_emoji_delete);
        } else {
            holder.imageView.setImageResource(0);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}