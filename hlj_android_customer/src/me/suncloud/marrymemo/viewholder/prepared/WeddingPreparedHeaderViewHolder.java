package me.suncloud.marrymemo.viewholder.prepared;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.prepared.RecyclerViewPagerAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.WeddingPreparedListModelItem;
import me.suncloud.marrymemo.model.prepared.WeddingPreparedCategoryMode;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.RecyclerViewPager;

/**
 * Created by jinxin on 2017/9/26 0026.
 */

public class WeddingPreparedHeaderViewHolder extends BaseViewHolder {

    @BindView(R.id.wedding_prepaerlist_type_pager)
    RecyclerViewPager viewPager;
    @BindView(R.id.pager_holder_layout)
    RelativeLayout pagerHolderLayout;
    @BindView(R.id.strategy_content)
    LinearLayout strategyContent;
    @BindView(R.id.strategy_load_more)
    TextView strategyLoadMore;
    @BindView(R.id.strategy_load_more_layout)
    LinearLayout strategyLoadMoreLayout;
    @BindView(R.id.layout_strategy)
    LinearLayout layoutStrategy;

    private int viewPagerHeight;
    private Context mContext;
    private RecyclerViewPagerAdapter viewPagerAdapter;
    private int screenWidth;
    private int mSelectPosition;
    private int offset;
    private OnViewPagerListener onViewPagerListener;
    private List<WeddingPreparedListModelItem> strategyList;
    private List<WeddingPreparedCategoryMode> categoryModeList;
    private City mCity;
    private int MINHEIGHT;
    private int MAXHEIGHT;
    private long categoryId;

    public WeddingPreparedHeaderViewHolder(
            View itemView,
            int mSelectPosition,
            int offset,
            List<WeddingPreparedCategoryMode> categoryModeList) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        this.mSelectPosition = mSelectPosition;
        this.offset = offset;
        this.categoryModeList = categoryModeList;

        initConstant();
        initWidget();
    }

    private void initConstant() {
        strategyList = new ArrayList<>();
        mCity = Session.getInstance()
                .getMyCity(mContext);
        MINHEIGHT = CommonUtil.dp2px(mContext, 44);
        MAXHEIGHT = CommonUtil.dp2px(mContext, 60);

        viewPagerHeight = CommonUtil.dp2px(mContext, 200) + CommonUtil.getStatusBarHeight(mContext);
        pagerHolderLayout.getLayoutParams().height = viewPagerHeight;
        screenWidth = CommonUtil.getDeviceSize(mContext).x;
        viewPagerAdapter = new RecyclerViewPagerAdapter(mContext, categoryModeList, screenWidth) {
            @Override
            public void snapViewToCenter(View view) {
                int viewCenter = (view.getLeft() + view.getRight()) / 2;
                int scrollNeed = viewCenter - screenWidth / 2;
                viewPager.setScrollType(RecyclerViewPager.SwipeType.SWIPE);
                viewPager.smoothScrollBy(scrollNeed, 0);
            }
        };
    }

    private void initWidget() {
        viewPager.addSnapListener(new RecyclerViewPager.SnapRecyclerViewListener() {
            @Override
            public void onCenterItemSnapped(
                    int centerHolderPosition,
                    RecyclerView.ViewHolder centerHolder,
                    RecyclerViewPager.SwipeType scrollType) {

                LinearLayoutManager manager = (LinearLayoutManager) viewPager.getLayoutManager();
                if (manager != null) {
                    View centerView = manager.findViewByPosition(centerHolderPosition);
                    View firstView = manager.findViewByPosition(manager
                            .findFirstVisibleItemPosition());
                    if (centerView != null && firstView != null) {
                        offset = centerView.getLeft() - firstView.getLeft();
                    }
                }
                mSelectPosition = centerHolderPosition;
                if (onViewPagerListener != null) {
                    onViewPagerListener.onViewPagerSnapListener(centerHolderPosition,
                            centerHolderPosition,
                            offset);
                }
            }

            @Override
            public void onLeftItemSnapped(
                    int holderPosition, RecyclerView.ViewHolder leftHolder) {

            }

            @Override
            public void onRightItemSnapped(
                    int holderPosition, RecyclerView.ViewHolder rightHolder) {

            }
        });
        viewPager.setScrollType(RecyclerViewPager.SwipeType.TAP);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setSpace(screenWidth * 3 / 80);
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public RecyclerViewPager getViewPager() {
        return viewPager;
    }

    public void setStrategyVisible(boolean show) {
        layoutStrategy.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setOnViewPagerListener(OnViewPagerListener onViewPagerListener) {
        this.onViewPagerListener = onViewPagerListener;
    }

    public void notifyItemCategoryChanged() {
        viewPager.setCenterViewAtPosition(mSelectPosition);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.strategy_load_more, R.id.strategy_load_more_layout})
    void onLoadMore() {
        String text = strategyLoadMore.getText()
                .toString();
        if (text.equalsIgnoreCase(mContext.getString(R.string.label_collapse))) {
            showMore(false);
        } else if (text.equalsIgnoreCase(mContext.getString(R.string.label_all_info))) {
            showMore(true);
        }
    }

    public void setStrategyList(
            List<WeddingPreparedListModelItem> strategyList,
            boolean isLoadMore) {
        if (strategyList == null) {
            return;
        }

        this.strategyList.clear();
        this.strategyList.addAll(strategyList);

        addStrategyView(isLoadMore);
    }

    private void addStrategyView(boolean isLoadMore) {
        strategyContent.removeAllViews();
        int size = strategyList.size();
        strategyLoadMoreLayout.setVisibility(size <= 3 ? View.GONE : View.VISIBLE);
        setLoadMoreTextAndDrawable(isLoadMore);
        //默认只加载三个，点击更多才展开
        List<WeddingPreparedListModelItem> initStrategyList = new ArrayList<>();
        //如果是全部加载的状态，就加载全部，否则只默认加载钱三个
        if (isLoadMore) {
            initStrategyList.addAll(strategyList);
        } else {
            if (size > 3) {
                List<WeddingPreparedListModelItem> tempList = strategyList.subList(0, 3);
                initStrategyList.addAll(tempList);
            } else {
                initStrategyList.addAll(strategyList);
            }
        }

        for (int i = 0, initSize = initStrategyList.size(); i < initSize; i++) {
            WeddingPreparedListModelItem item = initStrategyList.get(i);
            View itemView = getView();
            ViewHolder holder = (ViewHolder) itemView.getTag();
            holder.setItem(item, i != initSize - 1);
            strategyContent.addView(itemView);
        }
    }

    private void setLoadMoreTextAndDrawable(boolean isLoad) {
        strategyLoadMore.setText(isLoad ? mContext.getString(R.string.label_collapse) : mContext
                .getString(
                R.string.label_all_info));
        Drawable drawable = mContext.getResources()
                .getDrawable(isLoad ? R.mipmap.icon_arrow_up_primary_26_14 : R.drawable
                        .icon_arrow_down_primary_26_14);
        strategyLoadMore.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    private void showMore(boolean more) {
        setLoadMoreTextAndDrawable(more);
        if (strategyContent.getChildAt(2) != null) {
            View line = strategyContent.getChildAt(2)
                    .findViewById(R.id.strategy_item_line);
            line.setBackgroundResource(more ? R.drawable.image_dashline : R.color.colorLine);
        }
        if (more) {
            loadMore();
        } else {
            storeMore();
        }
        if (onViewPagerListener != null) {
            onViewPagerListener.onLoadMoreClickListener(categoryId, more);
        }
    }

    //加载更多
    private void loadMore() {
        //加载更多一定是从第三个开始加载的
        for (int i = 3, size = strategyList.size(); i < size; i++) {
            View itemView = strategyContent.getChildAt(i);
            WeddingPreparedListModelItem item = strategyList.get(i);
            if (itemView == null) {
                itemView = getView();
                strategyContent.addView(itemView);
            }
            ViewHolder holder = (ViewHolder) itemView.getTag();
            holder.setItem(item, i != size - 1);
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    //收起
    private void storeMore() {
        for (int count = strategyContent.getChildCount(), i = count - 1; i > 2; i--) {
            View itemView = strategyContent.getChildAt(i);
            itemView.setVisibility(View.GONE);
        }
    }

    private View getView() {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.strategy_item, null);
        WeddingPreparedHeaderViewHolder.ViewHolder holder = new WeddingPreparedHeaderViewHolder
                .ViewHolder(
                view);
        view.setTag(holder);
        return view;
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

    public View getPagerHolderLayout() {
        return pagerHolderLayout;
    }


    @Override
    protected void setViewData(
            Context mContext, Object item, int position, int viewType) {
    }

    class ViewHolder {
        @BindView(R.id.strategy_item_line)
        View strategyItemLine;
        @BindView(R.id.strategy_item_text)
        TextView strategyItemText;
        @BindView(R.id.strategy_item_layout)
        RelativeLayout strategyItemLayout;

        View itemView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }

        public void setItem(final WeddingPreparedListModelItem item, boolean dashLine) {
            strategyItemText.setText(JSONUtil.isEmpty(item.getTitle()) ? "" : item.getTitle());
            int lines = strategyItemText.getLineCount();
            LinearLayout.LayoutParams params;
            if (lines == 2) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        MAXHEIGHT);
            } else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        MINHEIGHT);
            }
            strategyItemLayout.setLayoutParams(params);
            if (dashLine) {
                strategyItemLine.setBackgroundResource(R.drawable.image_dashline);
            } else {
                strategyItemLine.setBackgroundResource(R.color.colorLine);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goLink(item);
                }
            });
        }
    }

    public interface OnViewPagerListener {
        void onViewPagerSnapListener(int centerPosition, int selectPosition, int offset);

        void onLoadMoreClickListener(long categoryId, boolean isExpand);
    }
}
