package me.suncloud.marrymemo.adpter.product;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RecyclingPagerAdapter;

/**
 * Created by Suncloud on 2015/8/6.
 */
public class ProductHeaderPhotoAdapter extends RecyclingPagerAdapter {

    private ArrayList<Photo> mDate;
    private int size;
    private OnItemClickListener onItemClickListener;

    public ProductHeaderPhotoAdapter(List<Photo> list, int size) {
        this.mDate = new ArrayList<>(list);
        this.size = size;
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
    public View getView(final int position, View convertView, final ViewGroup container) {
        if (convertView == null) {
            convertView = View.inflate(container.getContext(), R.layout.work_item_view, null);
            ViewHolder holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Photo item = mDate.get(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onPhotoItemClick(item, position);
                }
            }
        });

        if (item != null) {
            String url = ImageUtil.getImagePath2(item.getImagePath(), size);
            Glide.with(container.getContext())
                    .load(url)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imageView);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onPhotoItemClick(Object item, int position);
    }
}