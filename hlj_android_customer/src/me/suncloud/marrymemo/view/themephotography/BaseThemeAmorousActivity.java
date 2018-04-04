package me.suncloud.marrymemo.view.themephotography;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.themephotography.ThemeAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;

/**
 * Created by jinxin on 2016/10/8.
 */

public abstract class BaseThemeAmorousActivity extends HljBaseNoBarActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.list)
    PullToRefreshVerticalRecyclerView list;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_all_info)
    TextView tvAllInfo;
    @BindView(R.id.img_arrow_right)
    ImageView imgArrowRight;
    @BindView(R.id.theme_type)
    LinearLayout themeType;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_type_english)
    TextView tvTypeEnglish;
    @BindView(R.id.line)
    View typeLine;
    private NoticeUtil noticeUtil;
    private HljHttpSubscriber subscriber;
    protected ThemeAdapter adapter;
    protected int type;
    protected long id;
    private LinearLayoutManager manager;
    private int viewType;
    protected JourneyTheme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_hot_city);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initConstant();
        initWidget();
        loadData();
    }


    private void initConstant() {
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new ThemeAdapter(this, type);

    }

    private void initWidget() {
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        list.getRefreshableView()
                .setLayoutManager(manager);
        list.getRefreshableView()
                .setAdapter(adapter);
        list.setMode(PullToRefreshBase.Mode.DISABLED);
        list.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        int position = manager.findFirstVisibleItemPosition();
                        viewType = adapter.getItemViewType(position);
                        String strType = null;
                        String strTypeEnglish = null;
                        String allType = null;
                        switch (viewType) {
                            case Constants.JOURNEY_TYPE.DESTINATION:
                                strType = getString(R.string.label_destination);
                                strTypeEnglish = getString(R.string.label_destination_hint);
                                tvAllInfo.setVisibility(View.VISIBLE);
                                imgArrowRight.setVisibility(View.VISIBLE);
                                themeType.setVisibility(View.VISIBLE);
                                break;
                            case Constants.JOURNEY_TYPE.GUIDE:
                                strType = getString(R.string.label_strategy);
                                strTypeEnglish = getString(R.string.label_strategy_hint);
                                allType = getString(R.string.label_all_strategy);
                                tvAllInfo.setVisibility(View.VISIBLE);
                                imgArrowRight.setVisibility(View.VISIBLE);
                                themeType.setVisibility(View.VISIBLE);
                                break;
                            case Constants.JOURNEY_TYPE.WORK:
                                strType = getString(R.string.label_hot_packages);
                                strTypeEnglish = getString(R.string.label_hot_packages_hint);
                                allType = getString(R.string.label_all_work);
                                tvAllInfo.setVisibility(View.VISIBLE);
                                imgArrowRight.setVisibility(View.VISIBLE);
                                themeType.setVisibility(View.VISIBLE);
                                break;
                            case Constants.JOURNEY_TYPE.MERCHANT:
                                strType = getString(R.string.label_big_business);
                                strTypeEnglish = getString(R.string.label_big_business_hint);
                                allType = getString(R.string.label_all_merchant);
                                tvAllInfo.setVisibility(View.VISIBLE);
                                imgArrowRight.setVisibility(View.VISIBLE);
                                themeType.setVisibility(View.VISIBLE);
                                break;
                            default:
                                themeType.setVisibility(View.INVISIBLE);
                                break;
                        }
                        tvType.setText(JSONUtil.isEmpty(strType) ? "" : strType);
                        tvTypeEnglish.setText(JSONUtil.isEmpty(strTypeEnglish) ? "" :
                                strTypeEnglish);
                        tvAllInfo.setText(JSONUtil.isEmpty(allType) ? "" : allType);
                        typeLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                        super.onScrolled(recyclerView, dx, dy);
                    }

                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
    }

    private void loadData() {
        String url;
        if (type == Constants.THEME_TYPE.AMOROUS) {
            url = Constants.getAbsUrl(Constants.HttpPath.GET_JOURNEY_THEME) + "?id=2";
            title.setText(getString(R.string.label_amorous));
        } else {
            url = Constants.getAbsUrl(Constants.HttpPath.TRAVELBUNDLE) + "?id=" + id;
        }
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<JourneyTheme>() {
                    @Override
                    public void onNext(JourneyTheme journeyTheme) {
                        if (journeyTheme != null) {
                            theme = journeyTheme;
                            if (type != Constants.THEME_TYPE.AMOROUS) {
                                title.setText(journeyTheme.getTitle());
                                title.setVisibility(View.VISIBLE);
                            }
                            adapter.setTheme(journeyTheme);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();
        Observable<JourneyTheme> observable = ThemeApi.getTheme(url);
        observable.subscribe(subscriber);
    }

    @OnClick({R.id.tv_all_info, R.id.img_arrow_right, R.id.tv_type, R.id.tv_type_english})
    public void onMoreTypeClick() {
        Intent intent = null;
        if (theme == null) {
            return;
        }
        switch (viewType) {
            case Constants.JOURNEY_TYPE.DESTINATION:
                break;
            case Constants.JOURNEY_TYPE.GUIDE:
                intent = new Intent(this, ThemeGuideListActivity.class);
                intent.putExtra("id", theme.getId());
                break;
            case Constants.JOURNEY_TYPE.WORK:
                intent = new Intent(this, ThemeWorkListActivity.class);
                intent.putExtra("id", theme.getId());
                break;
            case Constants.JOURNEY_TYPE.MERCHANT:
                intent = new Intent(this, ThemeMerchantListActivity.class);
                intent.putExtra("id", theme.getId());
                break;
            default:
                break;

        }
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }


    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_msg)
    public void onOkButtonClick() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onFinish() {
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
        super.onFinish();
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
}
