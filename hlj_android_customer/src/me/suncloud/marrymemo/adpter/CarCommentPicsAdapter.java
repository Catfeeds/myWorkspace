package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.view.ThreadPicsPageViewActivity;

/**
 * Created by Suncloud on 2015/10/19.
 */
public class CarCommentPicsAdapter extends RecyclerView.Adapter<CarCommentPicsAdapter
        .ImageViewHolder> {

    private Context mContext;
    private ArrayList<Photo> photos;

    public CarCommentPicsAdapter(Context context) {
        this.mContext = context;
        this.photos = new ArrayList<>();
    }

    public void setPhotos(ArrayList<Photo> photos) {
        if (photos != null) {
            this.photos.clear();
            this.photos.addAll(photos);
            notifyDataSetChanged();
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(View.inflate(mContext, R.layout.car_comment_image_item, null));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.setView(mContext, getItem(position), position, 0);
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    private Photo getItem(int position) {
        return photos == null ? null : photos.get(position);
    }


    public class ImageViewHolder extends BaseViewHolder<Photo> {
        private ImageView imageView;
        private int size;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            DisplayMetrics dm = imageView.getResources()
                    .getDisplayMetrics();
            size = Math.round(dm.density * 60);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                Photo item,
                final int position,
                int viewType) {
            String path = item.getPath();
            path = JSONUtil.getImagePath2(path, size);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ThreadPicsPageViewActivity.class);
                    intent.putExtra("photos", photos);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });
            if (!JSONUtil.isEmpty(path)) {
                if (!path.equals(imageView.getTag())) {
                    imageView.setTag(path);
                    ImageLoadTask task = new ImageLoadTask(imageView);
                    task.loadImage(path,
                            size,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(mContext.getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            } else {
                imageView.setTag(null);
                imageView.setImageBitmap(null);
            }
        }
    }

}
