package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Item;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;

import java.util.ArrayList;


/**
 * Created by Suncloud on 2014/11/5.
 */
public class WorkItemAdapter extends RecyclingPagerAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Item> mDate;
    private Context mContext;
    private int height;
    private int width;
    private OnItemClickListener onItemClickListener;
    private boolean sizeLimit;


    public WorkItemAdapter(Context context, ArrayList<Item> list) {
        this(context,list,false);
    }

    public WorkItemAdapter(Context context, ArrayList<Item> list,boolean sizeLimit) {
        this.sizeLimit=sizeLimit;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mDate = new ArrayList<>(list);
        width = JSONUtil.getDeviceSize(mContext).x;
        height = Math.round(width * 3 / 4);
    }

    public void setmDate(ArrayList<Item> items) {
        if (items != null) {
            mDate.clear();
            mDate.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDate == null ? 0 : (mDate.size()>1&&!sizeLimit?Integer.MAX_VALUE:mDate.size());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.work_item_view, null);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.playView = convertView.findViewById(R.id.play);
            convertView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final Item item = mDate.get(position%mDate.size());
        if (item != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClickListener(item, position);
                    }
                }
            });
            String url;
            if (item.getKind() == 2) {
                if (item.getPersistent() != null
                        && !JSONUtil.isEmpty(item.getPersistent().getScreenShot())) {
                    url = JSONUtil.getImagePathForH(item.getPersistent().getScreenShot(),
                            height);
                } else {
                    url = JSONUtil.getImagePathForH(item.getMediaPath() + String.format(Constants.VIDEO_URL_TEN2), height);
                }
                holder.playView.setVisibility(View.VISIBLE);
            } else {
                holder.playView.setVisibility(View.GONE);
                url = JSONUtil.getImagePathForH(item.getMediaPath(), height);
            }
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView,null);
                holder.imageView.setTag(url);
                task.loadImage(url, 0, ScaleMode.ALL,
                        new AsyncBitmapDrawable(mContext.getResources(),
                                R.mipmap.icon_empty_image, task));
//                Glide.with(mContext).load(url).into(holder.imageView);
            } else {
                holder.imageView.setImageBitmap(null);
            }
        }
        return convertView;
    }

    public interface OnItemClickListener {
        void OnItemClickListener(Object item, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private class ViewHolder {
        ImageView imageView;
        View playView;
    }
}
