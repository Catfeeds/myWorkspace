package me.suncloud.marrymemo.view.experience;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.experienceshop.CommentAdapter;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CommentNewWorkActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/3/21.
 * 体验店 全部印象
 */

public class ExperienceShopImpressionActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {


    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private MarkFlowLayout markFlow;
    private ArrayList<Comment> comments;
    private View footerView;
    private TextView endView;
    private View loadView;
    private View headerView;
    private List<String> marks = new ArrayList<>();
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private long test_store_id = -1;
    private Unbinder unbinder;
    private CommentAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        test_store_id = getIntent().getLongExtra("testStoreId", -1);
        ArrayList<String> markArray = getIntent().getStringArrayListExtra("mark");
        if (markArray != null) {
            marks.addAll(markArray);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_shop_impression);
        unbinder = ButterKnife.bind(this);
        context = this;
        setOkTextSize(14);
        initFooter();
        initHeader();
        initView();
        EventBus.getDefault()
                .register(this);
        comments = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setItemPrefetchEnabled(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(linearLayoutManager);
        recyclerView.setOnRefreshListener(this);
        adapter = new CommentAdapter(context, comments);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        onRefresh(recyclerView);
    }

    private void initView() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(com.hunliji.hljquestionanswer.R.string
                .label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initHeader() {
        headerView = View.inflate(context, R.layout.experience_shop_impression_header, null);
        markFlow = (MarkFlowLayout) headerView.findViewById(R.id.mark_flow);
        setFlowLayout(marks);
    }

    @Override
    public void onOkButtonClick() {
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            Intent intent = new Intent(context, CommentNewWorkActivity.class);
            intent.putExtra("isTestStore", true);
            intent.putExtra("testStoreId", test_store_id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    //设置标签视图
    public void setFlowLayout(List<String> marks) {
        if (marks != null && marks.size() > 0) {
            markFlow.removeAllViews();
            markFlow.setMaxLineCount(3);
            for (int i = 0; i < marks.size(); i++) {
                String item = marks.get(i);
                TextView markText = (TextView) getLayoutInflater().inflate(R.layout
                                .question_hot_mark_list_item___qa,
                        markFlow,
                        false);
                markText.setText(item);
                markText.setTag(item);
                markText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack3));
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.
                        WRAP_CONTENT, CommonUtil.dp2px(getApplicationContext(), 28));
                markFlow.addView(markText, params);
            }
        }
    }

    private void initFooter() {
        footerView = View.inflate(context, R.layout.list_foot_no_more_2, null);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }


    /**
     * 评论成功,进行刷新
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.COMMENT_TEST_STORE_ACTIVITY) {
            onRefresh(recyclerView);
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpData<List<Comment>>> observable = ExperienceApi.getCommentList(1,
                    test_store_id);
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Comment>>>() {

                        @Override
                        public void onNext(HljHttpData<List<Comment>> hljHttpData) {
                            markFlow.setVisibility(View.VISIBLE);
                            setOkText(R.string.write_impression);
                            comments.clear();
                            comments.addAll(hljHttpData.getData());
                            adapter.setCommentList(comments);
                            adapter.notifyDataSetChanged();
                            initPage(hljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setPullToRefreshBase(refreshView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
       CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Comment>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Comment>>>() {
                    @Override
                    public Observable<HljHttpData<List<Comment>>> onNextPage(int page) {
                        return ExperienceApi.getCommentList(page, test_store_id);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Comment>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Comment>> hljHttpData) {
                        List<Comment> commentList = hljHttpData.getData();
                        if (commentList != null) {
                            comments.addAll(commentList);
                            adapter.setCommentList(comments);
                            adapter.notifyDataSetChanged();
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);

    }


    @Override
    protected void onFinish() {
        super.onFinish();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        EventBus.getDefault()
                .unregister(this);
    }
}
