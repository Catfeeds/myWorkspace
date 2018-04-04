package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;


/**
 * Created by mo_yu on 2018/3/9.我的新娘圈（最近常逛）
 */

public class MyCommunityChannelAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<CommunityChannel> list;
    private static final int ITEM_TYPE = 1;
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;
    private static final int HEADER_TYPE = 4;
    private int itemWidth;
    private int itemHeight;
    private boolean hasHeader;
    private boolean hasFooter;
    private OnChannelClickListener onChannelClickListener;

    public MyCommunityChannelAdapter(
            Context context) {
        this.context = context;
        itemWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                45)) / 2.8f);
        itemHeight = (itemWidth - CommonUtil.dp2px(context, 14)) * 106 / 200 + CommonUtil.dp2px(
                context,
                14);
    }

    public void setList(List<CommunityChannel> list) {
        this.list = list;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public void setOnChannelClickListener(OnChannelClickListener onChannelClickListener) {
        this.onChannelClickListener = onChannelClickListener;
    }

    public interface OnChannelClickListener {
        void onChannelClick();
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_community_channel_header, parent, false));
            case FOOTER_TYPE:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_community_channel_footer, parent, false));
            case ITEM_TYPE:
                return new MyChannelViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_community_channel_list_item, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == ITEM_TYPE) {
            holder.setView(context,
                    list.get(hasHeader ? position - 1 : position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(list == null ? 0 : list.size(),
                4) + (hasFooter ? 1 : 0) + (hasHeader ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return HEADER_TYPE;
        } else if (hasFooter && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    class MyChannelViewHolder extends BaseViewHolder<CommunityChannel> {
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;
        @BindView(R.id.tv_channel_count)
        TextView tvChannelCount;
        @BindView(R.id.card_view)
        LinearLayout cardView;

        MyChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            cardView.getLayoutParams().width = itemWidth;
            cardView.getLayoutParams().height = itemHeight;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityChannel communityChannel = getItem();
                    if (communityChannel.getId() > 0) {
                        if (communityChannel.getId() > 0) {
                            CommunityIntentUtil.startCommunityChannelIntent(view.getContext(),
                                    communityChannel.getId());
                        }
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, CommunityChannel communityChannel, int position, int viewType) {
            try {
                HljVTTagger.buildTagger(itemView)
                        .tagName(HljTaggerName.CommunityHomeFragment.RECENT_CHANNEL_CLICK)
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_COMMUNITY_CHANNEL)
                        .dataId(communityChannel.getId())
                        .hitTag();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvChannelName.setText(TextUtils.isEmpty(communityChannel.getTitle()) ? "" :
                    communityChannel.getTitle());
            tvChannelCount.setText(String.format("今日+%s",
                    String.valueOf(communityChannel.getTodayWatchCount())));

        }

    }

    class FooterViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_more_name)
        TextView tvMoreName;
        @BindView(R.id.iv_more_channel)
        ImageView ivMoreChannel;
        @BindView(R.id.cv_more_channel)
        LinearLayout cvMoreChannel;

        FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cvMoreChannel.getLayoutParams().width = Math.round((itemHeight - CommonUtil.dp2px
                    (view.getContext(),
                    14)) * 160 / 106) + CommonUtil.dp2px(view.getContext(), 14);
            cvMoreChannel.getLayoutParams().height = itemHeight;
            cvMoreChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChannelClickListener != null) {
                        onChannelClickListener.onChannelClick();
                    }
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {

        }
    }

    class HeaderViewHolder extends BaseViewHolder {
        @BindView(R.id.header_layout)
        RelativeLayout headerLayout;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            headerLayout.getLayoutParams().height = itemHeight;
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {

        }
    }
}
