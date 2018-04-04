package com.hunliji.hljhttplibrary.utils;

import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;

import rx.functions.Func1;

/**
 * Created by wangtao on 2018/3/21.
 */

public class PosterAnnotationFunc<T> implements Func1<PosterData, T> {

    private T t;

    public PosterAnnotationFunc(T t) {
        this.t = t;
    }

    @Override
    public T call(PosterData posterData) {
        return PosterUtil.getAnnotationPoster(posterData.getFloors(),t);
    }
}
