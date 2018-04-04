package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活动小图样式
 * Created by chen_bin on 2016/12/15 0015.
 */
public class SmallEventViewHolder extends BaseViewHolder<EventInfo> {
    @BindView(R2.id.top_line_layout)
    View topLineLayout;
    @BindView(R2.id.event_view)
    LinearLayout eventView;
    @BindView(R2.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R2.id.watch_count_layout)
    LinearLayout watchCountLayout;
    @BindView(R2.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.limit_time_layout)
    LinearLayout limitTimeLayout;
    @BindView(R2.id.tv_time_end)
    TextView tvTimeEnd;
    @BindView(R2.id.tv_day)
    TextView tvDay;
    @BindView(R2.id.tv_hour)
    TextView tvHour;
    @BindView(R2.id.tv_minute)
    TextView tvMinute;
    @BindView(R2.id.tv_second)
    TextView tvSecond;
    @BindView(R2.id.tv_hint_time_end)
    TextView tvHintTimeEnd;
    private int imageWidth;
    private int imageHeight;
    private DecimalFormat decimalFormat;

    public SmallEventViewHolder(ViewGroup viewGroup) {
        this(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.small_event_list_item___cv, viewGroup, false));
    }

    private SmallEventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.decimalFormat = new DecimalFormat("00");
        this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 118);
        this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 88.5f);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventInfo eventInfo = getItem();
                if (eventInfo != null && eventInfo.getId() > 0) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.EVENT_DETAIL_ACTIVITY)
                            .withLong("id", eventInfo.getId())
                            .navigation(v.getContext());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, EventInfo eventInfo, final int position, int viewType) {
        if (eventInfo == null) {
            return;
        }
        topLineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        imgCover.setColorFilter(Color.parseColor("#4c000000"));
        Glide.with(mContext)
                .load(ImagePath.buildPath(eventInfo.getListImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .error(R.mipmap.icon_empty_image)
                        .placeholder(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(eventInfo.getTitle());
        watchCountLayout.setVisibility(eventInfo.getWatchCount() > 0 ? View.VISIBLE : View.GONE);
        tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()));
        showTimeDown(eventInfo);
    }

    //倒计时
    public long showTimeDown(EventInfo eventInfo) {
        if (eventInfo == null || eventInfo.getId() == 0) {
            return 0;
        }
        //需要报名的活动
        long millis = 0;
        if (eventInfo.isNeedSignUp() && eventInfo.getSignUpEndTime() != null) {
            millis = eventInfo.getSignUpEndTime()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
        }
        //不需要报名的活动
        else if (eventInfo.getEndTime() != null) {
            millis = eventInfo.getEndTime()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
        }
        if (millis <= 0) {
            limitTimeLayout.setVisibility(View.GONE);
            tvTimeEnd.setVisibility(View.VISIBLE);
            tvTimeEnd.setText(eventInfo.isNeedSignUp() ? R.string.label_sign_up_end___cv : R
                    .string.label_event_end___cv);
        } else {
            limitTimeLayout.setVisibility(View.VISIBLE);
            tvTimeEnd.setVisibility(View.GONE);
            int days = (int) (millis / (1000 * 60 * 60 * 24));
            long leftMillis = millis % (1000 * 60 * 60 * 24);
            int hours = (int) (leftMillis / (1000 * 60 * 60));
            leftMillis %= 1000 * 60 * 60;
            int minutes = (int) (leftMillis / (1000 * 60));
            leftMillis %= 1000 * 60;
            int seconds = (int) (leftMillis / 1000);
            tvDay.setText(decimalFormat.format(days));
            tvHour.setText(decimalFormat.format(hours));
            tvMinute.setText(decimalFormat.format(minutes));
            tvSecond.setText(decimalFormat.format(seconds));
            tvHintTimeEnd.setText(eventInfo.isNeedSignUp() ? R.string.label_hint_sign_up_end___cv
                    : R.string.label_hint_event_end___cv);
        }
        return millis;
    }
}