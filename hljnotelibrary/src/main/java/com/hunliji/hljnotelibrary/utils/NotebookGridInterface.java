package com.hunliji.hljnotelibrary.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridImageView;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljnotelibrary.R;

import java.util.List;

/**
 * Created by wangtao on 2017/1/17.
 */

public class NotebookGridInterface implements HljGridView.GridInterface<Photo> {

    public transient final static float RATIO_1_TO_1 = 1.0f;
    public transient final static float RATIO_3_TO_4 = 3.0f / 4.0f;
    public transient final static float RATIO_4_TO_3 = 4.0f / 3.0f;
    private NoteMeasures measures;
    private int singleImgHeight;
    private int singleImgWidth;

    public NotebookGridInterface(Context context) {
        measures = new NoteMeasures(context.getResources()
                .getDisplayMetrics());
    }

    @Override
    public void setViewValue(Context context, View itemView, int position, List<Photo> data) {
        if (data != null && data.size() > position) {
            float ratio = getRatio(data.get(position));
            singleImgWidth = CommonUtil.getDeviceSize(context).x;
            singleImgHeight = (int) (singleImgWidth * ratio);
        }
    }

    public float getRatio(Photo photo) {
        int width = photo.getWidth();
        int height = photo.getHeight();
        if (height == 0) {
            return RATIO_1_TO_1;
        } else {
            float ratio = width * 1.0f / height;
            float rectangle1to1 = Math.abs(ratio - RATIO_1_TO_1);
            float rectangle3to4 = Math.abs(ratio - RATIO_3_TO_4);
            float rectangle4to3 = Math.abs(ratio - RATIO_4_TO_3);
            float min = Math.min(Math.min(rectangle1to1, rectangle3to4), rectangle4to3);
            if (min == rectangle1to1) {
                return RATIO_1_TO_1;
            } else if (min == rectangle3to4) {
                return RATIO_4_TO_3;
            } else {
                return RATIO_3_TO_4;
            }
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
            return measures.doubleSpace;
        }
        return measures.feedSpace;
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
                    if (item != null) {
                        Glide.with(context)
                                .asBitmap()
                                .load(ImageUtil.getImagePath2ForWxH(item.getImagePath(),
                                        width,
                                        height))
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
