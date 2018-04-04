package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by mo_yu on 2018/3/21.活动
 */

public abstract class TrackerCommunityEventViewHolder extends BaseViewHolder<CommunityEvent> {

    public TrackerCommunityEventViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(
            Context mContext, CommunityEvent item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_COMMUNITY_EVENT)
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();

    public String tagName() {
        return HljTaggerName.COMMUNITY_ACTIVITY;
    }
}
