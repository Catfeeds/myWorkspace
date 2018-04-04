package com.hunliji.hljnotelibrary.views.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.suncloud.hljweblibrary.client.HljChromeClient;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.example.suncloud.hljweblibrary.constructors.JsInterfaceConstructor;
import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.example.suncloud.hljweblibrary.jsinterface.HljWebJsInterface;
import com.example.suncloud.hljweblibrary.jsinterface.MadWebHandler;
import com.example.suncloud.hljweblibrary.jsinterface.MadWebJsInterface;
import com.example.suncloud.hljweblibrary.jsinterface.WebViewJsInterface;
import com.example.suncloud.hljweblibrary.utils.WebUtil;
import com.example.suncloud.hljweblibrary.views.widgets.ScrollWebView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.models.wrappers.RxWebNoteComment;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by mo_yu on 2017/7/19.运营笔记详情
 */

public class NoteWebDetailFragment extends RefreshFragment {

    @BindView(R2.id.web_view)
    ScrollWebView webView;
    @BindView(R2.id.progress)
    ProgressBar progress;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.layout)
    RelativeLayout layout;
    Unbinder unbinder;
    private ValueCallback<Uri[]> mUploadMessages;
    private ValueCallback<Uri> mUploadMessage;
    private String path;
    private static final int FILE_CHOOSER_RESULT = 1;
    protected SimpleArrayMap<String, WebViewJsInterface> jsInterfaces; //js交互相关接口
    private float alpha;
    private float disHeight;
    private Subscription rxBusEventSub;
    private HljHttpSubscriber deleteSubscriber;

    public static NoteWebDetailFragment newInstance(String path) {
        Bundle args = new Bundle();
        NoteWebDetailFragment fragment = new NoteWebDetailFragment();
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disHeight = (float) CommonUtil.getDeviceSize(getContext()).x;
        registerRxBusEvent();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_web_view___note, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
        initWebView();
        return rootView;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
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
        Map<String, String> header = new HashMap<>();
        path = WebUtil.addPathQuery(getContext(), path);
        if (WebUtil.isHljUrl(path)) {
            header = WebUtil.getWebHeaders(getContext());
        }

        initWebViewJsInterface(path);

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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new HljChromeClient() {


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progress == null) {
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

            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUploadMessages != null)
                    return true;
                mUploadMessages = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && fileChooserParams
                        .getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length >
                        0) {
                    intent.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    intent.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        FILE_CHOOSER_RESULT);
                return true;
            }


            @Override
            public void openFileChooser(
                    ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (mUploadMessage != null)
                    return;
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (TextUtils.isEmpty(acceptType)) {
                    intent.setType("*/*");
                } else {
                    intent.setType(acceptType);
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        FILE_CHOOSER_RESULT);
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, "");
            }
        });
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
        webView.setOnScrollChangedCallback(new ScrollWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (!isVisibleToUser()) {
                    return;
                }
                NoteDetailActivity noteDetailActivity = (NoteDetailActivity) getActivity();
                if (noteDetailActivity == null) {
                    return;
                }
                if (t > disHeight) {
                    alpha = 1;
                } else {
                    alpha = (float) t / disHeight;
                }
                noteDetailActivity.setToolbarAlpha(alpha);
            }
        });
    }

    public void initWebViewJsInterface(String path) {
        if (jsInterfaces == null) {
            jsInterfaces = new ArrayMap<>();
        }
        //hlj js接口判断
        WebViewJsInterface hljJsInterface = jsInterfaces.get(HljWebJsInterface.NAME);
        if (WebUtil.isHljUrl(path)) {
            if (hljJsInterface == null) {
                jsInterfaces.put(HljWebJsInterface.NAME,
                        new HljWebJsInterface(webView,
                                JsInterfaceConstructor.getJsInterface(getContext(),
                                        path,
                                        webView,
                                        handler)));
            }
        } else if (hljJsInterface != null) {
            hljJsInterface.onRemove(webView);
            jsInterfaces.remove(HljWebJsInterface.NAME);
        }


        //Mad js接口判断
        WebViewJsInterface madJsInterface = jsInterfaces.get(MadWebJsInterface.NAME);
        if (WebUtil.isMadUrl(path)) {
            if (madJsInterface == null) {
                jsInterfaces.put(MadWebJsInterface.NAME,
                        new MadWebJsInterface(webView, new MadWebHandler(getContext(), handler)));
            }
        } else if (madJsInterface != null) {
            madJsInterface.onRemove(webView);
            jsInterfaces.remove(MadWebJsInterface.NAME);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BaseWebHandler.ON_WEB_SHARE:
                    onShareInfo();
                    break;
                case BaseWebHandler.WEB_SHARE_CHECK:
                    break;
                case BaseWebHandler.WEB_UPDATE_DONE:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void onShareInfo() {
        ShareDialogUtil.onCommonShare(getContext(), getShareUtil());
    }

    public ShareUtil getShareUtil() {
        if (jsInterfaces == null || jsInterfaces.isEmpty()) {
            return null;
        }
        for (int i = 0, size = jsInterfaces.size(); i < size; i++) {
            WebViewJsInterface jsInterface = jsInterfaces.valueAt(i);
            if (jsInterface.getShareUtil() != null) {
                return jsInterface.getShareUtil();
            }
        }
        return null;
    }

    public float getAlpha() {
        return alpha > 1 ? 1 : alpha;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(rxBusEventSub, deleteSubscriber);
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof String) {
            path = (String) params[0];
            initWebView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        unbinder.unbind();
    }

    private void deleteNoteComment(final long replyId, final String function) {
        if (AuthUtil.loginBindCheck(getContext())) {
            if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
                deleteSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                ToastUtil.showCustomToast(getContext(),
                                        com.hunliji.hljnotelibrary.R.string
                                                .msg_delete_success___cm);
                                if (!TextUtils.isEmpty(function)) {
                                    webView.loadUrl("javascript:" + function + "" + "" + "" + ""
                                            + "(\'success\');");
                                }
                            }
                        })
                        .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                                .createProgressDialog(
                                getContext()))
                        .build();
                CommonApi.deleteFuncObb(replyId)
                        .subscribe(deleteSubscriber);
            }
        }
    }

    public void scrollToTop() {
        webView.scrollTo(0, 0);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case DELETE_NOTE_COMMENT:
                                    RxWebNoteComment rxWebNoteComment = (RxWebNoteComment)
                                            rxEvent.getObject();
                                    if (rxWebNoteComment != null) {
                                        deleteNoteComment(rxWebNoteComment.getReplyId(),
                                                rxWebNoteComment.getCallback());
                                    }
                                    break;
                                case REPLY_NOTE_COMMENT:
                                    String callback = (String) rxEvent.getObject();
                                    if (!TextUtils.isEmpty(callback)) {
                                        webView.loadUrl("javascript:" + callback + "(\'success\')" +
                                                "" + "" + "" + "" + "" + "" + "" + "" + ";");
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    public boolean isVisibleToUser() {
        BaseNoteDetailFragment fragment = (BaseNoteDetailFragment) getParentFragment();
        return fragment == null || fragment.isVisibility();
    }
}
