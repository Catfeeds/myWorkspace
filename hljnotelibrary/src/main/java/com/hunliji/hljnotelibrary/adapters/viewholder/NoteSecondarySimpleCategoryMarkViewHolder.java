package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/6/29 0029.
 */
public class NoteSecondarySimpleCategoryMarkViewHolder extends BaseViewHolder<CategoryMark> {
    @BindView(R2.id.flow_layout)
    FlowLayout flowLayout;
    private int flowItemWidth;
    private OnSelectSecondarySimpleCategoryMarkListener onSelectSecondarySimpleCategoryMarkListener;

    public NoteSecondarySimpleCategoryMarkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.flowItemWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                125)) / 3;
    }

    @Override
    protected void setViewData(
            Context mContext, CategoryMark categoryMark, int position, int viewType) {
        if (categoryMark == null || CommonUtil.isCollectionEmpty(categoryMark.getChildren())) {
            return;
        }
        int count = flowLayout.getChildCount();
        int size = categoryMark.getChildren()
                .size();
        if (count > size) {
            flowLayout.removeViews(size, count - size);
        }
        for (int i = 0; i < size; i++) {
            View view = null;
            if (count > i) {
                view = flowLayout.getChildAt(i);
            }
            if (view == null) {
                View.inflate(mContext,
                        R.layout.note_secondary_simple_category_mark_flow_item___note,
                        flowLayout);
                view = flowLayout.getChildAt(flowLayout.getChildCount() - 1);
                view.getLayoutParams().width = flowItemWidth;
            }
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            final Mark mark = categoryMark.getChildren()
                    .get(i)
                    .getMark();
            if (mark == null || TextUtils.isEmpty(mark.getName())) {
                tvName.setVisibility(View.GONE);
            } else {
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(mark.getName());
                tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onSelectSecondarySimpleCategoryMarkListener != null) {
                            onSelectSecondarySimpleCategoryMarkListener
                                    .onSelectSecondarySimpleCategoryMark(
                                    mark);
                        }
                    }
                });
            }
        }
    }

    public void setOnSelectSecondarySimpleCategoryMarkListener(
            OnSelectSecondarySimpleCategoryMarkListener onSelectSecondarySimpleCategoryMarkListener) {
        this.onSelectSecondarySimpleCategoryMarkListener = onSelectSecondarySimpleCategoryMarkListener;
    }

    public interface OnSelectSecondarySimpleCategoryMarkListener {
        void onSelectSecondarySimpleCategoryMark(Mark mark);
    }
}