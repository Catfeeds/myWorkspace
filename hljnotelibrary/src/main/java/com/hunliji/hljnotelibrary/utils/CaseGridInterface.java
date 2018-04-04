package com.hunliji.hljnotelibrary.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridImageView;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljnotelibrary.R;

import java.util.List;

/**
 * Created by wangtao on 2017/1/17.
 */

public class CaseGridInterface implements HljGridView.GridInterface<Photo> {

    private int singleImgHeight;
    private int singleImgWidth;
    private int doubleSpace;
    private int space;

    public CaseGridInterface(Context context) {
        doubleSpace = CommonUtil.dp2px(context, 4);
        space = CommonUtil.dp2px(context, 2);
    }

    @Override
    public void setViewValue(Context context, View itemView, int position, List<Photo> data) {
        if (data != null && data.size() > position) {
            singleImgWidth = CommonUtil.getDeviceSize(context).x;
            singleImgHeight = Math.round(singleImgWidth * 10 / 16);
        }
    }

    @Override
    public View getItemView(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_photos_item___note, viewGroup, false);
    }

    @Override
    public int getColumnCount(int size) {
        if (size == 1) {
            return 1;
        } else if (size == 2) {
            return 2;
        }
        return 3;
    }

    @Override
    public int getSpacing(
            int size, HljGridView.MeasureType type) {
        if (size == 1) {
            return 0;
        } else if (size == 2) {
            return doubleSpace;
        }
        return space;
    }

    @Override
    public int getItemSize(
            int totalWidth, int size, HljGridView.MeasureType type) {
        if (size == 1) {
            switch (type) {
                case HEIGHT:
                    return singleImgHeight;
                case WIDTH:
                    return singleImgWidth;
                default:
                    return 0;
            }
        }
        int mColumnCount = getColumnCount(size);
        int mHorizontalSpacing = getSpacing(size, HljGridView.MeasureType.HORIZONTAL);
        int width = ((totalWidth - mHorizontalSpacing * (mColumnCount - 1)) / mColumnCount);
        switch (type) {
            case HEIGHT:
                return width * 10 / 16;
            case WIDTH:
                return width;
            default:
                return width;
        }
    }

    @Override
    public HljGridImageView getGridImageView(View itemView) {
        HljGridImageView imageView = (HljGridImageView) itemView.getTag();
        if (imageView == null) {
            imageView = (HljGridImageView) itemView.findViewById(R.id.photo);
            imageView.setImageViewInterface(new HljGridImageView
                    .GridImageViewInterface<WorkMediaItem>() {
                @Override
                public void load(
                        Context context,
                        HljGridImageView imageView,
                        WorkMediaItem item,
                        int width,
                        int height) {
                    if (item != null) {
                        String path = ImagePath.buildPath(item.getItemCover())
                                .width(width)
                                .path();
                        item.setLocalPath(path);
                        Glide.with(context)
                                .asBitmap()
                                .load(path)
                                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                                .into(imageView);
                    }
                }
            });
            itemView.setTag(imageView);
        }
        return imageView;
    }
}
