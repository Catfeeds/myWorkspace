package me.suncloud.marrymemo.adpter.tracker.viewholder;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerTravelMerchantExposureViewHolder extends BaseViewHolder<TravelMerchantExposure> {

    public TrackerTravelMerchantExposureViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(
            Context mContext, TravelMerchantExposure item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(HljTaggerName.MERCHANT)
                    .atPosition(position)
                    .dataId(item.getWorks()
                            .get(0)
                            .getMerchant()
                            .getId())
                    .dataType("Merchant")
                    .tag();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();
}
