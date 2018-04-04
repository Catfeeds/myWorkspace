package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerMerchantViewHolder extends BaseViewHolder<Merchant> {

    public TrackerMerchantViewHolder(View itemView) {
        super(itemView);
    }


    public String cpmSource() {
        return null;
    }

    @Override
    public void setView(Context mContext, Merchant item, int position, int viewType) {
        try {
            HljVTTagger.Tagger tagger = HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_MERCHANT)
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_PROPERTY_ID, getPropertyId(item))
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, item.getCpm())
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource());
            if (hitTrackOnly()) {
                tagger.hitTag();
            } else {
                tagger.tag();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }


    public abstract View trackerView();

    public boolean hitTrackOnly() {
        return false;
    }

    public String tagName() {
        return HljTaggerName.MERCHANT;
    }

    private Long getPropertyId(Merchant merchant) {
        try {
            long id = merchant.getProperty()
                    .getId();
            if (id > 0) {
                return id;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
