package me.suncloud.marrymemo.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.suncloud.marrymemo.model.User;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Suncloud on 2016/9/28.
 */

public class NoticeUtil {

    private Context context;
    private ArrayList<TextView> tvMsgCountList; // 新消息个数视图
    private ArrayList<View> newNoticeViewList; // 新通知红点视图
    private long wsSessionId; // 聊天对象的UserSessionId
    private Subscription newMsgSubscription;

    public NoticeUtil(Context context, @Nullable TextView msgCount, @Nullable View msgNotice) {
        this.context = context;
        if (this.tvMsgCountList == null) {
            this.tvMsgCountList = new ArrayList<>();
        }
        if (this.newNoticeViewList == null) {
            this.newNoticeViewList = new ArrayList<>();
        }
        this.tvMsgCountList.add(msgCount);
        this.newNoticeViewList.add(msgNotice);
    }

    public NoticeUtil(
            Context context,
            ArrayList<TextView> tvMsgCountList,
            ArrayList<View> newNoticeViewList) {
        this.context = context;
        this.tvMsgCountList = tvMsgCountList;
        this.newNoticeViewList = newNoticeViewList;
    }

    /**
     * 初始化一个通知帮助类
     *
     * @param context
     * @param msgCount
     * @param msgNotice
     * @param wsSessionId 聊天对象的user id
     */
    public NoticeUtil(
            Context context,
            @Nullable TextView msgCount,
            @Nullable View msgNotice,
            long wsSessionId) {
        this.context = context;
        this.wsSessionId = wsSessionId;
        if (this.tvMsgCountList == null) {
            this.tvMsgCountList = new ArrayList<>();
        }
        if (this.newNoticeViewList == null) {
            this.newNoticeViewList = new ArrayList<>();
        }
        this.tvMsgCountList.add(msgCount);
        this.newNoticeViewList.add(msgNotice);
    }

    public void onPause() {
        if (newMsgSubscription != null && !newMsgSubscription.isUnsubscribed()) {
            newMsgSubscription.unsubscribe();
        }
    }

    public void onResume() {
        refreshNewMsg();
        if (newMsgSubscription != null && !newMsgSubscription.isUnsubscribed()) {
            return;
        }
        newMsgSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .buffer(1, TimeUnit.SECONDS)
                .filter(new Func1<List<RxEvent>, Boolean>() {
                    @Override
                    public Boolean call(List<RxEvent> rxEvents) {
                        for (RxEvent rxEvent : rxEvents) {
                            switch (rxEvent.getType()) {
                                case NEW_NOTIFICATION:
                                    if (rxEvent.getObject() != null && rxEvent.getObject()
                                            .equals(-1)) {
                                        continue;
                                    }
                                case EM_MESSAGE:
                                case WS_MESSAGE:
                                    return true;
                            }

                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<List<RxEvent>>() {
                    @Override
                    protected void onEvent(List<RxEvent> rxEvents) {
                        refreshNewMsg();
                    }
                });
    }

    private void refreshNewMsg() {
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            setNoNews();
            return;
        }
        int count;
        if (wsSessionId > 0) {
            count = NotificationUtil.getChatNewsCount(context, wsSessionId);
        } else {
            count = NotificationUtil.getChatNewsCount(context);
        }
        if (count > 0) {
            setMsgCount(count);
        } else if (NotificationUtil.getInstance(context)
                .getCount() > 0) {
            showNoticeNewView();
        } else {
            setNoNews();
        }
    }

    private void setNoNews() {
        if (this.tvMsgCountList != null) {
            for (TextView tvMsgCount : tvMsgCountList) {
                tvMsgCount.setVisibility(View.GONE);
            }
        }
        if (this.newNoticeViewList != null) {
            for (View newView : newNoticeViewList) {
                newView.setVisibility(View.GONE);
            }
        }
    }

    private void setMsgCount(int count) {
        if (this.tvMsgCountList != null) {
            for (TextView tvMsgCount : tvMsgCountList) {
                tvMsgCount.setVisibility(View.VISIBLE);
                tvMsgCount.setText(count > 99 ? "99+" : String.valueOf(count));
            }
        }
        if (this.newNoticeViewList != null) {
            for (View newView : newNoticeViewList) {
                newView.setVisibility(View.GONE);
            }
        }
    }

    private void showNoticeNewView() {
        if (this.tvMsgCountList != null) {
            for (TextView tvMsgCount : tvMsgCountList) {
                tvMsgCount.setVisibility(View.GONE);
            }
        }
        if (this.newNoticeViewList != null) {
            for (View newView : newNoticeViewList) {
                newView.setVisibility(View.VISIBLE);
            }
        }
    }


    public void resetViews(TextView msgCount, View msgNotice) {
        if (this.tvMsgCountList == null) {
            this.tvMsgCountList = new ArrayList<>();
        }
        if (this.newNoticeViewList == null) {
            this.newNoticeViewList = new ArrayList<>();
        }
        this.tvMsgCountList.add(msgCount);
        this.newNoticeViewList.add(msgNotice);
        refreshNewMsg();
    }
}
