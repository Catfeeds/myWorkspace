package com.hunliji.hljsharelibrary.adapters.viewholders;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljsharelibrary.R;

/**
 * Created by wangtao on 2018/3/20.
 */

public class ShareActionNotePosterViewHolder extends ShareActionViewHolder {


    public ShareActionNotePosterViewHolder(
            ViewGroup parent, OnShareClickListener onShareClickListener) {
        super(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.share_action_note_poster_item___share, parent, false),
                onShareClickListener);
    }
}
