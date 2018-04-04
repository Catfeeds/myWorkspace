package com.hunliji.hljnotelibrary.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.CommonNoteListAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/9/21 商家动态（用户端）
 */
public class MerchantNoteListFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<Note> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private String sort;
    private long id;
    private View footerView;
    private View endView;
    private View loadView;
    private CommonNoteListAdapter adapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private StaggeredGridLayoutManager layoutManager;
    private TabPageIndicator indicator;

    public static MerchantNoteListFragment newInstance(long id) {
        MerchantNoteListFragment noteListFragment = new MerchantNoteListFragment();
        Bundle arg = new Bundle();
        arg.putLong("id", id);
        noteListFragment.setArguments(arg);
        return noteListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong("id", 0);
        }
        sort = MerchantNoteActivity.SORT_NOTE_NEW;
        footerView = View.inflate(getContext(), R.layout.note_mark_list_footer, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new CommonNoteListAdapter(getContext());
        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout
                        .hlj_common_fragment_ptr_staggered_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        indicator = getActivity().findViewById(R.id.indicator);

        adapter.setOnItemClickListener(this);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0) {
            sort = (String) params[0];
            onRefresh(null);
        }
    }

    @Override
    public View getScrollableView() {
        if (recyclerView == null) {
            return null;
        }
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        super.onDestroyView();
    }

    @Override
    public void onRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }

        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(refreshView == null ? progressBar : null)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Note>> listHljHttpData) {
                        if (indicator != null) {
                            indicator.setTabText("动态" + listHljHttpData.getTotalCount(), 0);
                        }
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        adapter.setNotes(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        NoteApi.getNoteBookNotesObb(0, id, sort, 1, 20)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Note>>> noteObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Note>>>() {
                    @Override
                    public Observable<HljHttpData<List<Note>>> onNextPage(int page) {
                        return NoteApi.getNoteBookNotesObb(0, id, sort, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Note>> listHljHttpData) {
                        adapter.addNotes(listHljHttpData.getData());
                    }
                })
                .build();
        noteObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onItemClick(int position, Note note) {
        if (note != null && note.getId() > 0) {
            Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
            intent.putExtra("note_id", note.getId());
            intent.putExtra("item_position", position);
            intent.putExtra("url", note.getUrl());
            startActivityForResult(intent, HljNote.RequestCode.NOTE_DETAIL);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_DETAIL:
                    if (data != null) {
                        int position = data.getIntExtra("position", 0);
                        int collectCount = data.getIntExtra("collect_count", -1);
                        int commentCount = data.getIntExtra("comment_count", -1);
                        Note note = adapter.getItem(position);
                        //-1表示数据未改变
                        if (collectCount != -1) {
                            note.setCollectCount(collectCount);
                        }
                        if (commentCount != -1) {
                            note.setCommentCount(commentCount);
                        }
                        adapter.notifyItemChanged(position);
                    }
                    break;
            }
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
