package me.suncloud.marrymemo.fragment.finder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.animators.BaseItemAnimator;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
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
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.widget.finder.FinderItemAnimator;

/**
 * 婚礼人、婚纱照、婚品笔记列表
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderNotesFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnGetSimilarListener, OnItemClickListener<Note> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_new_count)
    TextView tvNewCount;

    private View footerView;
    private View endView;
    private View loadView;

    private StaggeredGridLayoutManager layoutManager;
    private FinderRecommendFeedsAdapter adapter;

    private Handler handler;

    private City city;
    private boolean isEnd = true;
    private String lastPostAt;
    private int notebookType;

    private HljHttpSubscriber getNotesSub;
    private HljHttpSubscriber getSimilarSub;

    private Unbinder unbinder;

    private final static int PER_PAGE = 30;

    public final static String ARG_NOTEBOOK_TYPE = "notebook_type";

    public static FinderNotesFragment newInstance(int notebookType) {
        FinderNotesFragment fragment = new FinderNotesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NOTEBOOK_TYPE, notebookType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notebookType = getArguments().getInt(ARG_NOTEBOOK_TYPE, 0);
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
            onRefresh(null);
        }
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "find_note_list_" + notebookType);
        HljVTTagger.buildTagger(recyclerView)
                .tagName("find_recommend_list")
                .dataId(notebookType)
                .dataType("NoteBookType")
                .tag();
    }

    private void initViews() {
        tvNewCount.setText(R.string.msg_refresh_new_notes___note);
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
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                if (!CommonUtil.isUnsubscribed(getNotesSub)) {
                                    return;
                                }
                                if (CommonUtil.isCollectionEmpty(adapter.getData())) {
                                    return;
                                }
                                int position = PaginationTool.getLastVisibleItemPosition(
                                        recyclerView);
                                int itemCount = recyclerView.getAdapter()
                                        .getItemCount();
                                if (position >= itemCount - 5 && !isEnd) {
                                    loadView.setVisibility(View.VISIBLE);
                                    endView.setVisibility(View.GONE);
                                    initPagination();
                                } else {
                                    setShowFooterView();
                                }
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (getNotesSub == null || getNotesSub.isUnsubscribed()) {
            getNotesSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                        @Override
                        public void onNext(List<FinderFeed> feeds) {
                            isEnd = false;
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            adapter.setData(feeds);
                            setLastPostAt(feeds);
                            setShowFooterView();

                            //笔记刷新顶部提示
                            tvNewCount.setVisibility(View.VISIBLE);
                            handler.removeCallbacks(runnable);
                            handler.postDelayed(runnable, 3000);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                            View.VISIBLE ? null : progressBar)
                    .build();
            FinderApi.getFinderNotesObb(null, notebookType, PER_PAGE)
                    .subscribe(getNotesSub);
        }
    }

    private void initPagination() {
        getNotesSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                    @Override
                    public void onNext(List<FinderFeed> feeds) {
                        isEnd = CommonUtil.isCollectionEmpty(feeds);
                        if (!isEnd) {
                            adapter.addData(feeds);
                            setLastPostAt(feeds);
                        }
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerView != null) {
                                    setShowFooterView();
                                }
                            }
                        }, 120);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        loadView.setVisibility(View.GONE);
                        endView.setVisibility(View.INVISIBLE);
                    }
                })
                .toastHidden()
                .setDataNullable(true)
                .build();
        FinderApi.getFinderNotesObb(lastPostAt, notebookType, PER_PAGE)
                .subscribe(getNotesSub);
    }

    @Override
    public void onGetSimilar(final int position) {
        if (!CommonUtil.isUnsubscribed(getSimilarSub)) {
            return;
        }
        final Object obj = adapter.getItem(position);
        if (obj == null) {
            return;
        }
        final FinderFeed feed = (FinderFeed) obj;
        getSimilarSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<FinderFeed>>() {
                    @Override
                    public void onNext(List<FinderFeed> feeds) {
                        int dataPosition = adapter.getDataPosition(feed);
                        if (dataPosition == -1) {
                            return;
                        }
                        feed.setShowSimilarIcon(false);
                        adapter.notifyItemChanged(dataPosition);
                        if (CommonUtil.isCollectionEmpty(feeds)) {
                            ToastUtil.showToast(getContext(), null, R.string.hint_no_more_similar);
                            return;
                        }
                        int feedPosition = adapter.getFinderFeedPosition(feed);
                        adapter.addData(feedPosition + 1, dataPosition + 1, feeds);
                    }
                })
                .setDataNullable(true)
                .build();
        FinderApi.getFinderSimilarFeedsObb(position,
                adapter.getData(),
                feed.getEntityObjId(),
                feed.getType())
                .subscribe(getSimilarSub);
    }

    @Override
    public void onItemClick(int position, Note note) {
        if (note == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.ARG_NOTE_ID, note.getId());
        intent.putExtra(NoteDetailActivity.ARG_ITEM_POSITION, position);
        intent.putExtra(NoteDetailActivity.ARG_URL, note.getUrl());
        startActivityForResult(intent, HljNote.RequestCode.NOTE_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_DETAIL:
                    if (data == null) {
                        return;
                    }
                    int position = data.getIntExtra("position", 0);
                    int collectCount = data.getIntExtra("collect_count", -1);
                    int commentCount = data.getIntExtra("comment_count", -1);
                    Object obj = adapter.getItem(position);
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
                    adapter.notifyItemChanged(position);
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setLastPostAt(List<FinderFeed> feeds) {
        if (CommonUtil.isCollectionEmpty(feeds)) {
            return;
        }
        Object obj = feeds.get(feeds.size() - 1)
                .getEntityObj();
        if (obj == null || !(obj instanceof Note)) {
            return;
        }
        lastPostAt = ((Note) obj).getLastPostAt();
    }

    private void setShowFooterView() {
        loadView.setVisibility(View.GONE);
        endView.setVisibility(CommonUtil.isCollectionEmpty(adapter.getData()) ? View.INVISIBLE :
                View.VISIBLE);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (tvNewCount != null) {
                tvNewCount.setVisibility(View.GONE);
            }
        }
    };

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
        CommonUtil.unSubscribeSubs(getNotesSub, getSimilarSub);
        onRefresh(null);
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
        CommonUtil.unSubscribeSubs(getNotesSub, getSimilarSub);
    }
}
