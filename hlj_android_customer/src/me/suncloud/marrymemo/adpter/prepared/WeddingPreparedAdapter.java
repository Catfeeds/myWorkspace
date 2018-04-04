package me.suncloud.marrymemo.adpter.prepared;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.WeddingPreparedListModelItem;
import me.suncloud.marrymemo.model.prepared.WeddingPreparedCategoryMode;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.viewholder.prepared.WeddingPreparedHeaderViewHolder;
import me.suncloud.marrymemo.viewholder.prepared.WeddingPreparedRecommendViewHolder;

/**
 * Created by jinxin on 2017/9/26 0026.
 */

public class WeddingPreparedAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        WeddingPreparedRecommendViewHolder.OnItemClickListener, WeddingPreparedHeaderViewHolder
        .OnViewPagerListener {

    private final int ITEM_HEADER = 11;//viewpager
    private final int ITEM_RECOMMEND = 12;//为你推荐
    private final int ITEM_FOOTER = 13;//footer

    private List<WeddingPreparedListModelItem> strategyData;
    private List<WeddingPreparedListModelItem> recommendData;
    private List<WeddingPreparedCategoryMode> categoryModeList;
    private Context mContext;
    private LayoutInflater inflater;
    private onViewHolderInflateListener onViewHolderInflateListener;
    private City mCity;
    private int selectPosition;
    private int offset;
    private LongSparseArray<Boolean> loadStateList;//用来保存每一个category下的加载更过的状态

    public WeddingPreparedAdapter(
            Context mContext,
            int selectPosition,
            int offset,
            List<WeddingPreparedListModelItem> strategyData,
            List<WeddingPreparedListModelItem> recommendData,
            List<WeddingPreparedCategoryMode> categoryModeList) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.strategyData = strategyData;
        this.recommendData = recommendData;
        this.categoryModeList = categoryModeList;
        mCity = Session.getInstance()
                .getMyCity(mContext);
        this.selectPosition = selectPosition;
        this.offset = offset;
        loadStateList = new LongSparseArray<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_HEADER:
                itemView = inflater.inflate(R.layout.wedding_prepared_view_pager_header,
                        parent,
                        false);
                WeddingPreparedHeaderViewHolder viewPagerViewHolder = new
                        WeddingPreparedHeaderViewHolder(
                        itemView,
                        selectPosition,
                        offset,
                        categoryModeList);
                if (onViewHolderInflateListener != null) {
                    onViewHolderInflateListener.onViewPagerHolderInflate(viewPagerViewHolder);
                }
                viewPagerViewHolder.setOnViewPagerListener(this);
                return viewPagerViewHolder;
            case ITEM_RECOMMEND:
                itemView = inflater.inflate(R.layout.wedding_prepared_recommend_item,
                        parent,
                        false);
                WeddingPreparedRecommendViewHolder recommendViewHolder = new
                        WeddingPreparedRecommendViewHolder(
                        itemView);
                recommendViewHolder.setOnItemClickListener(this);
                return recommendViewHolder;
            case ITEM_FOOTER:
                itemView = inflater.inflate(R.layout.hlj_foot_no_more___cm, parent, false);
                itemView.findViewById(R.id.no_more_hint)
                        .setVisibility(View.VISIBLE);
                return new ExtraBaseViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_HEADER:
                WeddingPreparedHeaderViewHolder viewPagerViewHolder =
                        (WeddingPreparedHeaderViewHolder) holder;
                viewPagerViewHolder.setStrategyVisible(!strategyData.isEmpty());
                viewPagerViewHolder.setStrategyList(strategyData,
                        loadStateList.get(viewPagerViewHolder.getCategoryId(), false));
                break;
            case ITEM_RECOMMEND:
                WeddingPreparedRecommendViewHolder recommendViewHolder =
                        (WeddingPreparedRecommendViewHolder) holder;
                WeddingPreparedListModelItem item = recommendData.get(position - 1);
                recommendViewHolder.setView(mContext, item, position - 1, ITEM_RECOMMEND);
                break;
            default:
                break;
        }
    }

    public void clearLoadState(){
        loadStateList.clear();
    }

    public void setOnViewHolderInflateListener(
            WeddingPreparedAdapter.onViewHolderInflateListener onViewHolderInflateListener) {
        this.onViewHolderInflateListener = onViewHolderInflateListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_HEADER;
        } else if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_RECOMMEND;
        }
    }

    @Override
    public int getItemCount() {
        return recommendData.size() + 1 + (recommendData.isEmpty() ? 0 : 1);
    }

    @Override
    public void onItemClickListener(WeddingPreparedListModelItem item, int position, int id) {
        switch (id) {
            case R.id.wedding_prepare_item_thread:
                goLink(item);
                break;
            case R.id.wedding_prepare_item_group:
                Intent intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                if (item != null) {
                    intent.putExtra("id", item.getEntityId());
                }
                mContext.startActivity(intent);
                break;
        }
    }

    private void goLink(WeddingPreparedListModelItem item) {
        if (item == null) {
            return;
        }
        String link = item.getLink();
        Poster poster = new Poster();
        poster.setUrl(link);
        poster.setTargetType(9);
        BannerUtil.bannerAction(mContext, poster, mCity, false, null);
    }

    @Override
    public void onViewPagerSnapListener(int centerPosition, int selectPosition, int offset) {
        if (onViewHolderInflateListener != null) {
            onViewHolderInflateListener.onViewPagerSnapListener(centerPosition,
                    selectPosition,
                    offset);
        }
    }

    @Override
    public void onLoadMoreClickListener(long categoryId, boolean isExpand) {
        loadStateList.put(categoryId, isExpand);
    }

    public interface onViewHolderInflateListener {
        void onViewPagerHolderInflate(WeddingPreparedHeaderViewHolder holder);

        void onViewPagerSnapListener(int centerPosition, int selectPosition, int offset);
    }
}
