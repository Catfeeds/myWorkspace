package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/6/29 0029.
 */
public class NoteSecondarySimpleCategoryMarkIndexViewHolder extends BaseViewHolder<CategoryMark> {
    @BindView(R2.id.tv_name)
    TextView tvName;

    public NoteSecondarySimpleCategoryMarkIndexViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, CategoryMark categoryMark, int position, int viewType) {
        if (categoryMark == null) {
            return;
        }
        if (categoryMark.getMark() == null || TextUtils.isEmpty(categoryMark.getMark()
                .getName())) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(categoryMark.getMark()
                    .getName());
        }
    }
}