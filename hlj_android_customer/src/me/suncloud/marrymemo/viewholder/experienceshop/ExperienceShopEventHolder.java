package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTimeFieldType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.experience.ExperienceEvent;
import me.suncloud.marrymemo.view.event.EventDetailActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * experience_shop_item_events
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopEventHolder extends RecyclerView.ViewHolder implements
        OnItemClickListener<EventInfo> {

    @BindView(R.id.layout_event)
    public LinearLayout layoutContent;

    private Context mContext;

    public ExperienceShopEventHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }


    public void setEvent(List<ExperienceEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        int size = Math.min(events.size(), layoutContent.getChildCount());
        for (int i = 0; i < size; i++) {
            ExperienceEvent event = events.get(i);
            View childView = layoutContent.getChildAt(i);
            childView.setVisibility(GONE);
            SmallEventViewHolder holder = new SmallEventViewHolder(childView);
            holder.setOnItemClickListener(this);
            holder.setViewData(mContext, event, size, i);
        }
    }


    @Override
    public void onItemClick(int position, EventInfo event) {
        if (event != null && event.getId() > 0) {
            Intent intent = new Intent(mContext, EventDetailActivity.class);
            intent.putExtra("id", event.getId());
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    class SmallEventViewHolder {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R.id.cover_layout)
        RelativeLayout coverLayout;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_des)
        TextView tvDes;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.line_layout)
        View lineLayout;
        View itemView;
        @BindView(R.id.layout_count)
        LinearLayout layoutCount;
        @BindView(R.id.img_event_end)
        ImageView imgEventEnd;

        private int imageWidth;
        private OnItemClickListener onItemClickListener;

        public SmallEventViewHolder(View itemView) {
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 118);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }


        public void setViewData(
                Context context, final ExperienceEvent event, int size, final int position) {
            if (event == null || event.getId() == 0) {
                itemView.setVisibility(GONE);
            } else {
                itemView.setVisibility(VISIBLE);
            }
            EventInfo eventInfo = event.getEventInfo();
            lineLayout.setVisibility(position < size - 1 ? VISIBLE : GONE);
            imgCover.setColorFilter(Color.parseColor("#4c000000"));
            Glide.with(context)
                    .load(ImageUtil.getImagePath(eventInfo.getListImg(), imageWidth))
                    .apply(new RequestOptions().dontAnimate()
                            .error(com.hunliji.hljcommonviewlibrary.R.mipmap.icon_empty_image)
                            .placeholder(com.hunliji.hljcommonviewlibrary.R.mipmap
                                    .icon_empty_image))
                    .into(imgCover);
            tvTitle.setText(event.getTitle());
            tvDes.setText(eventInfo.getSummary());
            tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()));

            String time = null;
            if (eventInfo.getPublishTime() != null && eventInfo.getSignUpEndTime() != null) {
                if (eventInfo.isSignUpEnd()) {
                    //报名已经截止
                    tvTitle.setTextColor(mContext.getResources()
                            .getColor(R.color.colorGray));
                    tvTime.setTextColor(mContext.getResources()
                            .getColor(R.color.colorGray));
                    time = mContext.getString(R.string.label_experience_store_time,
                            eventInfo.getSignUpEndTime()
                                    .toString("MM月dd日"));
                    layoutCount.setVisibility(GONE);
                    imgEventEnd.setVisibility(VISIBLE);
                } else {
                    layoutCount.setVisibility(VISIBLE);
                    imgEventEnd.setVisibility(GONE);
                    tvTitle.setTextColor(mContext.getResources()
                            .getColor(R.color.colorBlack2));
                    tvTime.setTextColor(mContext.getResources()
                            .getColor(R.color.colorBlack3));
                    if (eventInfo.getPublishTime()
                            .get(DateTimeFieldType.dayOfYear()) == eventInfo.getSignUpEndTime()
                            .get(DateTimeFieldType.dayOfYear())) {
                        time = eventInfo.getPublishTime()
                                .toString("MM月dd日 HH:mm") + eventInfo.getSignUpEndTime()
                                .toString("-HH:mm");
                    } else {
                        time = eventInfo.getPublishTime()
                                .toString("MM月dd日") + eventInfo.getSignUpEndTime()
                                .toString("-MM月dd日");
                    }
                    time = mContext.getString(R.string.label_experience_store_time, time);
                }
                tvTime.setText(time);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, event.getEventInfo());
                    }
                }
            });
        }
    }

}
