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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.CommonNoteListAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.Notebook;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljnotelibrary.views.activities.NotebookActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 我的笔记本(包括收藏，个人主页)
 * Created by jinxin on 2017/6/26 0026.
 */

public class MyNoteListFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<Note> {

    //我的笔记 列表不需要加header
    public static final int TYPE_MY_NOTE = 11;
    //用户主页 列表不需要header
    public static final int TYPE_USER = 12;

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    private int type;
    private CommonNoteListAdapter adapter;
    private HeaderViewHolder headerHolder;
    private View headerView;
    private View footerView;
    private View endView;
    private View loadView;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private long userId;

    public static MyNoteListFragment newInstance(int type, long userId) {
        MyNoteListFragment noteListFragment = new MyNoteListFragment();
        Bundle arg = new Bundle();
        arg.putInt("type", type);
        arg.putLong("user_id", userId);
        noteListFragment.setArguments(arg);
        return noteListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            userId = getArguments().getLong("user_id");
        }
        adapter = new CommonNoteListAdapter(getContext());
        if (type == TYPE_MY_NOTE) {
            headerView = LayoutInflater.from(getContext())
                    .inflate(R.layout.my_note_list_header___note, null, false);
            headerHolder = new HeaderViewHolder(headerView);
            adapter.setHeaderView(headerView);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        if (type == TYPE_MY_NOTE) {
            userId = UserSession.getInstance()
                    .getUser(getContext())
                    .getId();
        }
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
        initWidget();
        return rootView;
    }

    private void initWidget() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(adapter.getNotes())) {
            onRefresh(null);
        }
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0) {
            userId = (long) params[0];
            onRefresh(null);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        switch (type) {
            case TYPE_MY_NOTE:
                onRefreshMyNote(refreshView);
                break;
            case TYPE_USER:
                onRefreshUserNote(refreshView);
                break;
            default:
                break;
        }
    }

    private void onRefreshMyNote(View refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setResultZip(resultZip);
                    }
                })
                .build();

        Observable<HljHttpData<List<Notebook>>> bookObb = NoteApi.myNoteBookList(userId, 1);
        Observable<HljHttpData<List<Note>>> noteObb = NoteApi.myNoteList(userId, 1);
        Observable<ResultZip> zipObb = Observable.zip(bookObb,
                noteObb,
                new Func2<HljHttpData<List<Notebook>>, HljHttpData<List<Note>>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<Notebook>> noteBookData,
                            HljHttpData<List<Note>> noteData) {
                        ResultZip zip = new ResultZip();
                        zip.noteBooks = noteBookData;
                        zip.notes = noteData;
                        return zip;
                    }
                });
        zipObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(refreshSubscriber);
    }

    private void setResultZip(ResultZip zip) {
        if (zip.notes == null || CommonUtil.isCollectionEmpty(zip.notes.getData())) {
            emptyView.showEmptyView();
        } else {
            emptyView.hideEmptyView();
            setHeader(zip.noteBooks);
            adapter.setNotes(zip.notes.getData());
            initPagination(zip.notes.getPageCount());
            if (onTabTextChangeListener != null) {
                onTabTextChangeListener.onTabTextChange(zip.notes.getTotalCount());
            }
        }
    }

    private void onRefreshUserNote(View refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Note>> listHljHttpData) {
                        adapter.setNotes(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .build();
        Observable<HljHttpData<List<Note>>> noteObb = NoteApi.myNoteList(userId, 1);
        noteObb.subscribe(refreshSubscriber);
    }

    private void setHeader(HljHttpData<List<Notebook>> bookData) {
        if (bookData == null || bookData.getData() == null) {
            return;
        }

        if (headerHolder == null) {
            return;
        }

        headerHolder.setNoteBook(bookData.getData());
    }

    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            return;
        }

        Observable<HljHttpData<List<Note>>> noteObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Note>>>() {
                    @Override
                    public Observable<HljHttpData<List<Note>>> onNextPage(int page) {
                        return NoteApi.myNoteList(userId, page);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
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

    class ResultZip extends HljHttpResultZip {
        HljHttpData<List<Notebook>> noteBooks;
        HljHttpData<List<Note>> notes;

        @Override
        public boolean isDataEmpty() {
            return (noteBooks == null || noteBooks.isEmpty()) && (notes == null || notes.isEmpty());
        }
    }

    class HeaderViewHolder {

        @BindView(R2.id.layout_note)
        LinearLayout layoutNote;

        int width;
        int height;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            width = (int) ((CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px
                    (getContext(),
                    36)) * 1.0f / 3);
            height = Math.round(width * 10 / 19);
        }

        public void setNoteBook(List<Notebook> books) {
            if (books == null) {
                return;
            }
            int size = books.size();
            for (int i = 0; i < 3; i++) {
                View childView = layoutNote.getChildAt(i);
                childView.getLayoutParams().width = width;
                childView.getLayoutParams().height = height;
                childView.requestLayout();
                if (i < size) {
                    final Notebook book = books.get(i);
                    childView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (book.getNoteCount() <= 0) {
                                Toast.makeText(getContext(), "你还未在该笔记本创建笔记～", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Intent intent = new Intent(getContext(), NotebookActivity.class);
                                intent.putExtra("note_book_id", book.getId());
                                getContext().startActivity(intent);
                                ((Activity) getContext()).overridePendingTransition(R.anim
                                                .slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                    });
                    BookItemViewHolder itemViewHolder = (BookItemViewHolder) childView.getTag();
                    if (itemViewHolder == null) {
                        itemViewHolder = new BookItemViewHolder(childView);
                        childView.setTag(itemViewHolder);
                    }
                    if (book == null) {
                        childView.setVisibility(View.GONE);
                    } else {
                        childView.setVisibility(View.VISIBLE);
                        itemViewHolder.tvTitle.setText(getType(book.getNoteBookType()));
                        itemViewHolder.tvCount.setText(getString(R.string
                                        .label_note_img_count___note,
                                String.valueOf(book.getNoteCount()),
                                String.valueOf(book.getPicsCount())));
                        String coverPath = book.getCoverPath();
                        Glide.with(getContext())
                                .load(ImagePath.buildPath(coverPath)
                                        .width(width)
                                        .height(height)
                                        .cropPath())
                                .apply(new RequestOptions().dontAnimate()
                                        .override(width, height)
                                        .placeholder(R.mipmap.icon_empty_image)
                                        .error(R.mipmap.icon_empty_image))
                                .into(itemViewHolder.imgCover);
                    }
                } else {
                    childView.setVisibility(View.GONE);
                }
            }
        }

        private String getType(int type) {
            switch (type) {
                case 1:
                    return "婚纱照";
                case 2:
                    return "婚礼筹备";
                case 3:
                    return "婚品筹备";
                case 4:
                    return "婚礼人";
                default:
                    return null;
            }
        }
    }

    class BookItemViewHolder {

        @BindView(R2.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_count)
        TextView tvCount;

        public BookItemViewHolder(View view) {
            ButterKnife.bind(this, view);
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
