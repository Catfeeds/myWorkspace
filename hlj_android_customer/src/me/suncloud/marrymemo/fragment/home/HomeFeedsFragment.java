package me.suncloud.marrymemo.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.HomePageScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.api.NoteApi;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.home.HomeFeedsAdapter;
import me.suncloud.marrymemo.adpter.home.HomeFeedsFragmentCallBack;
import me.suncloud.marrymemo.api.home.HomeApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.home.HomeFeed;
import me.suncloud.marrymemo.model.home.HomeFeedList;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.merchant.WeddingDressChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingDressPhotoChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingMakeupChannelActivity;
import me.suncloud.marrymemo.view.merchant.WeddingPlanChannelActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 2017/4/20.
 */

public class HomeFeedsFragment extends HomePageScrollAbleFragment {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private String propertyId;
    private HomeFeedsAdapter adapter;
    private City city;
    private View loadMoreView;
    private View loadingView;

    private View rootView;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private ArrayList<HomeFeed> feeds;

    public static HomeFeedsFragment newInstance(String propertyId) {
        HomeFeedsFragment feedsFragment = new HomeFeedsFragment();
        Bundle arg = new Bundle();
        arg.putString("propertyId", propertyId);
        feedsFragment.setArguments(arg);
        return feedsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle arg = getArguments();
        if (arg != null) {
            propertyId = arg.getString("propertyId");
        }
        super.onCreate(savedInstanceState);

        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.hlj_common_fragment_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(getContext());
        recyclerView.addItemDecoration(spacesItemDecoration);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getHomeFeedsFragmentCallBack() == null) {
                    return;
                }
                if (recyclerView != null && recyclerView.getLayoutManager() != null &&
                        recyclerView.getAdapter() != null && recyclerView.getAdapter()
                        .getItemCount() > 0) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView
                            .getLayoutManager();
                    if (manager == null) {
                        return;
                    }
                    getHomeFeedsFragmentCallBack().onFiltrateAnimation(HomeFeedsFragment.this,
                            manager.findLastVisibleItemPosition() <= 3);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        initLoad();

        return rootView;
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(getActivity());

        feeds = new ArrayList<>();
        adapter = new HomeFeedsAdapter(getContext(), feeds);
        adapter.setCity(city);
        adapter.setPropertyIdString(propertyId);
        View footerView = View.inflate(getContext(), R.layout.home_feed_load_more, null);
        loadMoreView = footerView.findViewById(R.id.load_more_layout);
        loadingView = footerView.findViewById(R.id.loading);
        adapter.setFooterView(footerView);
        loadMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
    }

    private void initLoad() {
        Log.d("Feed Optimization", new DateTime().toString() + "  start");
        if (initSub != null) {
            CommonUtil.unSubscribeSubs(initSub);
        }
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setDataNullable(true)
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        //失败时parent回调
                        if (getHomeFeedsFragmentCallBack() != null) {
                            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeFeedsFragment
                                    .this);
                        }
                        if (getHomeFeedsFragmentCallBack() != null) {
                            getHomeFeedsFragmentCallBack().onShowEmptyView(HomeFeedsFragment
                                    .this, propertyId, adapter.isEmpty());
                        }
                    }
                })
                .setOnNextListener(new SubscriberOnNextListener<HomeFeedList>() {
                    @Override
                    public void onNext(HomeFeedList homeFeedList) {
                        Log.d("Feed Optimization", new DateTime().toString() + "  end");
                        if (getHomeFeedsFragmentCallBack() != null) {
                            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeFeedsFragment
                                    .this);
                        }
                        feeds.clear();
                        if (!homeFeedList.getMixedList()
                                .isEmpty()) {
                            feeds.addAll(homeFeedList.getMixedList());
                        } else {
                            if (getHomeFeedsFragmentCallBack() != null) {
                                getHomeFeedsFragmentCallBack().onShowEmptyView(HomeFeedsFragment
                                        .this, propertyId, adapter.isEmpty());
                            }
                        }
                        initPage(homeFeedList.getPageCount());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        HomeApi.getHomeFeedList(city.getId(), propertyId, 1, HljCommon.PER_PAGE)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HomeFeedList<List<HomeFeed>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HomeFeedList<List<HomeFeed>>>() {
                    @Override
                    public Observable<HomeFeedList<List<HomeFeed>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return HomeApi.getHomeFeedList(city.getId(),
                                propertyId,
                                page,
                                HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadingView)
                .setEndView(loadMoreView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HomeFeedList<List<HomeFeed>>>() {
                    @Override
                    public void onNext(HomeFeedList<List<HomeFeed>> homeFeedList) {
                        feeds.addAll(homeFeedList.getMixedList());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private void loadMore() {
        Intent intent;
        switch (propertyId) {
            case "6":
                intent = new Intent(getContext(), WeddingDressPhotoChannelActivity.class);
                break;
            case "2":
                intent = new Intent(getContext(), WeddingPlanChannelActivity.class);
                break;
            case "12":
                intent = new Intent(getContext(), WeddingDressChannelActivity.class);
                break;
            case "7,8,9,11":
                intent = new Intent(getContext(), WeddingMakeupChannelActivity.class);
                break;
            default:
                intent = new Intent(getContext(), WeddingDressPhotoChannelActivity.class);
                break;
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    public String getPropertyId() {
        return propertyId;
    }

    @Override
    public void refresh(Object... params) {
        boolean isCityChange = false;
        if (params[0] != null && params[0] instanceof City) {
            City nCity = (City) params[0];
            if (city == null) {
                city = nCity;
            } else if (!nCity.getId()
                    .equals(city.getId())) {
                city = nCity;
                isCityChange = true;
                adapter.setCity(city);
                scrollTop();
            }
        }
        if ((initSub == null || initSub.isUnsubscribed()) || isCityChange) {
            initLoad();
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void scrollTop() {
        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
            return;
        }
        if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition
                () >= 5) {
            recyclerView.scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSub,pageSub);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(Context context) {
            this.space = Math.round(context.getResources()
                    .getDisplayMetrics().density * 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) >= 1 && parent.getChildAdapterPosition(view)
                    < parent.getAdapter()
                    .getItemCount() - 1) {
                outRect.top = space;
            }
        }
    }

    private HomeFeedsFragmentCallBack getHomeFeedsFragmentCallBack() {
        if (getParentFragment() != null && getParentFragment() instanceof
                HomeFeedsFragmentCallBack) {
            return (HomeFeedsFragmentCallBack) getParentFragment();
        } else {
            return null;
        }
    }
}
