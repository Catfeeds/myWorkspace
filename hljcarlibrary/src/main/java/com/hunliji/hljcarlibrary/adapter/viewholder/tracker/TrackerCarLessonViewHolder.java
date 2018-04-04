package com.hunliji.hljcarlibrary.adapter.viewholder.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2018/2/2.
 */

public abstract class TrackerCarLessonViewHolder extends BaseViewHolder<CarLesson> {

    public TrackerCarLessonViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(Context mContext, CarLesson item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(HljTaggerName.CAR_LESSON)
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CAR_LESSON)
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();
}
