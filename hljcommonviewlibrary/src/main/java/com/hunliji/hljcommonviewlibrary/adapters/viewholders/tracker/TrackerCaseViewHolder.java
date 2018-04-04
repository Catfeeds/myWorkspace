package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerCaseViewHolder extends BaseViewHolder<Work> {

    public TrackerCaseViewHolder(View itemView) {
        super(itemView);
    }


    public String cpmSource() {
        return null;
    }

    @Override
    public void setView(Context mContext, Work item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_EXAMPLE)
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_PROPERTY_ID, getPropertyId(item))
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_MID, getMerchantId(item))
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, item.getCpm())
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource())
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }


    public abstract View trackerView();

    public String tagName() {
        return HljTaggerName.CASE;
    }

    private Long getPropertyId(Work work) {
        try {
            long id = work.getMerchant()
                    .getProperty()
                    .getId();
            if (id > 0) {
                return id;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Long getMerchantId(Work work) {
        try {
            long id = work.getMerchant()
                    .getId();
            if (id > 0) {
                return id;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
