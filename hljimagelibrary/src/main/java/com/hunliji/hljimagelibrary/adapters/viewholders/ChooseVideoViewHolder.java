package com.hunliji.hljimagelibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.views.widgets.CountSelectView;

/**
 * Created by wangtao on 2017/5/9.
 */

public class ChooseVideoViewHolder extends BasePhotoViewHolder {

    private OnPhotoItemInterface itemInterface;

    private ImageView imageView;
    private TextView tvDuration;
    private CountSelectView selectView;
    private View bgSelected;


    public ChooseVideoViewHolder(
            ViewGroup parent, OnPhotoItemInterface itemInterface) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_photo_item___img, parent, false));
        this.itemInterface = itemInterface;
        if(itemInterface!=null){
            selectView.setCountEnable(itemInterface.selectedCountEnable());
        }
    }


    private ChooseVideoViewHolder(View itemView) {
        super(itemView);
        tvDuration=(TextView)itemView.findViewById(R.id.tv_duration);
        tvDuration.setVisibility(View.VISIBLE);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        bgSelected = itemView.findViewById(R.id.bg_selected);
        selectView = (CountSelectView) itemView.findViewById(R.id.select_view);
        itemView.getLayoutParams().height = Math.round((CommonUtil.getDeviceSize(itemView
                .getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                20)) / 3);
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
        Glide.with(imageView.getContext())
                .load(item.getImagePath())
                .apply(new RequestOptions().dontAnimate())
                .into(imageView);
        tvDuration.setText(HljTimeUtils.formatForDurationTime(item.getDuration()));
        int index = itemInterface == null ? -1 : itemInterface.selectedIndex(getItem());
        selectView.setSelected(index);
        bgSelected.setVisibility(index >= 0 ? View.VISIBLE : View.GONE);
    }
}
