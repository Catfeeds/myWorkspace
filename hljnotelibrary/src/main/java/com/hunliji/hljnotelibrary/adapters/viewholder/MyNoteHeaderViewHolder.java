package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/7/5 0005.
 */

public class MyNoteHeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.layout_note)
    LinearLayout layoutNote;

    public MyNoteHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
