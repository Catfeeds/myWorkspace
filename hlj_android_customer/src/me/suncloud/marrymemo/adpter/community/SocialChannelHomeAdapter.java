package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder.SocialChannelHomeMyChannelViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder
        .SocialChannelHomeRecommendChannelViewHolder;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class SocialChannelHomeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int TYPE_MY_CHANNEL = 10;
    private final int TYPE_RECOMMEND_CHANNEL = 11;
    private final int TYPE_FOOTER = 12;

    private Context mContext;
    private LayoutInflater inflater;
    private View footerView;
    private SocialChannelHomeRecommendChannelViewHolder.onJoinClickListener onJoinClickListener;
    private List<CommunityChannel> recommendChannelList;
    private List<CommunityChannel> myChannelList;

    public SocialChannelHomeAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnJoinClickListener(
            SocialChannelHomeRecommendChannelViewHolder.onJoinClickListener onJoinClickListener) {
        this.onJoinClickListener = onJoinClickListener;
    }

    public void setRecommendChannelList(List<CommunityChannel> recommendChannelList) {
        this.recommendChannelList = recommendChannelList;
    }

    public void setMyChannelList(List<CommunityChannel> myChannelList) {
        this.myChannelList = myChannelList;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MY_CHANNEL:
                return new SocialChannelHomeMyChannelViewHolder(inflater.inflate(R.layout
                                .social_channel_home_my_channel,
                        parent,
                        false));
            case TYPE_RECOMMEND_CHANNEL:
                SocialChannelHomeRecommendChannelViewHolder
                        socialChannelHomeRecommendChannelViewHolder = new
                        SocialChannelHomeRecommendChannelViewHolder(
                        inflater.inflate(R.layout.social_channel_home_recommend_channel,
                                parent,
                                false));
                socialChannelHomeRecommendChannelViewHolder.setOnJoinClickListener(
                        onJoinClickListener);
                return socialChannelHomeRecommendChannelViewHolder;
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof SocialChannelHomeMyChannelViewHolder) {
            SocialChannelHomeMyChannelViewHolder holder = (SocialChannelHomeMyChannelViewHolder)
                    baseViewHolder;
            holder.setView(mContext, myChannelList, position, TYPE_MY_CHANNEL);
        } else if (baseViewHolder instanceof SocialChannelHomeRecommendChannelViewHolder) {
            SocialChannelHomeRecommendChannelViewHolder holder =
                    (SocialChannelHomeRecommendChannelViewHolder) baseViewHolder;
            if (getHeaderCount() > 0) {
                position--;
            }
            holder.setView(mContext,
                    recommendChannelList.get(position),
                    position,
                    TYPE_RECOMMEND_CHANNEL);
        }
    }


    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == 0 && !CommonUtil.isCollectionEmpty(myChannelList)) {
            type = TYPE_MY_CHANNEL;
        } else if (position == getItemCount() - 1 && footerView != null) {
            type = TYPE_FOOTER;
        } else {
            type = TYPE_RECOMMEND_CHANNEL;
        }
        return type;
    }

    private int getHeaderCount() {
        return CommonUtil.isCollectionEmpty(myChannelList) ? 0 : 1;
    }

    private int getRecommendCount() {
        return CommonUtil.isCollectionEmpty(recommendChannelList) ? 0 : CommonUtil
                .getCollectionSize(
                recommendChannelList) + 1;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getRecommendCount();
    }
}
