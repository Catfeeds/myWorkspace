package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonThreadViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * Created by mo_yu on 16/12/15.话题列表
 */
public class CommunityThreadAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private ArrayList<CommunityThread> threads;
    private View headerView;
    private View footerView;
    private OnReplyItemClickListener onReplyItemClickListener;
    private boolean isShowHotTag;
    private boolean isShowNewTag;
    private boolean isShowRichTag;
    private boolean isShowChannelView;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public CommunityThreadAdapter(
            Context context, ArrayList<CommunityThread> threads) {
        this.context = context;
        this.threads = threads;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setShowHotTag(boolean showHotTag) {
        this.isShowHotTag = showHotTag;
    }

    public void setShowNewTag(boolean showNewTag) {
        this.isShowNewTag = showNewTag;
    }

    public void setShowRichTag(boolean showRichTag) {
        this.isShowRichTag = showRichTag;
    }

    public void setShowChannelView(boolean showChannelView) {
        this.isShowChannelView = showChannelView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                CommonThreadViewHolder holder = new CommonThreadViewHolder(LayoutInflater.from(
                        context)
                        .inflate(R.layout.common_community_thread_list_item___cv, parent, false),
                        CommonThreadViewHolder.COMMUNITY_CHANNEL);
                holder.setShowBottomThinLineView(true);
                holder.setShowHotTag(isShowHotTag);
                holder.setShowNewTag(isShowNewTag);
                holder.setShowRichTag(isShowRichTag);
                holder.setShowChannelView(isShowChannelView);
                holder.setOnPraiseClickListener(new CommonThreadViewHolder.OnPraiseClickListener() {
                    @Override
                    public void onPraiseClick(
                            CommunityThread thread,
                            CheckableLinearButton checkPraised,
                            ImageView imgThumbUp,
                            TextView tvPraiseCount,
                            TextView tvAdd) {
                        CommunityTogglesUtil.onNewCommunityThreadListPraise((Activity) context,
                                checkPraised,
                                imgThumbUp,
                                tvPraiseCount,
                                tvAdd,
                                thread);
                    }
                });
                holder.setOnReplyClickListener(new CommonThreadViewHolder.OnReplyClickListener() {
                    @Override
                    public void onReply(CommunityThread item, int position) {
                        if (onReplyItemClickListener != null) {
                            onReplyItemClickListener.onReply(item, position);
                        }
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CommonThreadViewHolder) {
            CommonThreadViewHolder threadHolder = (CommonThreadViewHolder) holder;
            position = headerView == null ? position : position - 1;
            threadHolder.setView(context, getItem(position), position, getItemViewType(position));
            threadHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, Object object) {
                    Activity activity = (Activity) context;
                    CommunityThread communityThread = (CommunityThread) object;
                    if (communityThread.getId() != 0) {
                        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                        intent.putExtra("is_from_channel", !isShowChannelView);
                        intent.putExtra("id", communityThread.getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return threads.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public CommunityThread getItem(int position) {
        return threads.get(position);
    }

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }

    public interface OnReplyItemClickListener {
        void onReply(CommunityThread item, int position);
    }
}
