package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;

/**
 * 社区频道viewHolder
 * Created by mo_yu on 2016/1/5 0026.
 */
public class CommunityChannelViewHolder extends BaseViewHolder<CommunityChannel> {

    @BindView(R.id.iv_channel)
    RoundedImageView ivChannel;
    @BindView(R.id.tv_channel_name)
    TextView tvChannelName;
    @BindView(R.id.tv_channel_count)
    TextView tvChannelCount;
    @BindView(R.id.iv_tag_new)
    ImageView ivTagNew;
    private int itemWidth;
    private int imageWidth;
    public OnItemClickListener onItemClickListener;

    public CommunityChannelViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                8)) / 4 - 1;
        imageWidth = CommonUtil.dp2px(itemView.getContext(), 56);
        itemView.getLayoutParams().width = itemWidth;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityChannel communityChannel = getItem();
                if (communityChannel.getId() > 0) {
                    CommunityIntentUtil.startCommunityChannelIntent(v.getContext(),
                            communityChannel.getId());
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void setViewData(
            final Context context,
            final CommunityChannel communityChannel,
            final int position,
            int viewType) {
        try {
            HljVTTagger.buildTagger(itemView)
                    .tagName(HljTaggerName.CommunityHomeFragment.COMMUNITY_TOP_FLOOR_CLICK)
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_COMMUNITY_CHANNEL)
                    .dataId(communityChannel.getId())
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!CommonUtil.isEmpty(communityChannel.getCoverPath())) {
            String imagePath = ImagePath.buildPath(communityChannel.getCoverPath())
                    .width(imageWidth)
                    .height(imageWidth)
                    .cropPath();
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivChannel);
        } else {
            ivChannel.setImageResource(R.mipmap.icon_after_pay_122_122);
        }
        tvChannelCount.setVisibility(communityChannel.getType() < 0 ? View.GONE : View.VISIBLE);
        tvChannelCount.setText(String.format("+%s",
                String.valueOf(communityChannel.getTodayWatchCount())));
        tvChannelName.setText(TextUtils.isEmpty(communityChannel.getTitle()) ? "" :
                communityChannel.getTitle());
        int type = communityChannel.getType();
        ivTagNew.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
    }
}
