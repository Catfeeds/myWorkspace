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
import com.hunliji.hljcommonlibrary.models.Poster;
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
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;

/**
 * 社区频道poster viewHolder
 * Created by mo_yu on 2016/1/5 0026.
 */
public class CommunityTopPosterViewHolder extends BaseViewHolder<Poster> {

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

    public CommunityTopPosterViewHolder(View itemView) {
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
                Poster poster = getItem();
                BannerUtil.bannerJump(v.getContext(), poster, null);
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, Poster poster, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(itemView)
                    .tagName(HljTaggerName.CommunityHomeFragment.COMMUNITY_TOP_FLOOR_CLICK)
                    .poster(poster)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!CommonUtil.isEmpty(poster.getPath())) {
            String imagePath = ImagePath.buildPath(poster.getPath())
                    .width(imageWidth)
                    .height(imageWidth)
                    .cropPath();
            Glide.with(mContext)
                    .load(imagePath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivChannel);
        } else {
            ivChannel.setImageResource(R.mipmap.icon_after_pay_122_122);
        }
        tvChannelCount.setText(String.format("+%s", poster.getExtention()));
        tvChannelName.setText(TextUtils.isEmpty(poster.getTitle()) ? "" : poster.getTitle());
        ivTagNew.setVisibility(View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
