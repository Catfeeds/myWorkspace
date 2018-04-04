package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityEventHistoryViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityEventStartViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityThreadViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityPosterLayoutViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.HotCommunityChannelLayoutViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.HotQaLayoutViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.HotThreadLayoutViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityQuestionViewHolder;

/**
 * Created by mo_yu on 2018/3/12.新娘说3.0 精选列表
 */

public class CommunityChoiceListRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int DEFAULT_TYPE = 0;
    private static final int THREAD_TYPE = 1;//话题
    private static final int QUESTION_TYPE = 2;//问题
    private static final int FOOTER_TYPE = 3;
    private static final int POST_TYPE = 4;//poster
    private static final int HOT_THREAD_TYPE = 5;//今日热门话题横向列表
    private static final int HOT_QA_TYPE = 6;//有问必答
    private static final int EVENT_THREAD_TYPE = 7;//进行中的活动
    private static final int HOT_CHANNEL_TYPE = 8;//热门新娘圈
    private static final int HISTORY_EVENT_THREAD_TYPE = 9;//结束的活动

    private Context context;
    private View footerView;
    private List<CommunityFeed> communityFeeds;//推荐话题
    private List<CommunityFeed> unWeightFeeds;//未满足插入条件的数据列表
    private HotCommunityChannelLayoutViewHolder.OnChannelClickListener onChannelClickListener;

    public CommunityChoiceListRecyclerAdapter(Context context) {
        this.context = context;
        communityFeeds = new ArrayList<>();
    }

    public void setCommunityFeeds(List<CommunityFeed> dataList) {
        communityFeeds.clear();
        communityFeeds.addAll(dataList);
        notifyDataSetChanged();
    }

    public void addCommunityFeeds(List<CommunityFeed> dataList) {
        communityFeeds.addAll(dataList);
        if (!CommonUtil.isCollectionEmpty(unWeightFeeds)) {
            for (int i = 0; i < unWeightFeeds.size(); i++) {
                CommunityFeed communityFeed = unWeightFeeds.get(i);
                int addPosition = communityFeed.getWeight() - 1;
                if (addPosition <= communityFeeds.size()) {
                    communityFeeds.add(addPosition, communityFeed);
                    unWeightFeeds.remove(communityFeed);
                    i--;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCommunityFeedsWithFixList(
            List<CommunityFeed> dataList, List<CommunityFeed> fixList) {
        communityFeeds.clear();
        communityFeeds.addAll(dataList);
        //插入固定位数据
        if (!CommonUtil.isCollectionEmpty(fixList)) {
            unWeightFeeds = new ArrayList<>();
            Collections.sort(fixList, new Comparator<CommunityFeed>() {
                @Override
                public int compare(CommunityFeed o1, CommunityFeed o2) {
                    return o1.getWeight() - o2.getWeight();
                }
            });
            for (CommunityFeed communityFeed : fixList) {
                int addPosition = communityFeed.getWeight() - 1;
                if (addPosition > communityFeeds.size()) {
                    unWeightFeeds.add(communityFeed);
                } else {
                    communityFeeds.add(addPosition, communityFeed);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOnChannelClickListener(
            HotCommunityChannelLayoutViewHolder.OnChannelClickListener onChannelClickListener) {
        this.onChannelClickListener = onChannelClickListener;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case POST_TYPE:
                return new CommunityPosterLayoutViewHolder(parent);
            case HOT_THREAD_TYPE:
                return new HotThreadLayoutViewHolder(parent);
            case HOT_QA_TYPE:
                return new HotQaLayoutViewHolder(parent);
            case HOT_CHANNEL_TYPE:
                HotCommunityChannelLayoutViewHolder holder = new
                        HotCommunityChannelLayoutViewHolder(
                        parent);
                holder.setOnChannelClickListener(onChannelClickListener);
                return holder;
            case EVENT_THREAD_TYPE:
                return new CommunityEventStartViewHolder(parent);
            case HISTORY_EVENT_THREAD_TYPE:
                return new CommunityEventHistoryViewHolder(parent);
            case QUESTION_TYPE:
                return new CommunityQuestionViewHolder(parent);
            case THREAD_TYPE:
                return new CommunityThreadViewHolder(parent);
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType != FOOTER_TYPE && itemType != DEFAULT_TYPE) {
            holder.setView(context,
                    getItem(position).getEntity(),
                    position,
                    getItemViewType(position));
        }
    }

    private CommunityFeed getItem(int position) {
        return communityFeeds.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        } else if (communityFeeds != null && communityFeeds.size() > 0) {
            if (position < communityFeeds.size()) {
                if (!CommonUtil.isEmpty(communityFeeds.get(position)
                        .getEntityType())) {
                    switch (communityFeeds.get(position)
                            .getEntityType()) {
                        case CommunityFeed.COMMUNITY_THREAD:
                            return THREAD_TYPE;
                        case CommunityFeed.QA_QUESTION:
                            return QUESTION_TYPE;
                        case CommunityFeed.POSTER:
                            return POST_TYPE;
                        case CommunityFeed.HOT_THREAD:
                            return HOT_THREAD_TYPE;
                        case CommunityFeed.HOT_QUESTION:
                            return HOT_QA_TYPE;
                        case CommunityFeed.HOT_CHANNEL:
                            return HOT_CHANNEL_TYPE;
                        case CommunityFeed.COMMUNITY_EVENT:
                            CommunityEvent communityEvent = (CommunityEvent) communityFeeds.get(
                                    position)
                                    .getEntity();
                            if (communityEvent.getStatus() == CommunityEvent.EVENT_START) {
                                return EVENT_THREAD_TYPE;
                            } else if (communityEvent.getStatus() == CommunityEvent.EVENT_END) {
                                if (!CommonUtil.isCollectionEmpty(communityEvent.getHotThreads())){
                                    return HISTORY_EVENT_THREAD_TYPE;
                                }else {
                                    return DEFAULT_TYPE;
                                }
                            }
                        default:
                            break;
                    }
                }
            }
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return (communityFeeds != null ? communityFeeds.size() : 0) + (footerView == null ? 0 : 1);
    }

}
