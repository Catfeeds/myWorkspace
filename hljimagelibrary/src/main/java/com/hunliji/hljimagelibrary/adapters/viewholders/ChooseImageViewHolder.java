package com.hunliji.hljimagelibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.widgets.CountSelectView;

/**
 * Created by wangtao on 2017/5/9.
 */

public class ChooseImageViewHolder extends BasePhotoViewHolder {

    private OnPhotoItemInterface itemInterface;

    private ImageView imageView;
    private CountSelectView selectView;
    private View bgSelected;
    private int size;


    public ChooseImageViewHolder(
            ViewGroup parent, OnPhotoItemInterface itemInterface) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_photo_item___img, parent, false));
        this.itemInterface = itemInterface;
        if (itemInterface != null) {
            selectView.setCountEnable(itemInterface.selectedCountEnable());
        }
    }


    private ChooseImageViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        bgSelected = itemView.findViewById(R.id.bg_selected);
        selectView = (CountSelectView) itemView.findViewById(R.id.select_view);
        size = Math.round((CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                20)) / 3);
        itemView.getLayoutParams().height = size;
        selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemInterface != null && getItem() != null) {
                    itemInterface.onItemSelectClick(getItem());
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemInterface != null && getItem() != null) {
                    itemInterface.onItemPreviewClick(getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, Photo item, int position, int viewType) {
        String path = item.getImagePath();
        if (CommonUtil.isHttpUrl(path)) {
            path = ImagePath.buildPath(path)
                    .width(size)
                    .cropPath();
        }
        Glide.with(imageView.getContext())
                .load(path)
                .apply(new RequestOptions().dontAnimate())
                .into(imageView);
        int index = itemInterface == null ? -1 : itemInterface.selectedIndex(getItem());
        selectView.setSelected(index);
        bgSelected.setVisibility(index >= 0 ? View.VISIBLE : View.GONE);
    }
}
