package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import me.suncloud.marrymemo.R;

public class UMPayActivity extends Activity {

    private WebView webView;
    private String url;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umpay);
        url = getIntent().getStringExtra("url");

        progressBar = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] strs = url.split("/");
                if (strs != null && strs.length > 0) {
                    for (int i = 0; i < strs.length; i++) {
                        if (strs[i].startsWith("UmpaySuccess?")) {
                            // 支付成功，返回

                            Intent intent = getIntent();
                            intent.putExtra("success", true);
                            setResult(RESULT_OK, intent);
                            UMPayActivity.super.onBackPressed();
                        }
                    }
                }

                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){

        });

        webView.loadUrl(url);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("success", false);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

}
