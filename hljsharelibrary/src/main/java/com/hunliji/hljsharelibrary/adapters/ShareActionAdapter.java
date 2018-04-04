package com.hunliji.hljsharelibrary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionNotePosterViewHolder;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;


/**
 * Created by wangtao on 2018/3/20.
 */

public class ShareActionAdapter extends RecyclerView.Adapter<BaseViewHolder<ShareAction>> {

    private static final int ITEM_COMMON = 1;
    private static final int ITEM_NOTE_POSTER = 2;

    private ShareAction[] actions;
    private ShareActionViewHolder.OnShareClickListener onShareClickListener;

    public void setActions(ShareAction[] actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    public void setOnShareClickListener(
            ShareActionViewHolder.OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    @Override
    public BaseViewHolder<ShareAction> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_NOTE_POSTER:
                return new ShareActionNotePosterViewHolder(parent, onShareClickListener);
            default:
                return new ShareActionViewHolder(parent, onShareClickListener);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ShareAction> holder, int position) {
        holder.setView(holder.itemView.getContext(),
                actions[position],
                position,
                getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return actions == null ? 0 : actions.length;
    }

    @Override
    public int getItemViewType(int position) {
        switch (actions[position]) {
            case NotePoster:
                return ITEM_NOTE_POSTER;
            default:
                return ITEM_COMMON;
        }
    }
}
