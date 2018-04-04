package me.suncloud.marrymemo.fragment.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.CommunityThreadAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import me.suncloud.marrymemo.view.community.SameCityNewestThreadListActivity;
import me.suncloud.marrymemo.view.community.SameCityThreadListActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by mo_yu on 2016/12/14.社区频道页（优化）
 */
public class CommunityFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private CommunityThreadAdapter adapter;
    private View endView;
    private View loadView;
    private ArrayList<CommunityThread> communityThreads;
    private String url;
    private boolean isNeedRefresh;
    private boolean isShowHotTag;
    private boolean isShowNewTag;
    private boolean isShowRichTag;
    private boolean isShowChannelView;
    private long lastId;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static CommunityFragment newInstance(
            String url,
            boolean isShowHotTag,
            boolean isShowNewTag,
            boolean isShowRichTag,
            boolean isShowChannelView) {
        Bundle bundle = new Bundle();
        CommunityFragment fragment = new CommunityFragment();
        bundle.putString("url", url);
        bundle.putBoolean("isShowHotTag", isShowHotTag);
        bundle.putBoolean("isShowNewTag", isShowNewTag);
        bundle.putBoolean("isShowRichTag", isShowRichTag);
        bundle.putBoolean("isShowChannelView", isShowChannelView);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communityThreads = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            isShowNewTag = getArguments().getBoolean("isShowNewTag", true);
            isShowRichTag = getArguments().getBoolean("isShowRichTag");
            isShowChannelView = getArguments().getBoolean("isShowChannelView");
            isShowHotTag = getArguments().getBoolean("isShowHotTag", true);
        }
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh(null);
            }
        });
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new CommunityThreadAdapter(getContext(), communityThreads);
        adapter.setFooterView(footerView);
        adapter.setShowNewTag(isShowNewTag);
        adapter.setShowRichTag(isShowRichTag);
        adapter.setShowChannelView(isShowChannelView);
        adapter.setShowHotTag(isShowHotTag);
        adapter.setOnReplyItemClickListener(new CommunityThreadAdapter.OnReplyItemClickListener() {
            @Override
            public void onReply(CommunityThread item, int position) {
                if (!AuthUtil.loginBindCheck(getContext())) {
                    return;
                }
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
                intent.putExtra(CreatePostActivity.ARG_POSITION, position);
                intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
        if (getContext() instanceof SameCityThreadListActivity) {
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        } else if (getContext() instanceof SameCityNewestThreadListActivity) {
            recyclerView.getRefreshableView()
                    .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        }
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        initTracker();
        return rootView;
    }


    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "thread_list");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (CommonUtil.isCollectionEmpty(communityThreads)) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CommunityThread>> data) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            initPagination(data.getPageCount());
                            setCommunityList((ArrayList<CommunityThread>) data.getData(), true);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            CommunityApi.getCommunityThreadsObb(url, lastId, 1)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CommunityThread>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>> onNextPage(int page) {
                        return CommunityApi.getCommunityThreadsObb(url, lastId, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CommunityThread>> listHljHttpData) {
                        setCommunityList((ArrayList<CommunityThread>) listHljHttpData.getData(),
                                false);
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private void setCommunityList(ArrayList<CommunityThread> list, boolean isRefresh) {
        for (int i = 0; i < list.size(); i++) {
            CommunityThread thread = list.get(i);
            if (thread.getPost() != null && thread.getPost()
                    .getPhotos() != null && thread.getPost()
                    .getPhotos()
                    .size() > 0) {
                int num = (int) (Math.random() * 100);
                thread.setShowThree(num <= 80);
            }
        }
        if (isRefresh) {
            communityThreads.clear();
        }
        communityThreads.addAll(list);
        if (communityThreads.size() > 0) {
            lastId = communityThreads.get(0)
                    .getId();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                int position = data.getIntExtra("position", 0);
                CommunityThread thread = communityThreads.get(position);
                if (thread != null) {
                    thread.setPostCount(thread.getPostCount() + 1);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.isNeedRefresh = needRefresh;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView != null) {
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }

}
