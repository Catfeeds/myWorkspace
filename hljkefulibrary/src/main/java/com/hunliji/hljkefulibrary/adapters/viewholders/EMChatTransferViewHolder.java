package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.TransferToKefu;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.helpdesk.model.ToCustomServiceInfo;
import com.hyphenate.helpdesk.model.TransferIndication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by wangtao on 2017/10/25.
 */

public class EMChatTransferViewHolder extends EMChatMessageBaseViewHolder {

    private TextView tvText;
    private TextView tvTransferText;


    public EMChatTransferViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.em_chat_transfer_hint_item___kefu, parent, false));
    }

    private EMChatTransferViewHolder(View itemView) {
        super(itemView);
        tvText = itemView.findViewById(R.id.tv_text);
        tvTransferText = itemView.findViewById(R.id.tv_transfer_text);
        itemView.findViewById(R.id.transfer_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChatClickListener != null) {
                            onChatClickListener.onTransferToKefuClick(getItem().getTransferToKefu());
                        }
                    }
                });
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat chat, int position, int viewType) {
        tvText.setText(chat.getContent());
        ToCustomServiceInfo toCustomServiceInfo = chat.getTransferToKefu();
        if (TextUtils.isEmpty(toCustomServiceInfo.getLable())) {
            tvTransferText.setText("转人工客服");
        } else {
            tvTransferText.setText(toCustomServiceInfo.getLable());
        }
    }
}
