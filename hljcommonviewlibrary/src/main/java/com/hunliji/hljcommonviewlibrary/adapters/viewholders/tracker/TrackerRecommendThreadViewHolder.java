package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

/**
 * Created by wangtao on 2017/8/14.
 */
@Deprecated
// TODO: 2018/3/28 wangtao 删除
public abstract class TrackerRecommendThreadViewHolder extends BaseViewHolder<CommunityFeed> {

    public TrackerRecommendThreadViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(Context mContext, CommunityFeed item, int position, int viewType) {
        try {
            Object entity=item.getEntity();
            if(entity==null){
                return;
            }
            if(entity instanceof Question) {
                HljVTTagger.buildTagger(trackerView())
                        .tagName(HljTaggerName.QUESTION)
                        .atPosition(position)
                        .dataId(((Question) entity).getId())
                        .dataType("Question")
                        .tag();
            }else if(entity instanceof CommunityThread){
                HljVTTagger.buildTagger(trackerView())
                        .tagName(HljTaggerName.THREAD)
                        .atPosition(position)
                        .dataId(((CommunityThread) entity).getId())
                        .dataType("CommunityThread")
                        .tag();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();
}
