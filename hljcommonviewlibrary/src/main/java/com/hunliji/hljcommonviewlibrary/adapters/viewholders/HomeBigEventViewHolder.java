package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerEventViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活动大图样式
 * Created by chen_bin on 2016/12/15 0015.
 */
public class HomeBigEventViewHolder extends TrackerEventViewHolder {

    @BindView(R2.id.divider)
    Space divider;
    @BindView(R2.id.event_view)
    RelativeLayout eventView;
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.tv_watch_count)
    TextView tvWatchCount;
    public int imageWidth;
    public int imageHeight;
    private OnItemClickListener onItemClickListener;

    public HomeBigEventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                32);
        this.imageHeight = Math.round(imageWidth * 1.0f * 143.0f / 296.0f);
        this.imgCover.getLayoutParams().height = imageHeight;
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return eventView;
    }

    @Override
    protected void setViewData(
            Context context, EventInfo eventInfo, int position, int viewType) {
        if (eventInfo == null) {
            return;
        }
        divider.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        Glide.with(context)
                .load(ImagePath.buildPath(eventInfo.getSurfaceImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvWatchCount.setVisibility(eventInfo.getWatchCount() > 0 ? View.VISIBLE : View.GONE);
        tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()) + "人感兴趣");
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}