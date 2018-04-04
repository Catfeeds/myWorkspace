package me.suncloud.marrymemo.adpter.themephotography.viewholder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.TravelMerchantExposureWorkRecyclerAdapter;
import me.suncloud.marrymemo.adpter.tracker.viewholder.TrackerTravelMerchantExposureViewHolder;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.themephotography.TravelMerchantExposure;
import rx.Subscriber;

/**
 * 旅拍专场列表
 * Created by chen_bin on 2017/5/15 0015.
 */
public class TravelMerchantExposureViewHolder extends TrackerTravelMerchantExposureViewHolder {
    @BindView(R.id.exposure_header_view)
    RelativeLayout exposureHeaderView;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_start_at)
    TextView tvStartAt;
    @BindView(R.id.img_arrow_up)
    ImageView imgArrowUp;
    @BindView(R.id.work_recycler_view)
    RecyclerView workRecyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_time_end_hint)
    TextView tvTimeEndHint;
    @BindView(R.id.exposure_view)
    LinearLayout exposureView;
    private TravelMerchantExposureWorkRecyclerAdapter workAdapter;
    private int imageWidth;
    private int imageHeight;

    public TravelMerchantExposureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                24);
        this.imageHeight = Math.round(imageWidth / 2.0f);
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        Drawable drawable = ContextCompat.getDrawable(itemView.getContext(),
                R.mipmap.image_bg_start_at_192_96);
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvStartAt
                    .getLayoutParams();
            params.topMargin = (int) (Math.sqrt(0.25 * width * width + height * height) - height);
            tvStartAt.setPivotX(width);
            tvStartAt.setPivotY(height);
        }
        workRecyclerView.setFocusable(false);
        workRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        workRecyclerView.addItemDecoration(new SpacesItemDecoration(itemView.getContext()));
        workAdapter = new TravelMerchantExposureWorkRecyclerAdapter(itemView.getContext());
        workRecyclerView.setAdapter(workAdapter);
        workAdapter.setOnLoadMoreListener(new TravelMerchantExposureWorkRecyclerAdapter
                .OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                exposureView.performClick();
            }
        });
        exposureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelMerchantExposure exposure = getItem();
                if (exposure != null && exposure.getId() > 0) {
                    ThemeApi.postIncreaseWatchCountObb(new PostIdBody(exposure.getId()))
                            .subscribe(new Subscriber() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Object o) {

                                }
                            });
                    HljWeb.startWebView((Activity) v.getContext(), exposure.getTargetUrl());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return exposureView;
    }

    @Override
    protected void setViewData(
            Context mContext, TravelMerchantExposure exposure, int position, int viewType) {
        if (exposure == null) {
            return;
        }

        Glide.with(mContext)
                .load(ImagePath.buildPath(exposure.getCover())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(exposure.getTitle());
        tvWatchCount.setText(mContext.getString(R.string.label_crowd_count___cv,
                exposure.getWatchCount()));
        long millis = 0;
        if (exposure.getEndAt() == null) {
            tvTimeEndHint.setVisibility(View.GONE);
        } else {
            tvTimeEndHint.setVisibility(View.VISIBLE);
            millis = exposure.getEndAt()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
            if (millis <= 0) {
                tvTimeEndHint.setText("已结束");
                tvTimeEndHint.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
            } else {
                String str = "仅剩";
                int days = (int) (millis / (1000 * 60 * 60 * 24));
                long leftMillis = millis % (1000 * 60 * 60 * 24);
                int hours = (int) (leftMillis / (1000 * 60 * 60));
                leftMillis %= 1000 * 60 * 60;
                int minutes = (int) (leftMillis / (1000 * 60));
                leftMillis %= 1000 * 60;
                int seconds = (int) (leftMillis / 1000);
                if (days > 0) {
                    str = str.concat(days + "天");
                } else if (hours > 0) {
                    str = str.concat(hours + "小时");
                } else if (minutes > 0) {
                    str = str.concat(hours + "分钟");
                } else {
                    str = str.concat(seconds + "秒钟");
                }
                tvTimeEndHint.setText(str);
                tvTimeEndHint.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        }
        if (millis > 0 || exposure.getStartAt() == null) {
            tvStartAt.setVisibility(View.GONE);
        } else {
            tvStartAt.setVisibility(View.VISIBLE);
            tvStartAt.setText(exposure.getStartAt()
                    .toString("MMdd", Locale.getDefault()) + "期");
        }
        exposureHeaderView.setVisibility(millis > 0 && position == 0 ? View.VISIBLE : View.GONE);
        if (millis <= 0 || CommonUtil.isCollectionEmpty(exposure.getWorks())) {
            imgArrowUp.setVisibility(View.GONE);
            workRecyclerView.setVisibility(View.GONE);
        } else {
            imgArrowUp.setVisibility(View.VISIBLE);
            workRecyclerView.setVisibility(View.VISIBLE);
            workAdapter.setWorks(exposure.getWorks());
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;

        public SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 12);
            this.middleSpace = CommonUtil.dp2px(context, 7);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = middleSpace;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                left = space;
            } else if (position == parent.getAdapter()
                    .getItemCount() - 1) {
                right = space;
            }
            outRect.set(left, 0, right, 0);
        }
    }
}