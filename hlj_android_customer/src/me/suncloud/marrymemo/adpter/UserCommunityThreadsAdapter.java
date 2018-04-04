package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonThreadViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;

/**
 * Created by werther on 16/8/25.
 */
public class UserCommunityThreadsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<CommunityThread> threads;
    private View headerView;
    private View footerView;
    private onItemClickListener onItemClickListener;
    private OnReplyItemClickListener onReplyItemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public UserCommunityThreadsAdapter(
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

    @Override
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                CommonThreadViewHolder threadViewHolder = new CommonThreadViewHolder
                        (LayoutInflater.from(
                        context)
                        .inflate(R.layout.common_community_thread_list_item___cv, parent, false),
                        CommonThreadViewHolder.MY_COMMUNITY_THREAD);
                threadViewHolder.setShowChannelView(false);
                threadViewHolder.setOnPraiseClickListener(new CommonThreadViewHolder
                        .OnPraiseClickListener() {
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
                threadViewHolder.setOnReplyClickListener(new CommonThreadViewHolder
                        .OnReplyClickListener() {
                    @Override
                    public void onReply(CommunityThread item, int position) {
                        if (onReplyItemClickListener != null) {
                            onReplyItemClickListener.onReply(item, position);
                        }
                    }
                });
                return threadViewHolder;
        }
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CommonThreadViewHolder) {
            CommonThreadViewHolder threadViewHolder = (CommonThreadViewHolder) holder;
            threadViewHolder.setShowBottomThickLineView(position < threads.size() - 1);
            threadViewHolder.setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
            threadViewHolder.setOnItemClickListener(new com.hunliji.hljcommonlibrary.adapters
                    .OnItemClickListener() {
                @Override
                public void onItemClick(int position, Object object) {
                    CommunityThread communityThread = (CommunityThread) object;
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, communityThread);
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

    public void setOnItemClickListener(
            UserCommunityThreadsAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position, CommunityThread item);
    }

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }

    public interface OnReplyItemClickListener {
        void onReply(
                CommunityThread item, int position);
    }
}
