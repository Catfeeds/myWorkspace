package me.suncloud.marrymemo.view.themephotography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.GuideRecyclerAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.themephotography.Guide;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.community.CreatePostActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/9/20. 全部攻略页(话题和专题)
 */

public class ThemeGuideListActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView> {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private long id;//单元id
    private String url;

    private View endView;
    private View loadView;

    private LinearLayoutManager layoutManager;
    private GuideRecyclerAdapter adapter;
    private ArrayList<Guide> guideList;

    private HljHttpSubscriber refreshSubscriber;//初始(刷新)加载
    private HljHttpSubscriber pageSubscriber;//分页

    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        id = getIntent().getLongExtra("id", 0);
        url = Constants.HttpPath.GUIDELIST;
        guideList = new ArrayList<>();

    }

    private void initView() {
        tvTitle.setText(R.string.label_strategy);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter = new GuideRecyclerAdapter(ThemeGuideListActivity.this, guideList);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        adapter.setFooterView(footerView);
        adapter.setOnReplyItemClickListener(new GuideRecyclerAdapter.OnReplyItemClickListener() {
            @Override
            public void onReply(CommunityThread item, int position) {
                if (!AuthUtil.loginBindCheck(ThemeGuideListActivity.this)) {
                    return;
                }
                Intent intent = new Intent(ThemeGuideListActivity.this, CreatePostActivity.class);
                intent.putExtra(CreatePostActivity.ARG_POST, item.getPost());
                intent.putExtra(CreatePostActivity.ARG_POSITION, position);
                intent.putExtra(CreatePostActivity.ARG_IS_REPLY_THREAD, true);
                startActivityForResult(intent, 1);
                overridePendingTransition(com.hunliji.hljcommonviewlibrary.R.anim.slide_in_right,
                        com.hunliji.hljcommonviewlibrary.R.anim.activity_anim_default);
            }
        });
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }


    private void initPagination(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<Guide>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Guide>>>() {
                    @Override
                    public Observable<HljHttpData<List<Guide>>> onNextPage(int page) {
                        return ThemeApi.getGuideList(url, id, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Guide>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<Guide>> listHljHttpData) {
                        guideList.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(refreshView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Guide>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Guide>> data) {
                            initPagination(data.getPageCount());
                            guideList.clear();
                            guideList.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();

            ThemeApi.getGuideList(url, id, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    @OnClick({R.id.back_btn, R.id.msg_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.msg_layout:
                if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
                    Intent intent = new Intent(this, MessageHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra(CreatePostActivity.ARG_POSITION, 0);
            CommunityThread thread = (CommunityThread) guideList.get(position)
                    .getEntity();
            if (thread != null) {
                thread.setPostCount(thread.getPostCount() + 1);
            }
            adapter.notifyDataSetChanged();
        }
    }

}
