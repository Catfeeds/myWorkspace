package com.hunliji.marrybiz.fragment.experience;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.experience.AdvListAdapter;
import com.hunliji.marrybiz.api.experienceshop.ExperienceShopApi;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.HljExperienceData;
import com.hunliji.marrybiz.view.experience.AdvDetailActivity;
import com.hunliji.marrybiz.view.experience.AdvListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/12/19.体验店推荐订单列表
 */

public class AdvListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener, OnItemClickListener {

    public static final String ARG_ORDER_STATUS = "order_status";
    public static final String ARG_ORDER_TAB = "order_tab";
    public static final String ARG_SEARCH_KEYWORD = "search_keyword";
    public static final String ARG_ADV_TYPE = "adv_type";

    public static final int STATUS_RESULT_CODE = 1;

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private ArrayList<AdvDetail> experienceShopDetails;
    private AdvListAdapter adapter;
    private FooterViewHolder footerViewHolder;
    private Map<String, Object> map;
    private String keyWord;
    private int status;
    private String tab;
    private Dialog expiredDialog;
    private int advType;

    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber refreshSubscriber;

    public static AdvListFragment newInstance(
            int status,
            String keyword,
            String tab,
            int advType) {
        Bundle args = new Bundle();
        AdvListFragment fragment = new AdvListFragment();
        args.putInt(ARG_ORDER_STATUS, status);
        args.putString(ARG_SEARCH_KEYWORD, keyword);
        args.putString(ARG_ORDER_TAB, tab);
        args.putInt(ARG_ADV_TYPE, advType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        experienceShopDetails = new ArrayList<>();
        map = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        adapter = new AdvListAdapter(getContext());
        View footerView = inflater.inflate(R.layout.hlj_foot_no_more___cm, container, false);
        footerViewHolder = new FooterViewHolder(footerView);
        adapter.setFooterView(footerView);
        adapter.setList(experienceShopDetails);
        adapter.setOnItemClickListener(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt(ARG_ORDER_STATUS, AdvDetail.ORDER_ALL);
            keyWord = getArguments().getString(ARG_SEARCH_KEYWORD);
            tab = getArguments().getString(ARG_ORDER_TAB);
            advType = getArguments().getInt(ARG_ADV_TYPE);
        }
        adapter.setAdvType(advType);
        emptyView.setEmptyDrawableId(status == AdvDetail.ORDER_SEARCH ? R.mipmap
                .icon_empty_common : R.mipmap.icon_empty_order);
        emptyView.setHintText(status == AdvDetail.ORDER_SEARCH ? "未搜到相关推荐单" : "暂无相关推荐单");
        onRefresh(recyclerView);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (status == AdvDetail.ORDER_SEARCH && CommonUtil.isEmpty(keyWord)) {
            return;
        }
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljExperienceData<List<AdvDetail>>>() {

                        @Override
                        public void onNext(
                                HljExperienceData<List<AdvDetail>> listHljExperienceData) {
                            if (getContext() instanceof AdvListActivity) {
                                AdvListActivity activity = (AdvListActivity) getContext();
                                assert activity != null;
                                if (listHljExperienceData.getUnreadCount() > 0) {
                                    activity.showTabDotView(AdvDetail.ORDER_UN_READ + 1);
                                } else {
                                    activity.hideTabDotView(AdvDetail.ORDER_UN_READ + 1);
                                }
                            }
                            experienceShopDetails.clear();
                            experienceShopDetails.addAll(listHljExperienceData.getData());
                            adapter.notifyDataSetChanged();

                            initPage(listHljExperienceData.getPageCount());
                        }

                    })
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            map.clear();
            if (status > AdvDetail.ORDER_ALL) {
                map.put("tab", tab);
            }

            if (!CommonUtil.isEmpty(keyWord)) {
                map.put("keyword", keyWord);
            }
            if (advType == AdvListActivity.ADV_FOR_OTHERS) {
                // 推荐客资的Type为类型2
                map.put("type", 2);
            }
            map.put("page", 1);
            ExperienceShopApi.getExperienceShopOrderListObb(map)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljExperienceData<List<AdvDetail>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljExperienceData<List<AdvDetail>>>() {
                    @Override
                    public Observable<HljExperienceData<List<AdvDetail>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        map.put("page", page);
                        return ExperienceShopApi.getExperienceShopOrderListObb(map);
                    }
                })
                .setLoadView(footerViewHolder.loadView)
                .setEndView(footerViewHolder.endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljExperienceData<List<AdvDetail>>>() {
                    @Override
                    public void onNext(HljExperienceData<List<AdvDetail>> data) {
                        experienceShopDetails.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0 && params[0] instanceof String) {
            keyWord = (String) params[0];
            onRefresh(recyclerView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        if (adapter != null) {
            adapter.cancelAllTimers();
        }
    }

    @Override
    public void onItemClick(int position, Object object) {
        AdvDetail detail = (AdvDetail) object;
        if (detail != null) {
            if (detail.getStatus() != AdvDetail.ORDER_HAVE_EXPIRED) {
                Intent intent = new Intent(getContext(), AdvDetailActivity.class);
                intent.putExtra(AdvDetailActivity.ARG_ID, detail.getId());
                intent.putExtra(AdvDetailActivity.ARG_ITEM_POSITION, position);
                intent.putExtra(AdvDetailActivity.ARG_ADV_TYPE, advType);
                startActivityForResult(intent, STATUS_RESULT_CODE);
            } else {
                showExpiredDialog();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case STATUS_RESULT_CODE:
                    if (data != null) {
                        int position = data.getIntExtra(AdvDetailActivity.ARG_ITEM_POSITION,
                                0);
                        int status = data.getIntExtra(AdvDetailActivity.ARG_ITEM_STATUS,
                                AdvDetail.ORDER_ALL);
                        boolean isCome = data.getBooleanExtra(AdvDetailActivity
                                        .ARG_ITEM_IS_COME,
                                true);
                        if (status != AdvDetail.ORDER_ALL) {
                            experienceShopDetails.get(position)
                                    .setStatus(status);
                            experienceShopDetails.get(position)
                                    .setCome(isCome);
                            adapter.notifyItemChanged(position);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showExpiredDialog() {
        if (expiredDialog != null && expiredDialog.isShowing()) {
            return;
        }
        if (expiredDialog == null) {
            expiredDialog = DialogUtil.createSingleButtonDialog(getContext(),
                    getContext().getString(R.string.label_experience_shop_detail_expired),
                    "知道了",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            expiredDialog.dismiss();
                        }
                    });
        }
        expiredDialog.show();
    }

    static class FooterViewHolder {
        @BindView(R.id.no_more_hint)
        TextView endView;
        @BindView(R.id.xlistview_footer_progressbar)
        ProgressBar xlistviewFooterProgressbar;
        @BindView(R.id.xlistview_footer_hint_textview)
        TextView xlistviewFooterHintTextview;
        @BindView(R.id.loading)
        LinearLayout loadView;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
