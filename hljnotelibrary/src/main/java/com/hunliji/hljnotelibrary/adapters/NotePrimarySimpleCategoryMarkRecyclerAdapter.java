package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.NotePrimarySimpleCategoryMarkViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 笔记标签一级分类
 * Created by chen_bin on 2017/6/29 0029.
 */
public class NotePrimarySimpleCategoryMarkRecyclerAdapter extends RecyclerView
        .Adapter<BaseViewHolder> {
    private Context context;
    private List<CategoryMark> categoryMarks;
    private List<CategoryMark> searchCategoryMarks;
    private CategoryMark selectedCategoryMark;
    private String keyword;
    private LayoutInflater inflater;
    private NotePrimarySimpleCategoryMarkViewHolder.OnSelectPrimarySimpleCategoryMarkListener
            onSelectPrimarySimpleCategoryMarkListener;

    public NotePrimarySimpleCategoryMarkRecyclerAdapter(
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

    public void setSelectedCategoryMark(CategoryMark selectedCategoryMark) {
        this.selectedCategoryMark = selectedCategoryMark;
    }

    public void setOnSelectPrimarySimpleCategoryMarkListener(
            NotePrimarySimpleCategoryMarkViewHolder.OnSelectPrimarySimpleCategoryMarkListener
                    onSelectPrimarySimpleCategoryMarkListener) {
        this.onSelectPrimarySimpleCategoryMarkListener = onSelectPrimarySimpleCategoryMarkListener;
    }

    @Override
    public int getItemCount() {
        return TextUtils.isEmpty(keyword) ? CommonUtil.getCollectionSize(categoryMarks) :
                CommonUtil.getCollectionSize(
                searchCategoryMarks);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotePrimarySimpleCategoryMarkViewHolder categoryMarkViewHolder = new
                NotePrimarySimpleCategoryMarkViewHolder(
                inflater.inflate(R.layout.note_primary_simple_category_mark_list_item___note,
                        parent,
                        false));
        categoryMarkViewHolder.setOnSelectPrimarySimpleCategoryMarkListener(
                onSelectPrimarySimpleCategoryMarkListener);
        return categoryMarkViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof NotePrimarySimpleCategoryMarkViewHolder) {
            NotePrimarySimpleCategoryMarkViewHolder categoryViewHolder =
                    (NotePrimarySimpleCategoryMarkViewHolder) holder;
            categoryViewHolder.setSelectedCategoryMark(selectedCategoryMark);
            categoryViewHolder.setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    public int getIndex(int secondaryIndex) {
        int primaryIndex = -1;
        int sum = 0;
        for (int i = 0, size = getItemCount(); i < size; i++) {
            if (secondaryIndex >= sum) {
                primaryIndex++;
            } else {
                return primaryIndex;
            }
            sum += 1;
        }
        return primaryIndex;
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