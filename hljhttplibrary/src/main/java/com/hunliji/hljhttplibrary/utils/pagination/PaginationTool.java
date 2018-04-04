package com.hunliji.hljhttplibrary.utils.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by werther on 16/8/2.
 * 用于RecyclerView的翻页加载帮助类
 */
public class PaginationTool<T> {

    // 默认的翻页预加载(提前加载)的偏移量
    private static final int DEFAULT_PRE_LOAD_OFFSET = 5;
    // 默认的第一次加载的页码
    private static final int START_PAGE_NUMBER = 1;
    // 默认的失败重载的重试次数,默认不需要失败重载
    private static final int DEFAULT_RETRY_COUNT = 0;

    private RecyclerView recyclerView;
    private ListView listView;
    private PagingListener<T> pagingListener;
    private int currentPage;
    private int preLoadOffset;
    private int retryCount;
    private View pageLoadView;
    private View pageEndView;
    private int pageCount;

    public PaginationTool() {
    }

    /**
     * 生成翻页加载的Observable
     *
     * @return
     */
    public Observable<T> getPagingObservable() {
        final int startNumberOfRetryAttempt = 0;

        if (recyclerView != null) {
            return getScrollObservable(recyclerView,
                    preLoadOffset).subscribeOn(AndroidSchedulers.mainThread())
                    .distinctUntilChanged()
                    .observeOn(Schedulers.io())
                    .switchMap(new Func1<Integer, Observable<? extends T>>() {
                        @Override
                        public Observable<? extends T> call(Integer page) {
                            return getPagingObservable(pagingListener,
                                    pagingListener.onNextPage(page),
                                    startNumberOfRetryAttempt,
                                    page,
                                    retryCount);
                        }
                    });

        } else if (listView != null) {
            return getScrollObservable(listView,
                    preLoadOffset).subscribeOn(AndroidSchedulers.mainThread())
                    .distinctUntilChanged()
                    .observeOn(Schedulers.io())
                    .switchMap(new Func1<Integer, Observable<? extends T>>() {
                        @Override
                        public Observable<? extends T> call(Integer page) {
                            return getPagingObservable(pagingListener,
                                    pagingListener.onNextPage(page),
                                    startNumberOfRetryAttempt,
                                    page,
                                    retryCount);
                        }
                    });
        } else
            return null;
    }

    private Observable<Integer> getScrollObservable(
            final RecyclerView recyclerView, final int preLoadOffset) {
        Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(
                    final Subscriber<? super Integer> subscriber) {
                final RecyclerView.OnScrollListener sl = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                if (!subscriber.isUnsubscribed()) {
                                    if (currentPage >= pageCount) {
                                        // 到末尾页面了,显示没有更多
                                        if (pageLoadView != null) {
                                            pageLoadView.setVisibility(View.GONE);
                                        }
                                        if (pageEndView != null) {
                                            pageEndView.setVisibility(currentPage >= 1 ? View
                                                    .VISIBLE : View.INVISIBLE);
                                        }
                                        return;
                                    }
                                    int position = getLastVisibleItemPosition(recyclerView);
                                    int itemCount = recyclerView.getAdapter()
                                            .getItemCount();
                                    if (position >= itemCount - preLoadOffset) {
                                        // 发出下一个加载的信号
                                        // 显示加载视图
                                        Log.d("pagination tool",
                                                "on " + "next page: " + currentPage + 1);
                                        subscriber.onNext(currentPage + 1);
                                    }
                                }
                                break;
                        }
                    }
                };
                recyclerView.addOnScrollListener(sl);
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        Log.d("pagination tool", "remove recycler view scroll listener");
                        recyclerView.removeOnScrollListener(sl);
                    }
                }));
            }
        };

        return Observable.create(onSubscribe);
    }

    private Observable<Integer> getScrollObservable(
            final ListView listView, final int preLoadOffset) {
        Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(
                    final Subscriber<? super Integer> subscriber) {
                final AbsListView.OnScrollListener sl = new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        switch (scrollState) {
                            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                if (!subscriber.isUnsubscribed()) {
                                    if (currentPage >= pageCount) {
                                        // 到末尾页面了,显示没有更多
                                        if (pageLoadView != null) {
                                            pageLoadView.setVisibility(View.GONE);
                                        }
                                        if (pageEndView != null) {
                                            pageEndView.setVisibility(currentPage >= 1 ? View
                                                    .VISIBLE : View.INVISIBLE);
                                        }
                                        return;
                                    }
                                    int position = listView.getLastVisiblePosition();
                                    int itemCount = listView.getAdapter()
                                            .getCount();
                                    if (position >= itemCount - preLoadOffset) {
                                        // 发出下一个加载的信号
                                        // 显示加载视图
                                        Log.d("pagination tool",
                                                "on " + "next page: " + currentPage + 1);
                                        subscriber.onNext(currentPage + 1);
                                    }
                                }
                                break;
                        }
                    }

                    @Override
                    public void onScroll(
                            AbsListView view,
                            int firstVisibleItem,
                            int visibleItemCount,
                            int totalItemCount) {
                    }
                };
                listView.setOnScrollListener(sl);
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        Log.d("pagination tool", "remove list view scroll listener");
                        listView.setOnScrollListener(null);
                    }
                }));
            }
        };

        return Observable.create(onSubscribe);
    }

    public static int getLastVisibleItemPosition(RecyclerView recyclerView) {
        Class recyclerViewLMClass = recyclerView.getLayoutManager()
                .getClass();
        if (recyclerViewLMClass == LinearLayoutManager.class || LinearLayoutManager.class
                .isAssignableFrom(
                recyclerViewLMClass)) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            return linearLayoutManager.findLastVisibleItemPosition();
        } else if (recyclerViewLMClass == StaggeredGridLayoutManager.class ||
                StaggeredGridLayoutManager.class.isAssignableFrom(
                recyclerViewLMClass)) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)
                    recyclerView.getLayoutManager();
            int[] into = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            List<Integer> intoList = new ArrayList<>();
            for (int i : into) {
                intoList.add(i);
            }
            return Collections.max(intoList);
        }
        throw new PagingException("Unknown LayoutManager class: " + recyclerViewLMClass.toString());
    }

    private Observable<T> getPagingObservable(
            final PagingListener<T> listener,
            Observable<T> observable,
            final int numberOfAttemptToRetry,
            final int page,
            final int retryCount) {
        return observable.doOnNext(new Action1<T>() {
            @Override
            public void call(T t) {
                if (t instanceof HljHttpData) {
                    // 翻页数据返回来, 确认currentPage+1
                    pageCount = ((HljHttpData) t).getPageCount();
                }
                currentPage += 1;
                Log.d("pagination tool", "do on next");
            }
        })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        View view = listView == null ? recyclerView : listView;
                        if (view != null) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (pageLoadView != null) {
                                        pageLoadView.setVisibility(View.VISIBLE);
                                    }
                                    if (pageEndView != null) {
                                        pageEndView.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
                    @Override
                    public Observable<? extends T> call(Throwable throwable) {
                        View view = listView == null ? recyclerView : listView;
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if (pageLoadView != null) {
                                    pageLoadView.setVisibility(View.GONE);
                                }
                                if (pageEndView != null) {
                                    pageEndView.setVisibility(page >= 1 ? View.VISIBLE : View
                                            .INVISIBLE);
                                }
                            }
                        });
                        Log.d("pagination tool", "on error: " + page);
                        if (numberOfAttemptToRetry < retryCount) {
                            int attemptToRetryInc = numberOfAttemptToRetry + 1;
                            return getPagingObservable(listener,
                                    listener.onNextPage(page),
                                    attemptToRetryInc,
                                    page,
                                    retryCount);
                        } else {
                            return Observable.empty();
                        }
                    }
                });
    }

    public static <T> Builder<T> buildPagingObservable(
            RecyclerView recyclerView, int pageCount, PagingListener<T> pagingListener) {
        return new Builder<>(recyclerView, pagingListener, pageCount);
    }

    public static <T> Builder<T> buildPagingObservable(
            ListView listView, int pageCount, PagingListener<T> pagingListener) {
        return new Builder<>(listView, pagingListener, pageCount);
    }

    public static class Builder<T> {
        private RecyclerView recyclerView;
        private ListView listView;
        private PagingListener<T> pagingListener;
        private int currentPage = START_PAGE_NUMBER;
        private int preLoadOffset = DEFAULT_PRE_LOAD_OFFSET;
        private int retryCount = DEFAULT_RETRY_COUNT;
        private View loadView;
        private View endView;
        private int pageCount;

        private Builder(
                RecyclerView recyclerView, PagingListener<T> pagingListener, int pageCount) {
            if (recyclerView == null) {
                throw new PagingException("recyclerView is null");
            }
            if (recyclerView.getAdapter() == null) {
                throw new PagingException("recyclerView adapter is null");
            }
            if (pagingListener == null) {
                throw new PagingException("pagingListener is null");
            }
            this.recyclerView = recyclerView;
            this.pagingListener = pagingListener;
            this.pageCount = pageCount;
        }

        private Builder(
                ListView listView, PagingListener<T> pagingListener, int pageCount) {
            if (listView == null) {
                throw new PagingException("list view is null");
            }
            if (listView.getAdapter() == null) {
                throw new PagingException("list view adapter is null");
            }
            if (pagingListener == null) {
                throw new PagingException("pagingListener is null");
            }
            this.listView = listView;
            this.pagingListener = pagingListener;
            this.pageCount = pageCount;
        }

        /**
         * 设置翻页预加载的偏移量
         *
         * @param preLoadOffset
         * @return
         */
        public Builder<T> setPreLoadOffset(int preLoadOffset) {
            if (preLoadOffset <= 0) {
                throw new PagingException("pre load offset should greater than 0");
            }
            this.preLoadOffset = preLoadOffset;
            return this;
        }

        /**
         * 设置初始化加载的页码
         *
         * @param currentPage
         * @return
         */
        public Builder<T> setCurrentPage(int currentPage) {
            if (currentPage < 1) {
                throw new PagingException("init current page should greater than 0");
            }
            this.currentPage = currentPage;
            return this;
        }

        /**
         * 设置失败重载的重试次数
         *
         * @param retryCount
         * @return
         */
        public Builder<T> setRetryCount(int retryCount) {
            if (retryCount < 0) {
                throw new PagingException("retry count can't be less than 0");
            }
            this.retryCount = retryCount;
            return this;
        }

        /**
         * 设置翻页加载视图
         *
         * @param loadView
         * @return
         */
        public Builder<T> setLoadView(View loadView) {
            this.loadView = loadView;
            return this;
        }

        /**
         * 设置翻页结束视图
         *
         * @param endView
         * @return
         */
        public Builder<T> setEndView(View endView) {
            this.endView = endView;
            return this;
        }

        public PaginationTool<T> build() {
            PaginationTool<T> paginationTool = new PaginationTool<>();
            paginationTool.recyclerView = this.recyclerView;
            paginationTool.listView = this.listView;
            paginationTool.preLoadOffset = this.preLoadOffset;
            paginationTool.pagingListener = this.pagingListener;
            paginationTool.currentPage = this.currentPage;
            paginationTool.retryCount = this.retryCount;
            paginationTool.pageLoadView = this.loadView;
            paginationTool.pageEndView = this.endView;
            paginationTool.pageCount = this.pageCount;
            if (this.loadView != null) {
                this.loadView.setVisibility(currentPage < pageCount ? View.VISIBLE : View.GONE);
            }
            if (this.endView != null) {
                this.endView.setVisibility(currentPage >= pageCount ? View.VISIBLE : View.GONE);
            }
            return paginationTool;
        }
    }
}
