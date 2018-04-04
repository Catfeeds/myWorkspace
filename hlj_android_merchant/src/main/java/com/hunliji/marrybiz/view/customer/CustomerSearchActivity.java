package com.hunliji.marrybiz.view.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.customer.CustomerRecyclerAdapter;
import com.hunliji.marrybiz.api.customer.CustomerApi;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 客资搜索activity
 * Created by jinxin on 2017/8/10 0010.
 */

public class CustomerSearchActivity extends HljBaseActivity implements CheckableLinearGroup
        .OnCheckedChangeListener, ScrollableHelper.ScrollableContainer, PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<MerchantCustomer>, TextView
        .OnEditorActionListener {

    final String NICK = "nick";//用户名
    final String PHONE = "phone";//电话
    final String NAME = "name";//姓名
    final String SEARCH_KEY = "search";

    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.edit_search)
    ClearableEditText editSearch;
    @BindView(R.id.layout_search)
    LinearLayout layoutSearch;
    @BindView(R.id.check_group)
    CheckableLinearGroup checkGroup;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private String currentTag;
    private String currentKey;
    private String nick;
    private String name;
    private String phone;
    private View footerView;
    private View endView;
    private View loadView;
    private CustomerRecyclerAdapter customerListAdapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private LinearLayoutManager manager;
    private Map<String, Subscription> subscriptionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
    }

    private void initConstant() {
        currentKey = null;
        currentTag = PHONE;

        footerView = LayoutInflater.from(this)
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);

        customerListAdapter = new CustomerRecyclerAdapter(this);
        customerListAdapter.setFooterView(footerView);
        customerListAdapter.setOnItemClickListener(this);
        customerListAdapter.setSearchWord(currentKey);
        customerListAdapter.setType(CustomerRecyclerAdapter.ITEM_PHONE);
    }

    private void initWidget() {
        editSearch.setOnEditorActionListener(this);
        scrollableLayout.setMode(PullToRefreshBase.Mode.DISABLED);
        scrollableLayout.getRefreshableView()
                .setHeaderView(layoutHeader);
        scrollableLayout.getRefreshableView()
                .getHelper()
                .setCurrentScrollableContainer(this);
        checkGroup.setOnCheckedChangeListener(this);

        emptyView.setHintId(R.string.label_no_customer_search_result);
        emptyView.showEmptyView();
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(customerListAdapter);
    }


    @Override
    public void onItemClick(int position, MerchantCustomer customer) {
        Intent intent = new Intent(this, WSMerchantChatActivity.class);
        intent.putExtra("user", customer.getUser());
        intent.putExtra("channelId", customer.getChannelId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.check_phone:
                //电话
                name = null;
                nick = null;
                phone = currentKey;
                currentTag = PHONE;
                customerListAdapter.setType(CustomerRecyclerAdapter.ITEM_PHONE);
                break;
            case R.id.check_account_name:
                //用户名
                nick = currentKey;
                name = null;
                phone = null;
                currentTag = NICK;
                customerListAdapter.setType(CustomerRecyclerAdapter.ITEM_NAME);
                break;
            case R.id.check_name:
                //姓名
                name = currentKey;
                nick = null;
                phone = null;
                currentTag = NAME;
                customerListAdapter.setType(CustomerRecyclerAdapter.ITEM_NAME);
                break;
            default:
                break;
        }

        if(TextUtils.isEmpty(currentKey)){
            showEmptyAdapter();
        }else{
            emptyView.hideEmptyView();
            onRefresh(null);
        }
    }

    private void showEmptyAdapter(){
        emptyView.showEmptyView();
        customerListAdapter.clearItems();
    }

    private void onSearchKey() {
        switch (currentTag) {
            case NICK:
                nick = currentKey;
                name = null;
                phone = null;
                break;
            case PHONE:
                name = null;
                nick = null;
                phone = currentKey;
                break;
            case NAME:
                name = currentKey;
                nick = null;
                phone = null;
                break;
            default:
                break;
        }
    }

    public void saveSubscription(String key, Subscription s) {
        if (subscriptionMap == null) {
            subscriptionMap = new HashMap<>();
        }
        subscriptionMap.put(key, s);
    }

    public void unSubscribe(String key) {
        if (subscriptionMap != null) {
            Subscription subscription = subscriptionMap.get(key);
            if (subscription != null) {
                subscription.unsubscribe();
                subscriptionMap.remove(key);
            }
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public boolean isDisable() {
        return false;
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }

        refreshSubscriber = getRefreshSubscriber(refreshView);
        CustomerApi.getCustomerList(nick, phone, name, 1)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<MerchantCustomer>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<MerchantCustomer>>>() {
                    @Override
                    public Observable<HljHttpData<List<MerchantCustomer>>> onNextPage(int page) {
                        return CustomerApi.getCustomerList(nick, phone, name, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantCustomer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<MerchantCustomer>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            customerListAdapter.addCustomerList(listHljHttpData.getData());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private HljHttpSubscriber getRefreshSubscriber(View refreshView) {
        HljHttpSubscriber refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantCustomer>>>() {

                    @Override
                    public void onNext(HljHttpData<List<MerchantCustomer>> listHljHttpData) {
                        if (manager.findFirstVisibleItemPosition() > 5) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                        }
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            customerListAdapter.setCustomerList(listHljHttpData.getData());
                            if (listHljHttpData.getPageCount() > 1) {
                                initPagination(listHljHttpData.getPageCount());
                            }
                        }
                    }
                })
                .build();
        return refreshSubscriber;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        unSubscribe(SEARCH_KEY);
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard(editSearch);
            unSubscribe(SEARCH_KEY);
            currentKey = editSearch.getText()
                    .toString();
            if (!TextUtils.isEmpty(currentKey)) {
                currentKey = currentKey.trim();
            }
            if (TextUtils.isEmpty(currentKey)) {
                showEmptyAdapter();
                return true;
            }
            onSearchKey();
            customerListAdapter.setSearchWord(currentKey);
            HljHttpSubscriber searchSubscriber = getRefreshSubscriber(recyclerView);
            Subscription subscription = CustomerApi.getCustomerList(nick, phone, name, 1)
                    .subscribe(searchSubscriber);
            saveSubscription(SEARCH_KEY, subscription);
        }
        return true;
    }
}
