package com.hunliji.hljnotelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteInspirationViewHolder;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxin on 2017/7/12 0012.
 */

public class CommonNoteInspirationRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int ITEM_TYPE_HEADER = 11;
    private final int ITEM_TYPE_FOOTER = 12;
    private final static int ITEM_TYPE_LIST_RATIO_1_TO_1 = 3; //图片比例1:1
    private final static int ITEM_TYPE_LIST_RATIO_3_TO_4 = 4; //图片比例3:4
    private final static int ITEM_TYPE_LIST_RATIO_4_TO_3 = 5; //图片比例4:3

    private View headerView;
    private View footerView;
    private List<NoteInspiration> noteInspirationList;
    private Context mContext;

    public CommonNoteInspirationRecyclerAdapter(Context mContext, List<NoteInspiration> medias) {
        this.mContext = mContext;
        this.noteInspirationList = medias;
        if (noteInspirationList == null) {
            noteInspirationList = new ArrayList<>();
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                ExtraBaseViewHolder headerViewHolder = new ExtraBaseViewHolder(headerView);
                StaggeredGridLayoutManager.LayoutParams headerParams = new
                        StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                headerViewHolder.itemView.setLayoutParams(headerParams);
                return headerViewHolder;
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                StaggeredGridLayoutManager.LayoutParams footerParams = new
                        StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                footerViewHolder.itemView.setLayoutParams(footerParams);
                return footerViewHolder;
            default:
                int style;
                if (viewType == ITEM_TYPE_LIST_RATIO_1_TO_1) {
                    style = CommonNoteInspirationViewHolder.STYLE_RATIO_1_TO_1;
                } else if (viewType == ITEM_TYPE_LIST_RATIO_3_TO_4) {
                    style = CommonNoteInspirationViewHolder.STYLE_RATIO_3_TO_4;
                } else {
                    style = CommonNoteInspirationViewHolder.STYLE_RATIO_4_TO_3;
                }
                View itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.common_note_inspiration_list_item___note, parent, false);
                CommonNoteInspirationViewHolder mediaViewHolder = new
                        CommonNoteInspirationViewHolder(
                        itemView,
                        style);
                mediaViewHolder.setOnItemClickListener(new OnItemClickListener<NoteInspiration>() {
                    @Override
                    public void onItemClick(int position, NoteInspiration inspiration) {
                        if (inspiration == null || inspiration.getId() == 0 || inspiration
                                .getNote() == null) {
                            return;
                        }
                        if(inspiration.getNote().isDeleted()){
                            Toast.makeText(mContext,"该笔记已被删除",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mContext, NoteDetailActivity.class);
                        intent.putExtra("note_id",
                                inspiration.getNote()
                                        .getId());
                        intent.putExtra("inspiration_id",
                                inspiration.getId());
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                return mediaViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
            case ITEM_TYPE_HEADER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            case ITEM_TYPE_LIST_RATIO_1_TO_1:
            case ITEM_TYPE_LIST_RATIO_3_TO_4:
            case ITEM_TYPE_LIST_RATIO_4_TO_3:
                int index = position - (headerView != null ? 1 : 0);
                holder.setView(mContext, noteInspirationList.get(index), index, viewType);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return noteInspirationList.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        int headSize = headerView == null ? 0 : 1;
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else {
            int index = position - headSize;
            NoteInspiration inspiration = noteInspirationList.get(index);
            float ratio = inspiration.getNoteMedia()
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

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setItems(List<NoteInspiration> noteMedias) {
        noteInspirationList.clear();
        if (!CommonUtil.isCollectionEmpty(noteMedias)) {
            noteInspirationList.addAll(noteMedias);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<NoteInspiration> items) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            int start = getItemCount() - (footerView != null ? 1 : 0);
            noteInspirationList.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }
    }
}
