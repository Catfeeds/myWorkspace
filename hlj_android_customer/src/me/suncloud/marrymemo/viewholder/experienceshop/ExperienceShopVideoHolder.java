package me.suncloud.marrymemo.viewholder.experienceshop;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.experienceshop.VideoPageAdapter;
import me.suncloud.marrymemo.model.experience.StoreVideo;
import me.suncloud.marrymemo.util.Util;

/**
 * experience_shop_item_video
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopVideoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.view_pager_video)
    public SliderLayout viewPagerVideo;
    @BindView(R.id.tv_current_page)
    public TextView tvCurrentPage;
    @BindView(R.id.tv_page_count)
    public TextView tvPageCount;
    @BindView(R.id.layout_indicator)
    public LinearLayout layoutIndicator;

    private VideoPageAdapter videoPageAdapter;
    private Context mContext;
    private int allCount;

    public ExperienceShopVideoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        videoPageAdapter = new VideoPageAdapter(mContext);
        viewPagerVideo.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (allCount == 0) {
                    layoutIndicator.setVisibility(View.GONE);
                    return;
                } else {
                    layoutIndicator.setVisibility(View.VISIBLE);
                }
                tvCurrentPage.setText(String.valueOf((position % allCount) + 1));
                tvPageCount.setText(String.valueOf(allCount));
                super.onPageSelected(position);
            }
        });
    }

    public void setVideo(List<StoreVideo> videos) {
        if (videos == null) {
            return;
        }
        allCount = videos.size();
        if (viewPagerVideo.getLayoutParams().height <= 0) {
            viewPagerVideo.getLayoutParams().height = CommonUtil.getDeviceSize(mContext).x * 9 /
                    16 + Util.dp2px(
                    mContext,
                    85);
        }

        videoPageAdapter.setVideos(videos);
        if (viewPagerVideo.getmViewPager()
                .getAdapter() == null) {
            viewPagerVideo.setPagerAdapter(videoPageAdapter);
        } else {
            videoPageAdapter.notifyDataSetChanged();
        }
        if (!videos.isEmpty()) {
            viewPagerVideo.startAutoCycle();
        }
    }

    public void onResume() {
        viewPagerVideo.startAutoCycle();
    }

    public void onDestroy() {
        viewPagerVideo.stopAutoCycle();
    }
}
