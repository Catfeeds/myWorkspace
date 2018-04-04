package com.hunliji.hljlivelibrary.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/11/24.
 */

public class LiveStatusViewHolder {
    @BindView(R2.id.status_dot_view)
    ImageView statusDotView;
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.status_layout)
    LinearLayout statusLayout;

    private View view;

    public View getView() {
        return view;
    }

    public LiveStatusViewHolder(Context context) {
        this.view = View.inflate(context, R.layout.live_status_layout, null);
        ButterKnife.bind(this, view);
    }

    public void setStatus(LiveChannel channel) {
        if (!TextUtils.isEmpty(channel.getTitle())) {
            tvTitle.setText(channel.getTitle());
            tvTitle.setSelected(true);
        }
        switch (channel.getStatus()) {
            case 1:
                statusLayout.setBackgroundResource(R.drawable.sp_r10_primary);
                tvStatus.setText(R.string.label_live_detail_in2___live);
                statusDotView.setImageResource(R.drawable.anim_live_start_white);
                statusDotView.setVisibility(View.VISIBLE);
                ((AnimationDrawable) statusDotView.getDrawable()).start();
                break;
            case 2:
                statusLayout.setBackgroundResource(R.drawable.sp_r10_link);
                tvStatus.setText(R.string.label_live_detail_not2___live);
                statusDotView.setImageResource(R.drawable.anim_live_start_white2);
                statusDotView.setVisibility(View.VISIBLE);
                ((AnimationDrawable) statusDotView.getDrawable()).start();
                break;
            default:
                statusLayout.setBackgroundResource(R.drawable.sp_round10_gray3);
                tvStatus.setText(R.string.label_live_detail_end___live);
                statusDotView.setVisibility(View.GONE);
                break;
        }
    }
}
