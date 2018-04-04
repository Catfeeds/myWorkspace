package com.hunliji.hljsharelibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljsharelibrary.R;
import com.hunliji.hljsharelibrary.R2;
import com.hunliji.hljsharelibrary.models.ShareAction;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2018/3/20.
 */

public class ShareActionViewHolder extends BaseViewHolder<ShareAction> {

    @BindView(R2.id.iv_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_name)
    TextView tvName;

    public ShareActionViewHolder(ViewGroup parent, OnShareClickListener onShareClickListener) {
        this(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.share_action_common_item___share, parent, false),
                onShareClickListener);
    }

    ShareActionViewHolder(View itemView, final OnShareClickListener onShareClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClickListener != null) {
                    onShareClickListener.onShare(v, getItem());
                }
            }
        });
    }

    @Override
    public void setView(Context mContext, ShareAction item, int position, int viewType) {
        super.setView(mContext, item, position, viewType);
        String trackTagName = item.getTrackTagName();
        if (!TextUtils.isEmpty(trackTagName)) {
            HljVTTagger.buildTagger(itemView)
                    .tagName(trackTagName)
                    .hitTag();
        }
    }

    @Override
    protected void setViewData(
            Context mContext, ShareAction item, int position, int viewType) {
        ivIcon.setImageResource(item.getIconResId());
        tvName.setText(item.getNameResId());
    }

    public interface OnShareClickListener {
        void onShare(View v, ShareAction action);
    }
}
