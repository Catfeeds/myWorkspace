package com.hunliji.hljhttplibrary.utils.pagination;

import rx.Observable;

/**
 * Created by werther on 16/8/2.
 */
public interface PagingListener<T> {
    Observable<T> onNextPage(int page);
}
