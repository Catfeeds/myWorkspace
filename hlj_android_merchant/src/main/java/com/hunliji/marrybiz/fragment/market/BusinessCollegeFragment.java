package com.hunliji.marrybiz.fragment.market;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.college.CollegeListAdapter;
import com.hunliji.marrybiz.api.college.CollegeApi;
import com.hunliji.marrybiz.model.college.CollegeItem;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.view.BusinessCollegeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 2017/11/22.
 */

public class BusinessCollegeFragment extends ScrollAbleFragment implements OnItemClickListener,
        PullToRefreshVerticalRecyclerView.OnRefreshListener {


    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    public static final String ARG_TYPE = "type";

    Unbinder unbinder;
    private int type;

    private ArrayList<CollegeItem> items;
    private ArrayList<CollegeItem> preItems;
    private View footerView;
    private View endView;
    private View loadView;
    private LinearLayoutManager layoutManager;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber initSub;
    private CollegeListAdapter adapter;

    public static BusinessCollegeFragment newInstance(int type) {
        Bundle args = new Bundle();
        BusinessCollegeFragment fragment = new BusinessCollegeFragment();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
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
        initViews();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (items.isEmpty()) {
            initLoad();
        }
    }

    private void initValues() {
        Bundle args = getArguments();
        if (args != null) {
            type = args.getInt(ARG_TYPE, CollegeItem.TYPE_FINE_CLASS);
        }
        items = new ArrayList<>();
        preItems = new ArrayList<>();
        adapter = new CollegeListAdapter(getContext(), preItems, items);
        adapter.setOnItemClickListener(this);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter.setFooterView(footerView);
    }

    private void initViews() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() ? null : getProgressBar())
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        items.clear();
                        items.addAll(resultZip.hljHttpData.getData());
                        if (type == CollegeItem.TYPE_FINE_CLASS) {
                            preItems.clear();
                            preItems.addAll(resultZip.preList);
                            for (CollegeItem item : preItems) {
                                item.setTypeTitle(CollegeItem.TYPE_CLASS_PRE);
                            }
                        }
                        for (CollegeItem item : items) {
                            item.setTypeTitle(type);
                        }
                        adapter.notifyDataSetChanged();
                        initPagination(resultZip.hljHttpData.getPageCount());
                    }
                })
                .build();
        Observable.zip(CollegeApi.getCollegeItems(type, 1),
                CollegeApi.getCollegeItems(CollegeItem.TYPE_CLASS_PRE, 1),
                new Func2<HljHttpData<List<CollegeItem>>, HljHttpData<List<CollegeItem>>,
                        ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<CollegeItem>> listHljHttpData,
                            HljHttpData<List<CollegeItem>> listHljHttpData2) {
                        ResultZip zip = new ResultZip();
                        zip.hljHttpData = listHljHttpData;
                        zip.preList = listHljHttpData2.getData();
                        return zip;
                    }
                })
                .subscribe(initSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<CollegeItem>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CollegeItem>>>() {
                    @Override
                    public Observable<HljHttpData<List<CollegeItem>>> onNextPage(int page) {
                        return CollegeApi.getCollegeItems(type, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CollegeItem>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CollegeItem>> listHljHttpData) {
                        items.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private View getProgressBar() {
        Activity activity = getActivity();
        if (activity instanceof BusinessCollegeActivity) {
            BusinessCollegeActivity businessCollegeActivity = (BusinessCollegeActivity) activity;
            return businessCollegeActivity.getProgressBar();
        }
        return progressBar;
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpData<List<CollegeItem>> hljHttpData;
        @HljRZField
        List<CollegeItem> preList;
    }

    @Override
    public void refresh(Object... params) {
        initLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    @Override
    public void onItemClick(int position, Object object) {
        CollegeItem item = (CollegeItem) object;
        if (item == null) {
            return;
        }
        Intent intent = new Intent(getContext(), HljWebViewActivity.class);
        intent.putExtra("path", JSONUtil.getWebPath(item.getUrl()));
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad();
    }
}
