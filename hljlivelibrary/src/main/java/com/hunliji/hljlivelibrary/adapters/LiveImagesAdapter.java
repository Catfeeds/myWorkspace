package com.hunliji.hljlivelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.GifSupportImageChooserActivity;
import com.hunliji.hljimagelibrary.views.activities.MultiImageActivity;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suncloud on 2016/10/31.
 */

public class LiveImagesAdapter extends RecyclerView.Adapter<BaseViewHolder<Photo>> {

    private ArrayList<Photo> images;
    private Context context;
    private int limit;
    private OnImageRemoveListener listener;
    private OnAddImageListener addImageListener;


    /**
     * 直播选图列表
     *
     * @param context
     * @param limit    选图上限
     * @param listener
     */
    public LiveImagesAdapter(Context context, int limit, OnImageRemoveListener listener) {
        this.context = context;
        this.limit = limit;
        this.listener = listener;
    }

    public void setAddImageListener(OnAddImageListener addImageListener) {
        this.addImageListener = addImageListener;
    }

    @Override
    public BaseViewHolder<Photo> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_image_item___live, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Photo> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    public void addImages(ArrayList<Photo> photos) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.addAll(photos);
        notifyDataSetChanged();
    }

    public List<Photo> getImages() {
        return images;
    }

    public void clear() {
        if (images == null || images.isEmpty()) {
            return;
        }
        images.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (getImageSize() < limit) {
            return getImageSize() + 1;
        } else
            return getImageSize();
    }

    public int getImageSize() {
        return images == null ? 0 : images.size();
    }

    public Photo getItem(int position) {
        if (position < getImageSize()) {
            return images.get(position);
        } else {
            return null;
        }
    }

    private class ImageViewHolder extends BaseViewHolder<Photo> {

        private int size;
        private ImageView imageView;
        private ImageButton btnDelete;

        public ImageViewHolder(View itemView) {
            super(itemView);
            size = CommonUtil.dp2px(itemView.getContext(), 66);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);
            itemView.getLayoutParams().width = size;
            itemView.getLayoutParams().height = size;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = null;
                    if (v.getContext() != null && v.getContext() instanceof Activity) {
                        activity = (Activity) v.getContext();
                    }
                    if (activity == null) {
                        return;
                    }
                    if (getItem() == null) {
                        if (addImageListener != null) {
                            addImageListener.onAdd(limit - getImageSize());
                        } else {
                            Intent intent = new Intent(v.getContext(),
                                    GifSupportImageChooserActivity.class);
                            intent.putExtra("limit", limit - getImageSize());
                            activity.startActivityForResult(intent, HljLive.CHOOSE_PHOTO);
                        }
                    } else if (getImageSize() > 0) {
                        Intent intent = new Intent(v.getContext(), MultiImageActivity.class);
                        intent.putExtra("photos", images);
                        if (getItemCount() > getImageSize()) {
                            intent.putExtra("position", getAdapterPosition() - 1);
                        } else {
                            intent.putExtra("position", getAdapterPosition());
                        }
                        activity.startActivity(intent);

                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem() != null) {
                        images.remove(getItem());
                        notifyDataSetChanged();
                        if (listener != null) {
                            listener.onRemove();
                        }
                    }
                }
            });

        }

        @Override
        protected void setViewData(Context mContext, Photo item, int position, int viewType) {
            if (item == null) {
                imageView.setImageResource(R.mipmap.ic_image_item_add___live);
                btnDelete.setVisibility(View.GONE);
            } else {
                String path = item.getImagePath();
                if (!TextUtils.isEmpty(path) && (path.startsWith("http://") || path.startsWith(
                        "https://"))) {
                    path = ImageUtil.getImagePath2(path, size);
                }
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imageView);
                btnDelete.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 图片移除监听
     */
    public interface OnImageRemoveListener {
        void onRemove();
    }

    public interface OnAddImageListener {
        void onAdd(int limit);
    }
}
