package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Size;

/**
 * Created by jinxin on 2015/11/5.
 */
public class OverScrollViewPagerImageAdaprer extends RecyclingPagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Photo> mDate;
    private Context mContext;
    private int width;
    private OnItemClickListener onItemClickListener;


    public OverScrollViewPagerImageAdaprer(Context context, ArrayList<Photo> list) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mDate = new ArrayList<>(list);
        width = JSONUtil.getDeviceSize(mContext).x;
        if (width > 805) {
            width = width*3 / 4;
        }
    }

    public void setmDate(ArrayList<Photo> photos) {
        if (photos != null) {
            mDate.clear();
            mDate.addAll(photos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDate == null ? 0 : mDate.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.flow_item, null);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Photo item = mDate.get(position);
        if (item != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnPhotoClickListener(item, position);
                    }
                }
            });
            String url = JSONUtil.getImagePath(item.getPath(), width);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView);
                holder.imageView.setTag(url);
                task.loadImage(url, width, ScaleMode.ALL,
                        new AsyncBitmapDrawable(mContext.getResources(),
                                R.mipmap.icon_empty_image, task));
            } else {
                holder.imageView.setImageBitmap(null);
            }
        }
        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void OnPhotoClickListener(Object item, int position);
    }

    private class ViewHolder {
        ImageView imageView;
    }
}
