package me.suncloud.marrymemo.fragment.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.HotCommunityChannel;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ChannelRecyclerAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/9/22.社区首页-频道列表项
 */

public class ChannelListFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private City city;
    private User user;
    private View footView;
    private View endView;
    private View loadView;

    private ArrayList<CommunityChannel> followList;
    private ArrayList<HotCommunityChannel> hotList;

    private HljHttpSubscriber pageSubscriber;//热门推荐分页
    private HljHttpSubscriber initSubscriber;//频道
    private HljHttpSubscriber initHotSubscriber;//热门推荐频道

    private LinearLayoutManager layoutManager;
    private ChannelRecyclerAdapter adapter;

    private Unbinder unbinder;
    private int pageCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault()
                .register(this);
        initValue();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout
                .hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        initLoad();
        return rootView;
    }


    private void initValue() {
        hotList = new ArrayList<>();
        followList = new ArrayList<>();

        user = Session.getInstance()
                .getCurrentUser(getActivity());
        city = Session.getInstance()
                .getMyCity(getActivity());
        followList = new ArrayList<>();
        hotList = new ArrayList<>();
    }

    private void initView() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView
                .OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh(recyclerView);
            }
        });

        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.hlj_foot_no_more___cm, null);
        endView = footView.findViewById(R.id
                .no_more_hint);
        loadView = footView.findViewById(R.id
                .loading);

        adapter = new ChannelRecyclerAdapter(getActivity(),
                hotList,
                followList);
        adapter.setFooterView(footView);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }

    private void initPageChannel(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<HotCommunityChannel>>> pageObservable =
                PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<HotCommunityChannel>>>() {
                    @Override
                    public Observable<HljHttpData<List<HotCommunityChannel>>>
                    onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return CommunityApi.getHotListObb(city.getId(),
                                page,20);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<HotCommunityChannel>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<HotCommunityChannel>> data) {
                        hotList.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    /**
     * 我关注的频道和推荐频道
     */
    private void initFollowHotLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            city = Session.getInstance()
                    .getMyCity(getActivity());
            Observable<HljHttpData<List<CommunityChannel>>> fObservable =
                    CommunityApi.getIndexListObb(
                    1);
            Observable<HljHttpData<List<HotCommunityChannel>>> hObservable =
                    CommunityApi.getHotListObb(
                    city.getId(),
                    1,20);

            Observable<ResultZip> observable = Observable.zip(fObservable,
                    hObservable,
                    new Func2<HljHttpData<List<CommunityChannel>>,
                            HljHttpData<List<HotCommunityChannel>>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<CommunityChannel>>
                                        listHljHttpData,
                                HljHttpData<List<HotCommunityChannel>>
                                        listHljHttpData2) {
                            ResultZip zip = new ResultZip();
                            zip.followList.addAll(listHljHttpData.getData());
                            zip.hotList.addAll(listHljHttpData2.getData());
                            pageCount = listHljHttpData2.getPageCount();
                            return zip;
                        }
                    });

            initSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(
                                ResultZip zip) {
                            initPageChannel(pageCount);
                            followList.clear();
                            followList.addAll(zip.followList);
                            hotList.clear();
                            hotList.addAll(zip.hotList);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    /**
     * 推荐频道（未登陆时，不调用我关注的接口）
     */
    private void initHotLoad() {
        if (initHotSubscriber == null || initHotSubscriber.isUnsubscribed()) {
            city = Session.getInstance()
                    .getMyCity(getActivity());
            initHotSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<HotCommunityChannel>>>() {
                        @Override
                        public void onNext
                                (HljHttpData<List<HotCommunityChannel>> data) {
                            initPageChannel(data.getPageCount());
                            followList.clear();
                            hotList.clear();
                            hotList.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setPullToRefreshBase(recyclerView)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .build();
            CommunityApi.getHotListObb(city.getId(), 1,20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initHotSubscriber);
        }
    }

    private static class ResultZip {
        ArrayList<CommunityChannel> followList = new ArrayList<>();
        ArrayList<HotCommunityChannel> hotList = new ArrayList<>();
    }

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param u
     */
    public void userRefresh(User u) {
        if (u != null) {
            if (user != null) {
                if (!u.getId()
                        .equals(user.getId())) {
                    user = u;
                    scrollTop();
                    recyclerView.setRefreshing(true);
                }
            } else {
                user = u;
                scrollTop();
                recyclerView.setRefreshing(true);
            }
        } else if(user!=null){
            user = null;
            scrollTop();
            recyclerView.setRefreshing(true);
        }
    }

    /**
     * 选择城市之后，刷新数据
     *
     * @param c
     */
    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            scrollTop();
            recyclerView.setRefreshing(true);
        }
    }

    public void scrollTop() {
        if (layoutManager == null) {
            return;
        }
        if (layoutManager.findFirstVisibleItemPosition() >= 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }

    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (user != null && user.getId() > 0) {
            initFollowHotLoad();
        } else {
            initHotLoad();
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.COMMUNITY_CHANNEL_FOLLOW_FLAG) {
            scrollTop();
            onRefresh(recyclerView);
        }
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode
                .COMMUNITY_CHANNEL) {
            onRefresh(recyclerView);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
        if (initHotSubscriber != null && !initHotSubscriber.isUnsubscribed()) {
            initHotSubscriber.unsubscribe();
        }
        EventBus.getDefault()
                .unregister(this);
    }
}
