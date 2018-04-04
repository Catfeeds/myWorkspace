package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.subpage.MarkedKeyword;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.viewholder.SubPageKeywordsViewHolder;
import me.suncloud.marrymemo.adpter.finder.viewholder.SubPageViewHolder;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * 发现页列表适配器
 * Created by chen_bin on 2016/7/25 0025.
 */
public class SubPageRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<TopicUrl> topics;
    private List<MarkedKeyword> keywords; //8个推荐标签
    private boolean isShowBeginAt;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_LIST = 1;
    private static final int ITEM_TYPE_KEYWORDS = 2;
    private static final int ITEM_TYPE_FOOTER = 3;

    private static final int KEYWORDS_POSITION = 1;

    public SubPageRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<TopicUrl> getTopics() {
        return topics;
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
            if (start - getHeaderViewCount() > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    private int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    private int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    private int getKeywordsViewCount() {
        return !CommonUtil.isCollectionEmpty(topics) && !CommonUtil.isCollectionEmpty(keywords) ?
                1 : 0;
    }

    public void setKeywords(List<MarkedKeyword> keywords) {
        this.keywords = keywords;
    }

    public void setShowBeginAt(boolean showBeginAt) {
        this.isShowBeginAt = showBeginAt;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + getKeywordsViewCount() + CommonUtil
                .getCollectionSize(
                topics);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (getKeywordsViewCount() > 0 && position == KEYWORDS_POSITION +
                getHeaderViewCount()) {
            return ITEM_TYPE_KEYWORDS;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE_KEYWORDS:
                return new SubPageKeywordsViewHolder(inflater.inflate(R.layout
                                .sub_page_keywords_item,
                        parent,
                        false));
            default:
                return new SubPageViewHolder(inflater.inflate(R.layout.sub_page_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_KEYWORDS:
                if (holder instanceof SubPageKeywordsViewHolder) {
                    SubPageKeywordsViewHolder keywordsViewHolder = (SubPageKeywordsViewHolder)
                            holder;
                    keywordsViewHolder.setView(context, keywords, position, viewType);
                }
                break;
            case ITEM_TYPE_LIST:
                if (holder instanceof SubPageViewHolder) {
                    int index = position - getHeaderViewCount();
                    if (index > KEYWORDS_POSITION) { //关键词的位置固定在list的第2位
                        index = index - getKeywordsViewCount();
                    }
                    TopicUrl topic = topics.get(index);
                    final SubPageViewHolder subPageViewHolder = (SubPageViewHolder) holder;
                    subPageViewHolder.setShowBottomLineView(index < topics.size() - 1);
                    subPageViewHolder.setView(context, topic, index, viewType);
                    subPageViewHolder.setShowBeginAt(isShowBeginAt && topic.getBeginAt() != null
                            && (index == 0 || !TimeUtil.isSameDay(
                            topics.get(index - 1)
                                    .getBeginAt(),
                            topic.getBeginAt())), topic);
                }
                break;
        }
    }
}