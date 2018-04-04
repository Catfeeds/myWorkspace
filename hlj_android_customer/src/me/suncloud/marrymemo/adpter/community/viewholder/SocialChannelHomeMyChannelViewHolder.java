package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.slider.library.Indicators.CirclePageExIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.SocialChannelHomeViewPagerAdapter;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class SocialChannelHomeMyChannelViewHolder extends BaseViewHolder<List<CommunityChannel>> {

    @BindView(R.id.view_pager)
    OverScrollViewPager viewPager;
    @BindView(R.id.indicator)
    CirclePageExIndicator indicator;

    private SocialChannelHomeViewPagerAdapter socialChannelHomeViewPagerAdapter;
    private Context mContext;
    private List<CommunityChannel> channelList;
    private int heightOffset;
    public SocialChannelHomeMyChannelViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        channelList = new ArrayList<>();
        socialChannelHomeViewPagerAdapter = new SocialChannelHomeViewPagerAdapter(mContext,
                channelList);
        viewPager.setOverable(false);
        viewPager.getOverscrollView()
                .setAdapter(socialChannelHomeViewPagerAdapter);
        indicator.setViewPager(viewPager.getOverscrollView());
        int width = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                52)) / 3 + CommonUtil.dp2px(mContext, 14 - 1);
        heightOffset = width * 160 / 214 + CommonUtil.dp2px(mContext, 18 + 10+8-6-6);
    }

    @Override
    protected void setViewData(
            Context mContext, List<CommunityChannel> items, int position, int viewType) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            this.channelList.clear();
            this.channelList.addAll(items);
        }
        int height = 0;
        if (CommonUtil.getCollectionSize(this.channelList) > 3) {
            height = heightOffset * 2 + CommonUtil.dp2px(mContext, 20);
        } else {
            height = heightOffset + CommonUtil.dp2px(mContext, 10);
        }
        indicator.setVisibility(CommonUtil.getCollectionSize(this.channelList) > 6 ? View.VISIBLE
                : View.GONE);
        viewPager.getLayoutParams().height = height;
        viewPager.postInvalidate();
        socialChannelHomeViewPagerAdapter.notifyDataSetChanged();
    }
}
