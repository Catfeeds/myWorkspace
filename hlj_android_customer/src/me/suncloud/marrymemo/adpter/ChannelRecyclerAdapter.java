package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.HotCommunityChannel;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;

/**
 * Created by mo_yu on 2016/9/22.频道Adapter
 */
public class ChannelRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<HotCommunityChannel> hotCommunityChannels;
    private ArrayList<CommunityChannel> followCommunityChannels;


    private static final int FOLLOW_CHANNEL = 1;//我关注的频道
    private static final int HOT_CHANNEL = 2;//推荐频道
    private static final int DEFAULT_TYPE = 3;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 4;

    private DisplayMetrics dm;
    private int headWidth;

    private View headView;
    private View footerView;

    public ChannelRecyclerAdapter(
            Context context,
            ArrayList<HotCommunityChannel> hotCommunityChannels,
            ArrayList<CommunityChannel> followCommunityChannels) {
        this.context = context;
        this.hotCommunityChannels = hotCommunityChannels;
        this.followCommunityChannels = followCommunityChannels;
        initSize();
    }

    private void initSize() {
        dm = context.getResources()
                .getDisplayMetrics();
        headWidth = Math.round(50 * dm.density);
    }

    public void setHeadView(View headView) {
        this.headView = headView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case FOLLOW_CHANNEL:
                return new FollowChannelViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.channel_list_item, parent, false));
            case HOT_CHANNEL:
                return new HotChannelViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.channel_list_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof FollowChannelViewHolder && followCommunityChannels != null) {
            holder.setView(context,
                    followCommunityChannels.get(position),
                    position,
                    getItemViewType(position));
        } else if (holder instanceof HotChannelViewHolder && hotCommunityChannels != null) {
            int followPosition;
            if (followCommunityChannels == null) {
                followPosition = position;
            } else {
                followPosition = position - followCommunityChannels.size();
            }
            holder.setView(context,
                    hotCommunityChannels.get(followPosition),
                    followPosition,
                    getItemViewType(position));
        }
    }

    /**
     * 根据关注列表的数目，判断返回的type类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            if (followCommunityChannels != null) {
                if (position < followCommunityChannels.size()) {
                    return FOLLOW_CHANNEL;
                }
            }
            if (hotCommunityChannels != null) {
                if (position >= followCommunityChannels.size()) {
                    return HOT_CHANNEL;
                }
            }
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return followCommunityChannels.size() + hotCommunityChannels.size() + (footerView == null
                ? 0 : 1);
    }

    class HotChannelViewHolder extends BaseViewHolder<HotCommunityChannel> {

        @BindView(R.id.iv_find_channel_img)
        RoundedImageView ivFindChannelImg;
        @BindView(R.id.iv_same_city)
        TextView ivSameCity;
        @BindView(R.id.tv_find_channel_name)
        TextView tvFindChannelName;
        @BindView(R.id.tv_find_channel_hot_count)
        TextView tvFindChannelHotCount;
        @BindView(R.id.tv_find_channel_dec)
        TextView tvFindChannelDec;
        @BindView(R.id.tv_find_channel_post_count)
        TextView tvFindChannelPostCount;
        @BindView(R.id.tv_find_channel_popularity)
        TextView tvFindChannelPopularity;
        @BindView(R.id.tv_find_channel_focus)
        TextView tvFindChannelFocus;
        @BindView(R.id.tv_find_channel_focused)
        TextView tvFindChannelFocused;
        @BindView(R.id.channel_view)
        RelativeLayout channelView;
        @BindView(R.id.channel_head_view)
        LinearLayout channelHeadView;
        @BindView(R.id.tv_head_name)
        TextView tvHeadName;


        HotChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final HotCommunityChannel item,
                final int position,
                int viewType) {

            if (item != null) {

                //列表顶部视图，position值为关注列表数目
                if (position == 0) {
                    channelHeadView.setVisibility(View.VISIBLE);
                    tvHeadName.setText(mContext.getString(R.string.label_recommended_channel));
                } else {
                    channelHeadView.setVisibility(View.GONE);
                }
                final CommunityChannel communityChannel = item.getEntity();
                if (communityChannel == null) {
                    return;
                }
                String url = JSONUtil.getImagePath(communityChannel.getCoverPath(), headWidth);
                if (!JSONUtil.isEmpty(url)) {
                    ImageLoadUtil.loadImageView(mContext,
                            url,
                            R.mipmap.icon_avatar_primary,
                            ivFindChannelImg,
                            true);
                } else {
                    ImageLoadUtil.clear(mContext, ivFindChannelImg);
                    ivFindChannelImg.setImageBitmap(null);
                }
                tvFindChannelName.setText(JSONUtil.isEmpty(item.getEntity()
                        .getTitle()) ? "" : item.getEntity()
                        .getTitle());
                tvFindChannelPostCount.setText(mContext.getString(R.string
                                .label_find_channel_post_count,
                        String.valueOf(communityChannel.getThreadsCount())));
                tvFindChannelHotCount.setText(mContext.getString(R.string.label_add_thread_count,
                        item.getEntity()
                                .getTodayWatchCount()));
                tvFindChannelPopularity.setText(mContext.getString(R.string
                                .label_find_channel_popularity,
                        String.valueOf(communityChannel.getWatchCount())));
                tvFindChannelDec.setText(communityChannel.getDesc());

                final Activity activity = (Activity) mContext;
                channelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, CommunityChannelActivity.class);
                        intent.putExtra("id", communityChannel.getId());
                        activity.startActivityForResult(intent,
                                Constants.RequestCode.COMMUNITY_CHANNEL);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            }
        }
    }

    class FollowChannelViewHolder extends BaseViewHolder<CommunityChannel> {

        @BindView(R.id.iv_find_channel_img)
        RoundedImageView ivFindChannelImg;
        @BindView(R.id.iv_same_city)
        TextView ivSameCity;
        @BindView(R.id.tv_find_channel_name)
        TextView tvFindChannelName;
        @BindView(R.id.tv_find_channel_hot_count)
        TextView tvFindChannelHotCount;
        @BindView(R.id.tv_find_channel_dec)
        TextView tvFindChannelDec;
        @BindView(R.id.tv_find_channel_post_count)
        TextView tvFindChannelPostCount;
        @BindView(R.id.tv_find_channel_popularity)
        TextView tvFindChannelPopularity;
        @BindView(R.id.tv_find_channel_focus)
        TextView tvFindChannelFocus;
        @BindView(R.id.tv_find_channel_focused)
        TextView tvFindChannelFocused;
        @BindView(R.id.channel_view)
        RelativeLayout channelView;
        @BindView(R.id.channel_head_view)
        LinearLayout channelHeadView;
        @BindView(R.id.tv_head_name)
        TextView tvHeadName;
        @BindView(R.id.iv_channel_default)
        ImageView ivChannelDefault;

        FollowChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, final CommunityChannel item, final int position, int viewType) {
            if (item != null) {
                //列表顶部视图
                if (position == 0) {
                    channelHeadView.setVisibility(View.VISIBLE);
                    tvHeadName.setText(mContext.getString(R.string.label_my_follow_merchant));
                } else {
                    channelHeadView.setVisibility(View.GONE);
                }
                String url = JSONUtil.getImagePath(item.getCoverPath(), headWidth);
                if (!JSONUtil.isEmpty(url)) {
                    ImageLoadUtil.loadImageView(mContext,
                            url,
                            R.mipmap.icon_avatar_primary,
                            ivFindChannelImg,
                            true);
                } else {
                    ImageLoadUtil.clear(mContext, ivFindChannelImg);
                    ivFindChannelImg.setImageBitmap(null);
                }
                tvFindChannelName.setText(JSONUtil.isEmpty(item.getTitle()) ? "" : item.getTitle());
                tvFindChannelPostCount.setText(mContext.getString(R.string
                                .label_find_channel_post_count,
                        String.valueOf(item.getThreadsCount())));
                tvFindChannelHotCount.setText(mContext.getString(R.string.label_add_thread_count,
                        item.getTodayWatchCount()));
                tvFindChannelPopularity.setText(mContext.getString(R.string
                                .label_find_channel_popularity,
                        String.valueOf(item.getWatchCount())));
                tvFindChannelDec.setText(item.getDesc());
                //默认关注频道标志
                if (item.isDefault()) {
                    ivChannelDefault.setVisibility(View.VISIBLE);
                } else {
                    ivChannelDefault.setVisibility(View.GONE);
                }

                final Activity activity = (Activity) mContext;
                channelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, CommunityChannelActivity.class);
                        intent.putExtra("id", item.getId());
                        activity.startActivityForResult(intent,
                                Constants.RequestCode.COMMUNITY_CHANNEL);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
            }
        }
    }
}

