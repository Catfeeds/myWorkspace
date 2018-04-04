package com.hunliji.hljemojilibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.hunliji.hljcommonlibrary.adapters.RecyclingPagerAdapter;
import com.hunliji.hljemojilibrary.R;

import java.util.ArrayList;
import java.util.Collection;

public class EmojiPagerAdapter extends RecyclingPagerAdapter {
    private Context mContext;
    private ArrayList<String> tags;
    private OnFaceItemClickListener itemClickListener;
    private int width;

    public EmojiPagerAdapter(Context context, int width, OnFaceItemClickListener itemClickListener) {
        this.mContext = context;
        this.width = width;
        this.itemClickListener = itemClickListener;
        this.tags = new ArrayList<String>();
    }

    public void setTags(Collection<? extends String> tags) {
        if (tags != null) {
            this.tags.clear();
            this.tags.addAll(tags);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return tags.size() / 20 + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.face_view_layout, container, false);
            ViewHolder holder = new ViewHolder();
            GridView gridView = (GridView) convertView.findViewById(R.id.face_grid);
            holder.adapter = new EmojiesAdapter(mContext, width);
            gridView.setAdapter(holder.adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (itemClickListener != null) {
                        itemClickListener.onFaceItemClickListener(parent, view, position, id);
                    }

                }
            });
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        ArrayList<String> arrayList;
        if (tags.size() >= (20 * position + 20)) {
            arrayList = new ArrayList<String>(tags.subList(20 * position, 20 * position + 20));
        } else {
            arrayList = new ArrayList<String>(tags.subList(20 * position, tags.size()));
        }
        if (arrayList != null) {
            holder.adapter.setTags(arrayList);
        }
        return convertView;
    }

    private class ViewHolder {
        EmojiesAdapter adapter;
    }

    public interface OnFaceItemClickListener {
        void onFaceItemClickListener(AdapterView<?> parent, View view, int position, long id);
    }
}