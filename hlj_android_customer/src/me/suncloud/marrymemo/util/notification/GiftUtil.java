package me.suncloud.marrymemo.util.notification;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReceiveGiftCashActivity;
import com.hunliji.hljcardlibrary.utils.CardDialogUtil;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import io.realm.Realm;
import io.realm.RealmResults;
import me.suncloud.marrymemo.HljActivityLifeCycleImpl;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.wallet.WalletApi;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by luohanlin on 2018/1/11.
 * 礼物礼金通知消息的弹窗逻辑帮助类
 */

public enum GiftUtil {
    INSTANCE;

    // 允许显示礼物礼金弹窗的页面，主页，礼金页，和消息主页
    public static final Class[] ACTIVITIES_ALLOW_DLG = {MainActivity.class, MessageHomeActivity
            .class, ReceiveGiftCashActivity.class};


    private static Dialog openCashGiftDlg;

    public void showCardCashGiftOpenDlg(int type, long userId) {
        HljBaseActivity hljBaseActivity = checkAndGetActivity();
        if (hljBaseActivity != null) {
            getGiftInfo(hljBaseActivity, type, userId);
        }
    }

    /**
     * 请求弹窗所需要的礼物礼金信息
     *
     * @param hljBaseActivity
     * @param type
     */
    private void getGiftInfo(HljBaseActivity hljBaseActivity, int type, final long userId) {
        HljHttpSubscriber getGiftInfoSub = HljHttpSubscriber.buildSubscriber(hljBaseActivity)
                .setOnNextListener(new SubscriberOnNextListener<UserGift>() {
                    @Override
                    public void onNext(UserGift userGift) {
                        showDlg(userGift, userId);
                    }
                })
                .build();

        hljBaseActivity.insertSubFromOutSide(getGiftInfoSub);
        WalletApi.openLatestCashGift(type)
                .subscribe(getGiftInfoSub);
    }

    /**
     * 显示弹窗信息
     *
     * @param userGift
     */
    private void showDlg(UserGift userGift, long userId) {
        final Activity activity = checkAndGetActivity();
        if (activity == null) {
            // 重新检测
            return;
        }
        if (openCashGiftDlg == null) {
            openCashGiftDlg = CardDialogUtil.createGiftOpenDlg(activity);
        }
        TextView tvSender = openCashGiftDlg.findViewById(R.id.tv_sender);
        TextView tvDesc = openCashGiftDlg.findViewById(R.id.tv_desc);
        TextView tvGift = openCashGiftDlg.findViewById(R.id.tv_gift);
        TextView tvExtra = openCashGiftDlg.findViewById(R.id.tv_extra);
        TextView tvSign = openCashGiftDlg.findViewById(R.id.tv_sign);
        tvSender.setText(userGift.getGiverName());
        if (userGift.getCardGift2Id() > 0 && !TextUtils.isEmpty(userGift.getGiftName())) {
            // 礼物
            tvSign.setVisibility(View.GONE);
            tvDesc.setText("送了你一份礼物");
            tvGift.setText(userGift.getGiftName());
            tvGift.setTextSize(28);
            tvExtra.setVisibility(View.VISIBLE);
            tvExtra.setText(activity.getString(R.string.label_price,
                    CommonUtil.formatDouble2String(userGift.getPrice())));
        } else {
            // 礼金
            tvSign.setVisibility(View.VISIBLE);
            tvDesc.setText("送了你一份礼金");
            tvExtra.setVisibility(View.GONE);
            tvGift.setTextSize(40);
            tvGift.setText(CommonUtil.formatDouble2String(userGift.getPrice()));
        }
        openCashGiftDlg.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCashGiftDlg.cancel();
                    }
                });
        openCashGiftDlg.findViewById(R.id.btn_open)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, ReceiveGiftCashActivity.class);
                        activity.startActivity(intent);
                        openCashGiftDlg.cancel();
                    }
                });
        openCashGiftDlg.setCancelable(false);
        openCashGiftDlg.setCanceledOnTouchOutside(false);

        if (!openCashGiftDlg.isShowing()) {
            openCashGiftDlg.show();
        }

        setGiftNoticeAsRead(userId);
    }

    /**
     * 显示弹窗之后，将所有的gift类型都设置成为已读
     *
     * @param userId
     */
    private void setGiftNoticeAsRead(long userId) {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Notification> notifications = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("action", Notification.NotificationAction.RECV_CASH_GIFT)
                .or()
                .equalTo("action", Notification.NotificationAction.RECV_CARD_GIFT)
                .endGroup()
                .findAll();
        if (!CommonUtil.isCollectionEmpty(notifications)) {
            realm.beginTransaction();
            for (Notification notification : notifications) {
                notification.setStatus(2);
            }
            realm.commitTransaction();
            // 发送刷新通知的事件
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION, 0));
        }
        realm.close();
    }


    /**
     * 检测应用当前是不是在前台运行并且在指定的几个页面中
     *
     * @return
     */
    private HljBaseActivity checkAndGetActivity() {
        Activity activity = HljActivityLifeCycleImpl.getInstance()
                .getCurrentActivity();
        if (activity != null) {
            for (Class allowed : ACTIVITIES_ALLOW_DLG) {
                if (allowed.isInstance(activity)) {
                    try {
                        return (HljBaseActivity) activity;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
