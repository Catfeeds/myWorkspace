package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerProductViewHolder extends BaseViewHolder<ShopProduct> {

    public TrackerProductViewHolder(View itemView) {
        super(itemView);
    }


    @Override
    public void setView(Context mContext, ShopProduct item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PRODUCT)
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }


    public abstract View trackerView();

    public String tagName() {
        return HljTaggerName.PRODUCT;
    }
}
