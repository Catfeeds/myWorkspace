package com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

import java.util.List;

/**
 * Created by jinxin on 2018/3/2 0002.
 */

public abstract class TrackerCasePhotoViewHolder extends BaseViewHolder<List<CasePhoto>> {

    public TrackerCasePhotoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public final void setView(
            Context mContext, List<CasePhoto> casePhotoList, int position, int viewType) {
        super.setView(mContext, casePhotoList, position, viewType);
    }


    protected void trackerCasePhotoView(View view, int position, CasePhoto casePhoto) {
        try {
            HljVTTagger.buildTagger(view)
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(casePhoto.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CASE_PHOTO)
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected String tagName() {
        return HljTaggerName.WORK_CASE_PHOTO_ITEM;
    }

    @Override
    protected void setViewData(
            Context mContext, List<CasePhoto> item, int position, int viewType) {

    }
}
