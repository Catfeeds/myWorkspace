package me.suncloud.marrymemo.view.newsearch;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import me.suncloud.marrymemo.R;


/**
 * Created by hua_rong on 2018/1/4
 * 搜索分类下拉popWindoiw
 */

public class SearchMenuPopWindow extends PopupWindow {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.menu_layout)
    RelativeLayout menuLayout;
    @BindView(R.id.touch_view)
    View touchView;

    private OnSearchItemListener onSearchItemListener;
    private String category;//当前选中的分类
    private SearchSortAdapter searchSortAdapter;
    private Context context;
    private List<TipSearchType> searchResults;


    public SearchMenuPopWindow(Context context, List<TipSearchType> searchResults) {
        super(context);
        this.context = context;
        this.searchResults = searchResults;
        initView();
    }


    private void initView() {
        View view = View.inflate(context, R.layout.search_sort_menu_layout, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,
                android.R.color.transparent)));
        setOutsideTouchable(true);
        setFocusable(true);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        searchSortAdapter = new SearchSortAdapter();
        recyclerView.addItemDecoration(new SearchItemDecoration(context));
        recyclerView.setAdapter(searchSortAdapter);
    }


    @OnTouch(R.id.touch_view)
    boolean onTouchView(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent
                .ACTION_UP) {
            dismiss();
        }
        return true;
    }

    @Override
    public void showAsDropDown(View anchor) {
        TranslateAnimation animation = new TranslateAnimation(0,
                0,
                CommonUtil.dp2px(context, -getPopHeight()),
                0);
        animation.setDuration(400);
        menuLayout.setBackgroundResource(R.color.transparent);
        recyclerView.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuLayout.setBackgroundResource(R.color.transparent_black);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recyclerView.startAnimation(animation);
        super.showAsDropDown(anchor);
    }

    private int getPopHeight() {
        int raw;
        int size = CommonUtil.getCollectionSize(searchResults);
        if (size % 3 == 0) {
            raw = size / 3;
        } else {
            raw = size / 3 + 1;
        }
        return CommonUtil.dp2px(context, raw * 40 + 22);
    }

    private class SearchItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        private SearchItemDecoration(Context context) {
            space = CommonUtil.dp2px(context, 5);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.bottom = space;
            outRect.left = space;
            outRect.right = space;
        }
    }


    public class SearchSortAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        private LayoutInflater inflater;

        SearchSortAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.new_search_hot_word_layout, parent, false);
            return new SearchSortViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            if (holder instanceof SearchSortViewHolder) {
                SearchSortViewHolder viewHolder = (SearchSortViewHolder) holder;
                viewHolder.setView(context,
                        searchResults.get(position),
                        position,
                        getItemViewType(position));
            }
        }

        @Override
        public int getItemCount() {
            return CommonUtil.getCollectionSize(searchResults);
        }
    }

    public class SearchSortViewHolder extends BaseViewHolder<TipSearchType> {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;

        public SearchSortViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.getLayoutParams().width = Math.round((CommonUtil.getDeviceSize(context).x -
                    CommonUtil.dp2px(
                    context,
                    52)) / 3);
            itemView.getLayoutParams().height = CommonUtil.dp2px(context, 30);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSearchItemListener != null) {
                        onSearchItemListener.onSearchItemClick(getItem());
                    }
                }
            });
        }

        @Override
        public void setView(Context mContext, TipSearchType item, int position, int viewType) {
            try {
                HljVTTagger.buildTagger(itemView)
                        .tagName(HljTaggerName.SEARCH_CATEGORY)
                        .atPosition(position)
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_SEARCH_CATEGORY)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_SEARCH_CATEGORY,
                                NewSearchApi.getSearchName(item.getKey()))
                        .hitTag();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.setView(mContext, item, position, viewType);
        }

        @Override
        protected void setViewData(
                Context context, TipSearchType searchCategory, int position, int viewType) {
            String searchType = searchCategory.getKey();
            tvName.setText(NewSearchApi.getSearchName(searchType));
            if (searchCategory.getDocCount() > 0) {
                tvCount.setText(String.format(Locale.getDefault(),
                        "(%s)",
                        searchCategory.getDocCount()));
                tvCount.setVisibility(View.VISIBLE);
            } else {
                tvCount.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(searchType) && searchType.equals(category)) {
                itemView.setBackgroundResource(R.drawable.sp_r2_stroke_primary_solid_white);
                tvName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvCount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                itemView.setBackgroundResource(R.drawable.sp_r2_background2);
                tvName.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
                tvCount.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));
            }
        }
    }

    public void setOnSearchItemListener(OnSearchItemListener onSearchItemListener) {
        this.onSearchItemListener = onSearchItemListener;
    }

    public interface OnSearchItemListener {
        void onSearchItemClick(TipSearchType searchType);
    }

    public void setSearchResults(List<TipSearchType> searchResults) {
        this.searchResults = searchResults;
    }

    public void setSelectCategory(String category) {
        this.category = category;
        searchSortAdapter.notifyDataSetChanged();
    }

}

