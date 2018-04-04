package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/5/20.
 */
public class ThreadDraggableImgGridAdapter extends RecyclerView
        .Adapter<ThreadDraggableImgGridAdapter.MyViewHolder> implements
        DraggableItemAdapter<ThreadDraggableImgGridAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Photo> pics;
    private int imgWidth;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteListener;
    private View.OnClickListener onAddListener;

    public ThreadDraggableImgGridAdapter(Context context, ArrayList<Photo> pics) {
        this.mContext = context;
        this.pics = pics;
        setHasStableIds(true);
        Point point = JSONUtil.getDeviceSize(mContext);
        DisplayMetrics dm = mContext.getResources()
                .getDisplayMetrics();
        imgWidth = (int) ((point.x - 54 * dm.density) / 4);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.draggable_grid_img_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mImageView.getLayoutParams().height = imgWidth;
        holder.mImageView.getLayoutParams().width = imgWidth;
        holder.mImageView.requestLayout();

        final Photo pic = pics.get(position);
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pic.getId() != -1) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(pic, position);
                    }
                } else {
                    if (onAddListener != null) {
                        onAddListener.onClick(v);
                    }
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) {
                    onDeleteListener.onItemDelete(pic, position);
                }
            }
        });

        if (pic.getId() == -1) {
            holder.mImageView.setImageResource(R.drawable.icon_cross_add_white_gray);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            ImageLoadUtil.loadImageView(mContext, pic.getImagePath(), holder.mImageView);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    @Override
    public long getItemId(int position) {
        return pics.get(position)
                .getId();
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        int draggableMax = pics.get(pics.size() - 1)
                .getId() == -1 ? pics.size() - 2 : pics.size() - 1;
        ItemDraggableRange itemDraggableRange = new ItemDraggableRange(0, draggableMax);
        return itemDraggableRange;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final Photo pic = pics.remove(fromPosition);
        pics.add(toPosition, pic);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {

        public FrameLayout mContainer;
        public ImageView mImageView;
        public ImageButton btnDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mContainer = (FrameLayout) itemView.findViewById(R.id.container);
            mImageView = (ImageView) itemView.findViewById(R.id.img);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);
        }
    }

    public void setOnDeleteListener(OnDeleteClickListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnAddListener(View.OnClickListener onAddListener) {
        this.onAddListener = onAddListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, int position);
    }

    public interface OnDeleteClickListener {
        void onItemDelete(Object item, int position);
    }
}
