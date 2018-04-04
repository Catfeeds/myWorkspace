package com.hunliji.hljtrackerlibrary;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.models.realm.Tracker;
import com.hunliji.hljcommonlibrary.models.realm.TrackerRealmModule;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.view_tracker.models.MiaoZhenTracker;
import com.hunliji.hljtrackerlibrary.api.TrackerApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/3/23.
 */

public enum TrackerCollection {

    INSTANCE;

    private Subscription collectionSubscription;
    private Subscription sendTrackerSubscription;
    private Subscription miaoZhenCollectionSubscription;

    TrackerCollection() {
        registerSubscription();
    }

    public synchronized void registerSubscription() {
        if (collectionSubscription == null || collectionSubscription.isUnsubscribed()) {
            collectionSubscription = RxBus.getDefault()
                    .toObservable(Tracker.class)
                    .buffer(5, TimeUnit.SECONDS)
                    .filter(new Func1<List<Tracker>, Boolean>() {
                        @Override
                        public Boolean call(List<Tracker> trackers) {
                            return !trackers.isEmpty();
                        }
                    })
                    .subscribe(new RxBusSubscriber<List<Tracker>>() {
                        @Override
                        protected void onEvent(List<Tracker> trackers) {
                            onSave(trackers);
                        }
                    });
        }
        if (miaoZhenCollectionSubscription == null || miaoZhenCollectionSubscription
                .isUnsubscribed()) {
            miaoZhenCollectionSubscription = RxBus.getDefault()
                    .toObservable(MiaoZhenTracker.class)
                    .map(new Func1<MiaoZhenTracker, String>() {
                        @Override
                        public String call(MiaoZhenTracker tracker) {
                            return tracker.getUrl();
                        }
                    })
                    .filter(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String url) {
                            return !TextUtils.isEmpty(url);
                        }
                    })
                    .subscribe(new RxBusSubscriber<String>() {
                        @Override
                        protected void onEvent(String url) {
                            TrackerApi.miaoZhenTracker(url)
                                    .subscribe(new EmptySubscriber());
                        }
                    });
        }
    }

    private void onSave(List<Tracker> trackers) {
        Observable.just(trackers)
                .concatMap(new Func1<List<Tracker>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(final List<Tracker> trackers) {
                        return Observable.create(new Observable.OnSubscribe<Long>() {
                            @Override
                            public void call(
                                    final Subscriber<? super Long> subscriber) {
                                final Realm realm = initTrackerRealm();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (realm.where(Tracker.class)
                                                .count() > 99) {
                                            realm.where(Tracker.class)
                                                    .findAll()
                                                    .deleteAllFromRealm();
                                        }
                                        realm.copyToRealmOrUpdate(trackers);
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        subscriber.onNext(realm.where(Tracker.class)
                                                .count());
                                        subscriber.onCompleted();
                                        realm.close();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        subscriber.onError(error);
                                        realm.close();
                                    }
                                });
                            }
                        })
                                .subscribeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<Long>() {
                    @Override
                    protected void onEvent(Long count) {
                        if (count > 0) {
                            sendTracker(false);
                        }
                    }
                });
    }

    public void sendTracker(boolean ignoreCount) {
        if (sendTrackerSubscription != null && !sendTrackerSubscription.isUnsubscribed()) {
            return;
        }
        sendTrackerSubscription = Observable.mergeDelayError(sendV1TrackersObb(ignoreCount),
                sendV2TrackersObb(ignoreCount))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer version) {
                        if (version > 0) {
                            Realm realm = initTrackerRealm();
                            realm.beginTransaction();
                            realm.where(Tracker.class)
                                    .equalTo("version", version)
                                    .findAll()
                                    .deleteAllFromRealm();
                            realm.commitTransaction();
                        }
                    }
                });
    }


    private Observable<Integer> sendV1TrackersObb(final boolean ignoreCount) {
        return Observable.create(new Observable.OnSubscribe<JsonArray>() {
            @Override
            public void call(Subscriber<? super JsonArray> subscriber) {
                try {
                    Realm realm = initTrackerRealm();
                    RealmResults<Tracker> trackers = realm.where(Tracker.class)
                            .equalTo("version", 1)
                            .findAll();
                    if (trackers.size() > 4 || (ignoreCount && trackers.size() > 0)) {
                        JsonArray jsonArray = new JsonArray();
                        for (Tracker tracker : trackers) {
                            jsonArray.add(new JsonParser().parse(tracker.getTrackerString()));
                        }
                        subscriber.onNext(jsonArray);
                    }
                    realm.close();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .concatMap(new Func1<JsonArray, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(JsonArray array) {
                        return TrackerApi.postTrackers(array);
                    }
                })
                .map(new Func1<Boolean, Integer>() {
                    @Override
                    public Integer call(Boolean aBoolean) {
                        return aBoolean ? 1 : 0;
                    }
                });
    }

    private Observable<Integer> sendV2TrackersObb(final boolean ignoreCount) {
        return Observable.create(new Observable.OnSubscribe<JsonArray>() {
            @Override
            public void call(Subscriber<? super JsonArray> subscriber) {
                try {
                    Realm realm = initTrackerRealm();
                    RealmResults<Tracker> trackers = realm.where(Tracker.class)
                            .equalTo("version", 2)
                            .findAll();
                    if (trackers.size() > 4 || (ignoreCount && trackers.size() > 0)) {
                        JsonArray jsonArray = new JsonArray();
                        for (Tracker tracker : trackers) {
                            jsonArray.add(new JsonParser().parse(tracker.getTrackerString()));
                        }
                        subscriber.onNext(jsonArray);
                    }
                    realm.close();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .concatMap(new Func1<JsonArray, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(JsonArray array) {
                        return TrackerApi.postTrackersV2(array);
                    }
                })
                .map(new Func1<Boolean, Integer>() {
                    @Override
                    public Integer call(Boolean aBoolean) {
                        return aBoolean ? 2 : 0;
                    }
                });
    }

    private Realm initTrackerRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder().name("tracker.realm")
                .schemaVersion(1)
                .modules(new TrackerRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build());
    }
}
