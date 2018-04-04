package com.hunliji.hljpaymentlibrary.views.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/8/5.
 */
public class JDPayActivity extends HljBaseActivity {

    @BindView(R2.id.webview)
    WebView webView;
    @BindView(R2.id.progress)
    ProgressBar progress;

    private String payResult;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd_pay___pay);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        setTitle("");
        String orderInfo = getIntent().getStringExtra("orderInfo");
        payResult = getIntent().getStringExtra("payResult");
        String gateway = getIntent().getStringExtra("gateway");
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.contains("m.hunliji.com")) {
                    RxBus.getDefault()
                            .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_SUCCESS,
                                    new JsonParser().parse(payResult)
                                            .getAsJsonObject()));
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                } else {
                    progress.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        try {
            JSONObject jsonObject = new JSONObject(orderInfo);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head></head>");
            sb.append("<body onload='form1.submit()'>");
            sb.append(String.format("<form id='form1' action='%s' method='%s'>", gateway, "post"));
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!"gateway".equals(key)) {
                    sb.append(String.format("<input name='%s' type='hidden' value='%s' />",
                            key,
                            jsonObject.opt(key)));

                }
            }
            sb.append("</form></body></html>");
            webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "UTF-8", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_CANCEL, null));
    }

    @Override
    protected void onFinish() {
        webView.destroy();
        super.onFinish();
    }
}
