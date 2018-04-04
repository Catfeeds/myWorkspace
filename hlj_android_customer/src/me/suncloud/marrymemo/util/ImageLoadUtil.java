package me.suncloud.marrymemo.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2016/4/9.
 */
public class ImageLoadUtil {

    public static void loadImageView(
            Fragment fragment,
            String path,
            int placeholderId,
            ImageView imageView,
            boolean asBitmap) {
        if (asBitmap) {
            Glide.with(fragment)
                    .asBitmap()
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId)
                            .fitCenter())
                    .into(imageView);
        } else {
            Glide.with(fragment)
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId)
                            .fitCenter())
                    .into(imageView);
        }
    }

    public static void loadImageView(
            Context context,
            String path,
            int placeholderId,
            ImageView imageView,
            boolean asBitmap) {
        if (asBitmap) {
            Glide.with(context)
                    .asBitmap()
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId))
                    .into(imageView);
        }
    }

    public static void loadImageView(
            FragmentActivity activity,
            String path,
            int placeholderId,
            ImageView imageView,
            boolean asBitmap) {
        if (asBitmap) {
            Glide.with(activity)
                    .asBitmap()
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId))
                    .into(imageView);
        } else {
            Glide.with(activity)
                    .load(path)
                    .apply(new RequestOptions().placeholder(placeholderId))
                    .into(imageView);
        }
    }

    public static void loadImageViewWithoutTransition(
            Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imageView);
    }

    public static void loadImageViewWithoutTransitionNoCache(
            Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imageView);
    }

    public static void loadImageView(
            Context context, String path, int placeHolderId, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().placeholder(placeHolderId))
                .into(imageView);
    }

    public static void loadImageView(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imageView);
    }

    public static void loadImageView(Fragment fragment, String path, ImageView imageView) {
        Glide.with(fragment)
                .load(path)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imageView);
    }

    public static void loadImageView(
            Context context, String path, ImageView imageView, int width, int height) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().override(getValidDimension(width),
                        getValidDimension(height))
                        .fitCenter()
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imageView);
    }

    public static void loadPageImage(
            FragmentActivity activity,
            String path,
            int placeholderId,
            ImageView imageView,
            RequestListener<Drawable> listener,
            int width,
            int height) {
        Glide.with(activity)
                .load(path)
                .apply(new RequestOptions().override(getValidDimension(width),
                        getValidDimension(height))
                        .fitCenter()
                        .placeholder(placeholderId))
                .listener(listener)
                .into(imageView);
    }

    public static void loadLocalImage(
            FragmentActivity activity, String path, int placeholderId, ImageView imageView) {
        Glide.with(activity)
                .load(path)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(placeholderId)
                        .centerCrop())
                .into(imageView);
    }


    public static void loadImageResetH(
            Fragment fragment, String path, final ImageView imageView, int w, int h) {
        loadImageResetH(Glide.with(fragment), path, imageView, w, h);
    }

    public static void loadImageResetH(
            FragmentActivity activity, String path, final ImageView imageView, int w, int h) {
        loadImageResetH(Glide.with(activity), path, imageView, w, h);
    }

    public static void loadImageResetH(
            RequestManager requestManager,
            String path,
            final ImageView imageView,
            final int w,
            final int h) {
        requestManager.load(path)
                .apply(new RequestOptions().override(getValidDimension(w),
                        getValidDimension(h > 0 ? h : JSONUtil.getMaximumTextureSize()))
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
                        if (h == 0) {
                            imageView.getLayoutParams().height = Math.round(resource
                                    .getIntrinsicHeight() * w / resource.getIntrinsicWidth());

                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    private static int getValidDimension(int dimen) {
        if (dimen > 0) {
            return dimen;
        }
        return Target.SIZE_ORIGINAL;
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context)
                .clear(imageView);
    }
}
