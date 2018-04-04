package me.suncloud.marrymemo.adpter.newsearch;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonThreadViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;

/**
 * Created by werther on 16/12/8.
 * 帖子结果页
 */

public class NewSearchThreadResultAdapter extends NewBaseSearchResultAdapter {

    private OnReplyItemClickListener onReplyItemClickListener;

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }

    public interface OnReplyItemClickListener {
        void onReply(
                CommunityThread item, int position);
    }

    public NewSearchThreadResultAdapter(
            Context context, ArrayList<CommunityThread> data) {
        super(context, data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            default:
                CommonThreadViewHolder holder = new CommonThreadViewHolder(layoutInflater.inflate
                        (R.layout.common_community_thread_list_item___cv,
                        parent,
                        false), CommonThreadViewHolder.COMMUNITY_SEARCH);
                holder.setShowChannelView(true);
                holder.setOnItemClickListener(this.onItemClickListener);
                holder.setOnReplyClickListener(new CommonThreadViewHolder.OnReplyClickListener() {
                    @Override
                    public void onReply(CommunityThread item, int position) {
                        if (onReplyItemClickListener != null) {
                            onReplyItemClickListener.onReply(item, position);
                        }
                    }
                });
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
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                if (holder instanceof CommonThreadViewHolder) {
                    CommonThreadViewHolder threadViewHolder = (CommonThreadViewHolder) holder;
                    threadViewHolder.setShowBottomThickLineView(index < data.size() - 1);
                    threadViewHolder.setView(context,
                            (CommunityThread) data.get(index),
                            index,
                            viewType);
                }
                break;
        }
    }
}
