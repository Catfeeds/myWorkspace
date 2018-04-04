package com.hunliji.hljnotelibrary.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.NoteMerchantListService;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpDayLimitData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.CommonNoteListAdapter;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NotebookType;
import com.hunliji.hljnotelibrary.utils.NoteDialogUtil;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljnotelibrary.views.widgets.SpacesItemDecoration;
import com.hunliji.hljvideolibrary.activities.BaseVideoTrimActivity;
import com.hunliji.hljvideolibrary.activities.VideoChooserActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 商家端动态列表
 * Created by jinxin on 2017/7/13 0013.
 */
public class MerchantNoteListActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<Note> {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R2.id.btn_shadow_back)
    ImageButton btnShadowBack;
    @BindView(R2.id.btn_shadow_create)
    TextView btnShadowCreate;
    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.btn_create)
    TextView btnCreate;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R2.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private View footerView;
    private View headerView;
    private View endView;
    private View loadView;
    private int extraHeight;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private StaggeredGridLayoutManager layoutManager;
    private CommonNoteListAdapter adapter;
    private HeaderViewHolder headerViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_note_list___note);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);

        initConstants();
        initWidget();
        onRefresh(null);
        registerRxBusEvent();
    }

    private void initConstants() {
        extraHeight = CommonUtil.dp2px(this, 45) + getStatusBarHeight();
        adapter = new CommonNoteListAdapter(this);
    }

    private void initWidget() {
        adapter.setOnItemClickListener(this);
        headerView = View.inflate(this, R.layout.merchant_feed_list_header___note, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        recyclerView.setOnRefreshListener(this);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration(this, 1, 1));
        adapter.setHeaderView(headerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (headerViewHolder == null) {
                            return;
                        }
                        int headerTop = Math.abs(headerView.getTop());
                        int[] position = new int[2];
                        layoutManager.findFirstVisibleItemPositions(position);
                        if (headerTop >= headerViewHolder.headerHeight - extraHeight ||
                                position[0] >= 2) {
                            actionHolderLayout.setAlpha(0);
                            actionHolderLayout2.setAlpha(1);
                        } else {
                            float f = headerTop * 1.0f / (headerViewHolder.headerHeight -
                                    extraHeight);
                            actionHolderLayout.setAlpha(1 - f);
                            actionHolderLayout2.setAlpha(f);
                        }
                    }
                });
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CREATE_NOTE_SUCCESS:
                                case DELETE_NOTE_SUCCESS:
                                    onRefresh(null);
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpDayLimitData<List<Note>>>() {
                        @Override
                        public void onNext(HljHttpDayLimitData<List<Note>> dayLimitData) {
                            setNotesData(dayLimitData);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            setNotesData(null);
                        }
                    })
                    .setDataNullable(true)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            NoteApi.getMerchantNoteBookNotesObb(1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void setNotesData(HljHttpDayLimitData<List<Note>> dayLimitData) {
        int dayLimit = 0;
        int pageCount = 0;
        List<Note> notes = null;
        if (dayLimitData != null) {
            dayLimit = dayLimitData.getDayLimit();
            pageCount = dayLimitData.getPageCount();
            notes = dayLimitData.getData();
        }
        boolean isDataEmpty = CommonUtil.isCollectionEmpty(notes);
        if (headerViewHolder != null) {
            headerViewHolder.setShowEmptyView(isDataEmpty);
            headerViewHolder.tvLimitCount.setText(String.valueOf(dayLimit));
        }
        adapter.setFooterView(isDataEmpty ? null : footerView);
        adapter.setNotes(notes);
        initPagination(pageCount);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpDayLimitData<List<Note>>> pageOb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpDayLimitData<List<Note>>>() {
                    @Override
                    public Observable<HljHttpDayLimitData<List<Note>>> onNextPage(int page) {
                        return NoteApi.getMerchantNoteBookNotesObb(page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpDayLimitData<List<Note>>>() {
                    @Override
                    public void onNext(HljHttpDayLimitData<List<Note>> dayLimitData) {
                        adapter.addNotes(dayLimitData.getData());
                    }
                })
                .build();
        pageOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @OnClick({R2.id.btn_back, R2.id.btn_shadow_back})
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R2.id.btn_create, R2.id.btn_shadow_create})
    public void onCreateNote() {
        NoteMerchantListService service = (NoteMerchantListService) ARouter.getInstance()
                .build(RouterPath.ServicePath.GO_NOTE_ADS_WEB_VIEW)
                .navigation();
        if (service == null) {
            return;
        }
        if (service.isShowShopReview(this)) {
            return;
        }
        NoteDialogUtil.showCreateNoteMenuDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (NotePrefUtil.getInstance(MerchantNoteListActivity.this)
                        .isShowNoteEdu()) {
                    intent.setClass(MerchantNoteListActivity.this, NoteEduActivity.class);
                    intent.putExtra("note_type", Note.TYPE_NORMAL);
                } else {
                    intent.setClass(MerchantNoteListActivity.this, CreatePhotoNoteActivity.class);
                    intent.putExtra(CreatePhotoNoteActivity.ARG_NOTEBOOK_TYPE,
                            NotebookType.TYPE_WEDDING_PERSON);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (NotePrefUtil.getInstance(MerchantNoteListActivity.this)
                        .isShowNoteEdu()) {
                    intent.setClass(MerchantNoteListActivity.this, NoteEduActivity.class);
                    intent.putExtra("note_type", Note.TYPE_VIDEO);
                    startActivityForResult(intent, HljNote.RequestCode.NOTE_HELP);
                } else {
                    intent.setClass(MerchantNoteListActivity.this, VideoChooserActivity.class);
                    intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                            HljNote.NOTE_MAX_VIDEO_LENGTH);
                    startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HljNote.RequestCode.NOTE_HELP:
                    if (data != null) {
                        int noteType = data.getIntExtra("note_type", Note.TYPE_NORMAL);
                        if (noteType == Note.TYPE_VIDEO) {
                            Intent intent = new Intent(this, VideoChooserActivity.class);
                            intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                                    HljNote.NOTE_MAX_VIDEO_LENGTH);
                            startActivityForResult(intent, HljNote.RequestCode.CHOOSE_VIDEO);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
                case HljNote.RequestCode.CHOOSE_VIDEO:
                    if (data != null) {
                        Photo photo = data.getParcelableExtra("photo");
                        if (photo != null) {
                            Intent intent = new Intent(this, CreateVideoNoteActivity.class);
                            intent.putExtra("photo", photo);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up,
                                    R.anim.activity_anim_default);
                        }
                    }
                    break;
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(int position, Note note) {
        if (note != null && note.getId() > 0) {
            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra("note_id", note.getId());
            intent.putExtra("item_position", position);
            intent.putExtra("url", note.getUrl());
            startActivityForResult(intent, HljNote.RequestCode.NOTE_DETAIL);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public class HeaderViewHolder extends ExtraBaseViewHolder {
        @BindView(R2.id.header_layout)
        FrameLayout headerLayout;
        @BindView(R2.id.btn_go_merchant_feed_edu)
        TextView btnGoMerchantFeedEdu;
        @BindView(R2.id.tv_limit_count)
        TextView tvLimitCount;
        @BindView(R2.id.empty_view)
        HljEmptyView emptyView;
        private int headerHeight;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Point point = CommonUtil.getDeviceSize(itemView.getContext());
            headerHeight = Math.round(point.x * 7.0f / 16.0f);
            headerLayout.getLayoutParams().width = point.x;
            headerLayout.getLayoutParams().height = headerHeight;
            btnGoMerchantFeedEdu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    NoteMerchantListService service = (NoteMerchantListService) ARouter
                            .getInstance()
                            .build(RouterPath.ServicePath.GO_NOTE_ADS_WEB_VIEW)
                            .navigation();
                    if (service != null) {
                        service.onNoteAdsWebView(MerchantNoteListActivity.this, progressBar);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
            emptyView.setHintId(R.string.label_my_merchant_feed_empty___note);
            emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
            emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
                @Override
                public void onNetworkErrorClickListener() {
                    onRefresh(null);
                }
            });
        }

        private void setShowEmptyView(boolean showEmptyView) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height;
            if (showEmptyView) {
                emptyView.showEmptyView();
                emptyView.setVisibility(View.VISIBLE);
                height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                emptyView.hideEmptyView();
                emptyView.setVisibility(View.GONE);
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            } else {
                itemView.getLayoutParams().width = width;
                itemView.getLayoutParams().height = height;
            }
        }
    }
}
