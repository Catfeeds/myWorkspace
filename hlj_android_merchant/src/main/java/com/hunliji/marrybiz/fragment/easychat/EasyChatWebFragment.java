package com.hunliji.marrybiz.fragment.easychat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.chat.ChatApi;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.easychat.EasyChatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by wangtao on 2017/8/14.
 */

public class EasyChatWebFragment extends RefreshFragment {

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    private Subscriber<PayRxEvent> paySubscriber;
    private HljHttpSubscriber subscriber;

    public static EasyChatWebFragment newInstance() {
        Bundle args = new Bundle();
        EasyChatWebFragment fragment = new EasyChatWebFragment();
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
        initWebView();
        loadUrl();
        return rootView;
    }


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        //        wenbview缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getContext().getCacheDir()
                .getAbsolutePath());
        webSettings.setAppCacheEnabled(true);
        if (Constants.DEBUG) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        EasyChatHandler webHandler = new EasyChatHandler();
        webView.addJavascriptInterface(webHandler, "handler");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!JSONUtil.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
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
    }


    private void loadUrl() {
        String path = WebUtil.addPathQuery(getContext(),
                Constants.getAbsWebUrl(Constants.HttpPath.EASY_CHAT_ACTIVE));
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
        CommonUtil.unSubscribeSubs(paySubscriber, subscriber);
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        unbinder.unbind();
        super.onDestroyView();
    }


    private class EasyChatHandler {

        @JavascriptInterface
        public void easyChatSetting(final String money) {
            if (!TextUtils.isEmpty(money) && Double.valueOf(money) > 0) {
                if (paySubscriber == null) {
                    paySubscriber = initSubscriber();
                }
                DataConfig dataConfig = Session.getInstance()
                        .getDataConfig(getContext());
                ArrayList<String> payTypes = new ArrayList<>();
                if (dataConfig != null && dataConfig.getPayTypes() != null) {
                    payTypes.addAll(dataConfig.getPayTypes());
                }
                new PayConfig.Builder(getActivity()).params(new JSONObject())
                        // .path(Constants.HttpPath.BOND_PAY_URL)
                        .price(Double.valueOf(money))
                        .subscriber(paySubscriber)
                        .llpayMode(true)
                        .payAgents(payTypes, DataConfig.getPayAgents())
                        .build()
                        .pay();
            } else {
                CommonUtil.unSubscribeSubs(subscriber);
                Observable<HljHttpResult> observable = ChatApi.postActive()
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread());
                subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                            @Override
                            public void onNext(HljHttpResult hljHttpResult) {
                                goEasyChatSetting();
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .build();
                observable.subscribe(subscriber);
            }
        }

        //支付成功
        private Subscriber<PayRxEvent> initSubscriber() {
            return new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            goEasyChatSetting();
                            break;
                    }
                }
            };
        }
    }


    private void goEasyChatSetting() {
        Intent intent = new Intent(getContext(), EasyChatActivity.class);
        intent.putExtra("isActive", true);
        startActivity(intent);
        getActivity().finish();
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.OPEN_EASY_CHAT_SUCCEED, null));
    }
}
