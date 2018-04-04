package com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 小犀分期-未激活教育页
 * Created by chen_bin on 2017/9/13 0013.
 */
public class XiaoxiInstallmentUnactivatedFragment extends RefreshFragment {
    @BindView(R2.id.web_view)
    WebView webView;
    @BindView(R2.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R2.id.progress)
    ProgressBar progress;
    Unbinder unbinder;

    public static XiaoxiInstallmentUnactivatedFragment newInstance() {
        Bundle args = new Bundle();
        XiaoxiInstallmentUnactivatedFragment fragment = new XiaoxiInstallmentUnactivatedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_web_view___web, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        loadUrl();
        return rootView;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initViews() {
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
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
        webView.setWebViewClient(new HljWebClient(getContext()) {
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
                if (getActivity() == null || getActivity().isFinishing()) {
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
        View bottomView = View.inflate(getContext(),
                R.layout.xiaoxi_installment_edu_bottom_layout___pay,
                bottomLayout);
        bottomView.findViewById(R.id.btn_activate)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XiaoxiInstallmentAuthorization.getInstance()
                                .onAuthorization(getActivity(), false);
                    }
                });
    }

    private void loadUrl() {
        String path = WebUtil.addPathQuery(getContext(),
                XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_EDU_URL);
        Map<String, String> header = WebUtil.getWebHeaders(getContext());
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}