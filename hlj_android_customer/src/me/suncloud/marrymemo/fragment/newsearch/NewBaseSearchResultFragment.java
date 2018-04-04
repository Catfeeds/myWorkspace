package me.suncloud.marrymemo.fragment.newsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewBaseSearchResultAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.newsearch.NewSearchResultActivity;


/**
 * Created by hua_rong on 18/1/10.
 * 搜索Fragment通用的基类，实现通用的方法和视图
 */

public abstract class NewBaseSearchResultFragment extends RefreshFragment implements
        OnSearchKeywordRefreshListener, NewSearchFilterViewHolder.OnSearchFilterListener,
        OnSearchMenuListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Unbinder unbinder;
    protected FooterViewHolder footerViewHolder;
    protected View footerView;
    protected View headerView;
    protected long cid;

    // 搜索参数
    protected NewSearchApi.SearchType searchType;
    protected String keyword;
    protected SearchFilter filter;
    protected String sort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            keyword = args.getString(NewSearchApi.ARG_KEY_WORD);
            searchType = (NewSearchApi.SearchType) args.getSerializable(NewSearchApi
                    .ARG_SEARCH_TYPE);
        }
        City city = Session.getInstance()
                .getMyCity(getContext());
        cid = city == null ? 0 : city.getId();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        //设置筛选tab
        resetFilterView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initError();
        initLoad();
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
    }

    protected void initViews() {
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        headerView = View.inflate(getContext(), R.layout.new_search_result_list_header, null);
        footerViewHolder = new FooterViewHolder(footerView);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) >= ViewConfiguration.get(getContext())
                        .getScaledTouchSlop()) {
                    hideKeyboard();
                }
            }
        });
    }

    private void hideKeyboard() {
        if (getSearchActivity() != null) {
            getSearchActivity().hideKeyboard(null);
        }
    }

    @Override
    public void refresh(Object... params) {}

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onKeywordRefresh(String keyword) {
        this.keyword = keyword;
        this.filter = new SearchFilter();
        initLoad();
    }


    @Override
    public void onFilterRefresh(String sort, SearchFilter filter) {
        hideKeyboard();
        this.sort = sort;
        this.filter = filter;
        initLoad();
    }

    public NewSearchResultActivity getSearchActivity() {
        return (NewSearchResultActivity) getActivity();
    }

    @Override
    public void onSearchMenuRefresh(NewSearchApi.SearchType searchType) {
        this.searchType = searchType;
        this.filter = new SearchFilter();
        resetFilterView();
        initLoad();
    }

    /**
     * 如果不是下拉刷新先清空数据
     */
    protected void clearData(NewBaseSearchResultAdapter adapter) {
        if(emptyView==null){
            return;
        }
        emptyView.hideEmptyView();
        recyclerView.setVisibility(View.GONE);
        if (!adapter.isAllEmpty()) {
            adapter.clearAll();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 重置筛选
     */
    protected abstract void resetFilterView();

    protected void initLoad() {
        if (getSearchActivity() != null) {
            getSearchActivity().setExpanded();
        }
    }

    class FooterViewHolder {
        @BindView(R.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
