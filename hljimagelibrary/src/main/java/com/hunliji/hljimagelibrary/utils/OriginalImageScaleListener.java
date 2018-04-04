package com.hunliji.hljimagelibrary.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by luohanlin on 2017/5/4.
 * 固定宽，或固定高，或者固定宽高，的情况下尽可能使用原图比例
 */

public class OriginalImageScaleListener implements RequestListener<Drawable> {

    private ImageView imageView;
    private int fixedWidth;
    private int fixedHeight;

    /**
     * 固定宽或高，使用原图比例显示图片
     *
     * @param imageView   图片控件
     * @param fixedWidth  固定宽，为0时不固定
     * @param fixedHeight 固定高，为0时不固定
     */
    public OriginalImageScaleListener(ImageView imageView, int fixedWidth, int fixedHeight) {
        this.imageView = imageView;
        this.fixedWidth = fixedWidth;
        this.fixedHeight = fixedHeight;
    }

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
        if (fixedWidth > 0 && fixedHeight > 0) {
            // 固定宽高，剪裁图片
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            if (fixedWidth > 0) {
                // 固定宽，设置高
                float rate = (float) fixedWidth / resource.getIntrinsicWidth();
                imageView.getLayoutParams().width = fixedWidth;
                imageView.getLayoutParams().height = Math.round(resource.getIntrinsicHeight() *
                        rate);
            } else if (fixedHeight > 0) {
                // 固定高，设置宽
                float rate = (float) fixedHeight / resource.getIntrinsicHeight();
                imageView.getLayoutParams().height = fixedHeight;
                imageView.getLayoutParams().width = Math.round(resource.getIntrinsicWidth() * rate);
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.requestLayout();
        }
        return false;
    }
}
