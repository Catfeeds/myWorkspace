package me.suncloud.marrymemo.viewholder.themephotography;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.suncloud.marrymemo.R;

/**
 * Created by jinxin on 2016/10/13.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {
     public View itemView;
    public  ImageView image;
    public  TextView tvTitle;
    public  TextView tvCount;

    public ImageViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        image = (ImageView) itemView.findViewById(R.id.image);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvCount = (TextView) itemView.findViewById(R.id.tv_count);
    }
}
