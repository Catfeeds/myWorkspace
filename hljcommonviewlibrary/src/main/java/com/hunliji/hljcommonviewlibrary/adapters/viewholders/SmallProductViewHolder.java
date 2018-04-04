package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by mo_yu. on 2017/7/24.婚品小图样式
 */

public class SmallProductViewHolder extends CommonProductViewHolder {

    public SmallProductViewHolder(View itemView, int style, int itemWidth) {
        super(itemView, style);
        if (itemWidth != 0) {
            imageWidth = itemWidth;
        } else {
            imageWidth = Math.round((CommonUtil.getDeviceSize(itemView.getContext()).x -
                    CommonUtil.dp2px(
                    itemView.getContext(),
                    8)) / 2);
        }
        switch (style) {
            case STYLE_RATIO_1_TO_1:
                imageHeight = imageWidth;
                break;
            case STYLE_RATIO_2_TO_3:
                imageHeight = Math.round(imageWidth * 1.5f);
                break;
        }
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        tvShowPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                contentLayout.getLayoutParams();
        marginLayoutParams.topMargin = CommonUtil.dp2px(itemView.getContext(), 8);
        contentLayout.setPadding(0, 0, 0, 0);
        tvTitle.setPadding(0, 0, 0, 0);
    }
}
