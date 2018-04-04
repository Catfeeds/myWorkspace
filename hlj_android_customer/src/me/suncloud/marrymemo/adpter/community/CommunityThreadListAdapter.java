package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.adpter.community.viewholder.CommunityThreadViewHolder;

/**
 * Created by jinxin on 2018/3/19 0019.
 */

public class CommunityThreadListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int TYPE_THREAD = 10;
    private final int TYPE_FOOTER = 11;

    private View footerView;
    private List<CommunityThread> threadList;
    private Context mContext;

    public CommunityThreadListAdapter(Context mContext) {
        this.mContext = mContext;
        threadList = new ArrayList<>();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setThreadList(List<CommunityThread> threadList) {
        if (!CommonUtil.isCollectionEmpty(threadList)) {
            this.threadList.clear();
            this.threadList.addAll(threadList);
        }
        notifyDataSetChanged();
    }

    public void addThreadList(List<CommunityThread> threadList) {
        if (!CommonUtil.isCollectionEmpty(threadList)) {
            int start = CommonUtil.getCollectionSize(this.threadList);
            this.threadList.addAll(threadList);
            notifyItemChanged(start, threadList.size());
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_THREAD:
                return new CommunityThreadViewHolder(parent);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CommunityThreadViewHolder) {
            holder.setView(mContext, threadList.get(position), position, TYPE_THREAD);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && getFooterViewCount() > 0) {
            return TYPE_FOOTER;
        } else {
            return TYPE_THREAD;
        }
    }

    private int getThreadListCount() {
        return CommonUtil.getCollectionSize(threadList);
    }

    private int getFooterViewCount() {
        return footerView == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.isCollectionEmpty(threadList) ? 0 : getThreadListCount() + getFooterViewCount();
    }
}
