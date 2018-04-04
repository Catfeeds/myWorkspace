package me.suncloud.marrymemo.view.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.api.search.SearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.SelectMerchantAdapter;
import me.suncloud.marrymemo.widget.ClearableEditText;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/5/9.晒婚纱照选择商家
 */

public class SelectMerchantListActivity extends HljBaseActivity implements View.OnClickListener,
        OnItemClickListener {

    private final static String SORT_SCORE = "score";
    private final static SearchApi.SubType SEARCH_TYPE_MERCHANT = SearchApi.SubType
            .SUB_SEARCH_TYPE_MERCHANT;
    @BindView(R.id.et_keyword)
    ClearableEditText etKeyword;
    @BindView(R.id.keyword_layout)
    LinearLayout keywordLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private SelectMerchantAdapter adapter;
    private HeaderViewHolder headerViewHolder;
    private FooterViewHolder footerViewHolder;
    private CustomViewHolder customViewHolder;
    private HljHttpSubscriber consumedSub;
    private HljHttpSubscriber consumedPageSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber searchSub;

    private ArrayList<Merchant> merchants;
    private String keyword;
    private int currentPageCount;
    private long lastSearchTime;//记录上次搜索的时间戳
    SearchFilter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_merchant_list);
        ButterKnife.bind(this);
        hideDividerView();
        initValue();
        initView();
        initConsumedLoad();
    }

    private void initValue() {
        merchants = new ArrayList<>();
        searchFilter = new SearchFilter();
        searchFilter.setPropertyId(Merchant.PROPERTY_WEDDING_DRESS_PHOTO);
    }

    private void initView() {
        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH && etKeyword.getText()
                        .length() > 0) {
                    keyword = etKeyword.getText()
                            .toString();
                    onSearch();
                    hideKeyboard(etKeyword);
                    return false;
                } else {
                    return true;
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        View headerView = getLayoutInflater().inflate(R.layout.select_merchant_list_header, null);
        View footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        View customMerchantView = getLayoutInflater().inflate(R.layout
                        .select_custom_merchant_footer,
                null);
        headerViewHolder = new HeaderViewHolder(headerView);
        footerViewHolder = new FooterViewHolder(footerView);
        customViewHolder = new CustomViewHolder(customMerchantView);
        customViewHolder.customMerchantView.setOnClickListener(this);
        adapter = new SelectMerchantAdapter(this, merchants);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        adapter.setCustomMerchantView(customMerchantView);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initConsumedLoad() {
        if (consumedSub == null || consumedSub.isUnsubscribed()) {
            customViewHolder.customMerchantView.setVisibility(View.GONE);
            consumedSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Merchant>> data) {
                            merchants.clear();
                            merchants.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                            headerViewHolder.tvMerchantItemCount.setVisibility(View.VISIBLE);
                            headerViewHolder.tvMerchantItemCount.setText("您已消费的商家");
                            initConsumedPaging(data.getPageCount());
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            SearchApi.getConsumedMerchantsObb(1)
                    .subscribe(consumedSub);
        }
    }

    private void initConsumedPaging(final int pageCount) {
        CommonUtil.unSubscribeSubs(consumedPageSub);
        Observable<HljHttpData<List<Merchant>>> pageOb = PaginationTool.buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HljHttpData<List<Merchant>>>() {
                    @Override
                    public Observable<HljHttpData<List<Merchant>>> onNextPage(int page) {
                        return SearchApi.getConsumedMerchantsObb(page);
                    }
                })
                .setEndView(footerViewHolder.noMoreHint)
                .setLoadView(footerViewHolder.loading)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        consumedPageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Merchant>> data) {
                        merchants.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumedPageSub);
    }

    /**
     * 搜索酒店
     */
    private void onSearch() {
        //        long nowTime = Calendar.getInstance()
        //                .getTimeInMillis();
        //        if (nowTime - lastSearchTime < 200) {
        //            return;
        //        } else {
        //            lastSearchTime = nowTime;
        //        }
        CommonUtil.unSubscribeSubs(searchSub);
        searchSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ServiceSearchResult>() {
                    @Override
                    public void onNext(ServiceSearchResult serviceSearchResult) {
                        merchants.clear();
                        customViewHolder.customMerchantView.setVisibility(View.VISIBLE);
                        if (serviceSearchResult.getMerchantsSearchResult()
                                .isEmpty()) {
                            headerViewHolder.tvMerchantItemCount.setVisibility(View.GONE);
                            customViewHolder.topLineLayout.setVisibility(View.GONE);
                        } else {
                            merchants.addAll(serviceSearchResult.getMerchantsSearchResult()
                                    .getMerchants());
                            if (serviceSearchResult.getMerchantsSearchResult()
                                    .getPageCount() > 1) {
                                customViewHolder.customMerchantView.setVisibility(View.GONE);
                            }
                            customViewHolder.topLineLayout.setVisibility(View.VISIBLE);
                            headerViewHolder.tvMerchantItemCount.setVisibility(View.VISIBLE);
                            headerViewHolder.tvMerchantItemCount.setText(getString(R.string
                                            .label_merchant_item_count,
                                    serviceSearchResult.getMerchantsSearchResult()
                                            .getTotal()));
                        }
                        adapter.notifyDataSetChanged();
                        initPaging(serviceSearchResult.getMerchantsSearchResult()
                                .getPageCount());
                    }

                })
                .build();
        SearchApi.searchService(0, keyword, SEARCH_TYPE_MERCHANT, searchFilter, SORT_SCORE, 1)
                .subscribe(searchSub);
    }

    private void initPaging(final int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<ServiceSearchResult> pageOb = PaginationTool.buildPagingObservable(recyclerView,
                pageCount,
                new PagingListener<ServiceSearchResult>() {
                    @Override
                    public Observable<ServiceSearchResult> onNextPage(int page) {
                        currentPageCount = page;
                        return SearchApi.searchService(0,
                                keyword,
                                SEARCH_TYPE_MERCHANT,
                                searchFilter,
                                SORT_SCORE,
                                page);
                    }
                })
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ServiceSearchResult>() {
                    @Override
                    public void onNext(ServiceSearchResult serviceSearchResult) {
                        if (currentPageCount >= pageCount) {
                            customViewHolder.customMerchantView.setVisibility(View.VISIBLE);
                        }
                        if (!serviceSearchResult.getMerchantsSearchResult()
                                .isEmpty()) {
                            merchants.addAll(serviceSearchResult.getMerchantsSearchResult()
                                    .getMerchants());
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();

        pageOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(etKeyword);
        super.onBackPressed();
    }

    @OnTextChanged(R.id.et_keyword)
    void afterTextChanged(Editable s) {
        keyword = s.toString();
        customViewHolder.tvCustomMerchantName.setText("自定义商家名称：" + keyword);
        if (TextUtils.isEmpty(keyword)) {
            initConsumedLoad();
        }
        if (s.length() >= 2) {
            onSearch();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_merchant_view:
                if (!TextUtils.isEmpty(keyword)) {
                    Intent intent = getIntent();
                    intent.putExtra("unRecordedMerchant", keyword);
                    setResult(Activity.RESULT_OK, intent);
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(consumedSub, consumedPageSub, searchSub, pageSub);
    }

    @Override
    public void onItemClick(int position, Object object) {
        Merchant merchant = (Merchant) object;
        if (merchant != null) {
            Intent intent = getIntent();
            intent.putExtra("merchant", merchant);
            setResult(Activity.RESULT_OK, intent);
            onBackPressed();
        }
    }

    static class HeaderViewHolder {
        @BindView(R.id.tv_merchant_item_count)
        TextView tvMerchantItemCount;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class FooterViewHolder {
        @BindView(R.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R.id.xlistview_footer_progressbar)
        ProgressBar xlistviewFooterProgressbar;
        @BindView(R.id.xlistview_footer_hint_textview)
        TextView xlistviewFooterHintTextview;
        @BindView(R.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    static class CustomViewHolder {
        @BindView(R.id.tv_custom_merchant_name)
        TextView tvCustomMerchantName;
        @BindView(R.id.custom_merchant_view)
        LinearLayout customMerchantView;
        @BindView(R.id.top_line_layout)
        View topLineLayout;

        CustomViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
