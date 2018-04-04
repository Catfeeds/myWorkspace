package me.suncloud.marrymemo.fragment.live;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigEventViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.api.LiveApi;
import com.hunliji.hljlivelibrary.models.LiveRelevantWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.adpter.LiveRelevantAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/10/25.直播相关页面
 */

public class LiveRelevantFragment extends ScrollAbleFragment {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    private LiveRelevantAdapter adapter;
    private ArrayList<LiveRelevantWrapper> list;
    private Handler handler;
    private long channelId;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;//刷新

    /**
     * @param channelId
     * @return LiveMessageFragment
     */
    public static LiveRelevantFragment newInstance(long channelId) {
        Bundle bundle = new Bundle();
        bundle.putLong("channelId", channelId);
        LiveRelevantFragment fragment = new LiveRelevantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        handler = new Handler();
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
            channelId = getArguments().getLong("channelId", 0);
        }
        initView();
        initLoad();
        return rootView;
    }

    private void initView() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                refresh();
            }
        });
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        adapter = new LiveRelevantAdapter(getActivity(), list);
        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___cm, null);
        adapter.setFooterView(footerView);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        refresh();
    }

    @Override
    public void refresh(Object... params) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<LiveRelevantWrapper>>>() {
                        @Override
                        public void onNext(HljHttpData<List<LiveRelevantWrapper>> listHljHttpData) {
                            list.clear();
                            list.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            handler.post(mRunnable);
                        }
                    })
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            LiveApi.getLiveRelateListObb(channelId, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() == null || getActivity().isFinishing() || isDetached() ||
                    layoutManager == null || CommonUtil.isCollectionEmpty(
                    list)) {
                return;
            }
            int first = layoutManager.findFirstVisibleItemPosition();
            int end = layoutManager.findLastVisibleItemPosition();
            for (int i = first; i <= end; i++) {
                RecyclerView.ViewHolder holder = recyclerView.getRefreshableView()
                        .findViewHolderForAdapterPosition(i);
                if (holder != null && holder instanceof BigEventViewHolder) {
                    BigEventViewHolder eventHolder = (BigEventViewHolder) holder;
                    eventHolder.showTimeDown(getContext(), eventHolder.getItem());
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(mRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(mRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        handler.removeCallbacks(mRunnable);
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }
}
