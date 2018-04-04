package com.hunliji.hljdebuglibrary;

import com.hunliji.hljcommonlibrary.models.realm.Tracker;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by luohanlin on 2017/9/22.
 */

public class HljDebuger {

    private static Subscription collectionSubscription;
    private static List<String> trackers;

    public static synchronized void init(boolean isDebug) {
        if (!isDebug) {
            return;
        }
        if (collectionSubscription == null || collectionSubscription.isUnsubscribed()) {
            collectionSubscription = RxBus.getDefault()
                    .toObservable(Tracker.class)
                    .filter(new Func1<Tracker, Boolean>() {
                        @Override
                        public Boolean call(Tracker tracker) {
                            return tracker.getVersion() == 2;
                        }
                    })
                    .map(new Func1<Tracker, String>() {
                        @Override
                        public String call(Tracker tracker) {
                            return tracker.getTrackerString();
                        }
                    })
                    .buffer(1, TimeUnit.SECONDS)
                    .filter(new Func1<List<String>, Boolean>() {
                        @Override
                        public Boolean call(List<String> strings) {
                            return !CommonUtil.isCollectionEmpty(strings);
                        }
                    })
                    .subscribe(new RxBusSubscriber<List<String>>() {
                        @Override
                        protected void onEvent(List<String> trackers) {
                            if (HljDebuger.trackers == null) {
                                HljDebuger.trackers = new ArrayList<>();
                            }
                            HljDebuger.trackers.addAll(trackers);
                        }
                    });
        }
    }

    public static List<String> getTrackers() {
        return trackers;
    }
}
