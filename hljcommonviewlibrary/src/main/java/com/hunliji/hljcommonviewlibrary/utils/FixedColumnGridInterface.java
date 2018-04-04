package com.hunliji.hljcommonviewlibrary.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridImageView;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView.GridInterface;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView.MeasureType;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;


/**
 * 九宫格样式，每行固定显示3个
 * Created by chen_bin on 2017/5/23.
 */
public class FixedColumnGridInterface implements GridInterface<Photo> {
    private int space;
    private int columnCount = 3;

    public FixedColumnGridInterface(int space) {
        this.space = space;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    @Override
    public void setViewValue(Context context, View itemView, int position, List<Photo> data) {

    }

    @Override
    public View getItemView(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.photos_item___cv, viewGroup, false);
    }

    @Override
    public int getColumnCount(int size) {
        return columnCount;
    }

    @Override
    public int getSpacing(int size, MeasureType type) {
        return space;
    }

    @Override
    public int getItemSize(int totalWidth, int size, MeasureType type) {
        int mColumnCount = getColumnCount(size);
        int mHorizontalSpacing = getSpacing(size, MeasureType.HORIZONTAL);
        return (totalWidth - mHorizontalSpacing * (mColumnCount - 1)) / mColumnCount;
    }

    @Override
    public HljGridImageView getGridImageView(View itemView) {
        HljGridImageView imageView = (HljGridImageView) itemView.getTag();
        if (imageView == null) {
            imageView = (HljGridImageView) itemView.findViewById(R.id.photo);
            imageView.setImageViewInterface(new HljGridImageView.GridImageViewInterface<Photo>() {
                @Override
                public void load(
                        Context context,
                        HljGridImageView imageView,
                        Photo item,
                        int width,
                        int height) {
                    Glide.with(context)
                            .load(ImagePath.buildPath(item.getImagePath())
                                    .width(width)
                                    .height(height)
                                    .cropPath())
                            .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                            .into(imageView);
                }
            });
            itemView.setTag(imageView);
        }
        return imageView;
    }
}
