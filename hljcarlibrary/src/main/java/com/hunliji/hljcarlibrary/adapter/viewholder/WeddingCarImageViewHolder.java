package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarImageViewHolder extends BaseViewHolder<Photo> {

    @BindView(R2.id.image)
    ImageView image;

    private int width;
    private Context mContext;

    public WeddingCarImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        width = CommonUtil.getDeviceSize(mContext).x;
    }

    @Override
    protected void setViewData(Context mContext, Photo photo, int position, int viewType) {
        if (photo == null) {
            return;
        }
        int height = 0;
        if (photo.getHeight() > 0 && photo.getWidth() > 0) {
            height = Math.round(width * photo.getHeight() / photo.getWidth());

        }

        final int finalHeight = height;
        Glide.with(mContext)
                .load(ImagePath.buildPath(photo.getImagePath())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().override(width,
                        height > 0 ? height : ImageUtil.getMaximumTextureSize())
                        .fitCenter()
                        .placeholder(R.mipmap.icon_empty_image))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException e,
                            Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Drawable resource,
                            Object model,
                            Target<Drawable> target,
                            DataSource dataSource,
                            boolean isFirstResource) {
                        if (finalHeight == 0) {
                            image.getLayoutParams().height = Math.round(resource
                                    .getIntrinsicHeight() * width / resource
                                    .getIntrinsicWidth());

                        }
                        return false;
                    }
                })
                .into(image);
    }
}
