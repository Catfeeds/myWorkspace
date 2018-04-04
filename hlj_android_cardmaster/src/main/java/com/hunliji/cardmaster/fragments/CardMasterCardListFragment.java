package com.hunliji.cardmaster.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.activities.UserActivity;
import com.hunliji.hljcardcustomerlibrary.views.delegates.CardListBarDelegate;
import com.hunliji.hljcardcustomerlibrary.views.fragments.BaseCardListFragment;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljkefulibrary.HljKeFu;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/11/27.
 */

public class CardMasterCardListFragment extends BaseCardListFragment {

    @Override
    public String fragmentPageTrackTagName() {
        return "请帖首页";
    }

    private CardMasterActionBar cardMasterActionBar;
    private Subscription rxBusSub;
    private Realm realm;
    private User user;

    public static CardMasterCardListFragment newInstance() {
        Bundle args = new Bundle();
        CardMasterCardListFragment fragment = new CardMasterCardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerRxBusEvent();
    }

    @Override
    protected CardListBarDelegate getActionBarDelegate() {
        cardMasterActionBar = new CardMasterActionBar(getContext());
        return cardMasterActionBar;
    }

    private void initValue() {
        realm = Realm.getDefaultInstance();
        user = UserSession.getInstance()
                .getUser(getContext());
    }

    @Override
    protected Observable<Poster> getPosterObb() {
        return CommonApi.getBanner(getContext(), HljCommon.BLOCK_ID.CardMasterCardListFragment, 0)
                .map(new Func1<PosterData, Poster>() {
                    @Override
                    public Poster call(PosterData posterData) {
                        List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                HljCommon.POST_SITES.SITE_CARD_MASTER_CARD_LIST,
                                false);
                        return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                    }
                });
    }

    /**
     * 未完善保单被完善后 刷一下
     */
    private void registerRxBusEvent() {
        if (rxBusSub == null || rxBusSub.isUnsubscribed()) {
            rxBusSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case POLICY_INFO_COMPLETED_SUCCESS:
                                case EM_MESSAGE:
                                    setUnreadCount();
                                    break;
                                case NOTIFICATION_NEW_COUNT_CHANGE:
                                    onCountRefresh();
                                    setUnreadCount();
                                    break;
                            }
                        }
                    });
        }
    }

    private long getUnReadPolicyCount(User user) {
        if (user == null || realm == null || realm.isClosed()) {
            return 0;
        }
        return realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.RECV_INSURANCE)
                .count();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUnreadCount();
    }

    private void setUnreadCount() {
        long count = 0;
        if (user != null) {
            count = getUnReadPolicyCount(user);
        }
        count += HljKeFu.getUnreadCount();
        if (cardMasterActionBar != null) {
            if (count > 0) {
                cardMasterActionBar.tvCount.setVisibility(View.VISIBLE);
                cardMasterActionBar.tvCount.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                cardMasterActionBar.tvCount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(rxBusSub);
    }

    class CardMasterActionBar implements CardListBarDelegate, View.OnClickListener {

        private Context context;
        private TextView tvCount;

        private CardMasterActionBar(Context context) {
            this.context = context;
        }

        @Override
        public void inflateActionBar(ViewGroup parent) {
            View actionBar = View.inflate(context, R.layout.card_master_card_list_bar, parent);
            tvCount = actionBar.findViewById(R.id.tv_count);
            actionBar.findViewById(R.id.rl_card_master_mine)
                    .setOnClickListener(this);
        }

        @Override
        public void isHasOld(boolean isHasOld) {

        }

        @Override
        public void unbind() {

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, UserActivity.class);
            startActivity(intent);
        }
    }
}
