package com.hunliji.marrybiz.view.event;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.event.SignUpRecyclerAdapter;
import com.hunliji.marrybiz.api.event.EventApi;
import com.hunliji.marrybiz.model.event.EventPoint;
import com.hunliji.marrybiz.model.wrapper.HljHttpSignUpsData;
import com.hunliji.marrybiz.util.TimeUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 报名列表
 * Created by chen_bin on 2016/9/6 0006.
 */
public class SignUpListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<SignUpInfo>, SignUpRecyclerAdapter
        .OnCallListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View headerView;
    private View footerView;
    private View endView;
    private View loadView;
    private Dialog filterDialog;
    private Dialog limitPointDialog;
    private Handler handler;
    private SignUpRecyclerAdapter adapter;
    private HeaderViewHolder headerViewHolder;
    private EventInfo eventInfo;
    private EventPoint eventPoint;
    private long id;
    private int status;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber getSortSub;
    private HljHttpSubscriber getPaySub;
    private HljHttpSubscriber getPointSub;
    private static final String HLJ_KEFU_PHONE = "4001599090";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        setOkText(R.string.label_event_page);
        id = getIntent().getLongExtra("id", 0);
        eventInfo = new EventInfo();
        handler = new Handler();
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        headerView = View.inflate(this, R.layout.sign_up_header_item, null);
        footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new SignUpRecyclerAdapter(this);
        adapter.setOnCallListener(this);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            status = 0;
            Observable<EventPoint> eventPointObb = EventApi.getEventPointObb();
            final Observable<HljHttpSignUpsData> signUpsObb = EventApi.getSignUpListObb(id,
                    status,
                    1,
                    HljCommon.PER_PAGE);
            Observable<ResultZip> observable = Observable.zip(eventPointObb,
                    signUpsObb,
                    new Func2<EventPoint, HljHttpSignUpsData, ResultZip>() {
                        @Override
                        public ResultZip call(
                                EventPoint eventPoint, HljHttpSignUpsData signUpsData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.eventPoint = eventPoint;
                            resultZip.signUpsData = signUpsData;
                            return resultZip;
                        }
                    });
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSub);
        }
    }

    private class ResultZip {
        EventPoint eventPoint;
        HljHttpSignUpsData signUpsData;
    }

    private void setData(ResultZip resultZip) {
        if (resultZip.signUpsData == null || resultZip.signUpsData.getEventInfo() == null ||
                resultZip.eventPoint == null) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            eventPoint = resultZip.eventPoint;
            eventInfo = resultZip.signUpsData.getEventInfo();
            adapter.setEventInfo(eventInfo);
            handler.removeCallbacks(runnable);
            handler.post(runnable);
            headerViewHolder = (HeaderViewHolder) headerView.getTag();
            if (headerViewHolder == null) {
                adapter.setHeaderView(headerView);
                headerViewHolder = new HeaderViewHolder(headerView);
                headerView.setTag(headerViewHolder);
            }
            boolean isDataEmpty = CommonUtil.isCollectionEmpty(resultZip.signUpsData.getData());
            headerViewHolder.tvSignUpCount.setText(String.valueOf(resultZip.signUpsData
                    .getTotalCount()));
            headerViewHolder.btnFiltrate.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
            headerViewHolder.setShowEmptyView(isDataEmpty);
            adapter.setFooterView(isDataEmpty ? null : footerView);
            adapter.setSignUps(resultZip.signUpsData.getData());
            initPagination(resultZip.signUpsData.getPageCount());
        }
    }

    //加载更多
    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSignUpsData> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSignUpsData>() {
                    @Override
                    public Observable<HljHttpSignUpsData> onNextPage(int page) {
                        return EventApi.getSignUpListObb(id, status, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<SignUpInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<SignUpInfo>> listHljHttpData) {
                        adapter.addSignUps(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private void getSignUpsDataBySort(int status) {
        this.status = status;
        CommonUtil.unSubscribeSubs(getSortSub);
        getSortSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<SignUpInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<SignUpInfo>> listHljHttpData) {
                        setSignUpsDataBySort(listHljHttpData);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setSignUpsDataBySort(null);
                    }
                })
                .setDataNullable(true)
                .setProgressBar(progressBar)
                .build();
        EventApi.getSignUpListObb(id, status, 1, 20)
                .subscribe(getSortSub);
    }

    private void setSignUpsDataBySort(HljHttpData<List<SignUpInfo>> listHljHttpData) {
        int pageCount = 0;
        List<SignUpInfo> signUps = null;
        if (listHljHttpData != null) {
            pageCount = listHljHttpData.getPageCount();
            signUps = listHljHttpData.getData();
        }
        boolean isDataEmpty = CommonUtil.isCollectionEmpty(signUps);
        if (headerViewHolder != null) {
            headerViewHolder.setShowEmptyView(isDataEmpty);
        }
        adapter.setFooterView(isDataEmpty ? null : footerView);
        adapter.setSignUps(signUps);
        initPagination(pageCount);
    }

    //获取当前活动需要的点数
    private void getPayViewPhone() {
        if (getPaySub == null || getPaySub.isUnsubscribed()) {
            getPaySub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<EventPoint>() {
                        @Override
                        public void onNext(EventPoint object) {
                            eventPoint.setNum(object.getNum());
                            eventPoint.setBalance(object.getBalance());
                            if (eventPoint.getBalance() < eventPoint.getNum()) {
                                showLimitPointDialog();
                            }
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            EventApi.getPayViewPhoneObb(eventInfo.getId())
                    .subscribe(getPaySub);
        }
    }

    //获取自己的活动点信息
    private void getEventPoint() {
        if (getPointSub == null || getPointSub.isUnsubscribed()) {
            getPointSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<EventPoint>() {
                        @Override
                        public void onNext(EventPoint eventPoint) {
                            SignUpListActivity.this.eventPoint = eventPoint;
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            EventApi.getEventPointObb()
                    .subscribe(getPointSub);
        }
    }

    public class HeaderViewHolder extends ExtraBaseViewHolder {
        @BindView(R.id.tv_event_status)
        TextView tvEventStatus;
        @BindView(R.id.tv_sign_up_status)
        TextView tvSignUpStatus;
        @BindView(R.id.tv_sign_up_count)
        TextView tvSignUpCount;
        @BindView(R.id.btn_filtrate)
        Button btnFiltrate;
        @BindView(R.id.empty_view)
        HljEmptyView emptyView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnFiltrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog();
                }
            });
            emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
            emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
                @Override
                public void onNetworkErrorClickListener() {
                    getSignUpsDataBySort(status);
                }
            });
        }

        private void setShowEmptyView(boolean showEmptyView) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height;
            if (showEmptyView) {
                emptyView.showEmptyView();
                emptyView.setVisibility(View.VISIBLE);
                height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                emptyView.hideEmptyView();
                emptyView.setVisibility(View.GONE);
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            } else {
                itemView.getLayoutParams().width = width;
                itemView.getLayoutParams().height = height;
            }
        }
    }

    @Override
    public void onItemClick(int position, SignUpInfo signUpInfo) {
        if (eventPoint != null && eventPoint.getBalance() > 0) {
            return;
        }
        if (eventInfo.isSignUpEnd() && !signUpInfo.isMerchantPay()) {
            getPayViewPhone();
        } else if (!TextUtils.isEmpty(signUpInfo.getTel()) && signUpInfo.getTel()
                .contains("*")) {
            DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_point_limit5),
                    getString(R.string.label_contact2),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                callUp(Uri.parse("tel:" + HLJ_KEFU_PHONE));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null)
                    .show();
        }
    }

    @Override
    public void onCall(final SignUpInfo signUpInfo) {
        if (!TextUtils.isEmpty(signUpInfo.getTel()) && signUpInfo.getTel()
                .trim()
                .length() > 0) {
            DialogUtil.createDoubleButtonDialog(SignUpListActivity.this,
                    getString(R.string.label_call_msg),
                    getString(R.string.btn_adv_action3),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                callUp(Uri.parse("tel:" + signUpInfo.getTel()
                                        .trim()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null)
                    .show();
        }
    }

    @Override
    public void onOkButtonClick() {
        if (eventInfo != null && !TextUtils.isEmpty(eventInfo.getLink())) {
            Intent intent = new Intent(this, HljWebViewActivity.class);
            intent.putExtra("path", eventInfo.getLink());
            intent.putExtra("title", eventInfo.getTitle());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    //活动点不足的Dialog
    private void showLimitPointDialog() {
        if (limitPointDialog != null && limitPointDialog.isShowing()) {
            return;
        }
        if (limitPointDialog == null) {
            limitPointDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_point_limit),
                    null,
                    getString(R.string.label_contact2),
                    getString(R.string.label_cancel___cm),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            limitPointDialog.dismiss();
                            try {
                                callUp(Uri.parse("tel:" + HLJ_KEFU_PHONE));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null);
        }
        TextView tvAlertMsg = limitPointDialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(CommonUtil.fromHtml(this,
                getString(R.string.label_need_point_count,
                        eventPoint.getNum(),
                        eventPoint.getBalance()).replace("\n", "<br />")));
        limitPointDialog.show();
    }

    //分类排序的Dialog
    private void showFilterDialog() {
        if (filterDialog != null && filterDialog.isShowing()) {
            return;
        }
        if (filterDialog == null) {
            filterDialog = new Dialog(this, R.style.BubbleDialogTheme);
            filterDialog.setContentView(R.layout.dialog_filtrate_menu);
            Window win = filterDialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        LinearLayout actionLayout = filterDialog.findViewById(R.id.action_layout);
        LinearLayout actionLayout2 = filterDialog.findViewById(R.id.action_layout2);
        filterDialog.findViewById(R.id.btn_action)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                        getSignUpsDataBySort(status);
                    }
                });
        Button btnAction2 = filterDialog.findViewById(R.id.btn_action2);
        btnAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                getSignUpsDataBySort(status);
            }
        });
        filterDialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                    }
                });
        if ((!eventInfo.isSignUpEnd() || !eventInfo.isDrawStatus()) && eventInfo.getWinnerLimit()
                > 0) {
            actionLayout.setVisibility(View.VISIBLE);
            actionLayout2.setVisibility(View.GONE);
        } else {
            actionLayout.setVisibility(View.VISIBLE);
            actionLayout2.setVisibility(View.VISIBLE);
            btnAction2.setText(eventInfo.getWinnerLimit() > 0 ? R.string.label_see_prize_persons
                    : R.string.label_see_not_present_persons);
        }
        filterDialog.show();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (headerViewHolder == null) {
                return;
            }
            if (eventInfo == null || eventInfo.getSignUpEndTime() == null) {
                return;
            }
            long millis = eventInfo.getSignUpEndTime()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
            if (millis > 0) {
                String str = TimeUtil.countDownMillisFormat4(SignUpListActivity.this, millis);
                headerViewHolder.tvEventStatus.setText(R.string.label_sign_up_ing);
                headerViewHolder.tvSignUpStatus.setVisibility(View.VISIBLE);
                headerViewHolder.tvSignUpStatus.setText(eventInfo.getWinnerLimit() > 0 ?
                        getString(R.string.label_winner_limit_time,
                        str) : getString(R.string.label_limit_time, str));
                handler.postDelayed(this, 1000);
            } else {
                headerViewHolder.tvEventStatus.setText(eventInfo.isDrawStatus() && eventInfo
                        .getSignUpCount() > 0 && eventInfo.getWinnerLimit() > 0 ? R.string
                        .label_prize_result_published : R.string.label_sign_up_completed);
                if (!eventInfo.isPay()) {
                    headerViewHolder.tvSignUpStatus.setVisibility(View.GONE);
                } else {
                    headerViewHolder.tvSignUpStatus.setVisibility(View.VISIBLE);
                    headerViewHolder.tvSignUpStatus.setText(R.string.label_buy_success);
                }
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PAY:
                    setResult(RESULT_OK);
                    getEventPoint();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        handler.removeCallbacks(runnable);
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, getSortSub, getPaySub, getPointSub);
    }
}