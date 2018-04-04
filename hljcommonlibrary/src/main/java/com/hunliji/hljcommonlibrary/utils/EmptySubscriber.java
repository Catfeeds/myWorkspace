package com.hunliji.hljcommonlibrary.utils;

import rx.Subscriber;

/**
 * Created by wangtao on 2017/10/26.
 */

public class EmptySubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onNext(T t) {
    }
}
