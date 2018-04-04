package me.suncloud.marrymemo.view.finder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.jsinterface.HljWebJsInterface;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HLJCustomerApplication;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.jsinterface.WebHandler;
import me.suncloud.marrymemo.util.finder.FinderTogglesUtil;

/**
 * 专题详情
 * Created by chen_bin on 2016/7/28 0028.
 */
@Route(path = RouterPath.IntentPath.Customer.SUB_PAGE_DETAIL_ACTIVITY)
public class SubPageDetailActivity extends HljBaseActivity {

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.img_praise)
    ImageView imgPraise;
    @BindView(R.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R.id.check_praised)
    CheckableLinearLayout checkPraised;
    @BindView(R.id.tv_praise_add)
    TextView tvPraiseAdd;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.check_collected)
    CheckableLinearLayout checkCollected;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.btn_praised_hint)
    ImageButton btnPraisedHint;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private WebHandler webHandler;
    private TopicUrl topic;
    private long id; //专题id (sub_page表的主键)
    private long productSubPageId; // 婚品专题id,(sub_page_shop_product表的主键)
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber praiseSub;
    private HljHttpSubscriber collectSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_page_detail);
        ButterKnife.bind(this);
        setTitle("");
        String site = getIntent().getStringExtra("site");
        id = getIntent().getLongExtra("id", 0);
        productSubPageId = getIntent().getLongExtra("productSubPageId", 0);
        JSONObject siteJson = null;
        if (!TextUtils.isEmpty(site)) {
            try {
                siteJson = new JSONObject(site);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (siteJson == null && getApplication() != null && getApplication() instanceof
                HLJCustomerApplication) {
            siteJson = TrackerUtil.getSiteJson(null, 0, TrackerHelper.getActivityHistory(this));
        }
        new HljTracker.Builder(this).eventableId(productSubPageId > 0 ? productSubPageId : id)
                .eventableType(productSubPageId > 0 ? "ShopProductSubPage" : "SubPage")
                .action("hit")
                .site(siteJson)
                .build()
                .add();
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getSubPageDetail();
            }
        });
        getSubPageDetail();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void getSubPageDetail() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<TopicUrl>() {
                        @Override
                        public void onNext(TopicUrl object) {
                            topic = object;
                            //非百科类的专题或者是婚品专题,底部bar隐藏
                            if (topic.getType() != 1 || productSubPageId > 0) {
                                setTitle(topic.getTitle());
                                bottomLayout.setVisibility(View.GONE);
                            } else {
                                SharedPreferences preferences = getSharedPreferences(Constants
                                                .PREF_FILE,
                                        Context.MODE_PRIVATE);
                                if (!preferences.getBoolean(Constants
                                                .PREF_SUBJECT_PRAISE_HINT_CLICKED,
                                        false)) {
                                    preferences.edit()
                                            .putBoolean(Constants.PREF_SUBJECT_PRAISE_HINT_CLICKED,
                                                    true)
                                            .apply();
                                    btnPraisedHint.setVisibility(View.VISIBLE);
                                    btnPraisedHint.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            onPraisedHintClicked();
                                        }
                                    }, 4000);
                                }
                                setTitle(getString(R.string.title_activity_sub_page_detail));
                                bottomLayout.setVisibility(View.VISIBLE);
                                checkPraised.setChecked(topic.isPraised());
                                tvPraiseCount.setText(topic.getPraiseCount() > 0 ? getString(R
                                        .string.label_praise) + "·" + topic.getPraiseCount() :
                                        getString(
                                        R.string.label_praise));
                                if (topic.isCollected()) {
                                    checkCollected.setChecked(true);
                                    tvCollect.setText(R.string.label_collected___cm);
                                } else {
                                    checkCollected.setChecked(false);
                                    tvCollect.setText(R.string.label_collect_answer___cm);
                                }
                                tvCommentCount.setText(topic.getCommentCount() > 0 ? getString(R
                                        .string.label_comment___cm) + "·" + topic.getCommentCount
                                        () : getString(
                                        R.string.label_comment___cm));
                            }
                            if (topic.getShareInfo() != null) {
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
                            webHandler = new WebHandler(SubPageDetailActivity.this,
                                    null,
                                    webView,
                                    handler);
                            webHandler.setShareInfo(topic.getShareInfo());
                            webView.addJavascriptInterface(webHandler, HljWebJsInterface.NAME);
                            webView.setWebViewClient(new HljWebClient(SubPageDetailActivity.this) {
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
                            if (!TextUtils.isEmpty(topic.getHtml())) {
                                webView.loadDataWithBaseURL(null,
                                        topic.getHtml(),
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
            if (productSubPageId != 0) {
                ProductApi.getProductSubPageDetailObb(productSubPageId)
                        .subscribe(initSub);
            } else {
                FinderApi.getSubPageDetailObb(id)
                        .subscribe(initSub);
            }
        }
    }

    //去评价
    @OnClick(R.id.comment_layout)
    public void onComment(View view) {
        if (topic != null && topic.getId() > 0) {
            Intent intent = new Intent(this, SubPageCommentListActivity.class);
            intent.putExtra("id", topic.getId());
            startActivityForResult(intent, Constants.RequestCode.SUB_PAGE_COMMENT_LIST);
        }
    }

    //收藏
    @OnClick(R.id.check_collected)
    public void onCollect() {
        if (topic != null && topic.getId() > 0) {
            collectSub = FinderTogglesUtil.getInstance()
                    .onSubPageCollect(this, topic, checkCollected, tvCollect, collectSub);
        }
    }

    //点赞
    @OnClick(R.id.check_praised)
    public void onPraise() {
        handler.sendEmptyMessage(Constants.RequestCode.POST_PRAISE);
    }

    //点击取消提示图片
    @OnClick(R.id.btn_praised_hint)
    public void onPraisedHintClicked() {
        if (btnPraisedHint != null) {
            btnPraisedHint.setVisibility(View.GONE);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.RequestCode.POST_PRAISE:
                    if (topic != null && topic.getId() > 0) {
                        praiseSub = FinderTogglesUtil.getInstance()
                                .onSubPagePraise(SubPageDetailActivity.this,
                                        topic,
                                        webView,
                                        checkPraised,
                                        imgPraise,
                                        tvPraiseCount,
                                        tvPraiseAdd,
                                        praiseSub);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.PRAISE_LOGIN:
                    getSubPageDetail();
                    break;
                case Constants.RequestCode.SUB_PAGE_COMMENT_LIST:
                    if (data != null) {
                        int totalCount = data.getIntExtra("total_count", -1);
                        if (totalCount > -1) {
                            topic.setCommentCount(totalCount);
                            tvCommentCount.setText(topic.getCommentCount() > 0 ? getString(R
                                    .string.label_comment___cm) + "·" + topic.getCommentCount() :
                                    getString(
                                    R.string.label_comment___cm));
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onOkButtonClick() {
        if (topic != null && topic.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(this, topic.getShareInfo());
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        CommonUtil.unSubscribeSubs(initSub, praiseSub, collectSub);
    }
}