package me.suncloud.marrymemo.fragment.newsearch;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.adpter.newsearch.NewSearchNoteResultsAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/8
 * 笔记结果页
 */

public class NewSearchNoteResultFragment extends NewBaseSearchResultFragment {

    private ArrayList<Note> notes = new ArrayList<>();
    private NewSearchNoteResultsAdapter adapter;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private StaggeredGridLayoutManager layoutManager;

    public static NewSearchNoteResultFragment newInstance(Bundle args) {
        NewSearchNoteResultFragment fragment = new NewSearchNoteResultFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initViews() {
        super.initViews();
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        adapter = new NewSearchNoteResultsAdapter(getContext(), notes);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    protected void initLoad() {
        super.initLoad();
        clearData(adapter);
        CommonUtil.unSubscribeSubs(initSub, pageSub);
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<Note>> httpSearch) {
                        notes.addAll(httpSearch.getData());
                        initPage(httpSearch.getPageCount());
                        adapter.setData(notes);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                    }
                })
                .build();
        NewSearchApi.getNoteList(keyword, searchType, 1)
                .subscribe(initSub);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpSearch<List<Note>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpSearch<List<Note>>>() {
                    @Override
                    public Observable<HljHttpSearch<List<Note>>> onNextPage(int page) {
                        return NewSearchApi.getNoteList(keyword, searchType, page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpSearch<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpSearch<List<Note>> hljHttpSearch) {
                        adapter.addData(hljHttpSearch.getData());
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(initSub, pageSub);
    }

    @Override
    protected void resetFilterView() {
        if (getSearchActivity() != null) {
            getSearchActivity().setFilterView(null);
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int topSpace;
        private int leftAndRightSpace;

        private SpacesItemDecoration() {
            this.topSpace = CommonUtil.dp2px(getContext(), 4);
            this.leftAndRightSpace = CommonUtil.dp2px(getContext(), 6);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int headerCount = adapter.getHeaderViewCount();
            int position = parent.getChildAdapterPosition(view);
            if (position >= headerCount && position < adapter.getItemCount() - adapter
                    .getFooterViewCount()) {
                top = position > headerCount + 1 ? -topSpace : topSpace / 2;
                left = lp.getSpanIndex() == 0 ? leftAndRightSpace : 0;
                right = lp.getSpanIndex() == 0 ? 0 : leftAndRightSpace;
            }
            outRect.set(left, top, right, 0);
        }
    }
}
