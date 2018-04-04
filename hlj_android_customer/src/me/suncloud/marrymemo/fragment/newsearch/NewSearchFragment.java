package me.suncloud.marrymemo.fragment.newsearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.search.NewSearchTips;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.NewSearchAdapter;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by hua_rong on 2018/1/3
 * 通用的搜索下拉弹窗
 */

public class NewSearchFragment extends RefreshFragment implements OnSearchItemClickListener {

    @Override
    public String fragmentPageTrackTagName() {
        return "下拉结果页";
    }

    public static final String CPM_SOURCE = "search_tip";

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    Unbinder unbinder;
    private static final String ARG_KEY_WORD = "key_word";
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private String keyword;
    private NewSearchAdapter searchAdapter;
    private HljHttpSubscriber searchSubscriber;
    private OnSearchItemClickListener onSearchItemClickListener;
    private static final String ARG_SEARCH_CATEGORY = "search_category";
    private NewSearchApi.SearchType searchType;
    private Poster poster;

    public static NewSearchFragment newInstance(
            String keyWord, NewSearchApi.SearchType searchType) {
        Bundle args = new Bundle();
        NewSearchFragment fragment = new NewSearchFragment();
        args.putString(ARG_KEY_WORD, keyWord);
        args.putSerializable(ARG_SEARCH_CATEGORY, searchType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0) {
            try {
                keyword = (String) params[0];
                onLoad();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            keyword = bundle.getString(ARG_KEY_WORD);
            searchType = (NewSearchApi.SearchType) getArguments().getSerializable(
                    ARG_SEARCH_CATEGORY);
        }
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        onLoad();
        initError();
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    public void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = ((InputMethodManager) getActivity().getSystemService
                    (Activity.INPUT_METHOD_SERVICE));
            if (imm != null && getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void initView() {
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        mRecyclerView.setLayoutManager(layoutManager);
        searchAdapter = new NewSearchAdapter(getContext());
        searchAdapter.setOnSearchItemClickListener(this);
        mRecyclerView.setAdapter(searchAdapter);
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

    @SuppressWarnings("unchecked")
    private void onLoad() {
        searchAdapter.clearObject();
        CommonUtil.unSubscribeSubs(searchSubscriber);
        Observable<NewSearchTips> observable = Observable.timer(150, TimeUnit.MILLISECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return !TextUtils.isEmpty(keyword);
                    }
                })
                .concatMap(new Func1<Long, Observable<? extends NewSearchTips>>() {
                    @Override
                    public Observable<? extends NewSearchTips> call(Long aLong) {
                        return NewSearchApi.getSearchTips(keyword);
                    }
                });
        poster = null;
        searchSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<NewSearchTips>() {
                    @Override
                    public void onNext(NewSearchTips newSearchTips) {
                        poster = newSearchTips.getPoster();
                        if (getActivity() != null && poster != null) {
                            ((NewSearchActivity) getActivity()).showFragment(true, null);
                            return;
                        }
                        searchAdapter.setKeyword(keyword);
                        List<TipSearchType> eCommerces = newSearchTips.getECommerces();
                        List<TipSearchType> contents = newSearchTips.getContents();
                        Collections.sort(eCommerces, new Comparator<TipSearchType>() {
                            @Override
                            public int compare(TipSearchType o1, TipSearchType o2) {
                                return o2.getDocCount() - o1.getDocCount();
                            }
                        });
                        Collections.sort(contents, new Comparator<TipSearchType>() {
                            @Override
                            public int compare(TipSearchType o1, TipSearchType o2) {
                                return o2.getDocCount() - o1.getDocCount();
                            }
                        });
                        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT ||
                                searchType == NewSearchApi.SearchType.SEARCH_TYPE_CAR ||
                                searchType == NewSearchApi.SearchType.SEARCH_TYPE_HOTEL) {
                            //婚品和婚车/酒店中的二级搜索下来提示中，置顶婚品或婚车、酒店的分类入口
                            Collections.sort(eCommerces, new Comparator<TipSearchType>() {
                                @Override
                                public int compare(TipSearchType o1, TipSearchType o2) {
                                    if (searchType.getType()
                                            .equals(o1.getKey())) {
                                        return -1;
                                    } else if (searchType.getType()
                                            .equals(o2.getKey())) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                        }
                        searchAdapter.setObjects(eCommerces,
                                newSearchTips.getMerchants(),
                                contents);
                    }
                })
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .build();
        observable.subscribe(searchSubscriber);
    }

    public ArrayList<Object> getNewSearchTips() {
        return searchAdapter.getObjects();
    }

    public Poster getPoster() {
        return poster;
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(searchSubscriber);
    }

    @Override
    public void onSearchItemClick(String keyword, Object object) {
        if (object == null) {
            return;
        }
        if (onSearchItemClickListener != null) {
            onSearchItemClickListener.onSearchItemClick(keyword, object);
        }
    }
}
