package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;

/**
 * Created by chen_bin on 2017/12/12 0012.
 */
public class WeddingCalendarPosterViewHolder extends BaseViewHolder<WeddingCalendarItem> {


    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_lunar)
    TextView tvLunar;
    @BindView(R.id.img_marriage_tag)
    ImageView imgMarriageTag;
    @BindView(R.id.img_engagement_tag)
    ImageView imgEngagementTag;
    @BindView(R.id.img_betrothal_gift_tag)
    ImageView imgBetrothalGiftTag;
    @BindView(R.id.img_uxorilocal_marriage_tag)
    ImageView imgUxorilocalMarriageTag;
    @BindView(R.id.tv_wedding_count)
    TextView tvWeddingCount;
    @BindView(R.id.img_hot_tag)
    ImageView imgHotTag;
    @BindView(R.id.img_hot_tag2)
    ImageView imgHotTag2;
    @BindView(R.id.img_hot_tag3)
    ImageView imgHotTag3;
    @BindView(R.id.img_hot_tag4)
    ImageView imgHotTag4;
    @BindView(R.id.img_hot_tag5)
    ImageView imgHotTag5;
    @BindView(R.id.wedding_count_layout)
    LinearLayout weddingCountLayout;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;

    private ImageView[] imgHotTags;

    private DateTime statisticEndAt;

    public WeddingCalendarPosterViewHolder(
            final View itemView,
            DateTime statisticEndAt,
            int width) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.statisticEndAt = statisticEndAt;
        itemView.getLayoutParams().width = width;
        imgHotTags = new ImageView[]{imgHotTag, imgHotTag2, imgHotTag3, imgHotTag4, imgHotTag5};
        float scale = width * 1.0f / CommonUtil.dp2px(itemView.getContext(), 300);
        contentLayout.setScaleX(scale);
        contentLayout.setScaleY(scale);
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCalendarItem calendarItem, int position, int viewType) {
        if (calendarItem == null) {
            return;
        }
        Glide.with(mContext)
                .load(calendarItem.getSolarTermDrawable())
                .apply(new RequestOptions())
                .into(imgCover);
        tvDate.setText(calendarItem.getDate() == null ? null : calendarItem.getDate()
                .toString(mContext.getString(R.string.format_date_type16)));
        tvDay.setText(calendarItem.getDate() == null ? null : calendarItem.getDate()
                .toString(mContext.getString(R.string.format_day2)));
        int days = 0;
        if (calendarItem.getDate() != null) {
            DateTime currentDate = new DateTime();
            days = Days.daysBetween(currentDate.toLocalDate(),
                    calendarItem.getDate()
                            .toLocalDate())
                    .getDays();
            days = Math.max(0, days);
        }
        tvLunar.setText(mContext.getString(R.string.label_after,
                days) + "，" + calendarItem.getLunar() + "，" + mContext.getString(R.string
                        .label_week,
                calendarItem.getWeek()));
        imgMarriageTag.setImageResource(calendarItem.getYJDrawable(calendarItem.getMarriageStatus
                ()));
        imgEngagementTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getEngagementStatus()));
        imgBetrothalGiftTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getBetrothalGiftStatus()));
        imgUxorilocalMarriageTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getUxorilocalMarriageStatus()));

        if (calendarItem.getDate()
                .isAfter(statisticEndAt) && calendarItem.getCount() == 0) { //结婚对数服务器只生成2年内的数据
            weddingCountLayout.setVisibility(View.GONE);
        } else {
            weddingCountLayout.setVisibility(View.VISIBLE);
            tvWeddingCount.setText(CommonUtil.fromHtml(mContext,
                    mContext.getString(R.string.html_wedding_count2, calendarItem.getCount())));
            for (int i = 0, size = imgHotTags.length; i < size; i++) {
                imgHotTags[i].setVisibility(i < calendarItem.getHot() ? View.VISIBLE : View.GONE);
            }
        }
    }
}