package me.suncloud.marrymemo.fragment.work_case;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.MerchantWorkRecyclerAdapter;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 套餐
 * Created by Suncloud on 2016/7/5.
 */
public class MerchantWorkFragment extends ScrollAbleFragment implements CompoundButton
        .OnCheckedChangeListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {
    @BindView(R.id.sort_layout)
    LinearLayout sortLayout;
    @BindView(R.id.sort_default)
    CheckBox sortDefault;
    @BindView(R.id.tv_price_sort)
    AppCompatCheckedTextView tvPriceSort;
    @BindView(R.id.cl_price_sort_up)
    CheckableLinearLayout clPriceSortUp;
    @BindView(R.id.cl_price_sort_down)
    CheckableLinearLayout clPriceSortDown;
    @BindView(R.id.sort_price)
    LinearLayout sortPrice;
    @BindView(R.id.sort_create)
    CheckBox sortCreate;
    @BindView(R.id.sort_like)
    CheckBox sortLike;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    private View footerView;
    private View endView;
    private View loadView;
    private MerchantWorkRecyclerAdapter adapter;
    protected String sort = "";//排序
    private long id;
    private int style;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static MerchantWorkFragment newInstance(long id, int style) {
        MerchantWorkFragment fragment = new MerchantWorkFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putInt("style", style);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong("id");
            style = getArguments().getInt("style", SmallWorkViewHolder.STYLE_COMMON);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new MerchantWorkRecyclerAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merchant_work, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        sortDefault.setOnCheckedChangeListener(this);
        sortPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedChange(v.getId());
            }
        });
        sortCreate.setOnCheckedChangeListener(this);
        sortLike.setOnCheckedChangeListener(this);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        adapter.setStyle(style);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        sortLayout.setVisibility(View.VISIBLE);
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        adapter.setWorks(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .build();
        WorkApi.getMerchantWorksAndCasesObb(id, "set_meal", sort, 1, 20)
                .subscribe(refreshSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return WorkApi.getMerchantWorksAndCasesObb(id, "set_meal", sort, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        adapter.addWorks(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setClickable(false);
            checkedChange(buttonView.getId());
        } else {
            buttonView.setClickable(true);
        }
    }

    private void checkedChange(int id) {
        if (id != R.id.sort_default) {
            sortDefault.setChecked(false);
        } else {
            sort = "";
        }
        if (id != R.id.sort_price) {
            tvPriceSort.setChecked(false);
            clPriceSortUp.setChecked(false);
            clPriceSortDown.setChecked(false);
        } else {
            tvPriceSort.setChecked(true);
            if (clPriceSortUp.isChecked()) {
                clPriceSortDown.setChecked(true);
                clPriceSortUp.setChecked(false);
            } else {
                clPriceSortDown.setChecked(false);
                clPriceSortUp.setChecked(true);
            }
            sort = clPriceSortUp.isChecked() ? "price_asc" : "price_desc";
        }
        if (id != R.id.sort_create) {
            sortCreate.setChecked(false);
        } else {
            sort = "new";
        }
        if (id != R.id.sort_like) {
            sortLike.setChecked(false);
        } else {
            sort = "like";
        }
        onRefresh(null);
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void refresh(Object... params) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}
