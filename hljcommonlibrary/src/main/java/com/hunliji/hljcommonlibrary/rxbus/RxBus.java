package com.hunliji.hljcommonlibrary.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {
    private static volatile RxBus mDefaultInstance;
    private final Subject<Object, Object> mBus;

    public RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    /**
     * 发送事件
     */
    public void post(Object event) {
        if(mBus.hasObservers()) {
            mBus.onNext(event);
        }
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    public void reset() {
        mDefaultInstance = null;
    }

}