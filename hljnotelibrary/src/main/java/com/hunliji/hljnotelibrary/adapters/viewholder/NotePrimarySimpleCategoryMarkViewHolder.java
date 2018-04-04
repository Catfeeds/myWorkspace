package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 笔记标签一级分类viewHolder
 * Created by chen_bin on 2017/6/29 0029.
 */
public class NotePrimarySimpleCategoryMarkViewHolder extends BaseViewHolder<CategoryMark> {
    @BindView(R2.id.tv_name)
    TextView tvName;
    private CategoryMark selectedCategoryMark;
    private OnSelectPrimarySimpleCategoryMarkListener onSelectPrimarySimpleCategoryMarkListener;

    public NotePrimarySimpleCategoryMarkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectPrimarySimpleCategoryMarkListener != null) {
                    onSelectPrimarySimpleCategoryMarkListener.onSelectPrimarySimpleCategoryMark(
                            getAdapterPosition(),
                            getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, CategoryMark categoryMark, int position, int viewType) {
        if (categoryMark == null) {
            return;
        }
        tvName.setText(categoryMark.getMark()
                .getName());
        if (selectedCategoryMark == categoryMark) {
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        }
    }

    public void setSelectedCategoryMark(CategoryMark selectedCategoryMark) {
        this.selectedCategoryMark = selectedCategoryMark;
    }

    public void setOnSelectPrimarySimpleCategoryMarkListener(
            OnSelectPrimarySimpleCategoryMarkListener onSelectPrimarySimpleCategoryMarkListener) {
        this.onSelectPrimarySimpleCategoryMarkListener = onSelectPrimarySimpleCategoryMarkListener;
    }

    public interface OnSelectPrimarySimpleCategoryMarkListener {
        void onSelectPrimarySimpleCategoryMark(int position, CategoryMark categoryMark);
    }
}