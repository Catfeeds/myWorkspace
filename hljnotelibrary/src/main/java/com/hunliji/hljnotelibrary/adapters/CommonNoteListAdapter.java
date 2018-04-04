package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteViewHolder;

import java.util.List;


/**
 * 笔记带header的通用列表adapter
 * Created by chen_bin on 2017/6/24 0024.
 */
public class CommonNoteListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<Note> notes;
    private LayoutInflater inflater;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST_RATIO_1_TO_1 = 1; //图片比例1:1
    private final static int ITEM_TYPE_LIST_RATIO_3_TO_4 = 2; //图片比例3:4
    private final static int ITEM_TYPE_LIST_RATIO_4_TO_3 = 3; //图片比例4:3
    private final static int ITEM_TYPE_FOOTER = 4;

    private OnItemClickListener onItemClickListener;

    public CommonNoteListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void addNotes(List<Note> notes) {
        if (!CommonUtil.isCollectionEmpty(notes)) {
            int start = getItemCount() - getFooterViewCount();
            this.notes.addAll(notes);
            notifyItemRangeInserted(start, notes.size());
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(notes);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            Note note = getItem(position);
            float ratio = note.getCover()
                    .getRatio();
            if (ratio == NoteMedia.RATIO_1_TO_1) {
                return ITEM_TYPE_LIST_RATIO_1_TO_1;
            } else if (ratio == NoteMedia.RATIO_4_TO_3) {
                return ITEM_TYPE_LIST_RATIO_3_TO_4;
            } else {
                return ITEM_TYPE_LIST_RATIO_4_TO_3;
            }
        }
    }

    public Note getItem(int position) {
        int index = position - getHeaderViewCount();
        return notes.get(index);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                ExtraBaseViewHolder headerViewHolder = new ExtraBaseViewHolder(headerView);
                headerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return headerViewHolder;
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            default:
                int style;
                if (viewType == ITEM_TYPE_LIST_RATIO_3_TO_4) {
                    style = CommonNoteViewHolder.STYLE_RATIO_3_TO_4;
                } else if (viewType == ITEM_TYPE_LIST_RATIO_4_TO_3) {
                    style = CommonNoteViewHolder.STYLE_RATIO_4_TO_3;
                } else {
                    style = CommonNoteViewHolder.STYLE_RATIO_1_TO_1;
                }
                CommonNoteViewHolder noteViewHolder = new CommonNoteViewHolder(inflater.inflate(R
                                .layout.common_note_list_item___note,
                        parent,
                        false), style);
                noteViewHolder.setOnItemClickListener(onItemClickListener);
                return noteViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            case ITEM_TYPE_LIST_RATIO_1_TO_1:
            case ITEM_TYPE_LIST_RATIO_3_TO_4:
            case ITEM_TYPE_LIST_RATIO_4_TO_3:
                holder.setView(context, getItem(position), position, viewType);
                break;
        }
    }
}