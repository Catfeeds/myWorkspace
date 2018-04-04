package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonProductViewHolder;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.UserNoteViewHolder;
import com.hunliji.hljcommonlibrary.models.note.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/6/29.用户笔记adapter
 */

public class UserNoteRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int FOOTER_TYPE = 0;
    private final static int ITEM_TYPE = 1;

    private Context context;
    private List<Note> notes;
    private View footerView;
    private LayoutInflater inflater;
    public OnUserNoteItemListener onUserNoteItemListener;

    public UserNoteRecyclerAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
    }

    public void setNotes(List<Note> notes) {
        if(this.notes!=notes) {
            this.notes = notes;
            notifyDataSetChanged();
        }
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                UserNoteViewHolder noteViewHolder = new UserNoteViewHolder(inflater.inflate(R
                                .layout.user_note_list_item___note,
                        parent,
                        false), UserNoteViewHolder.STYLE_RATIO_1_TO_1);
                noteViewHolder.setOnItemClickListener(new OnItemClickListener<Note>() {
                    @Override
                    public void onItemClick(int position, Note note) {
                        if (onUserNoteItemListener!=null){
                            onUserNoteItemListener.onUserItemClick(position,note);
                        }
                    }
                });
                return noteViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE:
                holder.setView(context, notes.get(position), position, viewType);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return (notes != null ? Math.min(notes.size(),10) : 0) + (footerView != null
                ? 1 : 0);
    }

    public interface OnUserNoteItemListener{
        void onUserItemClick(int position,Note note);
    }

    public void setOnUserNoteItemListener(OnUserNoteItemListener onUserNoteItemListener) {
        this.onUserNoteItemListener = onUserNoteItemListener;
    }
}
