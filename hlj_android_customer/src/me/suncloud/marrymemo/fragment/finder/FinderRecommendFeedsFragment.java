package me.suncloud.marrymemo.fragment.finder;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.FinderRecommendFeedsAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.finder.CPMFeed;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.finder.FinderPrefUtil;
import me.suncloud.marrymemo.util.finder.FinderRecommendFeedsPaginationTool;
import me.suncloud.marrymemo.widget.finder.FinderItemAnimator;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 发现页-发现tab-精选tab
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderRecommendFeedsFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnGetSimilarListener, OnItemClickListener<Note> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_new_count)
    TextView tvNewCount;
    @BindView(R.id.btn_refresh)
    ImageButton btnRefresh;

    private View footerView;
    private View endView;
    private View loadView;

    private StaggeredGridLayoutManager layoutManager;
    private FinderRecommendFeedsAdapter adapter;

    private Handler handler;
    private ObjectAnimator transAnimator;

    private City city;
    private String tab;
    private boolean hasNew;
    private long posterCid = -1;
    private int currentPosition = -1;

    private Unbinder unbinder;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber getSimilarSub;
    private HljHttpSubscriber syncSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber hasNewSub;

    private final static int CPM_NUM = 4;

    public final static String ARG_TAB = "tab";

    public static FinderRecommendFeedsFragment newInstance(String tab) {
        FinderRecommendFeedsFragment fragment = new FinderRecommendFeedsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TAB, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tab = getArguments().getString(ARG_TAB, FinderRecommendFragment.TAB_STR_CHOICE);
        }
        handler = new Handler();
        city = Session.getInstance()
                .getMyCity(getContext());
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new FinderRecommendFeedsAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finder_feeds, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTracker();
        initViews();
        if (CommonUtil.isCollectionEmpty(adapter.getData())) {
            initLoad();
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "find_recommend_list?tab=" + tab);
        HljVTTagger.buildTagger(recyclerView)
                .tagName("find_recommend_list?tab=" + tab)
                .tag();
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(null);
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        FinderItemAnimator animator = new FinderItemAnimator();
        animator.setAddDuration(200);
        recyclerView.getRefreshableView()
                .setItemAnimator(animator);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setPadding(CommonUtil.dp2px(getContext(), 6),
                        CommonUtil.dp2px(getContext(), 6),
                        CommonUtil.dp2px(getContext(), 6),
                        CommonUtil.dp2px(getContext(), 50));
        adapter.setFooterView(footerView);
        adapter.setOnGetSimilarListener(this);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (btnRefresh.getVisibility() == View.VISIBLE) {
                            return;
                        }
                        if (transAnimator != null && transAnimator.isRunning()) {
                            return;
                        }
                        int position = PaginationTool.getLastVisibleItemPosition(recyclerView);
                        int count = adapter.getNewDataCount();
                        if (hasNew && count > 0 && position >= count - 1) {
                            showRefreshBtn();
                        }
                    }
                });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            progressBar.setVisibility(View.VISIBLE);
            final FinderRecommendFeedsPaginationTool paginationTool =
                    FinderRecommendFeedsPaginationTool.buildPagingObservable(
                    recyclerView.getRefreshableView(),
                    city.getId(),
                    tab,
                    new FinderRecommendFeedsPaginationTool.PagingListener() {
                        @Override
                        public Subscriber<List<FinderFeed>> syncServerFinderFeedsSub() {
                            syncSub = HljHttpSubscriber.buildSubscriber(getContext())
                                    .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                                        @Override
                                        public void onNext(List<FinderFeed> feeds) {
                                            List<Object> data = adapter.getData();
                                            if (CommonUtil.isCollectionEmpty(data)) {
                                                return;
                                            }
                                            int positionStart = data.indexOf(feeds.get(0));
                                            if (positionStart < 0) {
                                                return;
                                            }
                                            int itemCount = data.indexOf(feeds.get(feeds.size() -
                                                    1)) + 1;
                                            if (itemCount == 0) {
                                                return;
                                            }
                                            adapter.notifyItemRangeChanged(positionStart,
                                                    itemCount);
                                        }
                                    })
                                    .build();
                            return syncSub;
                        }
                    })
                    .setEndView(endView)
                    .setLoadView(loadView)
                    .build();
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                        @Override
                        public void onNext(List<FinderFeed> finderFeeds) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            adapter.setCanShowNoteClickHint(false);
                            adapter.setCanShowSimilarClickHint(false);
                            adapter.setData(finderFeeds, null, null, true);
                            if (CommonUtil.isCollectionEmpty(finderFeeds)) {
                                onRefresh(null);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setRefreshing(true);
                            }
                            initPagination(paginationTool);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .build();
            paginationTool.firstPageObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSub);
        }
    }

    private void initPagination(FinderRecommendFeedsPaginationTool paginationTool) {
        CommonUtil.unSubscribeSubs(pageSub);
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                    @Override
                    public void onNext(List<FinderFeed> feeds) {
                        if (CommonUtil.isCollectionEmpty(feeds)) {
                            return;
                        }
                        adapter.addData(feeds);

                        //加载更多时，如果保存的缓存feeds数据小于30时，重新保存下数据。
                        List<FinderFeed> fs = FinderPrefUtil.getInstance(getContext())
                                .getFinderFist30Feeds(city.getId(), tab);
                        if (CommonUtil.getCollectionSize(fs) < FinderRecommendFeedsPaginationTool
                                .PER_PAGE) {
                            FinderPrefUtil.getInstance(getContext())
                                    .setFinderFirst30Feeds(adapter.getFirst30FinderFeeds(),
                                            city.getId(),
                                            tab);
                        }
                    }
                })
                .build();
        paginationTool.getPagingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        hideRefreshBtn();
        pullToRefreshLoad();
    }

    private void pullToRefreshLoad() {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            Observable<List<FinderFeed>> finderFeedsObb = FinderRecommendFeedsPaginationTool
                    .getFinderRecommendFeedsObb(
                    getContext(),
                    city.getId(),
                    tab,
                    false);
            Observable<List<CPMFeed>> cpmFeedsObb = getCPMFeedsObb();
            Observable<Poster> posterObb = getPosterObb();
            Observable<ResultZip> observable = Observable.zip(finderFeedsObb,
                    cpmFeedsObb,
                    posterObb,
                    new Func3<List<FinderFeed>, List<CPMFeed>, Poster, ResultZip>() {

                        @Override
                        public ResultZip call(
                                List<FinderFeed> finderFeeds,
                                List<CPMFeed> cpmFeeds,
                                Poster poster) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.finderFeeds = finderFeeds;
                            resultZip.cpmFeeds = cpmFeeds;
                            resultZip.poster = poster;
                            return resultZip;
                        }
                    });
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override

                        public void onNext(ResultZip resultZip) {
                            hasNewRecommends(resultZip);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            setShowEmptyView(CommonUtil.isCollectionEmpty(adapter.getData()));
                        }
                    })
                    .setDataNullable(true)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(!recyclerView.isRefreshing() || progressBar.getVisibility()
                            == View.VISIBLE ? progressBar : null)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSub);
        }
    }

    private Observable<List<CPMFeed>> getCPMFeedsObb() {
        if (FinderRecommendFragment.TAB_STR_CHOICE.equals(tab)) {
            return FinderApi.getFinderCPMsObb(CPM_NUM);
        } else {
            return Observable.just(null);
        }
    }

    private Observable<Poster> getPosterObb() {
        if (FinderRecommendFragment.TAB_STR_CHOICE.equals(tab)) {
            return Observable.just(city.getId())
                    .concatMap(new Func1<Long, Observable<? extends Poster>>() {
                        @Override
                        public Observable<? extends Poster> call(final Long aLong) {
                            if (posterCid != aLong) {
                                return CommonApi.getBanner(getContext(),
                                        HljCommon.BLOCK_ID.RecommendNoteListFragment,
                                        aLong)
                                        .map(new Func1<PosterData, Poster>() {
                                            @Override
                                            public Poster call(PosterData posterData) {
                                                List<Poster> posters = PosterUtil.getPosterList(
                                                        posterData.getFloors(),
                                                        HljCommon.POST_SITES.SITE_FIND_TOP_BANNER,
                                                        false);
                                                return CommonUtil.isCollectionEmpty(posters) ?
                                                        null : posters.get(
                                                        0);
                                            }
                                        })
                                        .doOnNext(new Action1<Poster>() {
                                            @Override
                                            public void call(Poster poster) {
                                                posterCid = aLong;
                                            }
                                        })
                                        .onErrorReturn(new Func1<Throwable, Poster>() {
                                            @Override
                                            public Poster call(Throwable throwable) {
                                                return null;
                                            }
                                        });
                            }
                            return Observable.just(adapter.getPoster());
                        }
                    });
        } else {
            return Observable.just(null);
        }
    }

    private class ResultZip {
        private List<FinderFeed> finderFeeds;
        private List<CPMFeed> cpmFeeds;
        private Poster poster;
    }

    private void hasNewRecommends(final ResultZip resultZip) {
        if (CommonUtil.isCollectionEmpty(resultZip.finderFeeds)) {
            setServerData(resultZip);
            return;
        }
        if (hasNewSub == null || hasNewSub.isUnsubscribed()) {
            long lastId = FinderPrefUtil.getInstance(getContext())
                    .getLastId(city.getId(), tab);
            long lastTimestamp = FinderPrefUtil.getInstance(getContext())
                    .getLastTimestamp(city.getId(), tab);
            hasNewSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            hasNew = aBoolean != null && aBoolean;
                        }
                    })
                    .setDataNullable(true)
                    .build();
            FinderApi.hasNewRecommendsObb(lastId, lastTimestamp)
                    .doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            setServerData(resultZip);
                        }
                    })
                    .subscribe(hasNewSub);
        }
    }

    private void setServerData(ResultZip resultZip) {
        adapter.setCanShowNoteClickHint(isCanShowNoteClickHint());
        adapter.setCanShowSimilarClickHint(isCanShowSimilarClickHint());
        adapter.setData(resultZip.finderFeeds, resultZip.cpmFeeds, resultZip.poster, false);

        //请求到数据后，设置前30条数据到缓存中
        FinderPrefUtil.getInstance(getContext())
                .setFinderFirst30Feeds(adapter.getFirst30FinderFeeds(), city.getId(), tab);

        //顶部新数据提示
        tvNewCount.setVisibility(View.VISIBLE);
        int count = CommonUtil.getCollectionSize(resultZip.finderFeeds);
        if (count == 0) {
            tvNewCount.setText(R.string.msg_refresh_recommend_notes_empty_tip___note);
        } else {
            tvNewCount.setText(getString(R.string.msg_refresh_recommend_new_notes_count___note,
                    count));
        }
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 3000);
        setShowEmptyView(CommonUtil.isCollectionEmpty(adapter.getData()));
    }

    @Override
    public void onGetSimilar(int position) {
        adapter.hideSimilarClickHint();
        if (!CommonUtil.isUnsubscribed(getSimilarSub)) {
            return;
        }
        final Object obj = adapter.getItem(position);
        if (obj == null) {
            return;
        }
        long id = 0;
        String type = null;
        FinderFeed feed = null;
        boolean isCurrentFinderFeed = false;
        if (obj instanceof FinderFeed) {
            isCurrentFinderFeed = true;
            feed = (FinderFeed) obj;
            id = feed.getEntityObjId();
            type = feed.getType();
        } else if (obj instanceof CPMFeed) {
            CPMFeed cpmFeed = (CPMFeed) obj;
            id = cpmFeed.getEntityObjId();
            switch (cpmFeed.getEntityType()) {
                case CPMFeed.ENTITY_TYPE_MERCHANT:
                    type = FinderFeed.TYPE_MERCHANT;
                    break;
                case CPMFeed.ENTITY_TYPE_WORK:
                    type = FinderFeed.TYPE_WORK;
                    break;
                case CPMFeed.ENTITY_TYPE_CASE:
                    type = FinderFeed.TYPE_CASE;
                    break;
            }
            //如果是cpm的话，需要当前position往上找，直到找到finderFeed
            for (int i = position - 1; i >= 0; i--) {
                Object o = adapter.getItem(i);
                if (o instanceof FinderFeed) {
                    feed = (FinderFeed) o;
                    break;
                }
            }
        }
        final FinderFeed finalFeed = feed;
        getSimilarSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                    @Override
                    public void onNext(List<FinderFeed> feeds) {
                        int dataPosition = adapter.getDataPosition(obj);
                        if (dataPosition == -1) {
                            return;
                        }
                        if (obj instanceof FinderFeed) {
                            ((FinderFeed) obj).setShowSimilarIcon(false);
                        } else if (obj instanceof CPMFeed) {
                            ((CPMFeed) obj).setShowSimilarIcon(false);
                        }
                        adapter.notifyItemChanged(dataPosition);
                        if (CommonUtil.isCollectionEmpty(feeds)) {
                            ToastUtil.showToast(getContext(), null, R.string.hint_no_more_similar);
                            return;
                        }
                        int feedPosition = adapter.getFinderFeedPosition(finalFeed);
                        adapter.addData(feedPosition + 1, dataPosition + 1, feeds);

                        //保存列表前30条数据到缓存
                        FinderPrefUtil.getInstance(getContext())
                                .setFinderFirst30Feeds(adapter.getFirst30FinderFeeds(),
                                        city.getId(),
                                        tab);
                    }
                })
                .toastHidden()
                .setDataNullable(true)
                .build();
        FinderRecommendFeedsPaginationTool.getFinderSimilarFeedsObb(getContext(),
                position,
                feed,
                isCurrentFinderFeed,
                adapter.getData(),
                id,
                type,
                city.getId(),
                tab)
                .subscribe(getSimilarSub);
    }

    @Override
    public void onItemClick(int position, Note note) {
        if (note == null) {
            return;
        }
        adapter.hideNoteClickHint();
        currentPosition = position;
        Intent intent = new Intent(getContext(), NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.ARG_NOTE_ID, note.getId());
        intent.putExtra(NoteDetailActivity.ARG_ITEM_POSITION, position);
        intent.putExtra(NoteDetailActivity.ARG_URL, note.getUrl());
        startActivityForResult(intent, HljNote.RequestCode.NOTE_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HljNote.RequestCode.NOTE_DETAIL:
                if (currentPosition == -1) {
                    return;
                }
                //详情页回来判断需不需要显示找相似提示
                boolean isCanShowSimilarClickHint = isCanShowSimilarClickHint();
                adapter.setCanShowSimilarClickHint(isCanShowSimilarClickHint);
                adapter.setSimilarClickHintPosition(currentPosition);

                //详情页收藏数，评论数有改变的话，列表则需要更新
                if (resultCode == Activity.RESULT_OK && data != null) {
                    int collectCount = data.getIntExtra("collect_count", -1);
                    int commentCount = data.getIntExtra("comment_count", -1);
                    Object obj = adapter.getItem(currentPosition);
                    if (obj == null || !(obj instanceof FinderFeed)) {
                        return;
                    }
                    FinderFeed feed = (FinderFeed) obj;
                    Object o = feed.getEntityObj();
                    if (!(o instanceof Note)) {
                        return;
                    }
                    Note note = (Note) o;
                    //-1表示数据未改变
                    if (collectCount != -1) {
                        note.setCollectCount(collectCount);
                    }
                    if (commentCount != -1) {
                        note.setCommentCount(commentCount);
                    }
                }
                if (isCanShowSimilarClickHint || (resultCode == Activity.RESULT_OK && data !=
                        null)) {
                    adapter.notifyItemChanged(currentPosition);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (tvNewCount != null) {
                tvNewCount.setVisibility(View.GONE);
            }
        }
    };

    private void setShowEmptyView(boolean showEmptyView) {
        if (showEmptyView) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showRefreshBtn() {
        btnRefresh.setVisibility(View.VISIBLE);
        transAnimator = ObjectAnimator.ofFloat(btnRefresh,
                View.TRANSLATION_Y,
                0,
                CommonUtil.dp2px(getContext(), 20));
        transAnimator.setDuration(300);
        transAnimator.start();
    }

    private void hideRefreshBtn() {
        hasNew = false;
        btnRefresh.setVisibility(View.GONE);
        if (transAnimator != null && transAnimator.isRunning()) {
            transAnimator.end();
        }
    }

    /**
     * 是否能够显示点击进入笔记详情的提示
     *
     * @return
     */
    private boolean isCanShowNoteClickHint() {
        return FinderRecommendFragment.TAB_STR_CHOICE.equals(tab) && !FinderPrefUtil.getInstance(
                getContext())
                .isNoteClickHintClicked();
    }

    /**
     * 是否能够显示“找相似”的提示
     *
     * @return
     */
    private boolean isCanShowSimilarClickHint() {
        return FinderRecommendFragment.TAB_STR_CHOICE.equals(tab) && FinderPrefUtil.getInstance(
                getContext())
                .isCanShowSimilarClickHint();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        private SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 4);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int top = position < adapter.getItemCount() - adapter.getFooterViewCount() ? -space : 0;
            outRect.set(0, top, 0, 0);
        }
    }


    /**
     * 切换城市，重刷数据
     *
     * @param c
     */
    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        CommonUtil.unSubscribeSubs(initSub, refreshSub, getSimilarSub, syncSub, pageSub, hasNewSub);
        initLoad();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        }
    }

    @Override
    public void refresh(Object... params) {
        if (!CommonUtil.isUnsubscribed(initSub, refreshSub)) {
            return;
        }
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        recyclerView.setRefreshing(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.getRefreshableView()
                .setAdapter(null);
        unbinder.unbind();
        handler.removeCallbacks(runnable);
        CommonUtil.unSubscribeSubs(initSub, refreshSub, getSimilarSub, syncSub, pageSub, hasNewSub);
    }
}