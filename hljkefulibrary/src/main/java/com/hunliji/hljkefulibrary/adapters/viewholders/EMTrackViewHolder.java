package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.EMTrack;

/**
 * Created by wangtao on 2017/10/23.
 */

public class EMTrackViewHolder extends EMChatMessageBaseViewHolder {

    private ImageView cover;
    private TextView price;
    private TextView title;
    private int width;
    private int height;

    public EMTrackViewHolder(ViewGroup parent, boolean isReceive) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(isReceive ? R.layout.chat_product_left___chat : R.layout
                                .chat_product_right___chat,
                        parent,
                        false));
    }

    private EMTrackViewHolder(View itemView) {
        super(itemView);
        width = CommonUtil.dp2px(itemView.getContext(), 140);
        height = CommonUtil.dp2px(itemView.getContext(), 90);
        itemView.findViewById(R.id.product_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChatClickListener != null) {
                            onChatClickListener.onTrackClick(getItem().getTrack());
                        }
                    }
                });
        cover = itemView.findViewById(com.hunliji.hljchatlibrary.R.id.cover);
        price = itemView.findViewById(com.hunliji.hljchatlibrary.R.id.price);
        title = itemView.findViewById(com.hunliji.hljchatlibrary.R.id.title);
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat item, int position, int viewType) {
        EMTrack track = item.getTrack();
        title.setText(track.getTitle());
        if (!TextUtils.isEmpty(track.getPriceStr())) {
            price.setVisibility(View.VISIBLE);
            price.setText(track.getPriceStr());
        } else {
            price.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(track.getImagePath())) {
            cover.setVisibility(View.VISIBLE);
            int coverHeight = height;
            if (track.getTrackImageHeight() > 0 && track.getTrackImageWidth() > 0) {
                coverHeight = Math.round(width * track.getTrackImageHeight() / track
                        .getTrackImageWidth());
            }
            cover.getLayoutParams().height = coverHeight;
            Glide.with(cover.getContext())
                    .load(ImagePath.buildPath(track.getImagePath())
                            .width(width)
                            .height(coverHeight)
                            .cropPath())
                    .apply(new RequestOptions().fitCenter()
                            .dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image))
                    .into(cover);

        } else {
            cover.setVisibility(View.GONE);
            Glide.with(cover)
                    .clear(cover);
        }
    }
}
