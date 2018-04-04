package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityQuestionViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityThreadViewHolder;

/**
 * 推荐feeds adapter
 * Created by chen_bin on 2018/3/15 0015.
 */
public class SimilarWeddingFeedsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View footerView;

    private List<CommunityFeed> feeds;

    private LayoutInflater inflater;

    private final static int ITEM_TYPE_THREAD = 0;
    private final static int ITEM_TYPE_QA_QUESTION = 1;
    private final static int ITEM_TYPE_EMPTY = 2;
    private final static int ITEM_TYPE_FOOTER = 3;

    public SimilarWeddingFeedsAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<CommunityFeed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<CommunityFeed> feeds) {
        this.feeds = feeds;
        notifyDataSetChanged();
    }

    public void addFeeds(List<CommunityFeed> feeds) {
        if (!CommonUtil.isCollectionEmpty(feeds)) {
            int start = getItemCount() - getFooterViewCount();
            this.feeds.addAll(feeds);
            notifyItemRangeInserted(start, feeds.size());
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
        return getFooterViewCount() + CommonUtil.getCollectionSize(feeds);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return getFeedItemType(position);
        }
    }

    private int getFeedItemType(int position) {
        CommunityFeed feed = getItem(position);
        switch (feed.getEntityType()) {
            case CommunityFeed.COMMUNITY_THREAD:
                return ITEM_TYPE_THREAD;
            case CommunityFeed.QA_QUESTION:
                return ITEM_TYPE_QA_QUESTION;
            default:
                return ITEM_TYPE_EMPTY;
        }
    }

    private CommunityFeed getItem(int position) {
        return feeds.get(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE_EMPTY:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout.empty_place_holder___cm,
                        parent,
                        false));
            case ITEM_TYPE_THREAD:
                return new CommunityThreadViewHolder(parent);
            case ITEM_TYPE_QA_QUESTION:
                return new CommunityQuestionViewHolder(parent);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_THREAD:
            case ITEM_TYPE_QA_QUESTION:
                holder.setView(context, getItem(position).getEntity(), position, viewType);
                break;
            default:
                break;
        }
    }

}
