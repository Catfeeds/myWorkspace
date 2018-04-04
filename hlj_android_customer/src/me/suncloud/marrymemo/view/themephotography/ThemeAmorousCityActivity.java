package me.suncloud.marrymemo.view.themephotography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.UnitCityThemeAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;

/**
 * 旅拍单城 acitivy
 * Created by jinxin on 2016/9/23.
 */

public class ThemeAmorousCityActivity extends HljBaseNoBarActivity {

    public static final String CPM_SOURCE = "unit_city_travel";

    @BindView(R.id.list)
    PullToRefreshVerticalRecyclerView list;
    @BindView(R.id.emptyView)
    HljEmptyView emptyView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.title_r)
    TextView titleR;
    @BindView(R.id.action_layout_r)
    RelativeLayout actionLayoutR;
    @BindView(R.id.action_layout_w)
    RelativeLayout actionLayoutW;
    @BindView(R.id.msg_notice_w)
    View msgNoticeW;
    @BindView(R.id.msg_count_w)
    TextView msgCountW;
    @BindView(R.id.msg_notice_r)
    View msgNoticeR;
    @BindView(R.id.msg_count_r)
    TextView msgCountR;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private HljHttpSubscriber subscriber;
    private UnitCityThemeAdapter adapter;
    private int type;
    private long id;
    private LinearLayoutManager manager;
    private View firstView;
    private int firstViewHeight;

    private NoticeUtil noticeUtilW;
    private NoticeUtil noticeUtilR;


    @Override
    public String pageTrackTagName() {
        return "旅拍单城";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra("id", 0);
        return new VTMetaData(id, "ContentBundle");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_amorous_city);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        initConstant();
        initWidget();
        loadData();

        initTracker();
    }


    private void initTracker() {
        HljVTTagger.tagViewParentName(list, "single_city_list");
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    private void initConstant() {
        id = getIntent().getLongExtra("id", 0);
        type = getIntent().getIntExtra("type", Constants.THEME_TYPE.AMOROUS_CITY);
        adapter = new UnitCityThemeAdapter(this);
    }

    private void initWidget() {
        list.getRefreshableView()
                .setAdapter(adapter);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        list.getRefreshableView()
                .setLayoutManager(manager);
        list.setMode(PullToRefreshBase.Mode.DISABLED);
        list.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        if (firstView == null) {
                            firstView = manager.findViewByPosition(0);
                        }
                        if (firstView == null) {
                            return;
                        }
                        int top = firstView.getTop();
                        firstViewHeight = firstView.getHeight();
                        float rAlpha = Math.min(Math.abs(top * 1.0f / firstViewHeight), 1.0f);
                        float wAlpha = Math.max(1.0f - rAlpha, 0);
                        int position = manager.findFirstVisibleItemPosition();
                        if (position > 0) {
                            wAlpha = 0.0f;
                            rAlpha = 1.0f;
                        }
                        actionLayoutW.setAlpha(wAlpha);
                        actionHolderLayout2.setAlpha(rAlpha);
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
    }

    private void loadData() {
        String url = null;
        if (type == Constants.THEME_TYPE.AMOROUS_CITY) {
            url = Constants.getAbsUrl(Constants.HttpPath.GET_JOURNEY_THEME) + "?id=2";
        } else if (type == Constants.THEME_TYPE.UNIT) {
            url = Constants.getAbsUrl(Constants.HttpPath.TRAVELBUNDLE) + "?id=" + id;
        }

        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<JourneyTheme>() {
                    @Override
                    public void onNext(JourneyTheme journeyTheme) {
                        if (journeyTheme != null) {
                            titleR.setText(journeyTheme.getTitle());
                            titleR.setVisibility(View.VISIBLE);
                            adapter.setTheme(journeyTheme);
                            adapter.notifyDataSetChanged();
                        } else {
                            showEmptyView();
                        }
                    }
                })
                .build();
        Observable<JourneyTheme> observable = ThemeApi.getTheme(url);
        observable.subscribe(subscriber);
    }

    public void onMessage(View view) {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private void showEmptyView() {
        list.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        emptyView.showEmptyView();
    }

    @Override
    protected void onFinish() {
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
        super.onFinish();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode
                .NOTIFICATION_PAGE) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (noticeUtilW == null) {
            noticeUtilW = new NoticeUtil(this, msgCountW, msgNoticeW);
        }
        noticeUtilW.onResume();
        if (noticeUtilR == null) {
            noticeUtilR = new NoticeUtil(this, msgCountR, msgNoticeR);
        }
        noticeUtilR.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtilW == null) {
            noticeUtilW = new NoticeUtil(this, msgCountW, msgNoticeW);
        }
        noticeUtilW.onPause();
        if (noticeUtilR == null) {
            noticeUtilR = new NoticeUtil(this, msgCountR, msgNoticeR);
        }
        noticeUtilR.onPause();
        super.onPause();
    }
}
