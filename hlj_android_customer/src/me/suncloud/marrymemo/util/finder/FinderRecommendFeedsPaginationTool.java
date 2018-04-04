package me.suncloud.marrymemo.util.finder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingException;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.model.finder.FinderFeedHistory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by chen_bin on 2018/02/06.
 */
public class FinderRecommendFeedsPaginationTool {

    private Context context;
    private RecyclerView recyclerView;
    private View pageLoadView;
    private View pageEndView;

    private List<FinderFeedHistory> histories;
    private boolean isEnd;
    private long cid;
    private String tab;
    private int currentPage = 1;

    private PagingListener pagingListener;

    public final static int PER_PAGE = 30;

    public FinderRecommendFeedsPaginationTool() {

    }

    public Observable<List<FinderFeed>> firstPageObservable() {
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (pageLoadView != null) {
                        pageLoadView.setVisibility(View.INVISIBLE);
                    }
                    if (pageEndView != null) {
                        pageEndView.setVisibility(View.GONE);
                    }
                }
            });
        }
        return Observable.create(new Observable.OnSubscribe<List<FinderFeed>>() {
            @Override
            public void call(Subscriber<? super List<FinderFeed>> subscriber) {
                boolean hasFeedsRemoved = false;
                boolean hasHistoriesRemoved = false;
                List<FinderFeed> savedFeeds = FinderPrefUtil.getInstance(context)
                        .getFinderFist30Feeds(cid, tab);
                List<FinderFeedHistory> savedHistories = FinderPrefUtil.getInstance(context)
                        .getFinderFeedHistories(cid, tab);
                int feedsCount = CommonUtil.getCollectionSize(savedFeeds);
                int historiesCount = CommonUtil.getCollectionSize(savedHistories);
                //出现前30缓存条数>ids总数 或者ids总数>缓存条数的情况下，并且前30条缓存数据不存在
                if (feedsCount > historiesCount || (feedsCount == 0 && historiesCount >
                        feedsCount)) {
                    hasFeedsRemoved = true;
                    hasHistoriesRemoved = true;
                    savedFeeds = null;
                    savedHistories = null;
                } else {
                    for (int i = 0; i < historiesCount; i++) {
                        FinderFeedHistory history = savedHistories.get(i);
                        if (FinderPrefUtil.getInstance(context)
                                .isDateExpired(history.getDate())) {
                            if (i < feedsCount) {
                                hasFeedsRemoved = true;
                                savedFeeds.remove(0);
                                feedsCount--;
                            }
                            hasHistoriesRemoved = true;
                            savedHistories.remove(i);
                            i--;
                            historiesCount--;
                        }
                    }
                }
                if (hasFeedsRemoved) {
                    FinderPrefUtil.getInstance(context)
                            .setFinderFirst30Feeds(savedFeeds, cid, tab);
                }
                if (hasHistoriesRemoved) {
                    FinderPrefUtil.getInstance(context)
                            .setFinderFeedHistories(savedHistories, cid, tab);
                }
                if (!CommonUtil.isCollectionEmpty(savedHistories)) {
                    histories = new ArrayList<>();
                    histories.addAll(savedHistories);
                }
                subscriber.onNext(savedFeeds);
                subscriber.onCompleted();
            }
        })
                .doOnNext(new Action1<List<FinderFeed>>() {
                    @Override
                    public void call(List<FinderFeed> feeds) {
                        if (CommonUtil.isCollectionEmpty(feeds)) {
                            return;
                        }
                        if (pagingListener == null) {
                            return;
                        }
                        currentPage++;
                        List<FinderFeedHistory> hs = histories.subList(0, feeds.size());
                        FinderApi.syncFinderRecommendFeedsObb(feeds, hs)
                                .subscribe(pagingListener.syncServerFinderFeedsSub());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<FinderFeed>> getPagingObservable() {
        return getScrollObservable(recyclerView).subscribeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .observeOn(Schedulers.io())
                .switchMap(new Func1<Integer, Observable<List<FinderFeed>>>() {
                    @Override
                    public Observable<List<FinderFeed>> call(Integer page) {
                        return pageLoad(page).doOnNext(new Action1<List<FinderFeed>>() {
                            @Override
                            public void call(List<FinderFeed> feeds) {
                                currentPage++;
                            }
                        });
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<List<FinderFeed>>>() {
                    @Override
                    public Observable<List<FinderFeed>> call(Throwable throwable) {
                        if (recyclerView != null) {
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (pageLoadView != null) {
                                        pageLoadView.setVisibility(View.GONE);
                                    }
                                    if (pageEndView != null) {
                                        pageEndView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                        return Observable.empty();
                    }
                });
    }

    private Observable<Integer> getScrollObservable(
            final RecyclerView recyclerView) {
        Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                final RecyclerView.OnScrollListener sl = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }
                                if (recyclerView.getAdapter() == null || recyclerView.getAdapter()
                                        .getItemCount() <= 1) { //1代表footerView的布局
                                    return;
                                }
                                int position = PaginationTool.getLastVisibleItemPosition(
                                        recyclerView);
                                int itemCount = recyclerView.getAdapter()
                                        .getItemCount();
                                if (position >= itemCount - 5 && !isEnd) {
                                    // 发出下一个加载的信号
                                    // 显示加载视图
                                    if (pageLoadView != null) {
                                        pageLoadView.setVisibility(View.VISIBLE);
                                    }
                                    if (pageEndView != null) {
                                        pageEndView.setVisibility(View.GONE);
                                    }
                                    Log.d("pagination tool",
                                            "on " + "next page: " + currentPage + 1);
                                    subscriber.onNext(currentPage + 1);
                                } else {
                                    // 到末尾页面了,显示没有更多
                                    if (pageLoadView != null) {
                                        pageLoadView.setVisibility(View.GONE);
                                    }
                                    if (pageEndView != null) {
                                        pageEndView.setVisibility(currentPage >= 1 ? View.VISIBLE
                                                : View.INVISIBLE);
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

    private Observable<List<FinderFeed>> pageLoad(int page) {
        if (getCachePageCount() >= page) {
            int fromIndex = PER_PAGE * (page - 1);
            int toIndex = PER_PAGE * page;
            toIndex = toIndex > histories.size() ? histories.size() : toIndex;
            List<FinderFeedHistory> hs = histories.subList(fromIndex, toIndex);
            return FinderApi.syncFinderRecommendFeedsObb(null, hs);
        } else {
            return getFinderRecommendFeedsObb(context,
                    cid,
                    tab,
                    true).doOnNext(new Action1<List<FinderFeed>>() {
                @Override
                public void call(List<FinderFeed> feeds) {
                    isEnd = CommonUtil.isCollectionEmpty(feeds);
                    if (!isEnd) {
                        return;
                    }
                    if (recyclerView != null) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (pageLoadView != null) {
                                    pageLoadView.setVisibility(View.GONE);
                                }
                                if (pageEndView != null) {
                                    pageEndView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 获取推荐feeds列表
     *
     * @param context
     * @param cid
     * @param tab
     * @param isFromBottom
     * @return
     */
    public static Observable<List<FinderFeed>> getFinderRecommendFeedsObb(
            final Context context, final long cid, final String tab, final boolean isFromBottom) {
        long lastId = FinderPrefUtil.getInstance(context)
                .getLastId(cid, tab);
        return FinderApi.getFinderRecommendFeedsObb(lastId, tab)
                .doOnNext(new Action1<List<FinderFeed>>() {
                    @Override
                    public void call(List<FinderFeed> feeds) {
                        if (!CommonUtil.isCollectionEmpty(feeds)) {
                            setLastIdAndTimestamp(context, feeds, cid, tab);
                            setFinderFeedHistories(context,
                                    null,
                                    feeds,
                                    cid,
                                    tab,
                                    false,
                                    isFromBottom);
                        }
                    }
                });
    }

    /**
     * 找相似
     *
     * @param context
     * @param position
     * @param feed
     * @param isCurrentFinderFeed
     * @param data
     * @param id
     * @param type
     * @param cid
     * @param tab
     * @return
     */
    public static Observable<List<FinderFeed>> getFinderSimilarFeedsObb(
            final Context context,
            int position,
            final FinderFeed feed,
            final boolean isCurrentFinderFeed,
            List<Object> data,
            long id,
            String type,
            final long cid,
            final String tab) {
        return FinderApi.getFinderSimilarFeedsObb(position, data, id, type)
                .doOnNext(new Action1<List<FinderFeed>>() {
                    @Override
                    public void call(List<FinderFeed> feeds) {
                        if (!CommonUtil.isCollectionEmpty(feeds)) {
                            setFinderFeedHistories(context,
                                    feed,
                                    feeds,
                                    cid,
                                    tab,
                                    isCurrentFinderFeed,
                                    false);
                        }
                    }
                });
    }

    /**
     * 缓存ids列表分页总数
     *
     * @return
     */
    private int getCachePageCount() {
        return histories == null ? 0 : (int) Math.ceil(histories.size() * 1.0d / PER_PAGE);
    }

    /**
     * 设置lastId,timestamp
     *
     * @param context
     * @param feeds
     * @param cid
     * @param tab
     */
    private static void setLastIdAndTimestamp(
            Context context, List<FinderFeed> feeds, long cid, String tab) {
        if (CommonUtil.isCollectionEmpty(feeds)) {
            return;
        }
        Object obj = feeds.get(feeds.size() - 1)
                .getEntityObj();
        if (obj != null && obj instanceof Note) {
            Note note = (Note) obj;
            FinderPrefUtil.getInstance(context)
                    .setLastId(note.getId(), cid, tab);
            FinderPrefUtil.getInstance(context)
                    .setLastTimestamp(HljTimeUtils.getServerCurrentTimeMillis(), cid, tab);
        }
    }

    /**
     * @param context
     * @param feed
     * @param feeds
     * @param cid
     * @param tab
     * @param isCurrentFinderFeed //当前找相似的是finderFeed，不是cpmFeed
     * @param isFromBottom
     */
    private static void setFinderFeedHistories(
            Context context,
            FinderFeed feed,
            List<FinderFeed> feeds,
            long cid,
            String tab,
            boolean isCurrentFinderFeed,
            boolean isFromBottom) {
        if (CommonUtil.isCollectionEmpty(feeds)) {
            return;
        }
        List<FinderFeedHistory> savedHistories = FinderPrefUtil.getInstance(context)
                .getFinderFeedHistories(cid, tab);
        if (savedHistories == null) {
            savedHistories = new ArrayList<>();
        }
        int position = -1;
        if (feed != null) {
            for (int i = 0, size = savedHistories.size(); i < size; i++) {
                FinderFeedHistory history = savedHistories.get(i);
                if (feed.getEntityObjId() == history.getId() && feed.getType()
                        .equals(history.getType())) {
                    if (isCurrentFinderFeed) {
                        history.setShowSimilarIcon(false);
                    }
                    position = i;
                    break;
                }
            }
        }
        List<FinderFeedHistory> tempHistories = FinderPrefUtil.getInstance(context)
                .covertFinderFeedsToHistories(feeds);
        if (isFromBottom) {
            savedHistories.addAll(tempHistories);
        } else {
            savedHistories.addAll(position + 1, tempHistories);
        }
        FinderPrefUtil.getInstance(context)
                .setFinderFeedHistories(savedHistories, cid, tab);
    }

    public static Builder buildPagingObservable(
            RecyclerView recyclerView, long cid, String tab, PagingListener pagingListener) {
        return new Builder(recyclerView, cid, tab, pagingListener);
    }

    public static class Builder {
        private RecyclerView recyclerView;
        private View loadView;
        private View endView;
        private long cid;
        private String tab;
        private PagingListener pagingListener;

        private Builder(
                RecyclerView recyclerView, long cid, String tab, PagingListener pagingListener) {
            if (recyclerView == null) {
                throw new PagingException("recyclerView is null");
            }
            if (recyclerView.getAdapter() == null) {
                throw new PagingException("recyclerView adapter is null");
            }
            this.recyclerView = recyclerView;
            this.cid = cid;
            this.tab = tab;
            this.pagingListener = pagingListener;
        }

        public FinderRecommendFeedsPaginationTool build() {
            FinderRecommendFeedsPaginationTool paginationTool = new
                    FinderRecommendFeedsPaginationTool();

            paginationTool.recyclerView = this.recyclerView;
            paginationTool.context = recyclerView.getContext();
            paginationTool.cid = this.cid;
            paginationTool.tab = this.tab;
            paginationTool.pageLoadView = this.loadView;
            paginationTool.pageEndView = this.endView;
            paginationTool.pagingListener = this.pagingListener;
            if (this.loadView != null) {
                this.loadView.setVisibility(View.GONE);
            }
            if (this.endView != null) {
                this.endView.setVisibility(View.INVISIBLE);
            }
            return paginationTool;
        }

        public Builder setLoadView(View loadView) {
            this.loadView = loadView;
            return this;
        }

        public Builder setEndView(View endView) {
            this.endView = endView;
            return this;
        }

    }

    public interface PagingListener {
        Subscriber<List<FinderFeed>> syncServerFinderFeedsSub();
    }
}
