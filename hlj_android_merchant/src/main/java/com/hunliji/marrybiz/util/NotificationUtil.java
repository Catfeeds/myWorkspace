package com.hunliji.marrybiz.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteListActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljquestionanswer.activities.AnswerCommentListActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.api.notification.NotificationApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.model.comment.ComplainDetail;
import com.hunliji.marrybiz.view.ADVHMerchantActivity;
import com.hunliji.marrybiz.view.CaseDetailActivity;
import com.hunliji.marrybiz.view.CustomOrderDetailActivity;
import com.hunliji.marrybiz.view.EventLeafletsListActivity;
import com.hunliji.marrybiz.view.HomeActivity;
import com.hunliji.marrybiz.view.NewOrderDetailActivity;
import com.hunliji.marrybiz.view.ReservationManagerActivity;
import com.hunliji.marrybiz.view.RevenueWithdrawDetailActivity;
import com.hunliji.marrybiz.view.TextActivity;
import com.hunliji.marrybiz.view.WithdrawDetailActivity;
import com.hunliji.marrybiz.view.WorkActivity;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.view.comment.CommentDetailActivity;
import com.hunliji.marrybiz.view.comment.ComplainResultActivity;
import com.hunliji.marrybiz.view.easychat.EasyChatActivity;
import com.hunliji.marrybiz.view.event.ApplyEventActivity;
import com.hunliji.marrybiz.view.event.MyEventListActivity;
import com.hunliji.marrybiz.view.event.SignUpListActivity;
import com.hunliji.marrybiz.view.experience.AdvDetailActivity;
import com.hunliji.marrybiz.view.experience.AdvListActivity;
import com.hunliji.marrybiz.view.login.CompanyCertificationActivity;
import com.hunliji.marrybiz.view.orders.MerchantOrderDetailActivity;
import com.hunliji.marrybiz.view.revenue.RevenueManageActivity;
import com.hunliji.marrybiz.view.shop.EditShopActivity;
import com.hunliji.marrybiz.view.weddingcar.WeddingCarOrderDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NotificationUtil {

    private static WeakReference<Context> contextWeakReference;
    private int commonCount; //除互动未读通知数
    private int communityCount; //未读互动通知数
    private int carMerchantCount; //婚车商家未读通知数
    private static NotificationUtil INSTANCE;

    private Subscription notificationSubscription;

    private static Subscription workSubscription;

    private NotificationUtil() {
    }

    public static NotificationUtil getInstance(Context context) {
        contextWeakReference = new WeakReference<>(context);
        if (INSTANCE == null) {
            INSTANCE = new NotificationUtil();
        }
        return INSTANCE;
    }

    /**
     * 从本地数据库重新检索当前用户未读的通知条数
     *
     * @param userId
     */
    private void retrieveNewsCount(long userId) {
        Realm realm = Realm.getDefaultInstance();
        commonCount = (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .notEqualTo("notifyType", Notification.NotificationType.COMMUNITY)
                .notEqualTo("notifyType", Notification.NotificationType.MERCHANT_FEED)
                .notEqualTo("notifyType", Notification.NotificationType.NOTE_TYPE)
                .count();
        communityCount = (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .or()
                .equalTo("notifyType", Notification.NotificationType.MERCHANT_FEED)
                .or()
                .equalTo("notifyType", Notification.NotificationType.NOTE_TYPE)
                .endGroup()
                .count();
        carMerchantCount = (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .notEqualTo("notifyType", Notification.NotificationType.COUPON_RECEIVED)
                .notEqualTo("notifyType", Notification.NotificationType.EVENT)
                .notEqualTo("notifyType", Notification.NotificationType.INCOME)
                .notEqualTo("notifyType", Notification.NotificationType.COMMUNITY)
                .notEqualTo("notifyType", Notification.NotificationType.MERCHANT_FEED)
                .notEqualTo("notifyType", Notification.NotificationType.NOTE_TYPE)
                .count();
        realm.close();
    }


    public int getCount(MerchantUser user) {
        if (user != null && user.getShopType() == MerchantUser.SHOP_TYPE_CAR) {
            return carMerchantCount;
        } else {
            return commonCount + communityCount;
        }
    }

    public int getCommonCount() {
        return commonCount;
    }

    public int getCommunityCount() {
        return communityCount;
    }

    public void logout() {
        CommonUtil.unSubscribeSubs(notificationSubscription);
        commonCount = 0;
        communityCount = 0;
        carMerchantCount = 0;
    }

    public void getNewNotifications(long userId) {
        retrieveNewsCount(userId);
        getNotifications(userId);
    }


    public static int getChatNewsCount(Context context) {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            return 0;
        }
        int count = WSRealmHelper.getUnreadMessageCountCount(user.getId());
        count += HljKeFu.getUnreadCount();
        return count;
    }


    /**
     * 根据通知数据库最后一条通知id获取通知更新
     * <p>
     * 更新成功如果有新消息设置 isNewMsg 标志 和 消息数
     * 并且发送消息通知
     *
     * @param userId 用户id
     */
    private void getNotifications(final long userId) {
        if (notificationSubscription != null && !notificationSubscription.isUnsubscribed()) {
            return;
        }
        final Realm realm = Realm.getDefaultInstance();
        notificationSubscription = realm.where(Notification.class)
                .equalTo("userId", userId)
                .findAllSortedAsync("id", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first() //first设置 数据更新时不会多次触发
                .map(new Func1<RealmResults<Notification>, Long>() {
                    @Override
                    public Long call(RealmResults<Notification> notifications) {
                        //数据库有通知时取最后一条id，否则返回0 取所有通知
                        long id = 0L;
                        if (!notifications.isEmpty() && notifications.isValid()) {
                            id = notifications.first()
                                    .getId();
                        }
                        realm.close();
                        return id;
                    }
                })
                .concatMap(new Func1<Long, Observable<List<Notification>>>() {
                    @Override
                    public Observable<List<Notification>> call(Long lastId) {
                        //从服务器获取通知
                        return NotificationApi.getNotifications(lastId);
                    }
                })
                .concatMap(new Func1<List<Notification>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final List<Notification> notifications) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                //通知为空 没有新通知 结束
                                if (notifications == null || notifications.isEmpty()) {
                                    subscriber.onNext(false);
                                    subscriber.onCompleted();
                                    return;
                                }
                                //获取到新通知 对数据进行转换 存入数据库
                                for (Notification notification : notifications) {
                                    notification.onRealmChange(userId);
                                }
                                onNotificationEvent(notifications);
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(notifications);
                                realm.commitTransaction();
                                realm.close();
                                subscriber.onNext(true);
                                subscriber.onCompleted();

                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }

                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        //判断用户是否一致
                        MerchantUser user = null;
                        if (contextWeakReference != null && contextWeakReference.get() != null) {
                            user = Session.getInstance()
                                    .getCurrentUser(contextWeakReference.get());
                        }
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION,
                                        getCount(user)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION, -1));
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean b) {
                        //判断用户是否一致
                        MerchantUser user = null;
                        if (contextWeakReference != null && contextWeakReference.get() != null) {
                            user = Session.getInstance()
                                    .getCurrentUser(contextWeakReference.get());
                        }
                        if (user == null || user.getId() != userId) {
                            return;
                        }
                        if (b) {
                            // 取完所有通知后，更新未读条数
                            retrieveNewsCount(userId);
                        }
                    }
                });
    }


    /**
     * 收到通知后需要立即进行处理的操作在这里筛选和发送Event事件
     *
     * @param notifications
     */
    private void onNotificationEvent(List<Notification> notifications) {
        for (Notification notification : notifications) {
            if (notification.getNotifyType() == Notification.NotificationType.VERIFICATION) {
                // 收到商家审核类通知,如果会影响商家信息更新商家资料
                MerchantUserSyncUtil.getInstance()
                        .sync(contextWeakReference.get(), null);
                break;
            }
        }
        for (Notification notification : notifications) {
            if (notification.getNotifyType() == Notification.NotificationType.ORDER) {
                // 收到客资派单,发送消息刷新客资提醒
                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.ADV_HELPER, null));
                break;
            }
        }
    }


    /**
     * 通知跳转
     *
     * @param mContext     上下文
     * @param notification Notification实例
     */
    public static void notificationRoute(Context mContext, Notification notification) {
        if (notification == null) {
            return;
        }
        if (!TextUtils.isEmpty(notification.getEntity())) {
            switch (notification.getEntity()) {
                case "MerchantWithdraw": //提现记录
                case "AdvHelperMerchant": //值客保
                case "CustomOrder": //定制套餐
                    entityNotificationRoute(mContext, notification);
                    return;
            }
        }
        switch (notification.getNotifyType()) {
            case Notification.NotificationType.ORDER:
                orderNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.EVENT:
                eventNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.RESERVATION:
                reservationNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.INCOME:
                incomeNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.VERIFICATION:
                verificationNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.HUNLIJI:
                hunlijiNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.ORDER_COMMENT:
                orderCommentInviteNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.COUPON_RECEIVED:
                couponNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.MERCHANT_GRADE:
                gradeNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.COMMUNITY:
            case Notification.NotificationType.MERCHANT_FEED:
            case Notification.NotificationType.NOTE_TYPE:
                communityNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.SYSTEM_MESSAGE:
                messageNotificationUtil(mContext, notification);
                break;
            case Notification.NotificationType.ADV_OTHER_ORDER:
                Intent intent = new Intent(mContext, AdvDetailActivity.class);
                intent.putExtra(AdvDetailActivity.ARG_ID, notification.getEntityId());
                intent.putExtra(AdvDetailActivity.ARG_ADV_TYPE, AdvListActivity.ADV_FOR_OTHERS);
                mContext.startActivity(intent);
                break;
        }
    }

    private static void entityNotificationRoute(
            Context mContext, Notification notification) {
        Intent intent = null;
        switch (notification.getEntity()) {
            case "MerchantWithdraw":
                // 收入小管家,跳转至提现记录
                intent = new Intent(mContext, WithdrawDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "AdvHelperMerchant":
                intent = new Intent(mContext, ADVHMerchantActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "CustomOrder":
                // 定制套餐订单通知
                intent = new Intent(mContext, CustomOrderDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 订单通知
     */
    private static void orderNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getEntity())) {
            return;
        }
        Intent intent = null;
        switch (notification.getEntity()) {
            case "CarOrder":// 婚车订单详情
                intent = new Intent(mContext, WeddingCarOrderDetailActivity.class);
                intent.putExtra(WeddingCarOrderDetailActivity.ORDER_ID, notification.getEntityId());
                break;
            case "OrderSub": // 到订单详情
                intent = new Intent(mContext, NewOrderDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "OrderRefund": // 退款申请也到订单详情
                intent = new Intent(mContext, NewOrderDetailActivity.class);
                intent.putExtra("id", notification.getParentEntityId());
                break;
            case "HotelOrderSub": //体验店订单
                intent = new Intent(mContext, AdvDetailActivity.class);
                intent.putExtra(AdvDetailActivity.ARG_ID, notification.getEntityId());
                intent.putExtra(AdvDetailActivity.ARG_ADV_TYPE, AdvListActivity.ADV_FOR_EXPERIENCE);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 活动通知
     */
    private static void eventNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "finder_activity_point_exhausted": //活动点数耗尽通知
            case "finder_activity_begin": //活动开始
            case "finder_activity_new_signup": //用户报名
                intent = new Intent(mContext, SignUpListActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "finder_activity_online": //活动上架
            case "finder_activity_merchant_notify":
                intent = new Intent(mContext, MyEventListActivity.class);
                break;
            case "finder_activity_apply_pass":
            case "finder_activity_apply_unpass":
                //活动审核
                intent = new Intent(mContext, ApplyEventActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "finder_activity_recharge_success": //活动充值成功
            case "finder_activity_recharge_success_admin": //商家后台活动充值成功
                intent = new Intent(mContext, MyEventListActivity.class);
                break;
            case "finder_activity_source_apply_pass":
            case "finder_activity_source_apply_unpass":
                //微传单活动审核
                intent = new Intent(mContext, EventLeafletsListActivity.class);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 预约通知
     */
    private static void reservationNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "user_appointment": //预约看店
            case "user_car_appointment": //婚车预约看店
                intent = new Intent(mContext, ReservationManagerActivity.class);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 收入通知
     */
    private static void incomeNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "shop_order_withdraw": //商家提现
                intent = new Intent(mContext, RevenueWithdrawDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 审核通知
     */
    private static void verificationNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "chat_link":
                if (notification.getEntityId() > 0) {
                    intent = new Intent(mContext, EasyChatActivity.class);
                    intent.putExtra("isActive", true);
                }
                break;
            case "audit_fail"://套餐案例审核失败
            case "audit_success"://套餐案例审核通过
                CommonUtil.unSubscribeSubs(workSubscription);
                if (!(mContext instanceof Activity)) {
                    return;
                }
                final WeakReference<Activity> activityWeakReference = new WeakReference<>(
                        (Activity) mContext);
                Subscriber subscriber = HljHttpSubscriber.buildSubscriber(mContext)
                        .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                                .createProgressDialog(
                                mContext))
                        .setOnNextListener(new SubscriberOnNextListener<Work>() {
                            @Override
                            public void onNext(Work work) {
                                if (activityWeakReference.get() == null || activityWeakReference
                                        .get()
                                        .isFinishing()) {
                                    return;
                                }
                                Activity activity = activityWeakReference.get();
                                if (work == null || work.getId() == 0) {
                                    return;
                                }
                                Intent intent;
                                if (work.getCommodityType() == 0) {
                                    intent = new Intent(activity, WorkActivity.class);
                                } else {
                                    intent = new Intent(activity, CaseDetailActivity.class);
                                }
                                intent.putExtra("w_id", work.getId());
                                activity.startActivity(intent);
                            }
                        })
                        .build();
                workSubscription = MerchantApi.getWorkInfo(notification.getEntityId())
                        .subscribe(subscriber);
                break;
            case "add_hotel_fail": //提交酒店信息审核失败
            case "add_hotel_pass"://提交酒店信息审核通过
            case "edit_hotel_pass"://编辑酒店信息审核通过
            case "update_grade_hotel_pass": //酒店升级账户通过
                break;
            case "merchant_audit_fail": //商家资料审核失败
            case "merchant_audit_success": //商家资料审核通过
                intent = new Intent(mContext, EditShopActivity.class);
                break;
            case "feed_reject":
                // 动态审核未通过
                intent = new Intent(mContext, MerchantNoteListActivity.class);
                break;
            case "open_trade_certify": //开通在线交易已通过
            case "open_trade_fail":   //开通在线交易已失败
            case "merchant_and_setmeal"://铺资料和在线交易都已通
                MerchantUser user = Session.getInstance()
                        .getCurrentUser(mContext);
                if (user.getCertifyStatus() == 3) {
                    intent = new Intent(mContext, HomeActivity.class);
                    intent.putExtra("index", 0);
                } else {
                    intent = new Intent(mContext, CompanyCertificationActivity.class);
                }
                break;
            case "merchant_privilege_fail":
            case "merchant_privilege_verify":
                //商家营销
                intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("page_index", 1);
                break;
            case "appeal_failed":
            case "appeal_success":
                //订单评论商家申诉
                intent = new Intent(mContext, ComplainResultActivity.class);
                intent.putExtra(ComplainDetail.TYPE, ComplainDetail.TYPE_COMMENT);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "qa_question_appeal_failed":
            case "qa_question_appeal_success":
                //商家问答商家申诉
                intent = new Intent(mContext, ComplainResultActivity.class);
                intent.putExtra(ComplainDetail.TYPE, ComplainDetail.TYPE_QUESTION);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "status_fail":
            case "status_order_form":
                //体验店推荐单审核
                intent = new Intent(mContext, AdvDetailActivity.class);
                intent.putExtra(AdvDetailActivity.ARG_ID, notification.getEntityId());
                intent.putExtra(AdvDetailActivity.ARG_ADV_TYPE, AdvListActivity.ADV_FOR_EXPERIENCE);
                break;
            case "bank_fail":
            case "bank_success":
                //结算账户审核
                intent = new Intent(mContext, RevenueManageActivity.class);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }


    /**
     * 等级通知
     */
    private static void gradeNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        switch (notification.getAction()) {
            case "merchant_grade_change": //商家等级变动
                break;
        }
    }

    /**
     * 系统通知
     */
    private static void hunlijiNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "merchant_notify_feature":
            case "merchant_notify_html": // banner跳转
                if (!JSONUtil.isEmpty(notification.getContent())) {
                    try {
                        JSONObject jsonObject = new JSONObject(notification.getContent());
                        Poster poster = new Poster(jsonObject);
                        Util.bannerAction(mContext, poster);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                break;
            case "merchant_notify_text": //系统公告
                if (!JSONUtil.isEmpty(notification.getContent())) {
                    intent = new Intent(mContext, TextActivity.class);
                    intent.putExtra("text", notification.getContent());
                }
                break;
            case "ad_poor_merchant": //推广账号余额不足
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    /**
     * 商家评论
     */
    private static void orderCommentInviteNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "order_comment_merchant":
                if (notification.getEntityId() > 0) {
                    intent = new Intent(mContext, CommentDetailActivity.class);
                    intent.putExtra("order_comment_id", notification.getEntityId());
                }
                break;
            case "order_comment_review_reply_merchant":
                if (notification.getParentEntityId() > 0) {
                    intent = new Intent(mContext, CommentDetailActivity.class);
                    intent.putExtra("order_comment_id", notification.getParentEntityId());
                }
                break;
            case Notification.NotificationAction.QA_QUESTION_MERCHANT:
                if (notification.getEntityId() > 0) {
                    intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("questionId", notification.getEntityId());
                }
                break;
            case Notification.NotificationAction.QA_ANSWER_MERCHANT:
                if (notification.getParentEntityId() > 0) {
                    intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("questionId", notification.getParentEntityId());
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }


    /**
     * 优惠券通知
     */
    private static void couponNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case Notification.NotificationAction.COUPON_RECEIVE:
                if (notification.getParticipantId() > 0) {
                    intent = new Intent(mContext, WSMerchantChatActivity.class);
                    intent.putExtra("id", notification.getParticipantId());
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 互动通知
     */
    private static void communityNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case "post_qa_answer_deleted":
            case "post_qa_comment_deleted":
                if (!TextUtils.isEmpty(notification.getContent())) {
                    intent = new Intent(mContext, TextActivity.class);
                    intent.putExtra("text", notification.getContent());
                }
                break;
            case "qa_answer_comment":
            case "qa_answer_comment_reply":
                intent = new Intent(mContext, AnswerCommentListActivity.class);
                intent.putExtra("isFromNotification", true);
                intent.putExtra("answerId", notification.getParentEntityId());
                if (notification.getExtraObject() != null && notification.getExtraObject()
                        .getAnswer() != null) {
                    intent.putExtra("questionAuthId",
                            notification.getExtraObject()
                                    .getAnswer()
                                    .getUserId());
                }
                break;
            case Notification.NotificationAction.POST_QA_ANSWER_HOT:
                intent = new Intent(mContext, AnswerDetailActivity.class);
                intent.putExtra("answerId", notification.getParentEntityId());
                break;
            case "note_comment":
            case "note_comment_reply":
                intent = new Intent(mContext, NoteDetailActivity.class);
                intent.putExtra("note_id", notification.getParentEntityId());
                break;
            case "note_join_repository":
                intent = new Intent(mContext, NoteDetailActivity.class);
                intent.putExtra("note_id", notification.getEntityId());
                break;
            case "qa_question_help":
                intent = new Intent(mContext, QuestionDetailActivity.class);
                intent.putExtra("questionId", notification.getEntityId());
                break;
            case "qa_answer_praise":
                intent = new Intent(mContext, AnswerDetailActivity.class);
                intent.putExtra("answerId", notification.getParentEntityId());
                break;
            case "qa_praise":
                intent = new Intent(mContext, AnswerCommentListActivity.class);
                intent.putExtra("isFromNotification", true);
                intent.putExtra("answerId", notification.getParentEntityId());
                if (notification.getExtraObject() != null && notification.getExtraObject()
                        .getAnswer() != null) {
                    intent.putExtra("questionAuthId",
                            notification.getExtraObject()
                                    .getAnswer()
                                    .getUserId());
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    /**
     * 系统消息（商家订单）
     */
    private static void messageNotificationUtil(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        Intent intent = null;
        switch (notification.getAction()) {
            case Notification.NotificationAction.MERCHANT_ORDER_WARN:
            case Notification.NotificationAction.MERCHANT_PAY_WARN:
            case Notification.NotificationAction.BD_MERCHANT_ORDER:
                intent = new Intent(mContext, MerchantOrderDetailActivity.class);
                intent.putExtra(MerchantOrderDetailActivity.ARG_ORDER_ID,
                        notification.getEntityId());
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }
}
