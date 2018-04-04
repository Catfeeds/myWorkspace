package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;

/**
 * 专栏排行榜
 * Created by chen_bin on 2016/12/29 0029.
 */
public class SubPageRankRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<TopicUrl> topics;
    private int[] icons;
    private LayoutInflater inflater;
    private int imageWidth;
    private int imageHeight;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public SubPageRankRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageWidth = CommonUtil.dp2px(context, 115);
        this.imageHeight = CommonUtil.dp2px(context, 86);
        TypedArray typedArray = context.getResources()
                .obtainTypedArray(R.array.ranking_icons);
        int len = typedArray.length();
        this.icons = new int[len];
        for (int i = 0; i < len; i++) {
            this.icons[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
    }

    public void setTopics(List<TopicUrl> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    public void addTopics(List<TopicUrl> topics) {
        if (!CommonUtil.isCollectionEmpty(topics)) {
            int start = getItemCount() - getFooterViewCount();
            this.topics.addAll(topics);
            notifyItemRangeInserted(start, topics.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(topics);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new SubPageRankViewHolder(inflater.inflate(R.layout.common_rank_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, topics.get(position), position, viewType);
                break;
        }
    }

    public class SubPageRankViewHolder extends BaseViewHolder<TopicUrl> {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.img_rank)
        ImageView imgRank;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.line_layout)
        View lineLayout;

        public SubPageRankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgCover.getLayoutParams().width = imageWidth;
            imgCover.getLayoutParams().height = imageHeight;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopicUrl topic = getItem();
                    if (topic != null && topic.getId() > 0) {
                        Intent intent = new Intent(context, SubPageDetailActivity.class);
                        intent.putExtra("id", topic.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context, final TopicUrl topic, final int position, int viewType) {
            if (topic == null) {
                return;
            }
            lineLayout.setVisibility(position < topics.size() - 1 ? View.VISIBLE : View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(topic.getListImg())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
            if (position < icons.length) {
                imgRank.setVisibility(View.VISIBLE);
                imgRank.setImageResource(icons[position]);
            } else {
                imgRank.setVisibility(View.GONE);
            }
            tvTitle.setText(topic.getGoodTitle());
            tvContent.setText(topic.getSummary());
        }
    }
}