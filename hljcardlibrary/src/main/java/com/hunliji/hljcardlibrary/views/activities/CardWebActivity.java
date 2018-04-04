package com.hunliji.hljcardlibrary.views.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.adapter.CardDraggableAdapter;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Audio;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.CardRxEvent;
import com.hunliji.hljcardlibrary.models.ImageHole;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.TextHole;
import com.hunliji.hljcardlibrary.models.TextInfo;
import com.hunliji.hljcardlibrary.models.wrappers.PageEditResult;
import com.hunliji.hljcardlibrary.utils.CardEditObbUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.internal.util.SubscriptionList;

/**
 * Created by wangtao on 2017/6/20.
 */

public class CardWebActivity extends HljBaseNoBarActivity implements CardDraggableAdapter
        .OnPageActionListener {

    private final int CARD_SETTING_EDIT_RESULT = 1;
    private final int PAGE_TEXT_EDIT_RESULT = 2;
    private final int PAGE_IMAGE_EDIT_RESULT = 3;
    private final int PAGE_CREATE_RESULT = 4;

    @BindView(R2.id.web_view)
    WebView webView;
    @BindView(R2.id.progress)
    ProgressBar progress;
    @BindView(R2.id.add_layout)
    LinearLayout addLayout;
    @BindView(R2.id.sort_layout)
    LinearLayout sortLayout;
    @BindView(R2.id.setting_layout)
    LinearLayout settingLayout;
    @BindView(R2.id.send_layout)
    LinearLayout sendLayout;
    @BindView(R2.id.preview_layout)
    LinearLayout previewLayout;
    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;

    private Dialog dialog;
    private long id;
    private Card card;
    private String cardEditPath;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private CardDraggableAdapter adapter;
    private HljHttpSubscriber hideSubscriber;
    private HljHttpSubscriber deleteSubscriber;
    private RecyclerView recyclerView;


    private Subscription cardSubscription;
    private Subscription rxSubscription;
    private Subscription sortSubscription;

    private SubscriptionList subscriptionList;

    private boolean isMusicPause;
    private boolean isPause;
    private String reloadPath;

    public static final String ARG_ID = "id";

    private Subscription pauseTimerSubscription;

    @Override
    public String pageTrackTagName() {
        return "请帖编辑页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_web___card);
        ButterKnife.bind(this);
        setActionBarPadding(this, actionLayout);
        initWebView();
        initData();
        initLoad();
        registerRxBusEvent();

        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(previewLayout)
                .tagName("edit_preview_button")
                .hitTag();
        HljVTTagger.buildTagger(addLayout)
                .tagName("add_page_button")
                .hitTag();
        HljVTTagger.buildTagger(sortLayout)
                .tagName("order_page_button")
                .hitTag();
        HljVTTagger.buildTagger(settingLayout)
                .tagName("setting_button")
                .hitTag();
        HljVTTagger.buildTagger(sendLayout)
                .tagName("send_button")
                .hitTag();
    }

    private void initData() {
        id = getIntent().getLongExtra(ARG_ID, 0);
        card = getIntent().getParcelableExtra("card");
        cardEditPath = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(cardEditPath)) {
            webView.loadUrl(cardEditPath);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //wenbview缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (HljCommon.debug) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        webView.addJavascriptInterface(new CardEditHandler(), "messageHandlers");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(
                    WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
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

    private void initLoad() {
        if (cardSubscription != null && !cardSubscription.isUnsubscribed()) {
            return;
        }
        cardSubscription = CardApi.getCard(id)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<Card>() {
                            @Override
                            public void onNext(Card card) {
                                CardWebActivity.this.card = card;
                                if (card.getUserId() != UserSession.getInstance()
                                        .getUser(CardWebActivity.this)
                                        .getId()) {
                                    ToastUtil.showToast(CardWebActivity.this, "请帖已被领取", 0);
                                    RxBus.getDefault()
                                            .post(new CardRxEvent(CardRxEvent.RxEventType
                                                    .CARD_OWNER_CHANGE,
                                                    null));
                                    onBackPressed();
                                    return;
                                }
                                if (TextUtils.isEmpty(cardEditPath)) {
                                    cardEditPath = card.getEditLink();
                                    webView.loadUrl(cardEditPath);
                                }
                                onCardChange();
                            }
                        })
                        .build());
    }

    private void registerRxBusEvent() {
        rxSubscription = RxBus.getDefault()
                .toObservable(CardRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<CardRxEvent>() {
                    @Override
                    protected void onEvent(CardRxEvent cardRxEvent) {
                        switch (cardRxEvent.getType()) {
                            case CARD_INFO_EDIT:
                                Card card = (Card) cardRxEvent.getObject();
                                CardWebActivity.this.card = card;
                                if (isPause) {
                                    reloadPath = card.getEditLink();
                                } else {
                                    webView.loadUrl(card.getEditLink());
                                }
                                onCardChange();
                                break;
                            case CARD_MUSIC_EDIT:
                                Audio audio = (Audio) cardRxEvent.getObject();
                                if (audio != null && !TextUtils.isEmpty(audio.getCurrentPath())) {
                                    webView.loadUrl(String.format(
                                            "javascript:INVITATION_CARD.changeMusic(\'%s\');",
                                            audio.getCurrentPath()));
                                } else {
                                    webView.loadUrl("javascript:INVITATION_CARD" +
                                            ".changeMusic(null);");
                                }
                                break;
                            case CARD_DELETE:
                            case CARD_COPY:
                                finish();
                                break;
                            case PAGE_VIDEO_EDIT:
                                PageEditResult editResult = (PageEditResult) cardRxEvent
                                        .getObject();
                                if (editResult != null) {
                                    CardWebActivity.this.card.setPage(editResult.getCardPage());
                                    onCardChange();
                                    webView.loadUrl(String.format(
                                            "javascript:INVITATION_CARD.changeVideo" + "(%s)",
                                            editResult.getH5PageStr()));
                                }
                                break;
                            case PAGE_IMAGE_EDIT:
                                editResult = (PageEditResult) cardRxEvent.getObject();
                                if (editResult != null) {
                                    CardWebActivity.this.card.setPage(editResult.getCardPage());
                                    onCardChange();
                                    webView.loadUrl(String.format(
                                            "javascript:INVITATION_CARD.editPageHoles" + "(%s)",
                                            editResult.getH5PageStr()));
                                }
                                break;
                        }
                    }
                });
    }


    @OnClick(R2.id.preview_layout)
    public void onPreview() {
        if (card == null || TextUtils.isEmpty(card.getPreviewOnlyLink())) {
            return;
        }
        Intent intent = new Intent(this, CardPreviewActivity.class);
        intent.putExtra("path", card.getPreviewOnlyLink());
        intent.putExtra("card", card);
        startActivity(intent);
    }

    @OnClick(R2.id.add_layout)
    public void onCreatePage() {
        if (card == null) {
            return;
        }
        Intent intent = new Intent(this, CardTemplateActivity.class);
        intent.putExtra("card", card);
        startActivityForResult(intent, PAGE_CREATE_RESULT);
        overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
    }

    @OnClick(R2.id.sort_layout)
    public void onPageSort() {
        if (card == null) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogFragment);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_card_sort___card);
                recyclerView = (RecyclerView) window.findViewById(R.id.recycler_view);
                showSortView();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                window.setAttributes(params);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style___card);
            }
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    btnBack.setVisibility(View.VISIBLE);
                    CommonUtil.unSubscribeSubs(pauseTimerSubscription);
                    webView.loadUrl("javascript:INVITATION_CARD.hideEdit(false)");
                    webView.loadUrl(String.format("javascript:INVITATION_CARD.musicPause(%s)",
                            isMusicPause));
                }
            });
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    btnBack.setVisibility(View.GONE);
                    webView.loadUrl("javascript:INVITATION_CARD.hideEdit(true)");
                    CommonUtil.unSubscribeSubs(pauseTimerSubscription);
                    pauseTimerSubscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                            .timeout(5, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    webView.loadUrl("javascript:window.messageHandlers" + "" + ""
                                            + ".receiveMusicPauseState" + "(INVITATION_CARD" + ""
                                            + ".musicStatePause);");
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
                                }
                            });
                }
            });
        } else {
            adapter.setCard(card);
        }
        webView.loadUrl("javascript:window.messageHandlers.receiveCurrentPage(INVITATION_CARD" +
                ".getCurrentPage())");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showSortView() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = CommonUtil.dp2px(this, 96);
        int height = Math.round((width - dm.density * 14) * 122 / 75 + 20 * dm.density);
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setCheckCanDropEnabled(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        adapter = new CardDraggableAdapter(this, card, width, height);
        adapter.setOnPageActionListener(this);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(adapter);
        GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mWrappedAdapter);
        recyclerView.setItemAnimator(animator);
        mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);
    }

    @OnClick(R2.id.setting_layout)
    public void onSetting() {
        if (card == null) {
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_SETTING)
                .withParcelable("card", card)
                .navigation(this, CARD_SETTING_EDIT_RESULT);
    }

    @OnClick(R2.id.send_layout)
    public void onSend() {
        if (card == null) {
            return;
        }
        if (card.isClosed()) {
            DialogUtil.createSingleButtonDialog(this, "请在“设置”中重新开启请帖后再发送", null, null)
                    .show();
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Card.CARD_SEND)
                .withParcelable("card", card)
                .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                .navigation(this);
    }

    //    private void showNameEditDialog() {
    //        CardDialogUtil.createNameEditDialog(CardWebActivity.this, card, new View
    // .OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                Intent intent = new Intent(CardWebActivity.this, CardSendActivity.class);
    //                intent.putExtra("card", card);
    //                startActivity(intent);
    //                overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
    //            }
    //        }, new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                Intent intent = new Intent(CardWebActivity.this, CardInfoEditActivity.class);
    //                intent.putExtra("card", card);
    //                startActivity(intent);
    //            }
    //        })
    //                .show();
    //    }

    @OnClick(R2.id.btn_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && card != null) {
            switch (requestCode) {
                case PAGE_TEXT_EDIT_RESULT:
                    PageEditResult editResult = data.getParcelableExtra("editResult");
                    if (editResult != null) {
                        card.setPage(editResult.getCardPage());
                        onCardChange();
                    }
                    String editHoleStr = data.getStringExtra("editHoleStr");
                    if (!TextUtils.isEmpty(editHoleStr)) {
                        webView.loadUrl(String.format("javascript:INVITATION_CARD.editPageHoles"
                                        + "(%s)",
                                editHoleStr));
                    }
                    break;
                case PAGE_CREATE_RESULT:
                    editResult = data.getParcelableExtra("editResult");
                    if (editResult != null) {
                        webView.loadUrl(String.format("javascript:INVITATION_CARD.addPage" + "(%s)",
                                editResult.getH5PageStr()));
                        card.setPage(editResult.getCardPage());
                        onCardChange();
                    }
                    break;
                case CARD_SETTING_EDIT_RESULT:
                    String otherState = data.getStringExtra("other_state");
                    boolean isClosed = data.getBooleanExtra("is_closed", false);
                    if (card != null) {
                        card.setClosed(isClosed);
                    }
                    if (!TextUtils.isEmpty(otherState)) {
                        webView.loadUrl(String.format("javascript:INVITATION_CARD.otherAction(%s)",
                                otherState));
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.cancelDrag();
        }
        if (dialog == null || !dialog.isShowing()) {
            CommonUtil.unSubscribeSubs(pauseTimerSubscription);
            pauseTimerSubscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                    .timeout(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            webView.loadUrl(
                                    "javascript:window.messageHandlers.receiveMusicPauseState" +
                                            "(INVITATION_CARD.musicStatePause);");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
                        }
                    });
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onFinish() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mLayoutManager = null;
        CommonUtil.unSubscribeSubs(cardSubscription,
                deleteSubscriber,
                hideSubscriber,
                sortSubscription,
                rxSubscription,
                subscriptionList,
                pauseTimerSubscription);
        super.onFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.loadUrl("about:blank");
        webView.destroy();
    }

    protected void onResume() {
        CommonUtil.unSubscribeSubs(pauseTimerSubscription);
        if (dialog == null || !dialog.isShowing()) {
            webView.loadUrl(String.format("javascript:INVITATION_CARD.musicPause(%s)",
                    isMusicPause));
        }
        isPause = false;
        if (!TextUtils.isEmpty(reloadPath)) {
            webView.loadUrl(reloadPath);
            reloadPath = null;
        }
        super.onResume();
    }

    @Override
    public void onDeletePage(final CardPage cardPage) {
        if (cardPage == null) {
            return;
        }

        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_card_page_delete___card),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(deleteSubscriber);
                        if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
                            Observable observable = CardApi.deleteCardPage(cardPage.getId());
                            deleteSubscriber = HljHttpSubscriber.buildSubscriber(CardWebActivity
                                    .this)
                                    .setOnNextListener(new SubscriberOnNextListener() {

                                        @Override
                                        public void onNext(Object o) {
                                            int index = adapter.removePage(cardPage);
                                            card.removePage(cardPage);
                                            webView.loadUrl(String.format(
                                                    "javascript:INVITATION_CARD.delPage(%s)",
                                                    index));
                                        }
                                    })
                                    .build();
                            observable.subscribe(deleteSubscriber);
                        }
                    }
                },
                null)
                .show();
    }

    @Override
    public void onHidePage(final CardPage cardPage) {
        if (cardPage != null) {
            CommonUtil.unSubscribeSubs(hideSubscriber);
            if (hideSubscriber == null || hideSubscriber.isUnsubscribed()) {
                Observable observable = CardApi.postHiddenCardPage(cardPage.getId(),
                        cardPage.isHidden() ? 1 : 0);
                hideSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener() {

                            @Override
                            public void onNext(Object o) {
                                adapter.hidePageChanged();
                                card.hideSpeech(cardPage.isHidden());
                                webView.loadUrl(String.format(
                                        "javascript:INVITATION_CARD.guestPageHide(%s)",
                                        cardPage.isHidden()));
                            }
                        })
                        .build();
                observable.subscribe(hideSubscriber);
            }
        }
    }

    @Override
    public void onSelectPage(CardPage cardPage) {
        webView.loadUrl(String.format("javascript:INVITATION_CARD.gotoPage(%s)",
                card.getAllPages()
                        .indexOf(cardPage)));
    }

    @Override
    public void onDraggedPage(List<CardPage> pages, final int fromPosition, final int toPosition) {
        if (CommonUtil.isCollectionEmpty(pages)) {
            return;
        }
        CommonUtil.unSubscribeSubs(sortSubscription);
        final List<Long> ids = new ArrayList<>();
        for (CardPage cardPage : pages) {
            ids.add(cardPage.getId());
        }
        sortSubscription = CardApi.changePagePosition(ids)
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                            @Override
                            public void onNext(JsonElement jsonElement) {
                                for (int i = 0, from = fromPosition, size = Math.abs(fromPosition
                                        - toPosition); i < size; i++) {
                                    int to = from + (toPosition - fromPosition) / size;
                                    webView.loadUrl(String.format(
                                            "javascript:INVITATION_CARD.exchangePage" + "(%s,%s)",
                                            from,
                                            to));
                                    from = to;
                                }
                                Observable.from(card.getPages())
                                        .sorted(new Func2<CardPage, CardPage, Integer>() {
                                            @Override
                                            public Integer call(CardPage page1, CardPage page2) {
                                                if (!ids.contains(page1.getId())) {
                                                    return 1;
                                                } else if (!ids.contains(page2.getId())) {
                                                    return -1;
                                                }
                                                return ids.indexOf(page1.getId()) - ids.indexOf(
                                                        page2.getId());
                                            }
                                        })
                                        .toList()
                                        .subscribe(new Action1<List<CardPage>>() {
                                            @Override
                                            public void call(List<CardPage> cardPages) {
                                                card.setPages(cardPages);
                                            }
                                        });
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                adapter.reSortPageOrder(card.getPages());
                            }
                        })
                        .build());

    }

    private void onCardChange() {
        for (CardPage cardPage : card.getAllPages()) {
            if (subscriptionList == null) {
                subscriptionList = new SubscriptionList();
            }
            subscriptionList.add(CardEditObbUtil.createPageThumbObb(CardWebActivity.this,
                    cardPage,
                    cardPage == card.getFrontPage(),
                    cardPage == card.getSpeechPage()));
        }
    }

    private class CardEditHandler {

        @JavascriptInterface
        public void onEditBasicInfo(String s) {
            if (card == null) {
                return;
            }
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Card.CARD_INFO_EDIT)
                    .withParcelable("card", card)
                    .navigation(CardWebActivity.this);

        }

        @JavascriptInterface
        public void onEditMusic(String s) {
            if (card == null) {
                return;
            }
            Intent intent = new Intent(CardWebActivity.this, CardMusicListActivity.class);
            intent.putExtra("cardId", card.getId());
            startActivity(intent);

        }

        @JavascriptInterface
        public void receiveMusicPauseState(String isPause) {
            CommonUtil.unSubscribeSubs(pauseTimerSubscription);
            CardWebActivity.this.isMusicPause = Boolean.valueOf(isPause);
            if (webView != null) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:INVITATION_CARD.musicPause(true)");
                    }
                });
            }
        }

        @JavascriptInterface
        public void receiveCurrentPage(int currentPage) {
            Observable.just(currentPage)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer currentPage) {
                            if(recyclerView==null){
                                return;
                            }
                            recyclerView.scrollToPosition(currentPage);
                            adapter.setCurrentPage(currentPage);
                        }
                    });
        }

        @JavascriptInterface
        public void onEditPageHole(String s) {
            if (card == null) {
                return;
            }
            try {
                JsonObject jsonObject = new JsonParser().parse(s)
                        .getAsJsonObject();
                long pageId = jsonObject.get("page_id")
                        .getAsLong();
                long holdId = jsonObject.get("id")
                        .getAsLong();
                CardPage editPage = null;
                for (CardPage cardPage : card.getAllPages()) {
                    if (cardPage.getId() == pageId) {
                        editPage = cardPage;
                        editPage.setCardId(card.getId());
                        break;
                    }
                }
                if (editPage == null) {
                    return;
                }
                switch (jsonObject.get("type")
                        .getAsString()) {
                    case "text":
                        TextInfo textInfo = null;
                        TextHole textHole = null;
                        for (TextInfo info : editPage.getTextInfos()) {
                            if (info.getHoleId() == holdId) {
                                textInfo = info;
                                break;
                            }
                        }
                        for (TextHole hole : editPage.getTemplate()
                                .getTextHoles()) {
                            if (hole.getId() == holdId) {
                                textHole = hole;
                                break;
                            }
                        }
                        if (textHole != null) {
                            Intent intent = new Intent(CardWebActivity.this,
                                    PageTextEditActivity.class);
                            intent.putExtra("page", editPage);
                            intent.putExtra("textInfo", textInfo);
                            intent.putExtra("textHole", textHole);
                            startActivityForResult(intent, PAGE_TEXT_EDIT_RESULT);
                            overridePendingTransition(0, 0);
                        }
                        break;
                    case "image":
                        ImageInfo imageInfo = null;
                        ImageHole imageHole = null;
                        for (ImageInfo info : editPage.getImageInfos()) {
                            if (info.getHoleId() == holdId) {
                                imageInfo = info;
                                break;
                            }
                        }
                        for (ImageHole hole : editPage.getTemplate()
                                .getImageHoles()) {
                            if (hole.getId() == holdId) {
                                imageHole = hole;
                                break;
                            }
                        }
                        if (imageHole != null) {
                            Intent intent;
                            if (imageHole.isSupportVideo()) {
                                intent = new Intent(CardWebActivity.this,
                                        PageVideoChooserActivity.class);
                            } else {
                                intent = new Intent(CardWebActivity.this,
                                        PageImageChooserActivity.class);
                            }
                            intent.putExtra("page", editPage);
                            intent.putExtra("imageInfo", imageInfo);
                            intent.putExtra("imageHole", imageHole);
                            startActivity(intent);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
