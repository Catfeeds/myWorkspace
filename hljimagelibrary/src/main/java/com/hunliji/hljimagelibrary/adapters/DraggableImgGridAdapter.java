package com.hunliji.hljimagelibrary.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.adapters.viewholders.BaseDraggableItemViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;


/**
 * 9宫格图片adapter
 * Created by chen_bin on 16/7/16.
 */
public class DraggableImgGridAdapter extends RecyclerView
        .Adapter<BaseDraggableItemViewHolder<Photo>> implements
        DraggableItemAdapter<BaseDraggableItemViewHolder> {
    private Context context;
    private List<Photo> photos;
    private boolean isEdit = true;
    private int type;
    private int imageSize;
    private int limit;

    private LayoutInflater inflater;

    private OnItemAddListener onItemAddListener;
    private OnItemDeleteListener onItemDeleteListener;
    private OnItemClickListener<Photo> onItemClickListener;

    private final static int ITEM_TYPE_EMPTY = 0;
    private final static int ITEM_TYPE_PHOTO = 1;
    private final static int ITEM_TYPE_VIDEO = 2;

    public final static int TYPE_PHOTO = 0;
    public final static int TYPE_VIDEO = 1;

    public DraggableImgGridAdapter(Context context, int imageSize, int picSizeLimit) {
        this(context, null, imageSize, picSizeLimit);
    }

    public DraggableImgGridAdapter(
            Context context, List<Photo> photos, int imageSize, int limit) {
        this.context = context;
        this.photos = photos;
        this.imageSize = imageSize;
        this.limit = limit;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOnItemAddListener(OnItemAddListener onItemAddListener) {
        this.onItemAddListener = onItemAddListener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public void setOnItemClickListener(OnItemClickListener<Photo> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(photos) + (CommonUtil.getCollectionSize(photos) <
                limit ? 1 : 0);
    }

    @Override
    public long getItemId(int position) {
        int size = CommonUtil.getCollectionSize(photos);
        if (position >= size) {
            return super.getItemId(position);
        } else {
            return photos.get(position)
                    .getId();
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return isEdit;
    }

    @Override
    public boolean onCheckCanStartDrag(
            BaseDraggableItemViewHolder holder, int position, int x, int y) {
        return isEdit && getItemId(position) > 0;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(
            BaseDraggableItemViewHolder holder, int position) {
        return new ItemDraggableRange(0, photos.size() - 1);
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition != toPosition) {
            Photo photo = photos.remove(fromPosition);
            photos.add(toPosition, photo);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == CommonUtil.getCollectionSize(photos)) {
            return ITEM_TYPE_EMPTY;
        } else if (type == TYPE_PHOTO) {
            return ITEM_TYPE_PHOTO;
        } else {
            return ITEM_TYPE_VIDEO;
        }
    }

    @Override
    public BaseDraggableItemViewHolder<Photo> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_EMPTY:
                return new EmptyViewHolder(inflater.inflate(R.layout.draggable_img_grid_item___img,
                        parent,
                        false));
            case ITEM_TYPE_PHOTO:
                return new PhotoViewHolder(inflater.inflate(R.layout.draggable_img_grid_item___img,
                        parent,
                        false));
            default:
                return new VideoViewHolder(inflater.inflate(R.layout.draggable_img_grid_item___img,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseDraggableItemViewHolder<Photo> holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_EMPTY:
                holder.setView(context, null, position, viewType);
                break;
            default:
                holder.setView(context, photos.get(position), position, viewType);
                break;
        }
    }

    public class EmptyViewHolder extends BaseDraggableItemViewHolder<Photo> {
        FrameLayout container;
        ImageView imgCover;
        ImageView imgPlay;
        ImageButton btnDelete;

        EmptyViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            imgCover = itemView.findViewById(R.id.img_cover);
            imgPlay = itemView.findViewById(R.id.img_play);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            imgCover.getLayoutParams().width = imageSize;
            imgCover.getLayoutParams().height = imageSize;
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemAddListener != null) {
                        onItemAddListener.onItemAdd();
                    }
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, Photo item, int position, int viewType) {
            imgCover.setImageResource(R.mipmap.icon_cross_add_white_176_176);
        }
    }

    public class PhotoViewHolder extends EmptyViewHolder {

        PhotoViewHolder(View itemView) {
            super(itemView);
            btnDelete.setVisibility(isEdit ? View.VISIBLE : View.GONE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemDeleteListener != null) {
                        onItemDeleteListener.onItemDelete(getAdapterPosition());
                    }
                }
            });
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        public void setViewData(Context context, Photo photo, int position, int viewType) {
            String imagePath = photo.getImagePath();
            if (CommonUtil.isHttpUrl(imagePath)) {
                imagePath = ImagePath.buildPath(imagePath)
                        .width(imageSize)
                        .cropPath();
            }
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
        }
    }

    public class VideoViewHolder extends PhotoViewHolder {

        VideoViewHolder(View itemView) {
            super(itemView);
            imgPlay.setVisibility(View.VISIBLE);
            imgCover.setColorFilter(Color.parseColor("#99000000"));
        }

        @Override
        public void setViewData(Context context, Photo photo, int position, int viewType) {
            super.setViewData(context, photo, position, viewType);
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public interface OnItemAddListener {
        void onItemAdd(Object... objects);
    }
}