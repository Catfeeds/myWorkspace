package com.hunliji.cardmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.utils.CardMasterSupportUtil;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 我的页面
 * Created by jinxin on 2017/11/24 0024.
 */

public class UserActivity extends HljBaseNoBarActivity {


    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.img_vip)
    ImageView imgVip;
    @BindView(R.id.tv_wallet_notice)
    TextView tvWalletNotice;
    @BindView(R.id.img_new)
    ImageView imgNew;
    @BindView(R.id.tv_kefu_notice)
    TextView tvKefuNotice;
    @BindView(R.id.bg_view)
    ImageView bgView;
    @BindView(R.id.img_wallet_notice)
    ImageView imgWalletNotice;
    @BindView(R.id.layout_wallet)
    LinearLayout layoutWallet;
    @BindView(R.id.layout_setting)
    LinearLayout layoutSetting;
    @BindView(R.id.layout_ring)
    LinearLayout layoutRing;
    @BindView(R.id.img_kefu_notice)
    ImageView imgKefuNotice;
    @BindView(R.id.layout_headset)
    LinearLayout layoutHeadset;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.layout_action)
    RelativeLayout layoutAction;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;

    private Subscription rxSub;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        registerRxEvent();
        onAppSubmit();
    }


    /**
     * 审核特殊处理
     */
    private void onAppSubmit() {
        if (FinancialSwitch.INSTANCE.isClosed(this)) {
            layoutRing.setVisibility(View.GONE);
        }
    }

    private void initConstant() {
        realm = Realm.getDefaultInstance();
    }

    private void initWidget() {
        HljBaseActivity.setActionBarPadding(this, actionHolderLayout);
    }

    private void refreshWidget() {
        CustomerUser user = (CustomerUser) UserSession.getInstance()
                .getUser(this);
        tvKefuNotice.setVisibility(View.GONE);
        tvWalletNotice.setVisibility(View.GONE);
        if (user == null) {
            return;
        }
        //未读通知数
        int unreadCount = HljKeFu.getUnreadCount();
        //未读保单数
        long unreadPolicyCount = getUnReadPolicyCount(user);
        int avatarSize = CommonUtil.dp2px(this, 80);
        Glide.with(this)
                .load(ImagePath.buildPath(user.getAvatar())
                        .width(avatarSize)
                        .height(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvUserName.setText(user.getNick());
        imgVip.setImageResource(user.isMember() ? R.mipmap.icon_vip_selected : R.mipmap
                .icon_vip_unselected);
        tvKefuNotice.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
        imgKefuNotice.setVisibility(unreadCount > 0 ? View.GONE : View.VISIBLE);
        tvKefuNotice.setText(unreadCount > 0 ? (unreadCount > 99 ? "99+" : String.valueOf(
                unreadCount)) : null);
        tvWalletNotice.setVisibility(unreadPolicyCount > 0 ? View.VISIBLE : View.GONE);
        tvWalletNotice.setText(unreadPolicyCount > 0 ? (unreadPolicyCount > 99 ? "99+" : String
                .valueOf(
                unreadPolicyCount)) : null);
        imgWalletNotice.setVisibility(unreadPolicyCount > 0 ? View.GONE : View.VISIBLE);
    }

    private long getUnReadPolicyCount(CustomerUser user) {
        if (user == null || realm == null || realm.isClosed()) {
            return 0;
        }
        return realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.RECV_INSURANCE)
                .count();
    }

    private void setUnReadPolicyCount(CustomerUser user) {
        if (user == null || realm == null || realm.isClosed()) {
            return;
        }
        realm.beginTransaction();
        RealmResults<Notification> notifications = realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.RECV_INSURANCE)
                .findAll();
        for (Notification notification : notifications) {
            notification.setStatus(2);
        }
        realm.commitTransaction();
    }

    private void registerRxEvent() {
        if (rxSub == null || rxSub.isUnsubscribed()) {
            rxSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case POLICY_INFO_COMPLETED_SUCCESS:
                                case NEW_NOTIFICATION:
                                    refreshPolicyCount();
                                    break;
                            }
                        }
                    });
        }
    }

    private void refreshPolicyCount() {
        refreshWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWidget();
    }

    @OnClick(R.id.img_vip)
    void onVip() {
        startActivity(new Intent(this, OpenMemberActivity.class));
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        super.onBackPressed();
    }

    @OnClick(R.id.layout_wallet)
    void onWallet() {
        CustomerUser user = (CustomerUser) UserSession.getInstance()
                .getUser(this);
        setUnReadPolicyCount(user);
        Intent intent = new Intent(this, MyWalletActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_setting)
    void onSet() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_ring)
    void onRing() {
        HljWeb.startWebView(this, HljCard.CARD_MASTER_MARRY_PREPARE);
    }

    @OnClick(R.id.layout_headset)
    void onKefu() {
        CardMasterSupportUtil.goToSupport(this, Support.SUPPORT_KIND_CARD_MASTER);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(rxSub);
    }
}
