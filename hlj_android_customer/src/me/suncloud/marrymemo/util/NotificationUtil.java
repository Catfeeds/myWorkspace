package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hunliji.hljcardcustomerlibrary.models.UserGift;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcardlibrary.views.activities.ModifyNameStatusActivity;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljinsurancelibrary.views.activities.MyPolicyListActivity;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljquestionanswer.activities.AnswerCommentListActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmError;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.notification.NotificationApi;
import me.suncloud.marrymemo.model.HintData;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.notification.GiftUtil;
import me.suncloud.marrymemo.view.CarOrderDetailActivity;
import me.suncloud.marrymemo.view.CustomSetmealOrderDetailActivity;
import me.suncloud.marrymemo.view.ProductOrderDetailActivity;
import me.suncloud.marrymemo.view.RefundCarOrderDetailActivity;
import me.suncloud.marrymemo.view.StoryActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.event.AfterSignUpActivity;
import me.suncloud.marrymemo.view.event.EventDetailActivity;
import me.suncloud.marrymemo.view.finder.SubPageCommentListActivity;
import me.suncloud.marrymemo.view.comment.ServiceCommentDetailActivity;
import me.suncloud.marrymemo.view.orders.ServiceOrderDetailActivity;
import me.suncloud.marrymemo.view.wallet.MyRedPacketListActivity;
import me.suncloud.marrymemo.view.wallet.OpenMemberActivity;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NotificationUtil {

    private static WeakReference<Context> contextWeakReference;
    private int count;
    private static NotificationUtil INSTANCE;
    private HintData hintData;

    private Subscription notificationSubscription;
    private Subscription checkSub;
    private Subscription hintSubscription;

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
     * 获取或更新新的未读通知条数
     *
     * @param userId
     * @return
     */
    public int getNewsCount(long userId) {
        Realm realm = Realm.getDefaultInstance();
        count = (int) realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("notifyType", Notification.NotificationType.SIGN)
                .or()
                .equalTo("notifyType", Notification.NotificationType.GIFT)
                .or()
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .or()
                .equalTo("notifyType", Notification.NotificationType.ORDER)
                .or()
                .equalTo("notifyType", Notification.NotificationType.EVENT)
                .or()
                .equalTo("notifyType", Notification.NotificationType.MERCHANT_FEED)
                .or()
                .equalTo("notifyType", Notification.NotificationType.SUB_PAGE)
                .or()
                .equalTo("notifyType", Notification.NotificationType.PARTNER_INVITE)
                .or()
                .equalTo("notifyType", Notification.NotificationType.FINANCIAL)
                .or()
                .equalTo("notifyType", Notification.NotificationType.ORDER_COMMENT)
                .or()
                .equalTo("notifyType", Notification.NotificationType.NOTE_TYPE)
                .or()
                .equalTo("notifyType", Notification.NotificationType.RECV_INSURANCE)
                .or()
                .equalTo("notifyType", Notification.NotificationType.APPOINTMENT_LIVE_START)
                .or()
                .equalTo("notifyType", Notification.NotificationType.HUNLIJI)
                .endGroup()
                .count();
        realm.close();
        return count;
    }

    /**
     * 直接获取当前未读通知条数
     *
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * 直接获取hint数据
     *
     * @return
     */
    public HintData getHintData() {
        return hintData;
    }

    /**
     * 判断当前是否有未读订单通知
     *
     * @return
     */
    public boolean hasNewHint() {
        return hintData.hasNewHint();
    }

    /**
     * 判断当前是否有未读通知
     *
     * @return
     */
    public boolean isNews() {
        return count > 0;
    }

    public void logout() {
        CommonUtil.unSubscribeSubs(hintSubscription, notificationSubscription);
        count = 0;
        hintData = null;
    }

    /**
     * 获取最新通知消息列表，可以在任何地方需要更新最新通知条数或者列表的地方调用
     * 最核心被调用的地方是个推推送婚礼纪消息过来后发起通知列表更新，取到对应的通知消息详情
     *
     * @param userId
     */
    public void getNewNotifications(final long userId) {
        getNewsCount(userId);
        getHintData(userId);
        getNotifications(userId);
    }

    /**
     * 获取各个页面（主要是设置页面）中红点
     * 通过接口获取各个参数的状态（比如需处理订单个数），界面根据这些状态去决定是否显示hint红点
     *
     * @param userId
     */
    private void getHintData(final long userId) {
        if (hintSubscription != null && !hintSubscription.isUnsubscribed()) {
            return;
        }
        Subscriber hintSub = new Subscriber<HintData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(HintData hd) {
                User user = Session.getInstance()
                        .getCurrentUser(contextWeakReference.get());
                if (user != null && user.getId() == userId) {
                    hintData = hd;
                }
            }
        };

        hintSubscription = NotificationApi.getHintData()
                .subscribe(hintSub);
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
        if(!CommonUtil.isUnsubscribed(notificationSubscription)){
            return;
        }
        try {
            long lastId=0;
            Realm realm = Realm.getDefaultInstance();
            Number maxId = realm.where(Notification.class)
                    .equalTo("userId", userId)
                    .max("id");
            if (maxId != null) {
                lastId = maxId.longValue();
            }
            realm.close();
            notificationSubscription = Observable.just(lastId)
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
                            if(CommonUtil.isCollectionEmpty(notifications)) {
                                return Observable.just(false);
                            }
                            onNotificationEvent(notifications, userId);
                            return Observable.from(notifications)
                                    .map(new Func1<Notification, Notification>() {
                                        @Override
                                        public Notification call(Notification notification) {
                                            notification.onRealmChange(userId);
                                            return notification;
                                        }
                                    })
                                    .toList()
                                    .map(new Func1<List<Notification>, Boolean>() {
                                        @Override
                                        public Boolean call(List<Notification> notifications) {
                                            Realm realm = Realm.getDefaultInstance();
                                            realm.beginTransaction();
                                            realm.insertOrUpdate(notifications);
                                            realm.commitTransaction();
                                            realm.close();
                                            return true;
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }

                    })
                    .timeout(20, TimeUnit.SECONDS)
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.NEW_NOTIFICATION, count));
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
                            User user = null;
                            if (contextWeakReference != null && contextWeakReference.get() !=
                                    null) {
                                user = Session.getInstance()
                                        .getCurrentUser(contextWeakReference.get());
                            }
                            if (user == null || user.getId() != userId) {
                                return;
                            }
                            if (b) {
                                // 取完所有通知后，更新未读条数
                                getNewsCount(userId);
                            }
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * 收到通知后需要立即进行处理的操作在这里筛选和发送Event事件
     *
     * @param notifications
     */
    private void onNotificationEvent(List<Notification> notifications, long userId) {
        boolean hasInvitePartner = false;
        boolean hasAcceptPartner = false;
        boolean hasNewCardNotice = false;
        boolean hasCashGiftNotice = false;
        boolean hasCardGiftNotice = false;
        for (int i = notifications.size() - 1; i >= 0; i--) {
            Notification notification = notifications.get(i);
            if (notification.getStatus() == 2) {
                continue;
            }
            if (!hasInvitePartner && notification.getNotifyType() == Notification
                    .NotificationType.PARTNER_INVITE && notification.getEntityId() > 0 &&
                    notification.getAction()
                    .equals(Notification.NotificationAction.INVITE_PARTNER)) {
                // 收到结伴邀请，发出事件通知
                // 结伴邀请只处理最后一条作为通知,所以倒序循环
                hasInvitePartner = true;

                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.PARTNER_INVITATION, notification));
            }
            if (!hasAcceptPartner && notification.getNotifyType() == Notification
                    .NotificationType.PARTNER_INVITE && notification.getEntityId() > 0 &&
                    notification.getAction()
                    .equals(Notification.NotificationAction.ACCEPT_PARTNER)) {
                // 结伴同意的通知,需要立即更新用户信息,更新一次就可以
                hasAcceptPartner = true;
                new UserTask(contextWeakReference.get(), null).execute();
            }
            if (!hasNewCardNotice && notification.getNotifyType() == Notification
                    .NotificationType.GIFT) {
                //收到请帖礼物
                hasNewCardNotice = true;
                RxBus.getDefault()
                        .post(new RxEvent(RxEvent.RxEventType.NEW_CARD_NOTICE, notification));


            }
            if (!hasCashGiftNotice && notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CASH_GIFT)) {
                // 礼金，推送消息，显示红包
                hasCashGiftNotice = true;
                GiftUtil.INSTANCE.showCardCashGiftOpenDlg(UserGift.TYPE_CASH, userId);
            }
            if (!hasCardGiftNotice && notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CARD_GIFT)) {
                // 礼物，推送消息，显示红包
                hasCardGiftNotice = true;
                GiftUtil.INSTANCE.showCardCashGiftOpenDlg(UserGift.TYPE_GIFT, userId);
            }
            if (hasNewCardNotice && hasAcceptPartner && hasInvitePartner && hasCashGiftNotice &&
                    hasCardGiftNotice) {
                return;
            }
        }
        if (!hasCardGiftNotice && !hasCashGiftNotice) {
            checkOldNoticeForGift(userId);
        }
    }

    /**
     * 如果新消息全部检查完都没有礼物礼金的消息，则去检查旧的消息
     */
    private void checkOldNoticeForGift(final long userId) {
        final Realm realm = Realm.getDefaultInstance();
        Notification notification = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("action", Notification.NotificationAction.RECV_CASH_GIFT)
                .or()
                .equalTo("action", Notification.NotificationAction.RECV_CARD_GIFT)
                .endGroup()
                .findAllSorted("id", Sort.ASCENDING).first(null);
        if (notification!=null) {
            int type = notification.getAction()
                    .equals(Notification.NotificationAction.RECV_CARD_GIFT) ? UserGift.TYPE_GIFT
                    : UserGift.TYPE_CASH;
            GiftUtil.INSTANCE.showCardCashGiftOpenDlg(type, userId);
        }
        realm.close();
    }

    public static int getChatNewsCount(Context context) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            return 0;
        }
        int count = WSRealmHelper.getUnreadMessageCountCount(user.getId());
        count += HljKeFu.getUnreadCount();
        return count;
    }

    public static int getChatNewsCount(Context context, long wsSessionUserId) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            return 0;
        }
        int count = WSRealmHelper.getUnreadMessageCountCount(user.getId(), wsSessionUserId);
        count += HljKeFu.getUnreadCount();
        return count;
    }


    /**
     * banner的路由跳转
     *
     * @param mContext     上下文
     * @param notification Notification实例
     */
    public static void notificationRoute(Context mContext, Notification notification) {
        if (notification == null) {
            return;
        }
        switch (notification.getNotifyType()) {
            case Notification.NotificationType.COMMUNITY:
                communityNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.ORDER:
                orderNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.FINANCIAL:
                try {
                    BannerUtil.bannerJump(mContext,
                            notification.getExtraObject()
                                    .getPoster(),
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Notification.NotificationType.SUB_PAGE:
                subPageNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.EVENT:
                eventNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.PARTNER_INVITE:
                partnerInviteNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.ORDER_COMMENT:
                orderCommentInviteNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.NOTE_TYPE:
                noteNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.RECV_INSURANCE:
                insuranceNotificationRoute(mContext, notification);
                break;
            case Notification.NotificationType.APPOINTMENT_LIVE_START:
                liveStartNotificationRouter(mContext, notification);
                break;
            case Notification.NotificationType.HUNLIJI:
                hljNotificationRouter(mContext, notification);
                break;
            case Notification.NotificationType.MEMBER_RIGHTS:
                memberRightRouter(mContext, notification);
                break;
        }
    }

    private static void communityNotificationRoute(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        Intent intent = null;
        switch (notification.getEntity()) {
            case "Story":
                intent = new Intent(mContext, StoryActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "StoryItem":
            case "StoryComment":
            case "StoryItemComment":
                intent = new Intent(mContext, StoryActivity.class);
                intent.putExtra("id", notification.getParentEntityId());
                break;
            case "CommunityComment":
                switch (notification.getAction()) {
                    case "post_qa_comment_deleted":
                        if (!TextUtils.isEmpty(notification.getContent())) {
                            com.hunliji.hljcommonlibrary.utils.DialogUtil.createSingleButtonDialog(
                                    mContext,
                                    notification.getContent(),
                                    mContext.getString(R.string.label_i_see),
                                    null)
                                    .show();
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
                }
                break;
            case "CommunityPost":
                switch (notification.getAction()) {
                    case "post_reply":
                    case "thread_reply":
                    case "thread_post_hot":
                    case "post_praise":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getParentEntityId());
                        if (notification.getExtraObject() != null && notification.getExtraObject()
                                .getExpand() != null) {
                            intent.putExtra("serial_no",
                                    notification.getExtraObject()
                                            .getExpand()
                                            .getSerialNo());
                        }
                        break;
                    case "del_post":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getParentEntityId());
                        break;
                }
                break;
            case "CommunityThreadItem":
                switch (notification.getAction()) {
                    case "plus1":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getParentEntityId());
                        if (notification.getExtraObject() != null && notification.getExtraObject()
                                .getExpand() != null) {
                            intent.putExtra("serial_no",
                                    notification.getExtraObject()
                                            .getExpand()
                                            .getSerialNo());
                        }
                        break;

                }
            case "CommunityPraise":
                switch (notification.getAction()) {
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
                break;
            case "CommunityPunish":
                switch (notification.getAction()) {
                    case "community_warning":
                    case "community_banned":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getParentEntityId());
                        break;
                }
                break;
            case "CommunityThread":
                switch (notification.getAction()) {
                    case "del_thread":
                        if (!TextUtils.isEmpty(notification.getContent())) {
                            com.hunliji.hljcommonlibrary.utils.DialogUtil.createSingleButtonDialog(
                                    mContext,
                                    notification.getContent(),
                                    mContext.getString(R.string.label_i_see),
                                    null)
                                    .show();
                        }
                        break;
                    case "plus1":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getParentEntityId());
                        if (notification.getExtraObject() != null && notification.getExtraObject()
                                .getExpand() != null) {
                            intent.putExtra("serial_no",
                                    notification.getExtraObject()
                                            .getExpand()
                                            .getSerialNo());
                        }
                        break;
                    case "thread_essence":
                    case "thread_included_top_list":
                    case "thread_stick":
                        intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", notification.getEntityId());
                        break;

                }
                break;
            case "QaAnswer":
                switch (notification.getAction()) {
                    case "post_qa_answer_hot":
                    case "qa_answer":
                        intent = new Intent(mContext, AnswerDetailActivity.class);
                        intent.putExtra("answerId", notification.getEntityId());
                        break;
                }
                break;
            case "QaQuestion":
                switch (notification.getAction()) {
                    case "post_qa_answer_deleted":
                    case "post_qa_question_deleted":
                        if (!TextUtils.isEmpty(notification.getContent())) {
                            com.hunliji.hljcommonlibrary.utils.DialogUtil.createSingleButtonDialog(
                                    mContext,
                                    notification.getContent(),
                                    mContext.getString(R.string.label_i_see),
                                    null)
                                    .show();
                        }
                        break;
                    case Notification.NotificationAction.QA_QUESTION_HELP:
                        intent = new Intent(mContext, QuestionDetailActivity.class);
                        intent.putExtra("questionId", notification.getEntityId());
                        break;
                }
                break;
            case "QaVote":
                switch (notification.getAction()) {
                    case "qa_answer_praise":
                        intent = new Intent(mContext, AnswerDetailActivity.class);
                        intent.putExtra("answerId", notification.getParentEntityId());
                        break;
                }
                break;
            case "Subscription":
                switch (notification.getAction()) {
                    case "follow":
                        intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra("id", notification.getParticipantId());
                        break;
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    private static void orderNotificationRoute(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        Intent intent = null;
        switch (notification.getEntity()) {
            case "ShopOrderSub":
                intent = new Intent(mContext, ProductOrderDetailActivity.class);
                intent.putExtra("id", notification.getParentEntityId());
                break;
            case "ShopOrder":
                intent = new Intent(mContext, ProductOrderDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "CarOrder":
                switch (notification.getAction()) {
                    case "car_order_refund":
                        intent = new Intent(mContext, RefundCarOrderDetailActivity.class);
                        intent.putExtra("id", notification.getEntityId());
                        break;
                    default:
                        intent = new Intent(mContext, CarOrderDetailActivity.class);
                        intent.putExtra("id", notification.getEntityId());
                        break;
                }
                break;
            case "CustomOrder":
                // 定制套餐订单
                intent = new Intent(mContext, CustomSetmealOrderDetailActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
            case "OrderSub":
            case "OrderRefund":
                intent = new Intent(mContext, ServiceOrderDetailActivity.class);
                intent.putExtra("id", notification.getParentEntityId());
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    private static void subPageNotificationRoute(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction())) {
            return;
        }
        switch (notification.getEntity()) {
            case "CommunityComment":
            case "CommunityPraise":
                if (notification.getParentEntityId() > 0) {
                    Intent intent = new Intent(mContext, SubPageCommentListActivity.class);
                    intent.putExtra("id", notification.getParentEntityId());
                    intent.putExtra("isFromNotification", true);
                    mContext.startActivity(intent);
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
                break;
        }
    }

    private static void eventNotificationRoute(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        Intent intent = null;
        switch (notification.getEntity()) {
            case "FinderActivity":
                switch (notification.getAction()) {
                    case "finder_activity_begin":
                        if (notification.getEntityId() > 0) {
                            intent = new Intent(mContext, EventDetailActivity.class);
                            intent.putExtra("id", notification.getEntityId());
                        }
                        break;
                    case "finder_activity_winner_notify":
                        if (notification.getEntityId() > 0) {
                            intent = new Intent(mContext, AfterSignUpActivity.class);
                            intent.putExtra("id", notification.getEntityId());
                        }
                        break;
                }
                break;
            case "User":
                switch (notification.getAction()) {
                    case "user_member_red_package":
                        intent = new Intent(mContext, MyRedPacketListActivity.class);
                        break;
                }
                break;
            case "UserMemberOrder":
                switch (notification.getAction()) {
                    case "user_member_gift_address":
                        intent = new Intent(mContext, OpenMemberActivity.class);
                        break;
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    private static void partnerInviteNotificationRoute(
            Context mContext, Notification notification) {
        if (notification.getEntityId() <= 0 || TextUtils.isEmpty(notification.getAction()) ||
                TextUtils.isEmpty(
                notification.getEntity())) {
            return;
        }
        Intent intent = null;
        switch (notification.getEntity()) {
            case "User":
                switch (notification.getAction()) {
                    case Notification.NotificationAction.INVITE_PARTNER:
                        // 结伴邀请，进入伴侣主页查看当前的邀请信息，或者提示邀请过期
                        intent = new Intent(mContext, BindingPartnerActivity.class);
                        intent.putExtra("user_id", notification.getEntityId());
                        intent.putExtra("user_avatar", notification.getParticipantAvatar());
                        intent.putExtra("user_nick", notification.getParticipantName());
                        break;
                    case Notification.NotificationAction.ACCEPT_PARTNER:
                    case Notification.NotificationAction.UNBIND_PARTNER:
                        // 同意结伴，直接进入伴侣主页
                        intent = new Intent(mContext, BindingPartnerActivity.class);
                        break;
                    case Notification.NotificationAction.ACCEPT_PARTNER_POINT:
                    case Notification.NotificationAction.INVITE_PARTNER_POINT:
                        // 金币奖励，跳转金币商城
                        Poster poster = new Poster();
                        poster.setTargetType(52);
                        BannerUtil.bannerJump(mContext, poster, null);
                        break;
                }
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }


    private static void orderCommentInviteNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        switch (notification.getEntity()) {
            case "CommunityComment":
                switch (notification.getAction()) {
                    case Notification.NotificationAction.ORDER_COMMENT_REVIEW:
                    case Notification.NotificationAction.ORDER_COMMENT_REVIEW_REPLY:
                        if (notification.getParentEntityId() > 0) {
                            Intent intent = new Intent(mContext,
                                    ServiceCommentDetailActivity.class);
                            intent.putExtra("id", notification.getParentEntityId());
                            mContext.startActivity(intent);
                            if (mContext instanceof Activity) {
                                ((Activity) mContext).overridePendingTransition(R.anim
                                                .slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                        break;
                }
                break;
        }
    }

    private static void noteNotificationRoute(
            Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        switch (notification.getEntity()) {
            case "CommunityComment":
            case "Note":
                long id = 0;
                switch (notification.getAction()) {
                    case Notification.NotificationAction.NOTE_COMMENT:
                    case Notification.NotificationAction.NOTE_COMMENT_REPLY:
                        id = notification.getParentEntityId();
                        break;
                    case Notification.NotificationAction.NOTE_JOIN_REPOSITORY:
                    case Notification.NotificationAction.DEL_NOTE:
                        id = notification.getEntityId();
                        break;
                }
                if (id > 0) {
                    Intent intent = new Intent(mContext, NoteDetailActivity.class);
                    intent.putExtra("note_id", id);
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    private static void insuranceNotificationRoute(Context mContext, Notification notification) {
        if (TextUtils.isEmpty(notification.getAction()) || TextUtils.isEmpty(notification
                .getEntity())) {
            return;
        }
        Intent intent = new Intent(mContext, MyPolicyListActivity.class);
        mContext.startActivity(intent);
        if (mContext instanceof Activity) {
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private static void liveStartNotificationRouter(Context mContext, Notification notification) {
        Intent intent = new Intent(mContext, LiveChannelActivity.class);
        intent.putExtra(LiveChannelActivity.ARG_ID, notification.getEntityId());
        mContext.startActivity(intent);
    }

    private static void hljNotificationRouter(Context mContext, Notification notification) {
        Intent intent = null;
        switch (notification.getAction()) {
            case Notification.NotificationAction.MODIFY_NAME_FAILED:
                intent = new Intent(mContext, ModifyNameStatusActivity.class);
                intent.putExtra("id", notification.getEntityId());
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    private static void memberRightRouter(Context mContext, Notification notification) {
        Intent intent = null;
        switch (notification.getAction()) {
            case Notification.NotificationAction.MEMBER_CENTER:
                intent = new Intent(mContext, OpenMemberActivity.class);
                break;
            case Notification.NotificationAction.GO_TO_INVITATION_CARD:
                intent = new Intent(mContext, CardListActivity.class);
                break;
        }
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }
}
