package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.note.NoteSpotEntity;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.OrderedListAdapter;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchMerchantBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.SearchProductBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.wrappers.HljHttpOrderedData;
import com.hunliji.hljnotelibrary.views.activities.SelectNoteSpotEntityActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action0;

/**
 * 已下单历史数据
 * Created by chen_bin on 2017/6/28 0028.
 */
public class OrderedListFragment extends RefreshFragment implements
        SearchMerchantBriefInfoViewHolder.OnSelectMerchantListener,
        SearchProductBriefInfoViewHolder.OnSelectProductListener {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private OrderedListAdapter adapter;
    private InputMethodManager imm;
    private Unbinder unbinder;
    private HljHttpSubscriber initSub;

    public static OrderedListFragment newInstance() {
        Bundle args = new Bundle();
        OrderedListFragment fragment = new OrderedListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        adapter = new OrderedListAdapter(getContext());
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

    private void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setNeedChangeSize(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnSelectMerchantListener(this);
        adapter.setOnSelectProductListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (imm != null && getActivity().getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpOrderedData>() {

                        @Override
                        public void onNext(HljHttpOrderedData orderedData) {
                            adapter.setOrderedData(orderedData);
                        }
                    })
                    .setDataNullable(true)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            NoteApi.getOrderedObb()
                    .doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            if (isOrdered()) {
                                return;
                            }
                            if (getContext() instanceof SelectNoteSpotEntityActivity) {
                                ((SelectNoteSpotEntityActivity) getContext()).requestFocus();
                            }
                            if (imm != null && getActivity().getCurrentFocus() != null) {
                                imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                                        .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }
                    })
                    .subscribe(initSub);
        }
    }

    @Override
    public void onSelectMerchant(Merchant merchant) {
        if (merchant != null && merchant.getId() > 0) {
            setResult(merchant.getId(), NoteSpotEntity.TYPE_MERCHANT, merchant.getName());
        }
    }

    @Override
    public void onSelectProduct(ShopProduct product) {
        if (product != null && product.getId() > 0) {
            setResult(product.getId(), NoteSpotEntity.TYPE_SHOP_PRODUCT, product.getTitle());
        }
    }

    private void setResult(long id, String type, String title) {
        NoteSpotEntity noteSpotEntity = new NoteSpotEntity();
        noteSpotEntity.setId(id);
        noteSpotEntity.setType(type);
        noteSpotEntity.setTitle(title);
        Intent intent = getActivity().getIntent();
        intent.putExtra("note_spot_entity", noteSpotEntity);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int merchantsSize = CommonUtil.getCollectionSize(adapter.getMerchants());
            int top = merchantsSize > 0 && position == merchantsSize + adapter
                    .getMerchantHeaderViewCount() ? space : 0;
            outRect.set(0, top, 0, 0);
        }
    }

    public boolean isOrdered() {
        return !CommonUtil.isCollectionEmpty(adapter.getMerchants()) || !CommonUtil
                .isCollectionEmpty(
                adapter.getProducts());
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(initSub);
    }
}