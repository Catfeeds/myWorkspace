package com.hunliji.hljimagelibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * Created by wangtao on 2017/5/10.
 */

public abstract class BasePhotoViewHolder extends BaseViewHolder<Photo> {

    BasePhotoViewHolder(View itemView) {
        super(itemView);
    }

    public interface OnPhotoItemInterface {

        void onItemSelectClick(Photo photo);

        void onItemPreviewClick(Photo photo);

        int selectedIndex(Photo photo);

        boolean selectedCountEnable();
    }
}
