package com.hunliji.hljimagelibrary.utils;

import android.net.Uri;
import android.text.TextUtils;

import retrofit2.http.Url;

/**
 * Created by Suncloud on 2016/11/4.
 */

public class ImagePath {
    private int width;
    private int height;
    private int quality;
    private boolean crop;
    private String path;
    private boolean ignoreFormat;

    public static ImagePath buildPath(String path) {
        return new ImagePath(path);
    }

    private ImagePath(String path) {
        this.path = path;
    }

    public ImagePath width(int width) {
        this.width = width;
        return this;
    }

    public ImagePath height(int height) {
        this.height = height;
        return this;
    }

    public ImagePath quality(int quality) {
        this.quality = quality;
        return this;
    }

    public ImagePath ignoreFormat(boolean ignoreFormat) {
        this.ignoreFormat = ignoreFormat;
        return this;
    }

    public String cropPath() {
        this.crop = true;
        return path();
    }

    public String path() {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (!path.startsWith("http://") && !path.startsWith("https://")) {
            return path;
        }
        StringBuilder builder = new StringBuilder(path);
        if (path.contains("?")) {
            builder.append(ImageUtil.getOrSignURLEncoder());
        } else {
            builder.append("?");
        }
        builder.append("imageView2");
        if (crop) {
            builder.append("/1");
        } else {
            builder.append("/2");
            int maxSize = ImageUtil.getMaximumTextureSize();
            if (maxSize > 0) {
                if (width == 0 || width > maxSize) {
                    width = maxSize;
                }
                if (height == 0 || height > maxSize) {
                    height = maxSize;
                }
            }
        }
        if (width > 0) {
            builder.append("/w/")
                    .append(width);
        }
        if (height > 0) {
            builder.append("/h/")
                    .append(height);
        }
        try {
            if (!ignoreFormat && !Uri.parse(path)
                    .getPath()
                    .toLowerCase()
                    .endsWith(".gif")) {
                builder.append("/format/webp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.append("/q/")
                .append(quality > 0 ? quality : 100);
        //        builder.append("/ignore-error/1");
        return builder.toString();
    }
}
