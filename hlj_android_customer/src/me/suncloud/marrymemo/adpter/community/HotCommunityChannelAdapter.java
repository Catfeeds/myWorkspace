package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityChannelViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;

/**
 * Created by mo_yu on 2018/3/13.热门新娘圈
 */

public class HotCommunityChannelAdapter extends RecyclerView
        .Adapter<BaseViewHolder<CommunityChannel>> {

    private Context context;
    private List<CommunityChannel> list;
    public static final int ITEM_TYPE = 0;

    public HotCommunityChannelAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<CommunityChannel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<CommunityChannel> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.community_hot_channel_list_item, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CommunityChannel> holder, int position) {
        if (holder instanceof ChannelViewHolder) {
            holder.setView(context, list.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }

    public class ChannelViewHolder extends TrackerCommunityChannelViewHolder {

        @BindView(R.id.iv_channel)
        RoundedImageView ivChannel;
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;
        @BindView(R.id.tv_channel_count)
        TextView tvChannelCount;
        @BindView(R.id.card_view)
        LinearLayout cardView;
        private int itemWidth;
        private int itemHeight;
        private int imageWidth;

        public ChannelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            final Activity activity = (Activity) itemView.getContext();
            itemWidth = Math.round((CommonUtil.getDeviceSize(activity).x - CommonUtil.dp2px
                    (activity,
                    18)) / 3f);
            itemHeight = (itemWidth - CommonUtil.dp2px(activity,
                    14)) * 100 / 108 + CommonUtil.dp2px(activity, 14);
            imageWidth = CommonUtil.dp2px(activity, 32);
            cardView.getLayoutParams().width = itemWidth;
            cardView.getLayoutParams().height = itemHeight;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityChannel communityChannel = getItem();
                    if (communityChannel.getId() > 0) {
                        CommunityIntentUtil.startCommunityChannelIntent(view.getContext(),
                                communityChannel.getId());
                    }
                }
            });
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityChannel item, int position, int viewType) {
            tvChannelName.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            tvChannelCount.setText(String.format("今日+%s",
                    String.valueOf(item.getTodayWatchCount())));
            String imagePath = ImagePath.buildPath(item.getCoverPath())
                    .width(imageWidth)
                    .height(imageWidth)
                    .cropPath();
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivChannel);
        }
    }
}
