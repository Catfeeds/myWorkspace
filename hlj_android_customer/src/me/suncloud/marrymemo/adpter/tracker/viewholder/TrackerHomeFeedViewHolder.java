package me.suncloud.marrymemo.adpter.tracker.viewholder;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

import me.suncloud.marrymemo.model.home.HomeFeed;

/**
 * Created by wangtao on 2017/8/14.
 */

public abstract class TrackerHomeFeedViewHolder extends BaseViewHolder<HomeFeed> {

    public TrackerHomeFeedViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(Context mContext, HomeFeed item, int position, int viewType) {
        try {
            Object entity = item.getEntityObj();
            if (entity == null) {
                return;
            }
            switch (getItemViewType()) {
                case HomeFeed.FEED_TYPE_INT_CASE:
                    HljVTTagger.buildTagger(trackerView())
                            .tagName(HljTaggerName.CASE)
                            .tagParentName("feeds_list")
                            .atPosition(position)
                            .dataId(((Work) entity).getId())
                            .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_EXAMPLE)
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_FEED_PROPERTY_ID, propertyIdString())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_MID, getMerchantId((Work) entity))
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, ((Work) entity).getCpm())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                            .tag();
                    break;
                case HomeFeed.FEED_TYPE_INT_WORK:
                    HljVTTagger.buildTagger(trackerView())
                            .tagName(HljTaggerName.WORK)
                            .tagParentName("feeds_list")
                            .atPosition(position)
                            .dataId(((Work) entity).getId())
                            .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PACKAGE)
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_FEED_PROPERTY_ID, propertyIdString())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_MID, getMerchantId((Work) entity))
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, ((Work) entity).getCpm())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                            .tag();
                    break;
                case HomeFeed.FEED_TYPE_INT_THREAD:
                    HljVTTagger.buildTagger(trackerView())
                            .tagName(HljTaggerName.THREAD)
                            .tagParentName("feeds_list")
                            .atPosition(position)
                            .dataId(((CommunityThread) entity).getId())
                            .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_COMMUNITY_THREAD)
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_FEED_PROPERTY_ID, propertyIdString())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                            .tag();
                    break;
                case HomeFeed.FEED_TYPE_INT_POSTER:
                case HomeFeed.FEED_TYPE_INT_BANNER:
                    HljVTTagger.buildTagger(trackerView())
                            .tagName(HljTaggerName.POSTER)
                            .tagParentName("feeds_list")
                            .atPosition(position)
                            .dataId(((Poster) entity).getId())
                            .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_POSTER)
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_FEED_PROPERTY_ID, propertyIdString())
                            .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, item.getDtExtend())
                            .tag();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();

    public abstract String propertyIdString();

    private String cpmSource() {
        return "main_page_feeds";
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
