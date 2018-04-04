package me.suncloud.marrymemo.view.themephotography;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.GuideWorkAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 全部套餐列表
 * Created by jinxin on 2016/9/23.
 */

public class ThemeWorkListActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private long id;//单元id
    private String url;

    private LinearLayoutManager layoutManager;
    private ArrayList<Work> works;
    private GuideWorkAdapter adapter;

    private HljHttpSubscriber refreshSubscriber;//初始（刷新）加载
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide_list);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();
        initValue();
        initWidget();
        initLoad();
    }

    private void initValue() {
        id = getIntent().getLongExtra("id", 0);
        url = Constants.HttpPath.GET_ALL_THEME_WORK;
        works = new ArrayList<>();
    }


    private void initWidget() {
        tvTitle.setText(R.string.label_hot_packages);
        adapter = new GuideWorkAdapter(this, works);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setBackgroundResource(R.color.colorWhite);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }


    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(refreshView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {

                        @Override
                        public void onNext(
                                HljHttpData<List<Work>> data) {
                            works.clear();
                            works.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            ThemeApi.getPackageList(url, id, 1)
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
        if (refreshSubscriber != null && refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
    }
}
