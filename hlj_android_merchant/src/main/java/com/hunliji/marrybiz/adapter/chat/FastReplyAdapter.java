package com.hunliji.marrybiz.adapter.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.adapter.chat.viewholders.FastReplyViewHolder;
import com.hunliji.marrybiz.model.chat.FastReply;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/11/6.
 */

public class FastReplyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<FastReply> replies;
    private Context context;
    private FastReply selectedReply;
    private View footerView;

    private final int FAST_REPLY_TYPE = 1;
    private final int FOOTER_VIEW_TYPE = -1;

    public FastReplyAdapter(Context context) {
        this.context = context;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setReplies(List<FastReply> replies) {
        this.replies = replies;
        if (!CommonUtil.isCollectionEmpty(replies)) {
            if (selectedReply == null) {
                selectedReply = replies.get(0);
            }
            for (FastReply fastReply : replies) {
                if (fastReply.getId() == selectedReply.getId()) {
                    selectedReply=fastReply;
                    fastReply.setSelecked(true);
                    break;
                }
            }

        }
        notifyDataSetChanged();
    }


    public void addReplies(List<FastReply> replies) {
        if (CommonUtil.isCollectionEmpty(replies)) {
            return;
        }
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.addAll(replies);
        if (selectedReply !=null) {
            for (FastReply fastReply : replies) {
                if (fastReply.getId() == selectedReply.getId()) {
                    fastReply.setSelecked(true);
                    break;
                }
            }
        }
        notifyItemRangeInserted(getItemCount() - replies.size(), replies.size());
    }

    public FastReply getSelectedItem() {
        return selectedReply;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FAST_REPLY_TYPE:
                return new FastReplyViewHolder(parent,
                        new FastReplyViewHolder.OnSelectedListener() {
                            @Override
                            public void onSelected(FastReply item) {
                                selectedReply = item;
                                Observable.from(replies)
                                        .filter(new Func1<FastReply, Boolean>() {
                                            @Override
                                            public Boolean call(FastReply fastReply) {
                                                if (fastReply.isSelecked()) {
                                                    return fastReply.getId() != selectedReply.getId();
                                                } else {
                                                    return fastReply.getId() == selectedReply.getId();
                                                }
                                            }
                                        })
                                        .map(new Func1<FastReply, Integer>() {
                                            @Override
                                            public Integer call(FastReply fastReply) {
                                                fastReply.setSelecked(!fastReply.isSelecked());
                                                return replies.indexOf(fastReply);
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Integer>() {
                                            @Override
                                            public void call(Integer position) {
                                                notifyItemChanged(position);
                                            }
                                        });
                            }
                        });
            case FOOTER_VIEW_TYPE:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof FastReplyViewHolder) {
            ((FastReplyViewHolder) holder).setView(context,
                    replies.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = replies == null ? 0 : replies.size();
        if (count > 0 && footerView != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < replies.size()) {
            return FAST_REPLY_TYPE;
        }
        return FOOTER_VIEW_TYPE;
    }
}
