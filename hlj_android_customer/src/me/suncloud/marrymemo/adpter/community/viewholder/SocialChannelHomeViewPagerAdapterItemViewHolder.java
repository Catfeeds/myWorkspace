package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class SocialChannelHomeViewPagerAdapterItemViewHolder {
    private final long SAME_WEDDING_DAY_ID = 256L;

    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;

    private Context mContext;
    private LayoutInflater inflater;
    private int width;
    private int height;
    private int logoSize;

    public SocialChannelHomeViewPagerAdapterItemViewHolder(Context mContext, View itemView) {
        ButterKnife.bind(this, itemView);
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        width = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                52)) / 3 + CommonUtil.dp2px(mContext, 14 - 1);
        height = width * 160 / 214 + CommonUtil.dp2px(mContext, 18 + 10 + 1 - 6 - 6);
        logoSize = CommonUtil.dp2px(mContext, 36);
    }

    public void setChannelList(List<CommunityChannel> channelList) {
        flowLayout.removeAllViews();
        if (!CommonUtil.isCollectionEmpty(channelList)) {
            for (int i = 0, size = channelList.size(); i < size; i++) {
                View gridItemView = inflater.inflate(R.layout.social_channel_home_grid_item,
                        null,
                        false);
                flowLayout.addView(gridItemView);
                GridItemViewHolder holder = new GridItemViewHolder(gridItemView);
                final CommunityChannel channel = channelList.get(i);
                setGridItem(holder, channel);
                gridItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(channel);
                    }
                });
            }
        }
    }

    public void onItemClick(CommunityChannel channel) {
        if (channel == null) {
            return;
        }
        CommunityIntentUtil.startCommunityChannelIntent(mContext,
                channel.getId());
    }

    private void setGridItem(GridItemViewHolder holder, CommunityChannel channel) {
        holder.rlContent.getLayoutParams().width = width;
        holder.rlContent.getLayoutParams().height = height;
        holder.rlContent.postInvalidate();
        holder.tvName.setText(channel.getTitle());
        holder.tvIncreaseCount.setText(mContext.getResources()
                .getString(R.string.label_today_get, String.valueOf(channel.getTodayWatchCount())));
        if (channel.getId() != SAME_WEDDING_DAY_ID) {
            Glide.with(mContext)
                    .load(ImagePath.buildPath(channel.getCoverPath())
                            .width(logoSize)
                            .height(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgLogo);
        } else {
            holder.imgLogo.setImageResource(R.drawable.icon_same_wedding_day);
        }
    }

    class GridItemViewHolder {
        @BindView(R.id.grid_view)
        View gridView;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_increase_count)
        TextView tvIncreaseCount;
        @BindView(R.id.rl_content)
        RelativeLayout rlContent;
        @BindView(R.id.img_logo)
        RoundedImageView imgLogo;
        View itemView;

        public GridItemViewHolder(View itemView) {
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
