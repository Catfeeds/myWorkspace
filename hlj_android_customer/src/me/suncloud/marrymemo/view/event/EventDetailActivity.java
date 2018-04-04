package me.suncloud.marrymemo.view.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.jsinterface.HljWebJsInterface;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HLJCustomerApplication;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.event.EventApi;
import me.suncloud.marrymemo.jsinterface.WebHandler;

import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;

import me.suncloud.marrymemo.view.MyOrderListActivity;

/**
 * 活动详情页
 * Created by chen_bin on 2016/8/10 0010.
 */
@Route(path = RouterPath.IntentPath.Customer.EVENT_DETAIL_ACTIVITY)
public class EventDetailActivity extends HljBaseActivity {
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private EventInfo eventInfo;
    private WebHandler webHandler;
    private long id;
    private HljHttpSubscriber initSub;
    private long startTimeMillis; //页面时间统计

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id", 0);
        JSONObject siteJson = null;
        if (getApplication() != null && getApplication() instanceof HLJCustomerApplication) {
            siteJson = TrackerUtil.getSiteJson(null, 0, TrackerHelper.getActivityHistory(this));
        }
        TrackerHelper.activityInfo(this, id, siteJson);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        initLoad();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<EventInfo>() {
                        @Override
                        public void onNext(EventInfo eventInfo) {
                            EventDetailActivity.this.eventInfo = eventInfo;
                            setEventInfoData();
                            setTitle(eventInfo.getTitle());
                            if (eventInfo.getShareInfo() != null) {
                                setOkButton(R.mipmap.icon_share_primary_44_44);
                            }
                            webView.getSettings()
                                    .setJavaScriptEnabled(true);
                            webView.getSettings()
                                    .setAllowFileAccess(true);
                            webView.getSettings()
                                    .setDomStorageEnabled(true);
                            webView.getSettings()
                                    .setAppCachePath(getCacheDir().getAbsolutePath());
                            webView.getSettings()
                                    .setAppCacheEnabled(true);
                            if (HljCommon.debug) {
                                webView.getSettings()
                                        .setCacheMode(WebSettings.LOAD_NO_CACHE);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                webView.getSettings()
                                        .setMixedContentMode(WebSettings
                                                .MIXED_CONTENT_ALWAYS_ALLOW);
                            }
                            webHandler = new WebHandler(EventDetailActivity.this,
                                    null,
                                    webView,
                                    null);
                            webHandler.setShareInfo(eventInfo.getShareInfo());
                            webView.addJavascriptInterface(webHandler, HljWebJsInterface.NAME);
                            webView.setWebViewClient(new HljWebClient(EventDetailActivity.this) {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    if (!TextUtils.isEmpty(url) && url.startsWith("tel:")) {
                                        callUp(Uri.parse(url));
                                        return true;
                                    }
                                    return super.shouldOverrideUrlLoading(view, url);
                                }
                            });
                            webView.setWebChromeClient(new WebChromeClient() {
                                @Override
                                public void onProgressChanged(WebView view, int newProgress) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    if (newProgress < 100) {
                                        progress.setVisibility(View.VISIBLE);
                                        progress.setProgress(newProgress);
                                    } else {
                                        progress.setVisibility(View.GONE);
                                    }
                                    super.onProgressChanged(view, newProgress);
                                }
                            });
                            if (!TextUtils.isEmpty(eventInfo.getContentHtml())) {
                                webView.loadDataWithBaseURL(null,
                                        eventInfo.getContentHtml(),
                                        "text/html",
                                        "UTF-8",
                                        null);
                            }
                        }
                    })
                    .setContentView(webView)
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .build();
            EventApi.getEventDetailObb(id)
                    .subscribe(initSub);
        }
    }

    private void setEventInfoData() {
        if (eventInfo == null || eventInfo.getId() == 0) {
            return;
        }
        if (!eventInfo.isNeedSignUp() || eventInfo.getSignUpEndTime() == null) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            tvWatchCount.setText(String.valueOf(eventInfo.getWatchCount()));
            if (eventInfo.getSignUpInfo()
                    .getStatus() >= 1) {
                tvWatchCount.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
                btnSubmit.setEnabled(true);
                btnSubmit.setText(eventInfo.getSignUpFee() != 0 ? R.string
                        .label_sign_up_pay_success : R.string.label_sign_up_success);
                return;
            }
            if (eventInfo.isSignUpEnd()) {
                tvWatchCount.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
                btnSubmit.setEnabled(false);
                btnSubmit.setText(R.string.label_sign_up_end);
                return;
            }
            if (eventInfo.getSignUpLimit() > 0 && eventInfo.getSignUpCount() >= eventInfo
                    .getSignUpLimit()) {
                tvWatchCount.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
                btnSubmit.setEnabled(false);
                btnSubmit.setText(R.string.label_sign_up_limit_full);
                return;
            }
            tvWatchCount.setTextColor(ContextCompat.getColor(this,
                    eventInfo.getWatchCount() > 0 ? R.color.colorPrimary : R.color.colorGray));
            btnSubmit.setEnabled(true);
            btnSubmit.setText(eventInfo.getSignUpFee() != 0 ? R.string.label_sign_up_pay : R
                    .string.label_sign_up_immediately);
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (eventInfo == null || eventInfo.getId() == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (eventInfo.getSignUpInfo()
                .getStatus() >= 1) {
            Intent intent = new Intent(this, AfterSignUpActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("eventInfo", eventInfo);
            startActivityForResult(intent, Constants.RequestCode.SIGN_UP);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.SIGN_UP:
                    if (data == null) {
                        return;
                    }
                    eventInfo = data.getParcelableExtra("eventInfo");
                    setEventInfoData();
                    Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                            Constants.PREF_NOTICE_OPEN_DLG_EVENT,
                            "报名成功",
                            "您可以在“我的-活动”中查看报名记录，立即开启消息通知，以免错过活动通知哦~",
                            R.drawable.icon_dlg_appointment);
                    if (dialog != null) {
                        dialog.show();
                    } else {
                        DialogUtil.createDoubleButtonDialog(this,
                                getString(R.string.label_sign_up_success),
                                getString(R.string.label_sign_up_success_msg),
                                getString(R.string.label_see),
                                getString(R.string.label_cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(EventDetailActivity.this,
                                                MyOrderListActivity.class);
                                        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                        .ARG_BACK_MAIN,
                                                true);
                                        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                        .ARG_SELECT_TAB,
                                                RouterPath.IntentPath.Customer.MyOrder.Tab.EVENT);
                                        startActivity(intent);
                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(EventDetailActivity.this,
                                                AfterSignUpActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right,
                                                R.anim.activity_anim_default);
                                    }
                                })
                                .show();
                    }

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onOkButtonClick() {
        if (eventInfo != null && eventInfo.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(this, eventInfo.getShareInfo());
        }
    }

    @Override
    protected void onResume() {
        startTimeMillis = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (startTimeMillis > 0) {
            new HljTracker.Builder(this).eventableId(id)
                    .eventableType("Activity")
                    .action("page_out")
                    .additional(String.valueOf((float) (System.currentTimeMillis() -
                            startTimeMillis) / 1000))
                    .build()
                    .add();
            startTimeMillis = 0;
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        CommonUtil.unSubscribeSubs(initSub);
    }
}