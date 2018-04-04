package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.models.GroupAdapterHeader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2017/9/28.
 */

public class CommonGroupHeaderViewHolder extends BaseViewHolder<GroupAdapterHeader> {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_describe)
    TextView tvDescribe;
    @BindView(R2.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R2.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R2.id.divider)
    View divider;

    private GroupHeaderClickListener listener;

    public CommonGroupHeaderViewHolder(ViewGroup viewGroup, GroupHeaderClickListener listener) {
        this(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.common_group_adapter_header___cv, viewGroup, false));
        this.listener = listener;
    }

    private CommonGroupHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener == null) {
                    return;
                }
                GroupAdapterHeader item = getItem();
                if (item == null || !item.isClickable()) {
                    return;
                }
                listener.onHeaderClick(item);
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, GroupAdapterHeader item, int position, int viewType) {
        headerLayout.setEnabled(item.isClickable());
        ivArrow.setVisibility(item.isClickable() ? View.VISIBLE : View.GONE);
        tvTitle.setText(item.getTitle());
        tvDescribe.setText(item.getDescribe());
        item.setLayoutPadding(headerLayout);
        if (item.showDivider()) {
            divider.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.GONE);
        }

    }


    public interface GroupHeaderClickListener {
        void onHeaderClick(GroupAdapterHeader header);
    }
}
