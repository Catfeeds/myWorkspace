package com.hunliji.hljlivelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljlivelibrary.models.LiveMeasures;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2016/10/24.直播列表Adapter
 */

public class SocialLiveAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int ITEM_TYPE = 4;
    private static final int FOOTER_TYPE = 5;
    private static final int HEADER_TYPE = 6;

    private static final int LIVE_IN_STATE = 1;
    private static final int LIVE_NOT_STATE = 2;
    private static final int LIVE_END_STATE = 3;

    private Context context;
    private ArrayList<LiveChannel> liveChannels;
    private View footerView;
    private View headerView;

    private LiveMeasures measures;

    public SocialLiveAdapter(
            Context context, ArrayList<LiveChannel> liveChannels) {
        this.context = context;
        this.liveChannels = liveChannels;
        measures = new LiveMeasures(context.getResources()
                .getDisplayMetrics(), CommonUtil.getDeviceSize(context));
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setData(ArrayList<LiveChannel> liveChannels) {
        if (liveChannels != null) {
            int oldSize = this.liveChannels.size();
            int size = liveChannels.size();
            this.liveChannels.clear();
            this.liveChannels.addAll(liveChannels);
            if (Math.min(oldSize, size) > 0) {
                notifyItemRangeChanged(0, Math.min(oldSize, size) - 1);
            }
            if (oldSize > size) {
                notifyItemRangeRemoved(size, oldSize - size - 1);
            } else if (oldSize < size) {
                notifyItemRangeInserted(oldSize, size - oldSize - 1);
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            default:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.social_live_list_item___live, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            holder.setView(context,
                    liveChannels.get(headerView == null ? position : position - 1),
                    headerView == null ? position : position - 1,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return liveChannels.size() + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }


    class ItemViewHolder extends BaseViewHolder<LiveChannel> {
        @BindView(R2.id.iv_social_live)
        ImageView ivSocialLive;
        @BindView(R2.id.tv_social_live_title)
        TextView tvSocialLiveTitle;
        @BindView(R2.id.tv_social_live_state)
        TextView tvSocialLiveState;
        @BindView(R2.id.tv_social_live_count)
        TextView tvSocialLiveCount;
        @BindView(R2.id.tv_social_live_count_tip)
        TextView tvSocialLiveCountTip;
        @BindView(R2.id.tv_social_live_time)
        TextView tvSocialLiveTime;
        @BindView(R2.id.tv_social_live_time_state)
        TextView tvSocialLiveTimeState;
        @BindView(R2.id.social_live_view)
        LinearLayout socialLiveView;
        @BindView(R2.id.live_in_tag)
        FrameLayout liveInTag;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, final LiveChannel item, int position, int viewType) {
            if (item != null) {
                ViewGroup.LayoutParams params = ivSocialLive.getLayoutParams();
                params.width = measures.liveImgWidth;
                params.height = measures.liveImgHeight;
                if (TextUtils.isEmpty(item.getImagePath())) {
                    Glide.with(mContext)
                            .clear(ivSocialLive);
                    ivSocialLive.setImageBitmap(null);
                    ivSocialLive.setVisibility(View.GONE);
                } else {
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(item.getImagePath())
                                    .width(measures.liveImgWidth)
                                    .path())
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.mipmap.icon_empty_image)
                                    .error(R.mipmap.icon_empty_image))
                            .into(ivSocialLive);
                }
                tvSocialLiveCount.setText(String.valueOf(item.getWatch_count()));
                tvSocialLiveTimeState.setVisibility(View.VISIBLE);
                tvSocialLiveState.setVisibility(View.VISIBLE);
                tvSocialLiveTime.setVisibility(View.VISIBLE);
                switch (item.getStatus()) {
                    case LIVE_IN_STATE:
                        tvSocialLiveState.setText(context.getString(R.string
                                .label_social_live_in___live));
                        tvSocialLiveState.setBackgroundResource(R.drawable.sp_r2_primary);
                        tvSocialLiveTimeState.setText(context.getResources()
                                .getString(R.string.label_social_live_in_time___live));
                        tvSocialLiveTime.setText(HljTimeUtils.getShowEndTime(context,
                                item.getEndTime()));
                        liveInTag.setVisibility(View.VISIBLE);
                        break;
                    case LIVE_NOT_STATE:
                        tvSocialLiveState.setText(context.getString(R.string
                                .label_social_live_not___live));
                        tvSocialLiveState.setBackgroundResource(R.drawable.sp_r2_blue);
                        tvSocialLiveState.setBackgroundColor(ContextCompat.getColor(
                                tvSocialLiveState.getContext(),
                                R.color.blue));
                        tvSocialLiveTimeState.setText(context.getResources()
                                .getString(R.string.label_social_live_not_time___live));
                        tvSocialLiveTime.setText(HljTimeUtils.getShowStartTime(context,
                                item.getStartTime()));
                        liveInTag.setVisibility(View.GONE);
                        break;
                    case LIVE_END_STATE:
                        tvSocialLiveState.setText(context.getString(R.string
                                .label_social_live_end___live));
                        tvSocialLiveState.setBackgroundColor(ContextCompat.getColor(
                                tvSocialLiveState.getContext(),
                                R.color.colorGray));
                        tvSocialLiveTimeState.setVisibility(View.GONE);
                        tvSocialLiveTime.setVisibility(View.GONE);
                        liveInTag.setVisibility(View.GONE);
                        break;
                    default:
                        tvSocialLiveTimeState.setVisibility(View.GONE);
                        tvSocialLiveState.setVisibility(View.GONE);
                        tvSocialLiveTime.setVisibility(View.GONE);
                        liveInTag.setVisibility(View.GONE);
                        break;
                }
                tvSocialLiveState.setPadding(CommonUtil.dp2px(mContext, 4),
                        0,
                        CommonUtil.dp2px(mContext, 4),
                        0);

                tvSocialLiveTitle.setText(item.getTitle());
                socialLiveView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        if (AuthUtil.loginBindCheck(context)) {
                            Intent intent = new Intent(context, LiveChannelActivity.class);
                            intent.putExtra(LiveChannelActivity.ARG_ID, item.getId());
                            context.startActivity(intent);
                            if (context instanceof Activity) {
                                ((Activity) context).overridePendingTransition(R.anim
                                                .slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                    }
                });
            }
        }
    }

}
