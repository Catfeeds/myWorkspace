package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder
        .NoteSecondarySimpleCategoryMarkIndexViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.NoteSecondarySimpleCategoryMarkViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * 笔记标签二级分类
 * Created by chen_bin on 2017/6/29 0029.
 */
public class NoteSecondarySimpleCategoryMarkRecyclerAdapter extends RecyclerView
        .Adapter<BaseViewHolder> implements StickyRecyclerHeadersAdapter<BaseViewHolder> {
    private Context context;
    private List<CategoryMark> categoryMarks;
    private List<CategoryMark> searchCategoryMarks;
    private String keyword;
    private LayoutInflater inflater;
    private NoteSecondarySimpleCategoryMarkViewHolder.OnSelectSecondarySimpleCategoryMarkListener
            onSelectSecondarySimpleCategoryMarkListener;

    public NoteSecondarySimpleCategoryMarkRecyclerAdapter(
            Context context,
            List<CategoryMark> categoryMarks,
            List<CategoryMark> searchCategoryMarks) {
        this.context = context;
        this.categoryMarks = categoryMarks;
        this.searchCategoryMarks = searchCategoryMarks;
        this.inflater = LayoutInflater.from(context);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setOnSelectSecondarySimpleCategoryMarkListener(
            NoteSecondarySimpleCategoryMarkViewHolder.OnSelectSecondarySimpleCategoryMarkListener
                    onSelectSecondarySimpleCategoryMarkListener) {
        this.onSelectSecondarySimpleCategoryMarkListener =
                onSelectSecondarySimpleCategoryMarkListener;
    }

    @Override
    public int getItemCount() {
        return TextUtils.isEmpty(keyword) ? CommonUtil.getCollectionSize(categoryMarks) :
                CommonUtil.getCollectionSize(
                searchCategoryMarks);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NoteSecondarySimpleCategoryMarkViewHolder categoryMarkViewHolder = new
                NoteSecondarySimpleCategoryMarkViewHolder(
                inflater.inflate(R.layout.note_secondary_simple_category_mark_flow___note,
                        parent,
                        false));
        categoryMarkViewHolder.setOnSelectSecondarySimpleCategoryMarkListener(
                onSelectSecondarySimpleCategoryMarkListener);
        return categoryMarkViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    @Override
    public long getHeaderId(int position) {
        Mark mark = getItem(position).getMark();
        return mark == null ? -1 : mark.getId();
    }

    @Override
    public BaseViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new NoteSecondarySimpleCategoryMarkIndexViewHolder(inflater.inflate(R.layout
                        .note_secondary_simple_category_mark_index___note,
                parent,
                false));
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof NoteSecondarySimpleCategoryMarkIndexViewHolder) {
            NoteSecondarySimpleCategoryMarkIndexViewHolder indexViewHolder =
                    (NoteSecondarySimpleCategoryMarkIndexViewHolder) holder;
            indexViewHolder.setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    public CategoryMark getItem(int position) {
        CategoryMark categoryMark = null;
        if (TextUtils.isEmpty(keyword) && !CommonUtil.isCollectionEmpty(categoryMarks)) {
            categoryMark = categoryMarks.get(position);
        } else if (!CommonUtil.isCollectionEmpty(searchCategoryMarks)) {
            categoryMark = searchCategoryMarks.get(position);
        }
        return categoryMark;
    }
}