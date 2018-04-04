package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityPosterLayoutViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityQuestionViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.CommunityThreadViewHolder;
import me.suncloud.marrymemo.adpter.community.viewholder.WeddingCatalogViewHolder;
import me.suncloud.marrymemo.model.community.WeddingCatalog;

/**
 * 结婚宝典feeds列表
 * Created by chen_bin on 2018/3/16 0016.
 */
public class WeddingBibleFeedsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View footerView;

    private List<CommunityFeed> feeds;
    private List<WeddingCatalog> catalogs;
    private Poster poster;

    private LayoutInflater inflater;

    private int limit = 2; //目录默认为2条

    private final static int ITEM_TYPE_CATALOG = 0;
    private final static int ITEM_TYPE_CATALOG_MORE = 1;
    private final static int ITEM_TYPE_SECTION_INDEX = 2;
    private final static int ITEM_TYPE_POSTER = 3;
    private final static int ITEM_TYPE_THREAD = 4;
    private final static int ITEM_TYPE_QA_QUESTION = 5;
    private final static int ITEM_TYPE_EMPTY = 6;
    private final static int ITEM_TYPE_FOOTER = 7;

    public WeddingBibleFeedsAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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

    public int getCatalogViewsCount() {
        int catalogsCount = CommonUtil.getCollectionSize(catalogs);
        return catalogsCount > limit ? limit : catalogsCount;
    }

    public int getMoreCatalogViewCount() {
        int catalogsCount = CommonUtil.getCollectionSize(catalogs);
        return catalogsCount > limit ? 1 : 0;
    }

    public void setCatalogs(List<WeddingCatalog> catalogs) {
        this.catalogs = catalogs;
    }

    public int getSectionIndexViewCount() {
        return 1;
    }

    public int getPosterViewCount() {
        return poster != null ? 1 : 0;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + getCatalogViewsCount() + getMoreCatalogViewCount() +
                getSectionIndexViewCount() + getPosterViewCount() + CommonUtil.getCollectionSize(
                feeds);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (position < getCatalogViewsCount()) {
            return ITEM_TYPE_CATALOG;
        } else if (getMoreCatalogViewCount() > 0 && position == getCatalogViewsCount()) {
            return ITEM_TYPE_CATALOG_MORE;
        } else if (getSectionIndexViewCount() > 0 && position == getCatalogViewsCount() +
                getMoreCatalogViewCount()) {
            return ITEM_TYPE_SECTION_INDEX;
        } else if (getPosterViewCount() > 0 && position == getCatalogViewsCount() +
                getMoreCatalogViewCount() + getSectionIndexViewCount()) {
            return ITEM_TYPE_POSTER;
        } else {
            return getFeedItemType(position);
        }
    }

    private int getFeedItemType(int position) {
        CommunityFeed feed = getFeedItem(position);
        switch (feed.getEntityType()) {
            case CommunityFeed.COMMUNITY_THREAD:
                return ITEM_TYPE_THREAD;
            case CommunityFeed.QA_QUESTION:
                return ITEM_TYPE_QA_QUESTION;
            default:
                return ITEM_TYPE_EMPTY;
        }
    }

    private CommunityFeed getFeedItem(int position) {
        return feeds.get(position - getCatalogViewsCount() - getMoreCatalogViewCount() -
                getSectionIndexViewCount() - getPosterViewCount());
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
            case ITEM_TYPE_SECTION_INDEX:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout
                                .wedding_bible_section_index,
                        parent,
                        false));
            case ITEM_TYPE_CATALOG:
                return new WeddingCatalogViewHolder(parent);
            case ITEM_TYPE_CATALOG_MORE:
                ExtraBaseViewHolder catalogMoreViewHolder = new ExtraBaseViewHolder(inflater
                        .inflate(
                        R.layout.wedding_catalog_more_layout,
                        parent,
                        false));
                catalogMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int catalogsCount = CommonUtil.getCollectionSize(catalogs);
                        int tempLimit = limit;
                        limit = catalogsCount;
                        notifyItemRemoved(tempLimit);
                        notifyItemRangeInserted(tempLimit, catalogsCount - tempLimit);
                    }
                });
                return catalogMoreViewHolder;
            case ITEM_TYPE_POSTER:
                return new CommunityPosterLayoutViewHolder(parent);
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
            case ITEM_TYPE_CATALOG:
                holder.setView(context, catalogs.get(position), position, viewType);
                break;
            case ITEM_TYPE_POSTER:
                holder.setView(context, poster, position, viewType);
                break;
            case ITEM_TYPE_THREAD:
            case ITEM_TYPE_QA_QUESTION:
                holder.setView(context, getFeedItem(position).getEntity(), position, viewType);
                break;
        }
    }
}