package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.utils.EMVoiceUtil;

/**
 * Created by wangtao on 2017/10/23.
 */

public class EMVoiceViewHolder extends EMChatMessageBaseViewHolder implements EMVoiceUtil
        .PlayStatusListener {

    private ImageView imgVoice;
    private TextView tvLength;


    public EMVoiceViewHolder(ViewGroup parent, boolean isReceive) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(isReceive ? R.layout.chat_voice_left___chat : R.layout
                                .chat_voice_right___chat,
                        parent,
                        false));
    }

    private EMVoiceViewHolder(View itemView) {
        super(itemView);
        imgVoice = itemView.findViewById(R.id.img_voice);
        tvLength = itemView.findViewById(R.id.tv_length);
        itemView.findViewById(R.id.voice_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EMVoiceUtil.getInstance()
                                .onVoicePlayer(v.getContext(),
                                        EMVoiceUtil.getInstance()
                                                .getLocalFile(v.getContext(), getItem()),
                                        EMVoiceViewHolder.this);
                    }
                });
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        tvLength.setText(tvLength.getContext()
                .getString(R.string.label_voice_length___cm,item.getVoiceLength()));
        EMVoiceUtil.getInstance()
                .setPlayListener(EMVoiceUtil.getInstance()
                        .getLocalFile(tvLength.getContext(), item), this);
    }

    @Override
    public void onStop() {
        if (imgVoice.getDrawable() != null && imgVoice.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) imgVoice.getDrawable()).stop();
        }
        imgVoice.setImageResource(getItem().isReceive()? R.mipmap.icon_voice_left_03___cm
                : R.mipmap.icon_voice_right_03___cm);
    }

    @Override
    public void onStart() {
        Drawable animationDrawable = ContextCompat.getDrawable(imgVoice.getContext(),
                getItem().isReceive() ? R.drawable.sl_ic_voice_left : R.drawable
                        .sl_ic_voice_right);
        imgVoice.setImageDrawable(animationDrawable);
        if (animationDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) animationDrawable).start();
        }
    }
}
