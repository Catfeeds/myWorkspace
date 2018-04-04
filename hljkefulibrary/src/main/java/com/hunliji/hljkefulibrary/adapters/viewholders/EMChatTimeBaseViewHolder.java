package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

import org.joda.time.DateTime;

/**
 * Created by wangtao on 2017/10/20.
 */

public abstract class EMChatTimeBaseViewHolder extends BaseViewHolder<EMChat> {

    private TextView timeView;

    public EMChatTimeBaseViewHolder(View itemView) {
        super(itemView);
        timeView =  itemView.findViewById(R.id.time);
    }


    public void setChatView(
            Context mContext,
            String avatarPath,
            EMChat item,
            EMChat lastMessage,
            int position,
            int viewType) {
        long lastTime = 0;
        if (lastMessage != null) {
            lastTime = lastMessage.getTime();
        }
        if (item.isShowTime() || (item.getTime() >0 && item.getTime() - lastTime >= 180000)) {
            timeView.setVisibility(View.VISIBLE);
            timeView.setText(new DateTime(item.getTime()).toString("MM-dd HH:mm"));
        } else {
            timeView.setVisibility(View.GONE);
        }
        this.setView(mContext, item, position, viewType);
    }
}
