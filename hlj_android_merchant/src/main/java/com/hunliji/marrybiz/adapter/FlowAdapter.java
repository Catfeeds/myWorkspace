package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Util;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

public class FlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Poster> mDate;
    private Context mContext;
    private int width;
    private SliderLayout sliderLayout;

    public FlowAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mDate = new ArrayList<>();
        width = JSONUtil.getDeviceSize(mContext).x;
    }

    public void setmDate(List<Poster> posters) {
        if (posters != null) {
            mDate.clear();
            mDate.addAll(posters);
            notifyDataSetChanged();
        }
    }

    public void setSliderLayout(SliderLayout sliderLayout) {
        this.sliderLayout = sliderLayout;
    }

    @Override
    public int getCount() {
        return mDate == null ? 0 : mDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View convertView = mInflater.inflate(R.layout.flow_item, null);
        ViewHolder holder = new ViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.image);
        convertView.setTag(holder);
        final Poster item = mDate.get(position);
        if (item != null) {
            holder.imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Poster newItem = item;
                    if (sliderLayout != null) {
                        newItem = mDate.get(sliderLayout.getCurrentPosition());
                    }
                    if (newItem != null) {
                        Util.bannerAction(mContext,newItem);
                    }
                }
            });

            String url = JSONUtil.getImagePath(item.getPath(), width);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView, null, null, 0, true);
                holder.imageView.setTag(url);
                task.loadImage(url, width, ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(convertView.getResources(),
                                R.mipmap.icon_empty_image, task));
            } else {
                holder.imageView.setImageBitmap(null);
            }
        }
        container.addView(convertView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}