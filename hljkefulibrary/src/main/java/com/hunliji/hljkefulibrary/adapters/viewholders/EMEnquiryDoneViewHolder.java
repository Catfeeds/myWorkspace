package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/24.
 */

public class EMEnquiryDoneViewHolder extends EMChatTimeBaseViewHolder {


    public EMEnquiryDoneViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.em_chat_enquiry_done_item___kefu, parent, false));
    }

    private EMEnquiryDoneViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
    }
}
