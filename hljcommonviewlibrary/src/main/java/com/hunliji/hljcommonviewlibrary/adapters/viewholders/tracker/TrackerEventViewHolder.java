package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerEventViewHolder extends BaseViewHolder<EventInfo> {

    public TrackerEventViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(
            Context mContext, EventInfo item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType("Activity")
                    .tag();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();

    public String tagName(){
        return HljTaggerName.EVENT;
    }
}
