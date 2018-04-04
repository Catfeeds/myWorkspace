package me.suncloud.marrymemo.adpter.newsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteViewHolder;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by luohanlin on 2017/7/19.
 * 笔记结果页
 */

public class NewSearchNoteResultsAdapter extends NewBaseSearchResultAdapter {

    private final static int ITEM_TYPE_LIST_RATIO_1_TO_1 = 11; //图片比例1:1
    private final static int ITEM_TYPE_LIST_RATIO_3_TO_4 = 12; //图片比例3:4
    private final static int ITEM_TYPE_LIST_RATIO_4_TO_3 = 13; //图片比例4:3

    public NewSearchNoteResultsAdapter(
            Context context, ArrayList<? extends Object> data) {
        super(context, data);
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
                CommonNoteViewHolder noteViewHolder = new CommonNoteViewHolder(layoutInflater
                        .inflate(
                        R.layout.common_note_list_item___note,
                        parent,
                        false), style);
                noteViewHolder.setOnItemClickListener(new OnItemClickListener<Note>() {
                    @Override
                    public void onItemClick(int position, Note note) {
                        if (note != null && note.getId() > 0) {
                            if (TextUtils.isEmpty(note.getRichText())) {
                                Intent intent = new Intent(context, NoteDetailActivity.class);
                                intent.putExtra("note_id", note.getId());
                                if (context instanceof Activity) {
                                    context.startActivity(intent);
                                    ((Activity) context).overridePendingTransition(R.anim
                                                    .slide_in_right,
                                            R.anim.activity_anim_default);
                                }
                            } else {
                                //富文本字段不为空表示运营笔记
                                if (context instanceof Activity) {
                                    HljWeb.startWebView(context, note.getRichText());
                                }
                            }
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
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            default:
                int index = getItemIndex(position);
                if (holder instanceof CommonNoteViewHolder) {
                    CommonNoteViewHolder viewHolder = (CommonNoteViewHolder) holder;
                    viewHolder.setView(context, (Note) data.get(index), position, viewType);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headSize = (headerView == null ? 0 : 1);
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return ITEM_TYPE_FOOTER;
        } else {
            int index = position - headSize;
            float ratio = ((Note) data.get(index)).getCover()
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

}
