package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.CardReplyAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.CardReply;
import com.hunliji.hljcardcustomerlibrary.models.CardUserReply;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardReplyActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReplyCardUserActivity;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hua_rong on 2017/6/20.
 * 宾客回复
 */

public class CardReplyFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, CardReplyAdapter.OnCardReplyListener {

    private static final String ARG_STATE = "state";
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_arrange_seat)
    Button btnArrangeSeat;
    Unbinder unbinder;
    private long cardId;
    private int state;
    private HljHttpSubscriber subscriber;
    private HljHttpSubscriber pageSubscriber;
    private View footerView;
    private TextView endView;
    private View loadView;
    private CardReplyAdapter adapter;
    private List<CardReply> cardReplyList;
    private HljHttpSubscriber deleteSubscriber;
    private long userId;
    private long lastReplyId;
    private String key;
    private int position;
    private int totalCount;
    private CardReplyActivity activity;
    private long firstReplyId;

    public static CardReplyFragment newInstance(int state) {
        Bundle args = new Bundle();
        args.putInt(ARG_STATE, state);
        CardReplyFragment fragment = new CardReplyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardReplyList = new ArrayList<>();
        activity = (CardReplyActivity) getActivity();
        initFooter();
        initValue();
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_reply___card, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setOnRefreshListener(this);
        emptyView.setHintId(R.string.hint_card_reply_empty_text___card);
        onRefresh(recyclerView);
        initView();
        onNetError();
    }

    private void initValue() {
        Bundle bundle = getArguments();
        User user = UserSession.getInstance()
                .getUser(getContext());
        if (user != null) {
            userId = user.getId();
        }
        if (bundle != null) {
            state = bundle.getInt(ARG_STATE);
            if (state == CardReplyActivity.CARD_STATE_WISH) {
                key = "wish_key" + userId;
                position = 0;
            } else {
                key = "feast_key" + userId;
                position = 1;
            }
        }
        //回复id
        lastReplyId = SPUtils.getLong(getContext(), key, 0);
    }

    private void initFooter() {
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    public void setRefresh(long cardId, int state) {
        this.cardId = cardId;
        this.state = state;
        activity.updateTabText(0, state);
        recyclerView.setVisibility(View.GONE);
        emptyView.hideEmptyView();
        onRefresh(recyclerView);
    }

    private void onNetError() {
        emptyView.setNetworkHint2Id(R.string.label_network_disconnected___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        if (HljCard.isCustomer(getContext())) {
            btnArrangeSeat.setVisibility(View.VISIBLE);
        } else {
            btnArrangeSeat.setVisibility(View.GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        recyclerView.getRefreshableView()
                .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 20));
        recyclerView.setOnRefreshListener(this);
        adapter = new CardReplyAdapter(getContext(), cardReplyList);
        adapter.setOnCardReplyListener(this);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void refresh(Object... params) {
    }

    @Override
    public void onRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(subscriber);
        Observable<HljHttpData<List<CardReply>>> observable = CustomerCardApi.getReplyList(cardId,
                state,
                1);
        subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CardReply>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CardReply>> hljHttpData) {
                        int pageCount = 0;
                        if (hljHttpData != null) {
                            pageCount = hljHttpData.getPageCount();
                            totalCount = hljHttpData.getTotalCount();
                            activity.updateTabText(totalCount, state);
                            adapter.setLastReplyId(lastReplyId);
                            List<CardReply> replies = hljHttpData.getData();
                            if (!CommonUtil.isCollectionEmpty(replies)) {
                                cardReplyList.clear();
                                cardReplyList.addAll(replies);
                                emptyView.hideEmptyView();
                                recyclerView.setVisibility(View.VISIBLE);
                                CardReply cardReply = replies.get(0);
                                if (cardReply != null) {
                                    firstReplyId = cardReply.getId();
                                }
                                activity.updateTabTag(position, firstReplyId > lastReplyId);
                                setLastReplyId();
                            } else {
                                cardReplyList.clear();
                                activity.updateTabTag(position, false);
                                recyclerView.setVisibility(View.GONE);
                                emptyView.showEmptyView();
                            }
                            if (getReplyActivity() != null) {
                                CardReplyActivity activity = getReplyActivity();
                                if (position == activity.getCurrentItemPosition()) {
                                    activity.updateTabTag(position, false);
                                }
                            }
                            adapter.setCardReplyList(cardReplyList);
                        }
                        initPage(pageCount);
                    }
                })
                .setDataNullable(true)
                .setPullToRefreshBase(refreshView)
                .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                .build();
        observable.subscribe(subscriber);
    }

    @OnClick(R2.id.btn_arrange_seat)
    void onArrangeSeat() {
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WEDDING_TABLE_LIST_ACTIVITY)
                .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                .navigation(getContext());
    }

    /**
     * 手动控制 记录用户id_回复id
     */
    public void setLastReplyId() {
        if (firstReplyId > lastReplyId) {
            lastReplyId = firstReplyId;
            SPUtils.put(getContext(), key, firstReplyId);
        }
    }

    private CardReplyActivity getReplyActivity() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return null;
        }
        return (CardReplyActivity) getActivity();
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<CardReply>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CardReply>>>() {
                    @Override
                    public Observable<HljHttpData<List<CardReply>>> onNextPage(int page) {
                        return CustomerCardApi.getReplyList(cardId, state, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CardReply>>>() {

                    @Override
                    public void onNext(HljHttpData<List<CardReply>> hljHttpData) {
                        if (hljHttpData != null && hljHttpData.getData() != null) {
                            cardReplyList.addAll(hljHttpData.getData());
                            adapter.setCardReplyList(cardReplyList);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        pageObservable.subscribe(pageSubscriber);
    }


    @Override
    public void onCardReplyLongClick(final CardReply cardReply, final int position) {
        final Dialog dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_msg_notice___card);
            Button btnConfirm = window.findViewById(R.id.btn_confirm);
            Button btnCancel = window.findViewById(R.id.btn_cancel);
            btnConfirm.setText(R.string.label_card_reply_delete___card);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onCardDelete(cardReply, position);
                }
            });
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(getContext());
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            dialog.show();
        }
    }

    @Override
    public void onCardReply(CardReply cardReply, int position) {
        CardUserReply cardUserReply = cardReply.getCardUserReply();
        if (cardUserReply != null && cardUserReply.getId() > 0) {
            Intent intent = new Intent(getContext(), ReplyCardUserActivity.class);
            intent.putExtra("id", cardUserReply.getId());
            intent.putExtra("position", position);
            startActivityForResult(intent, HljCard.RequestCode.REPLY_CARD_REPLY);
            getActivity().overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljCard.RequestCode.REPLY_CARD_REPLY:
                    if (data != null) {
                        int position = data.getIntExtra("position", 0);
                        int status = data.getIntExtra("status", 0);
                        CardReply cardReply = adapter.getItem(position);
                        CardUserReply cardUserReply = cardReply.getCardUserReply();
                        if (cardUserReply != null) {
                            cardUserReply.setStatus(status);
                        }
                        adapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    @Override
    public void onCardDelete(CardReply cardReply, final int position) {
        CommonUtil.unSubscribeSubs(deleteSubscriber);
        Observable observable = CustomerCardApi.deleteReply(cardReply.getId());
        deleteSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        cardReplyList.remove(position);
                        totalCount--;
                        activity.updateTabText(totalCount, state);
                        adapter.setCardReplyList(cardReplyList);
                        if (!CommonUtil.isCollectionEmpty(cardReplyList)) {
                            long id = cardReplyList.get(0)
                                    .getId();
                            if (id <= lastReplyId) {
                                activity.updateTabTag(position, false);
                            }
                        }
                        ToastUtil.showCustomToast(getContext(),
                                R.string.label_card_delete_success___card);
                    }
                })
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(deleteSubscriber);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(subscriber, deleteSubscriber, pageSubscriber);
    }

}
