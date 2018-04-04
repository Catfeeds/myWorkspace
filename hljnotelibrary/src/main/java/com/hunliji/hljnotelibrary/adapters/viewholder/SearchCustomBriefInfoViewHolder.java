package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen_bin on 2017/6/29 0029.
 */

public class SearchCustomBriefInfoViewHolder extends ExtraBaseViewHolder {
    @BindView(R2.id.top_line_layout)
    View topLineLayout;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R2.id.custom_view)
    LinearLayout customView;
    private OnSelectCustomListener onSelectCustomListener;

    public SearchCustomBriefInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectCustomListener != null) {
                    onSelectCustomListener.onSelectCustom();
                }
            }
        });
    }

    public void setShowCustomView(boolean show) {
        customView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setSubTitle(String subTitle) {
        tvSubTitle.setText(subTitle);
    }

    public void setShowTopLineView(boolean show) {
        topLineLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setOnSelectCustomListener(OnSelectCustomListener onSelectCustomListener) {
        this.onSelectCustomListener = onSelectCustomListener;
    }

    public interface OnSelectCustomListener {
        void onSelectCustom();
    }

}