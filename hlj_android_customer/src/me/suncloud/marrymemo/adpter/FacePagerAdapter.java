package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

public class FacePagerAdapter extends RecyclingPagerAdapter {
    private Context mContext;
    private ArrayList<String> tags;
    private OnFaceItemClickListener itemClickListener;
    private int width;

    public FacePagerAdapter(Context context, int width, OnFaceItemClickListener itemClickListener) {
        this.mContext = context;
        this.width = width;
        this.itemClickListener = itemClickListener;
        this.tags = new ArrayList<>();
    }

    public void setTags(ArrayList<String> tags) {
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
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.face_view_layout, container, false);
            ViewHolder holder = new ViewHolder();
            GridView gridView = (GridView) convertView
                    .findViewById(R.id.face_grid);
            holder.adapter = new FacesAdapter(mContext, width);
            gridView.setAdapter(holder.adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
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
            arrayList = new ArrayList<>(tags.subList(20 * position,
                    20 * position + 20));
        } else {
            arrayList = new ArrayList<>(tags.subList(20 * position, tags.size()));
        }
        holder.adapter.setTags(arrayList);
        return convertView;
    }

    public interface OnFaceItemClickListener {
        void onFaceItemClickListener(AdapterView<?> parent, View view, int position,
                                     long id);
    }

    private class ViewHolder {
        FacesAdapter adapter;
    }
}