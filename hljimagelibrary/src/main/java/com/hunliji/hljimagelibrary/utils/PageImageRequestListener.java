package com.hunliji.hljimagelibrary.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Suncloud on 2016/8/23.
 */
public class PageImageRequestListener implements RequestListener<Drawable> {

    private PhotoView photoView;
    private View progressBar;

    public PageImageRequestListener(PhotoView photoView, View progressBar) {
        this.photoView = photoView;
        this.progressBar = progressBar;
    }

    @Override
    public boolean onLoadFailed(
            @Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
        return false;
    }


    @Override
    public boolean onResourceReady(
            Drawable resource,
            Object model,
            Target<Drawable> target,
            DataSource dataSource,
            boolean isFirstResource) {
        progressBar.setVisibility(View.GONE);
        int x = photoView.getWidth();
        int y = photoView.getHeight();
        float rate = (float) x / resource.getIntrinsicWidth();
        int h = Math.round(resource.getIntrinsicHeight() * rate);
        if (h > y) {
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        return false;
    }
}
