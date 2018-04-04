package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.LightUpActivity;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;

/**
 * Created by chen_bin on 2017/12/12 0012.
 */
public class WeddingCalendarItemViewHolder extends BaseViewHolder<WeddingCalendarItem> {

    @BindView(R.id.tv_date)
    TextView tvDate;
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
    @BindView(R.id.img_collect)
    ImageView imgCollect;
    @BindView(R.id.collect_layout)
    LinearLayout collectLayout;
    @BindView(R.id.schedule_layout)
    LinearLayout scheduleLayout;
    @BindView(R.id.share_layout)
    LinearLayout shareLayout;
    @BindView(R.id.img_brand_tag)
    ImageView imgBrandTag;

    private ImageView[] imgHotTags;

    private DateTime statisticEndAt;

    private OnCollectListener onCollectListener;
    private OnShareListener onShareListener;
    private String brandTagImagePath;

    public WeddingCalendarItemViewHolder(
            View itemView, DateTime statisticEndAt, String tagImagePath) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.statisticEndAt = statisticEndAt;
        this.brandTagImagePath = tagImagePath;
        imgHotTags = new ImageView[]{imgHotTag, imgHotTag2, imgHotTag3, imgHotTag4, imgHotTag5};
        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCollectListener != null) {
                    onCollectListener.onCollect(getAdapterPosition(), getItem());
                }
            }
        });
        scheduleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (!AuthUtil.loginBindCheck(context)) {
                    return;
                }
                City city = Session.getInstance()
                        .getMyCity(context);
                String firstMsg = context.getString(R.string.msg_calendar_hotel_adv_helper_address,
                        getItem().getDate()
                                .toString("yyyy年MM月dd日"),
                        Session.getInstance()
                                .getAddress(context));
                if (DataConfig.isAdvOpen(context, city)) {
                    Intent intent = new Intent(context, AdvHelperActivity.class);
                    intent.putExtra(AdvHelperActivity.ARG_IS_CALENDAR, true);
                    intent.putExtra(AdvHelperActivity.ARG_FIRST_MSG, firstMsg);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, LightUpActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra(AdvHelperActivity.ARG_IS_CALENDAR, true);
                    intent.putExtra(AdvHelperActivity.ARG_FIRST_MSG, firstMsg);
                    intent.putExtra("type", 3);
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.fade_in,
                                R.anim.activity_anim_default);
                    }
                }
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareListener != null) {
                    onShareListener.onShare(getAdapterPosition(), getItem());
                }
            }
        });

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(collectLayout)
                .tagName("calendar_collect_item")
                .hitTag();
        HljVTTagger.buildTagger(scheduleLayout)
                .tagName("calendar_hotel_schedule_item")
                .hitTag();
        HljVTTagger.buildTagger(shareLayout)
                .tagName("calendar_share_item")
                .hitTag();
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingCalendarItem calendarItem, int position, int viewType) {
        if (calendarItem == null) {
            return;
        }
        tvDate.setText(calendarItem.getDate() == null ? null : calendarItem.getDate()
                .toString(mContext.getString(R.string.format_date_type7)) + " " + calendarItem
                .getLunar() + " " + mContext.getString(
                R.string.label_week,
                calendarItem.getWeek()));
        imgMarriageTag.setImageResource(calendarItem.getYJDrawable(calendarItem.getMarriageStatus
                ()));
        imgEngagementTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getEngagementStatus()));
        imgBetrothalGiftTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getBetrothalGiftStatus()));
        imgUxorilocalMarriageTag.setImageResource(calendarItem.getYJDrawable(calendarItem
                .getUxorilocalMarriageStatus()));
        imgCollect.setImageResource(calendarItem.getId() > 0 ? R.mipmap
                .icon_collect_primary_34_34 : R.mipmap.icon_collect_stroke_primary_34_34);

        boolean isShowHotTag = true;
        if (calendarItem.getDate()
                .isAfter(statisticEndAt) && calendarItem.getCount() == 0) { //结婚对数服务器只生成2年内的数据
            isShowHotTag = false;
            tvWeddingCount.setText(mContext.getString(R.string.msg_after_calendar,
                    calendarItem.getDate()
                            .toString("yyyy")));
        } else {
            tvWeddingCount.setText(CommonUtil.fromHtml(mContext,
                    mContext.getString(R.string.html_wedding_count, calendarItem.getCount())));
        }
        for (int i = 0, size = imgHotTags.length; i < size; i++) {
            imgHotTags[i].setVisibility(isShowHotTag && i < calendarItem.getHot() ? View.VISIBLE
                    : View.GONE);
        }
        if (!CommonUtil.isEmpty(brandTagImagePath)) {
            Glide.with(mContext)
                    .load(ImagePath.buildPath(brandTagImagePath)
                            .width(CommonUtil.dp2px(mContext, 300))
                            .path())
                    .into(imgBrandTag);
            imgBrandTag.setVisibility(View.VISIBLE);
        } else {
            imgBrandTag.setVisibility(View.GONE);
        }
    }

    public void setOnCollectListener(OnCollectListener onCollectListener) {
        this.onCollectListener = onCollectListener;
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    public interface OnCollectListener {
        void onCollect(int position, WeddingCalendarItem calendarItem);
    }

    public interface OnShareListener {
        void onShare(int position, WeddingCalendarItem calendarItem);
    }

}
