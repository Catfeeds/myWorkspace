package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class SocialChannelHomeRecommendChannelViewHolder extends BaseViewHolder<CommunityChannel> {

    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.tv_join)
    TextView tvJoin;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.layout_recommend)
    LinearLayout layoutRecommend;

    private Context mContext;
    private int logoSize;
    private onJoinClickListener onJoinClickListener;

    public SocialChannelHomeRecommendChannelViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        logoSize = CommonUtil.dp2px(mContext, 44);
    }

    public void setOnJoinClickListener(
            SocialChannelHomeRecommendChannelViewHolder.onJoinClickListener onJoinClickListener) {
        this.onJoinClickListener = onJoinClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext, final CommunityChannel channel, final int position, int viewType) {
        if (channel == null) {
            return;
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(channel);
            }
        });
        layoutRecommend.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        tvName.setText(channel.getTitle());
        tvDes.setText(channel.getDesc());
        tvWatchCount.setText("今日+" + String.valueOf(channel.getTodayWatchCount()));
        Glide.with(mContext)
                .load(ImagePath.buildPath(channel.getCoverPath())
                        .width(logoSize)
                        .height(logoSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgLogo);
        tvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onJoinClickListener != null) {
                    onJoinClickListener.onJoinClick(channel);
                }
            }
        });
    }

    public void onItemClick(CommunityChannel channel) {
        if (channel == null) {
            return;
        }
        CommunityIntentUtil.startCommunityChannelIntent(mContext,
                channel.getId());
    }

    public interface onJoinClickListener {
        void onJoinClick(CommunityChannel channel);
    }
}
