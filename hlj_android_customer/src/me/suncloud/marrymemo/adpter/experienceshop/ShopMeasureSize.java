package me.suncloud.marrymemo.adpter.experienceshop;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class ShopMeasureSize {

    public int imgHeaderHeight;
    public int imgHeaderMargin;
    public int imgSingleHeight;
    public int imgHeaderBgHeight;
    public int imgHeaderBgWidth;

    public ShopMeasureSize(Context mContext) {
        DisplayMetrics dm = mContext.getResources()
                .getDisplayMetrics();
        Point size = CommonUtil.getDeviceSize(mContext);
        imgHeaderHeight = Math.round(size.x * 1/ 2);
        imgHeaderBgHeight = Math.round(dm.density * 325 +16);
        imgHeaderBgWidth = Math.round( size.x- Math.round(dm.density *16));
        imgHeaderMargin = Math.round(imgHeaderHeight - Math.round(dm.density * 28));
        imgSingleHeight = Math.round(size.x / 4);
    }

}
