package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerEventViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活动大图样式
 * Created by chen_bin on 2016/12/15 0015.
 */
public class BigEventViewHolder extends TrackerEventViewHolder {
    @BindView(R2.id.event_header_view)
    RelativeLayout eventHeaderView;
    @BindView(R2.id.tv_event_header_title)
    TextView tvEventHeaderTitle;
    @BindView(R2.id.event_end_header_view)
    LinearLayout eventEndHeaderView;
    @BindView(R2.id.event_view)
    LinearLayout eventView;
    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.sign_up_limit_layout)
    LinearLayout signUpLimitLayout;
    @BindView(R2.id.tv_sign_up_limit)
    TextView tvSignUpLimit;
    @BindView(R2.id.img_shade)
    ImageView imgShade;
    @BindView(R2.id.img_event_end)
    ImageView imgEventEnd;
    @BindView(R2.id.tv_sign_up_ing_hint)
    TextView tvSignUpIngHint;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_time_end)
    TextView tvTimeEnd;
    @BindView(R2.id.limit_time_layout)
    LinearLayout limitTimeLayout;
    @BindView(R2.id.tv_hour)
    TextView tvHour;
    @BindView(R2.id.tv_minute)
    TextView tvMinute;
    @BindView(R2.id.tv_second)
    TextView tvSecond;
    @BindView(R2.id.tv_time_end_hint)
    TextView tvTimeEndHint;
    @BindView(R2.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R2.id.tv_watch_count_hint)
    TextView tvWatchCountHint;
    @BindView(R2.id.bottom_thin_line_layout)
    View bottomThinLineLayout;
    @BindView(R2.id.bottom_thick_line_layout)
    View bottomThickLineLayout;
    private DecimalFormat decimalFormat;
    private long millis;
    public int imageWidth;
    public int imageHeight;
    public int topMargin;
    public static final int STYLE_COMMON = 0; //大图活动通用列表
    public static final int STYLE_HOME_PAGE = 1; //首页里的活动，跟列表的差距在于图片的大小
    public static final int STYLE_LIVE = 2; //直播相关里的活动
    private OnItemClickListener onItemClickListener;

    public BigEventViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.decimalFormat = new DecimalFormat("00");
        this.topMargin = CommonUtil.dp2px(itemView.getContext(), 16);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                32);
        switch (style) {
            case STYLE_COMMON:
                this.imageHeight = Math.round(imageWidth / 2.0f);
                break;
            case STYLE_HOME_PAGE:
            case STYLE_LIVE:
                this.imageHeight = Math.round(imageWidth * 1.0f * 143.0f / 296.0f);
                break;
        }
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
            Context context, final EventInfo eventInfo, final int position, int viewType) {
        if (eventInfo == null) {
            return;
        }
        Glide.with(context)
                .load(ImagePath.buildPath(eventInfo.getSurfaceImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(eventInfo.getTitle());
        tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()));
        if (eventInfo.getShowSignUpLimit() <= 0) {
            signUpLimitLayout.setVisibility(View.GONE);
        } else {
            signUpLimitLayout.setVisibility(View.VISIBLE);
            tvSignUpLimit.setText(String.valueOf(eventInfo.getShowSignUpLimit()));
        }
        showTimeDown(context, eventInfo);
    }

    //倒计时
    public void showTimeDown(Context context, EventInfo eventInfo) {
        if (eventInfo == null || eventInfo.getId() == 0) {
            return;
        }
        //需要报名的活动
        if (eventInfo.isNeedSignUp() && eventInfo.getSignUpEndTime() != null) {
            millis = eventInfo.getSignUpEndTime()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
        }
        //不需要报名的活动
        else if (eventInfo.getEndTime() != null) {
            millis = eventInfo.getEndTime()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
        }
        //倒计时结束
        if (millis <= 0) {
            imgShade.setVisibility(View.VISIBLE);
            imgEventEnd.setVisibility(View.VISIBLE);
            tvSignUpIngHint.setVisibility(View.GONE);
            limitTimeLayout.setVisibility(View.GONE);
            tvTimeEnd.setVisibility(View.VISIBLE);
            tvTimeEnd.setText(eventInfo.isNeedSignUp() ? R.string.label_sign_up_end___cv : R
                    .string.label_event_end___cv);
            tvWatchCount.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
            tvWatchCountHint.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
        }
        //倒计时未结束
        else {
            eventEndHeaderView.setVisibility(View.GONE);
            imgShade.setVisibility(View.GONE);
            imgEventEnd.setVisibility(View.GONE);
            tvSignUpIngHint.setVisibility(View.VISIBLE);
            limitTimeLayout.setVisibility(View.VISIBLE);
            tvTimeEnd.setVisibility(View.GONE);
            int days = (int) (millis / (1000 * 60 * 60 * 24));
            long leftMillis = millis % (1000 * 60 * 60 * 24);
            int hours = (int) (leftMillis / (1000 * 60 * 60));
            leftMillis %= 1000 * 60 * 60;
            int minutes = (int) (leftMillis / (1000 * 60));
            leftMillis %= 1000 * 60;
            int seconds = (int) (leftMillis / 1000);
            tvHour.setText(decimalFormat.format(hours + days * 24));
            tvMinute.setText(decimalFormat.format(minutes));
            tvSecond.setText(decimalFormat.format(seconds));
            tvTimeEndHint.setText(eventInfo.isNeedSignUp() ? R.string.label_hint_sign_up_end___cv
                    : R.string.label_hint_event_end___cv);
            tvWatchCount.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
            tvWatchCountHint.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
        }
    }

    //报名是否结束了
    public boolean isSignUpEnd() {
        return millis <= 0;
    }

    public void setShowEventHeaderView(boolean showEventHeaderView) {
        eventHeaderView.setVisibility(showEventHeaderView ? View.VISIBLE : View.GONE);
    }

    public void setEventHeaderTitle(String eventHeaderTitle) {
        tvEventHeaderTitle.setText(eventHeaderTitle);
    }

    public void setShowEventEndHeaderView(
            Context context, boolean showEventEndHeaderView, int position) {
        if (!showEventEndHeaderView) {
            eventEndHeaderView.setVisibility(View.GONE);
        } else {
            eventEndHeaderView.setVisibility(View.VISIBLE);
            eventEndHeaderView.setPadding(0,
                    CommonUtil.dp2px(context, position == 0 ? 18 : 8),
                    0,
                    CommonUtil.dp2px(context, 18));
        }
    }

    public void setShowBottomThinLineView(boolean showBottomThinLineView) {
        bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
        bottomThickLineLayout.setVisibility(View.GONE);
    }

    public void setShowBottomThickLineView(boolean showBottomThickLineView) {
        bottomThinLineLayout.setVisibility(View.GONE);
        bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}