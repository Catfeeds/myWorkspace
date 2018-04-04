package me.suncloud.marrymemo.fragment.wallet;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.wallet.FinancialCreditCardAdapter;
import me.suncloud.marrymemo.adpter.wallet.FinancialLoanAdapter;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.model.wallet.FinancialProduct;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;

/**
 * Created by luohanlin on 2017/12/18.
 */

public class FinancialProductsFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    public static final int FINANCIAL_TYPE_PRODUCT = 2;
    public static final int FINANCIAL_TYPE_CREDIT_CARD = 3;
    public static final String ARG_TYPE = "type";

    private int type;
    private List<FinancialProduct> products;
    private RecyclerView.Adapter adapter;
    private HljHttpSubscriber initSub;

    public static FinancialProductsFragment newInstance(int type) {
        Bundle args = new Bundle();
        FinancialProductsFragment fragment = new FinancialProductsFragment();
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
        initTracker();
        return rootView;
    }

    private void initTracker() {
        switch (type) {
            case FINANCIAL_TYPE_PRODUCT:
                HljVTTagger.tagViewParentName(recyclerView, "debt_list");
                break;
            case FINANCIAL_TYPE_CREDIT_CARD:
                HljVTTagger.tagViewParentName(recyclerView, "credit_list");
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (products.isEmpty()) {
            initLoad();
        }
    }

    private void initValues() {
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE, FINANCIAL_TYPE_PRODUCT);
        }
        products = new ArrayList<>();
        if (type == FINANCIAL_TYPE_PRODUCT) {
            adapter = new FinancialLoanAdapter(getContext(), products);
        } else {
            adapter = new FinancialCreditCardAdapter(getContext(), products);
        }
    }

    private void initViews() {
        if (type == FINANCIAL_TYPE_PRODUCT) {
            recyclerView.getRefreshableView()
                    .addItemDecoration(new SpacesItemDecoration());
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            layoutManager.setItemPrefetchEnabled(false);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            recyclerView.getRefreshableView()
                    .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 18));
            recyclerView.setOnRefreshListener(this);
            recyclerView.getRefreshableView()
                    .setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.getRefreshableView()
                    .setLayoutManager(layoutManager);
        }

        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(initSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(recyclerView.isRefreshing() ? null : getFinancialHomeActivity()
                        .getProgressBar())
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<FinancialProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<FinancialProduct>> listHljHttpData) {
                        products.clear();
                        products.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        WalletApi.getFinancialMarketListV3(type)
                .subscribe(initSub);
    }

    @Override
    public void refresh(Object... params) {
        initLoad();
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(initSub);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        initLoad();
    }

    private FinancialHomeActivity getFinancialHomeActivity() {
        FinancialHomeActivity financialHomeActivity = null;
        if (getActivity() instanceof FinancialHomeActivity) {
            financialHomeActivity = (FinancialHomeActivity) getActivity();
        }

        return financialHomeActivity;
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 12);
            this.middleSpace = CommonUtil.dp2px(getContext(), 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            int top = middleSpace;
            int left = 0;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position < adapter.getItemCount()) {
                left = lp.getSpanIndex() == 0 ? space : middleSpace / 2;
                right = lp.getSpanIndex() == 0 ? middleSpace / 2 : space;
            }
            outRect.set(left, top, right, 0);
        }

    }
}
