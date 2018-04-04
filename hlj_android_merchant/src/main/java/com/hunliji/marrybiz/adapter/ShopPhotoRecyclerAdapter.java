package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mo_yu on 2017/8/22.店铺照片
 */

public class ShopPhotoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Photo> list;
    private LayoutInflater inflater;
    private int imageWidth;
    private static final int ITEM_TYPE = 0;
    private OnDeleteClickListener onDeleteClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnDeleteClickListener {
        void onDelete(Photo photo, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(Photo photo, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ShopPhotoRecyclerAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.list = photos;
        this.inflater = LayoutInflater.from(context);
        imageWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                28)) / 3);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE:
                ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout
                                .shop_photo_list_item,
                        parent,
                        false));
                return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE:
                holder.setView(context, getItem(position), position, viewType);
                break;
        }
    }

    public Photo getItem(int position) {
        return list.get(position);
    }

    public static int getItemType() {
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseViewHolder<Photo> {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.delete_view)
        RelativeLayout deleteView;
        @BindView(R.id.img_add)
        ImageView imgAdd;
        @BindView(R.id.tv_add_image)
        TextView tvAddImage;
        @BindView(R.id.image_layout)
        RelativeLayout imageLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams itemParams = imageLayout.getLayoutParams();
            if (itemParams != null) {
                itemParams.width = imageWidth;
                itemParams.height = imageWidth;
            }

            ViewGroup.LayoutParams imageParams = imgCover.getLayoutParams();
            if (imageParams != null) {
                imageParams.width = imageWidth;
                imageParams.height = imageWidth;
            }

            ViewGroup.LayoutParams addParams = imgAdd.getLayoutParams();
            if (addParams != null) {
                addParams.width = imageWidth;
                addParams.height = imageWidth;
            }
        }

        @Override
        public void setViewData(
                Context mContext, final Photo item, final int position, int viewType) {
            if (item.getId() != -1) {
                imgCover.setVisibility(View.VISIBLE);
                deleteView.setVisibility(View.VISIBLE);
                tvAddImage.setVisibility(View.GONE);
                imgAdd.setVisibility(View.GONE);
                String path;
                if (item.getImagePath()
                        .startsWith("http") || item.getImagePath()
                        .startsWith("https")) {
                    //qiniu 图片
                    path = ImagePath.buildPath(item.getImagePath())
                            .width(imageWidth)
                            .height(imageWidth)
                            .cropPath();
                } else {
                    //local 图片
                    path = item.getImagePath();
                }
                Glide.with(mContext)
                        .load(path)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgCover);
                deleteView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDelete(item, position);
                        }
                    }
                });
            } else {
                imgCover.setVisibility(View.GONE);
                imgAdd.setVisibility(View.VISIBLE);
                deleteView.setVisibility(View.GONE);
                tvAddImage.setVisibility(View.GONE);
            }
            imageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(item, position);
                    }
                }
            });
        }
    }
}
