package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.ReceiveCashRecyclerAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReceiveGiftCashActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReplyCardUserActivity;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpCardData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/2/7.收到的礼物
 */

public class ReceiveGiftCashFragment extends RefreshFragment implements View.OnClickListener,
        ReceiveCashRecyclerAdapter.OnHiddenClickListener, ReceiveCashRecyclerAdapter
                .OnReplyGiftCashClickListener {
    public static final String FSG_CASH_TYPE = "type";
    public static final String PREF_NAME_FOR_IDS = "user_cash_last_id_";
    public static final int CASH_TYPE = 2;
    public static final int GIFT_TYPE = 1;

    Unbinder unbinder;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_balance_empty_tip)
    TextView tvBalanceEmptyTip;
    @BindView(R2.id.action_gift_empty)
    TextView actionGiftEmpty;
    @BindView(R2.id.gift_empty_view)
    LinearLayout giftEmptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.iv_card_view)
    RoundedImageView ivCardView;
    @BindView(R2.id.poster_layout)
    RelativeLayout posterLayout;
    private ArrayList<UserGift> userGifts;
    private ReceiveCashRecyclerAdapter adapter;

    private int type;//1礼物 2礼金 。不传为全部
    private boolean isFromCard; //从请帖进入，引导发布请帖直接返回
    private long cashLastId;
    private User user;


    private Realm realm;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber hiddenSubscriber;

    public ReceiveGiftCashFragment() {}

    public static ReceiveGiftCashFragment newInstance(int type) {
        Bundle args = new Bundle();
        ReceiveGiftCashFragment fragment = new ReceiveGiftCashFragment();
        args.putInt(FSG_CASH_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt(FSG_CASH_TYPE, 0);
        }
        initValue();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_cash, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initLoad();
        readAll();
    }

    private void initValue() {
        userGifts = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        isFromCard = getActivity().getIntent()
                .getBooleanExtra("is_from_card", false);
        user = UserSession.getInstance()
                .getUser(getContext());
        //用户id_礼金id
        String userCashLastId = getContext().getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE)
                .getString(PREF_NAME_FOR_IDS + type, "0_0");
        String[] split = userCashLastId.split("_");
        long userId = Long.valueOf(split[0]);
        if (user != null && user.getId() == userId) {
            cashLastId = Long.valueOf(split[1]);
        }
    }

    private void initView() {
        if (type == CASH_TYPE) {
            tvBalanceEmptyTip.setText(getString(R.string.label_balance_cash_empty_tip___card));
        } else {
            tvBalanceEmptyTip.setText(getString(R.string.label_balance_gift_empty_tip___card));
        }
        actionGiftEmpty.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ReceiveCashRecyclerAdapter(getActivity(), userGifts);
        adapter.setOnHiddenClickListener(this);
        adapter.setOnReplyGiftCashClickListener(this);
        adapter.setCashLastId(cashLastId);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCardData<List<UserGift>>>() {
                        @Override
                        public void onNext(HljHttpCardData<List<UserGift>> data) {
                            //设置总金额
                            if (data.getData() == null || data.getData()
                                    .size() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                giftEmptyView.setVisibility(View.VISIBLE);
                            } else {
                                if (user != null) {
                                    long cashId = data.getData()
                                            .get(0)
                                            .getId();
                                    long userId = user.getId();
                                    //记录用户id_礼金id
                                    getContext().getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                                            Context.MODE_PRIVATE)
                                            .edit()
                                            .putString(PREF_NAME_FOR_IDS + type,
                                                    userId + "_" + cashId)
                                            .apply();
                                }
                                giftEmptyView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                userGifts.clear();
                                userGifts.addAll(data.getData());


                                adapter.notifyDataSetChanged();
                            }
                            ReceiveGiftCashActivity activity = getReceiveGiftCashActivity();
                            if (activity != null) {
                                activity.refreshHeadView(data.getTotalMoney(),
                                        data.getCashMoney(),
                                        data.getGiftMoney());
                                if (CommonUtil.isCollectionEmpty(userGifts)) {
                                    // 没有数据，设置1970年
                                    activity.setLatestTimeOfType(type,
                                            new DateTime().withMillis(0));
                                } else {
                                    activity.setLatestTimeOfType(type,
                                            userGifts.get(0)
                                                    .getCreatedAt());
                                }
                            }
                        }
                    })
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .build();

            CustomerCardApi.getUserCashGiftsObb(type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initSubscriber);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int i = view.getId();
        if (i == R.id.action_gift_empty) {
            if (isFromCard) {
                getActivity().onBackPressed();
            } else {
                intent.setClass(getActivity(), CardListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }

        }
    }

    @Override
    public void onDestroy() {
        readAll();
        realm.close();
        super.onDestroy();
        CommonUtil.unSubscribeSubs(initSubscriber, hiddenSubscriber);
    }


    private void readAll() {
        realm.beginTransaction();
        RealmResults<Notification> notifications = realm.where(Notification.class)
                .equalTo("userId",
                        UserSession.getInstance()
                                .getUser(getActivity())
                                .getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.GIFT)
                .findAll();
        for (Notification notification : notifications) {
            notification.setStatus(2);
        }
        realm.commitTransaction();
    }

    @Override
    public void refresh(Object... params) {
        initLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onHidden(final UserGift userGift, int position) {
        if (hiddenSubscriber == null || hiddenSubscriber.isUnsubscribed()) {
            userGift.setHidden(!userGift.isHidden());
            hiddenSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            userGift.setHidden(!userGift.isHidden());
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CardApi.hiddenCardGiftObb(userGift.getId(), userGift.isHidden())
                    .subscribe(hiddenSubscriber);
        }
    }

    @Override
    public void onReply(UserGift userGift, int position) {
        if (userGift.getCardUserReply() != null && userGift.getCardUserReply()
                .getId() > 0) {
            Intent intent = new Intent(getContext(), ReplyCardUserActivity.class);
            intent.putExtra("id",
                    userGift.getCardUserReply()
                            .getId());
            intent.putExtra("position", position);
            startActivityForResult(intent, HljCard.RequestCode.REPLY_GIFT_CASH);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljCard.RequestCode.REPLY_GIFT_CASH:
                    if (data != null) {
                        int position = data.getIntExtra("position", 0);
                        int status = data.getIntExtra("status", 0);
                        UserGift userGift = adapter.getItem(position);
                        if (userGift.getCardUserReply() != null) {
                            userGift.getCardUserReply()
                                    .setStatus(status);
                        }
                        adapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    private ReceiveGiftCashActivity getReceiveGiftCashActivity() {
        Activity activity = getActivity();
        if (activity instanceof ReceiveGiftCashActivity) {
            return (ReceiveGiftCashActivity) activity;
        }
        return null;
    }
}
