package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2018/2/2.
 */

public abstract class TrackerWeddingCarProductViewHolder extends BaseViewHolder<WeddingCarProduct> {

    public TrackerWeddingCarProductViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(
            Context mContext, WeddingCarProduct item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(HljTaggerName.CAR)
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CAR)
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }


    public abstract View trackerView();
}
