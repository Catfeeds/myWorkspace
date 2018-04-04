package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/24.
 */

public class EMExtraViewHolder extends EMChatMessageBaseViewHolder {

    private FrameLayout extraLayout;

    public EMExtraViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.em_chat_extra_left_item___kefu, parent, false));
    }

    private EMExtraViewHolder(View itemView) {
        super(itemView);
        extraLayout=itemView.findViewById(R.id.extra_layout);
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        extraLayout.removeAllViews();
        if (item.getExtraView() != null) {
            extraLayout.addView(item.getExtraView());
        }
    }
}
