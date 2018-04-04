package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;

/**
 * Created by wangtao on 2017/10/20.
 */

public class EMTextViewHolder extends EMChatMessageBaseViewHolder {


    private TextView content;
    private int faceSize;


    public EMTextViewHolder(ViewGroup parent, boolean isReceive) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(isReceive ? R.layout.chat_text_left___chat : R.layout
                                .chat_text_right___chat,
                        parent,
                        false));
    }

    private EMTextViewHolder(View itemView) {
        super(itemView);
        faceSize = CommonUtil.dp2px(itemView.getContext(), 24);
        content = itemView.findViewById(R.id.content);
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(getItem().getType()
                        .equals(EMChat.UNKNOWN)){
                    return false;
                }
                if(onChatClickListener!=null){
                    onChatClickListener.onTextCopyClick(getItem().getContent());
                }
                return false;
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        if (item.getType()
                .equals(EMChat.UNKNOWN)) {
            content.setText("消息类型不支持");
        } else {
            content.setText(EmojiUtil.parseEmojiByTextForHeight(content.getContext(),
                    item.getContent(),
                    faceSize,
                    (int) content.getTextSize()));
        }

    }
}
