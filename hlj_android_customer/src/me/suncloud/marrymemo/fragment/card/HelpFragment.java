package me.suncloud.marrymemo.fragment.card;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.HljWebJsInterface;
import com.example.suncloud.hljweblibrary.jsinterface.WebViewJsInterface;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;

/**
 * Created by wangtao on 2017/4/26.
 */

public class HelpFragment extends RefreshFragment {

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progress;
    Unbinder unbinder;

    private WebViewJsInterface hljJsInterface; //js交互相关接口

    public static Fragment newInstance(String path) {
        Fragment fragment = new HelpFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_help, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        //        wenbview缓存
        webView.getSettings()
                .setDomStorageEnabled(true);
        webView.getSettings()
                .setAppCachePath(getContext().getCacheDir()
                        .getAbsolutePath());
        webView.getSettings()
                .setAppCacheEnabled(true);

        if (HljCommon.debug) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(
                    String url,
                    String userAgent,
                    String contentDisposition,
                    String mimetype,
                    long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        webView.setWebViewClient(new HljWebClient(getContext()) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith(
                        "https://"))) {
                    initWebViewJsInterface(url);
                }
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(progress!=null) {
                    if (newProgress < 100) {
                        progress.setVisibility(View.VISIBLE);
                        progress.setProgress(newProgress);
                    } else {
                        progress.setVisibility(View.GONE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
    }

    private void iniData() {
        String path = null;
        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Map<String, String> header = new HashMap<>();
        path = WebUtil.addPathQuery(getContext(), path);
        if (WebUtil.isHljUrl(path)) {
            header = WebUtil.getWebHeaders(getContext());
        }
        initWebViewJsInterface(path);
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }

    }

    private void initWebViewJsInterface(String path) {
        //hlj js接口判断
        if (WebUtil.isHljUrl(path)) {
            if (hljJsInterface == null) {
                hljJsInterface = new HljWebJsInterface(webView,
                        JsInterfaceConstructor.getJsInterface(getContext(), path, webView, null));
            }
        } else if (hljJsInterface != null) {
            hljJsInterface.onRemove(webView);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        unbinder.unbind();
        super.onDestroyView();
    }
}
