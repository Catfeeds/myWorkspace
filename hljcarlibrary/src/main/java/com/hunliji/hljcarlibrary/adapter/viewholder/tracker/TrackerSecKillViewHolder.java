package com.hunliji.hljcarlibrary.adapter.viewholder.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

/**
 * Created by wangtao on 2018/2/2.
 */

public abstract class TrackerSecKillViewHolder extends BaseViewHolder<SecKill> {

    public TrackerSecKillViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(Context mContext, SecKill item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(HljTaggerName.CAR)
                    .atPosition(position)
                    .dataId(item.getExtraData().getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CAR)
                    .tag();
            if(trackerChatBtnView()!=null) {
                HljVTTagger.buildTagger(trackerChatBtnView())
                        .tagName(HljTaggerName.SEC_KILL_CHAT)
                        .atPosition(position)
                        .dataId(item.getExtraData()
                                .getId())
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CAR)
                        .hitTag();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();


    public abstract View trackerChatBtnView();
}
