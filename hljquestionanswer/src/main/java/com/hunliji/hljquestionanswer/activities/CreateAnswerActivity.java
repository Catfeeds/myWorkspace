package com.hunliji.hljquestionanswer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.editor.EditorWebViewAbstract;
import com.hunliji.hljquestionanswer.editor.EditorWebViewCompatibility;
import com.hunliji.hljquestionanswer.editor.HtmlUtils;
import com.hunliji.hljquestionanswer.editor.JsCallbackReceiver;
import com.hunliji.hljquestionanswer.editor.OnJsEditorStateChangedListener;
import com.hunliji.hljquestionanswer.utils.EditWebUtil;
import com.hunliji.hljquestionanswer.widgets.UploadingTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

@RuntimePermissions
public class CreateAnswerActivity extends HljBaseNoBarActivity implements
        OnJsEditorStateChangedListener {

    @BindView(R2.id.web_view_edit)
    EditorWebViewAbstract webViewEdit;
    @BindView(R2.id.btn_keyboard)
    View btnKeyboard;
    @BindView(R2.id.layout)
    LinearLayout layout;
    @BindView(R2.id.loading)
    UploadingTextView loading;


    private Answer answer;
    private long questionId;
    private long userId;
    private Realm realm;
    private PostAnswerBody draft;
    private String mContentHtml;
    private String htmlHint;

    private InputMethodManager imm;
    private boolean immIsShow;

    private ArrayList<Subscriber> subscribers; //图片上传订阅列表
    private Subscription postSubscription; //回答上传订阅
    private Dialog progressDialog;

    private final int PHOTO_FROM_GALLERY = 1;

    private boolean isDomLoaded;
    private Subscriber<? super String> contentSubscriber;
    private Subscriber<? super List<String>> failedMediaSubscriber;
    private Subscriber<? super List<String>> localMediaSubscriber;
    private Subscription jsSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_answer___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);
        initData();
        initView();
        initJsEditor();
    }

    private void initData() {
        if (HljQuestionAnswer.isMerchant(this)) {
            htmlHint = getString(R.string.hint_answer_merchant_content___qa);
        } else {
            htmlHint = getString(R.string.hint_answer_content___qa);
        }
        userId = UserSession.getInstance()
                .getUser(this)
                .getId();
        answer = getIntent().getParcelableExtra("answer");
        questionId = getIntent().getLongExtra("questionId", 0);
        if (questionId > 0) {
            realm = Realm.getDefaultInstance();
            draft = realm.where(PostAnswerBody.class)
                    .equalTo("userId", userId)
                    .equalTo("questionId", questionId)
                    .findFirst();
        }
        if (answer != null && !TextUtils.isEmpty(answer.getContent())) {
            //编辑回答
            mContentHtml = answer.getContent();
        } else if (draft != null && !TextUtils.isEmpty(draft.getContent())) {
            //提交回答判断是否有草稿
            mContentHtml = draft.getContent();
        }
        if (TextUtils.isEmpty(mContentHtml)) {
            mContentHtml = "";
        }
    }

    private void initView() {
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    onImmShow();
                } else {
                    onImmHide();
                }

            }
        });
    }

    /**
     * 初始化富文本编辑器
     */
    @SuppressLint("AddJavascriptInterface")
    protected void initJsEditor() {
        if (webViewEdit.shouldSwitchToCompatibilityMode()) {
            ViewGroup parent = (ViewGroup) webViewEdit.getParent();
            int index = parent.indexOfChild(webViewEdit);
            parent.removeView(webViewEdit);
            webViewEdit = new EditorWebViewCompatibility(this, null);
            webViewEdit.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout
                    .LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            parent.addView(webViewEdit, index);
        }

        // Ensure that the content field is always filling the remaining screen space
        webViewEdit.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                webViewEdit.post(new Runnable() {
                    @Override
                    public void run() {
                        webViewEdit.execJavaScriptFromString(
                                "try {ZSSEditor.refreshVisibleViewportSize();} catch (e) " +
                                        "{console.log(e)}");
                    }
                });
            }
        });

        String htmlEditor = HtmlUtils.getHtmlFromFile(this, "android-editor.html");
        if (htmlEditor != null) {
            htmlEditor = htmlEditor.replace("%%TITLE%%", getTitle());
            htmlEditor = htmlEditor.replace("%%ANDROID_API_LEVEL%%",
                    String.valueOf(Build.VERSION.SDK_INT));
            htmlEditor = htmlEditor.replace("%%LOCALIZED_STRING_INIT%%",
                    "nativeState.localizedStringEdit = 'Edit';\n" + "nativeState" + "" + "" + ""
                            + ".localizedStringUploading = '" + "aa" + "';\n" + "nativeState" +
                            "" + ".localizedStringUploadingGallery = '" + "aaa" + "';\n");
        }
        if (Build.VERSION.SDK_INT < 17) {
            webViewEdit.setJsCallbackReceiver(new JsCallbackReceiver(this));
        } else {
            webViewEdit.addJavascriptInterface(new JsCallbackReceiver(this),
                    JsCallbackReceiver.JS_CALLBACK_HANDLER);
        }

        webViewEdit.loadDataWithBaseURL("file:///android_asset/",
                htmlEditor,
                "text/html",
                "utf-8",
                "");
        if (HljCommon.debug) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            webViewEdit.setDebugModeEnabled(true);
        }
        webViewEdit.getSettings()
                .setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webViewEdit.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    private void onImmShow() {
        btnKeyboard.setVisibility(View.VISIBLE);
    }

    private void onImmHide() {
        btnKeyboard.setVisibility(View.GONE);
    }

    /**
     * 发布回答直接保存并退出
     * 编辑回答不保存弹窗提示
     */
    @Override
    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        if (!isDomLoaded) {
            super.onBackPressed();
            return;
        }
        //隐藏软键盘
        if (imm != null && immIsShow && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (jsSubscription != null && !jsSubscription.isUnsubscribed()) {
            return;
        }
        jsSubscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                contentSubscriber = subscriber;
                webViewEdit.execJavaScriptFromString(
                        "ZSSEditor.getField('zss_field_content').getHTMLForCallback()");
            }
        })
                .timeout(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        CreateAnswerActivity.super.onBackPressed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        CreateAnswerActivity.super.onBackPressed();
                    }

                    @Override
                    public void onNext(String mContentHtml) {
                        if (realm != null) {
                            Intent intent = getIntent();
                            intent.putExtra("isRefresh", false);
                            setResult(RESULT_OK, intent);
                            if (draft != null) {
                                realm.beginTransaction();
                                if (EditWebUtil.isEditEmpty(mContentHtml)) {
                                    draft.deleteFromRealm();
                                } else {
                                    draft.setContent(mContentHtml);
                                }
                                realm.commitTransaction();
                            } else if (!EditWebUtil.isEditEmpty(mContentHtml)) {
                                realm.beginTransaction();
                                draft = new PostAnswerBody(userId, questionId, mContentHtml);
                                realm.copyToRealm(draft);
                                realm.commitTransaction();
                            }
                        } else if (!EditWebUtil.isEditEmpty(mContentHtml) && answer != null) {
                            DialogUtil.createDoubleButtonDialog(CreateAnswerActivity.this,
                                    getString(R.string.msg_edit_back___qa),
                                    getString(R.string.label_give_up___cm),
                                    getString(R.string.label_continue___cm),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CreateAnswerActivity.super.onBackPressed();
                                        }
                                    },
                                    null)
                                    .show();
                        }
                    }
                });
    }

    /**
     * 添加图片每次一张
     */
    @OnClick(R2.id.btn_add)
    public void addImage() {
        CreateAnswerActivityPermissionsDispatcher.onActionPickWithCheck(this);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onDeniedReadPhotos() {
        ToastUtil.showToast(this, null, R.string.label_photo_empty_permission___img);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onActionPick() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_FROM_GALLERY);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CreateAnswerActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 隐藏软键盘
     */
    @OnClick(R2.id.btn_keyboard)
    public void hideKeyboard(View view) {
        if (imm != null && immIsShow && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 发布按钮点击
     * 无图片上传直接校验发布内容，当前有上传任务是在上传结束后进行通过 progressDialog 判断是否触发发布操作
     */
    @OnClick(R2.id.btn_post)
    public void onPostAction() {
        if (!isDomLoaded) {
            return;
        }
        if (subscribers != null && !subscribers.isEmpty()) {
            DialogUtil.createSingleButtonDialog(this,
                    getString(R.string.msg_post_image_uploading___qa),
                    null,
                    null)
                    .show();
            return;
        }
        if (jsSubscription != null && !jsSubscription.isUnsubscribed()) {
            return;
        }
        jsSubscription = Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                failedMediaSubscriber = subscriber;
                webViewEdit.execJavaScriptFromString("ZSSEditor.getFailedMedia()");
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(1, TimeUnit.SECONDS)
                .filter(new Func1<List<String>, Boolean>() {
                    @Override
                    public Boolean call(List<String> strings) {
                        if (!strings.isEmpty()) {
                            ToastUtil.showToast(CreateAnswerActivity.this,
                                    null,
                                    R.string.hint_post_image_failed_err___qa);
                            return false;
                        }
                        return true;
                    }
                })
                .concatMap(new Func1<List<String>, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(List<String> strings) {
                        return Observable.create(new Observable.OnSubscribe<List<String>>() {
                            @Override
                            public void call(Subscriber<? super List<String>> subscriber) {
                                localMediaSubscriber = subscriber;
                                webViewEdit.execJavaScriptFromString("ZSSEditor.getLocalMedia()");
                            }
                        })
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .timeout(1, TimeUnit.SECONDS)
                .filter(new Func1<List<String>, Boolean>() {
                    @Override
                    public Boolean call(List<String> strings) {
                        if (!strings.isEmpty()) {
                            DialogUtil.createSingleButtonDialog(CreateAnswerActivity.this,
                                    getString(R.string.msg_post_image_uploading___qa),
                                    null,
                                    null)
                                    .show();
                            return false;
                        }
                        return true;
                    }
                })
                .concatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                contentSubscriber = subscriber;
                                webViewEdit.execJavaScriptFromString(
                                        "ZSSEditor.getField('zss_field_content')" + "" + "" + ""
                                                + ".getHTMLForCallback()");
                            }
                        })
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .timeout(1, TimeUnit.SECONDS)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (EditWebUtil.isEditEmpty(s)) {
                            ToastUtil.showToast(CreateAnswerActivity.this,
                                    null,
                                    R.string.msg_answer_content_empty___qa);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        onPostAnswer(s);
                    }
                });
    }

    /**
     * 发布先判断失败图片，然后获取html内容
     */
    @SuppressWarnings("unchecked")
    private void onPostAnswer(String mContentHtml) {
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        PostAnswerBody body = new PostAnswerBody();
        if (answer != null) {
            body.setAnswerId(answer.getId());
            body.setQuestionId(answer.getQuestionId());
        }
        if (questionId > 0) {
            body.setQuestionId(questionId);
        }
        body.setContent(mContentHtml);
        CommonUtil.unSubscribeSubs(postSubscription);
        postSubscription = QuestionAnswerApi.postAnswerObb(body,
                QuestionAnswerApi.TYPE_COMMUNITY_QA)
                .subscribe(HljHttpSubscriber.buildSubscriber(CreateAnswerActivity.this)
                        .setProgressDialog(progressDialog)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<BasePostResult>>() {
                            @Override
                            public void onNext(HljHttpResult<BasePostResult> result) {
                                //发布成功或者已经发布过回答，删除草稿
                                if (result.getStatus()
                                        .getRetCode() == 0 || result.getStatus()
                                        .getRetCode() == 503) {
                                    if (draft != null) {
                                        RealmResults<PostAnswerBody> results = realm.where(
                                                PostAnswerBody.class)
                                                .equalTo("userId", userId)
                                                .equalTo("questionId", questionId)
                                                .findAll();
                                        realm.beginTransaction();
                                        results.deleteAllFromRealm();
                                        realm.commitTransaction();
                                    }
                                }
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    Intent intent = getIntent();
                                    intent.putExtra("answerId",
                                            result.getData()
                                                    .getId());
                                    setResult(RESULT_OK, intent);
                                } else {
                                    ToastUtil.showToast(CreateAnswerActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            0);
                                }
                                finish();
                                overridePendingTransition(0, R.anim.slide_out_right);
                            }
                        })
                        .build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PHOTO_FROM_GALLERY:
                    String path = ImageUtil.getImagePathForUri(data.getData(), this);
                    // 先插入图片上传成功后替换，通过时间戳关联
                    final String idStr = System.currentTimeMillis() + "";

                    webViewEdit.execJavaScriptFromString("ZSSEditor.insertLocalImage(" + idStr +
                            ", '" + path + "');");
                    uploadImage(idStr, path);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        if (subscribers != null) {
            for (Subscriber subscriber : subscribers) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.unsubscribe();
                }
            }
        }
        CommonUtil.unSubscribeSubs(postSubscription,
                jsSubscription,
                contentSubscriber,
                failedMediaSubscriber,
                localMediaSubscriber);
        webViewEdit.loadUrl("about:blank");
        webViewEdit.destroy();
        if (realm != null) {
            realm.close();
        }
        super.onFinish();
    }

    private void uploadImage(final String idStr, String path) {
        loading.setVisibility(View.VISIBLE);
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }
        final Subscriber<HljUploadResult> subscriber = new Subscriber<HljUploadResult>() {
            @Override
            public void onCompleted() {
                subscribers.remove(this);
                if (subscribers.isEmpty()) {
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                //上传失败提示
                subscribers.remove(this);
                webViewEdit.execJavaScriptFromString("ZSSEditor.markImageUploadFailed" + "(" +
                        idStr + ", '" + HtmlUtils.escapeQuotes(
                        "上传失败") + "');");
                if (subscribers.isEmpty()) {
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNext(HljUploadResult hljUploadResult) {
                //上传成功替换图片
                webViewEdit.execJavaScriptFromString("ZSSEditor.replaceLocalImageWithRemoteImage"
                        + "(" + idStr + ", '" + idStr + "', '" + hljUploadResult.getUrl() + "');");
            }
        };
        subscribers.add(subscriber);
        new HljFileUploadBuilder(new File(path)).compress(this)
                .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                .compress(this)
                .build()
                .subscribe(subscriber);
    }

    /**
     * dom加载完成初始化webview
     */
    @Override
    public void onDomLoaded() {
        webViewEdit.post(new Runnable() {
            @Override
            public void run() {
                isDomLoaded = true;
                //内容多行设置
                webViewEdit.execJavaScriptFromString(
                        "ZSSEditor.getField('zss_field_content').setMultiline('true');");

                //提示内容
                webViewEdit.execJavaScriptFromString(
                        "ZSSEditor.getField('zss_field_content').setPlaceholderText('" +
                                HtmlUtils.escapeHtml(
                                htmlHint) + "');");
                //html文本内容
                webViewEdit.execJavaScriptFromString(
                        "ZSSEditor.getField('zss_field_content').setHTML('" + HtmlUtils.escapeHtml(
                                mContentHtml) + "');");
                //获取焦点
                webViewEdit.execJavaScriptFromString("ZSSEditor.focusFirstEditableField();");
                if (!immIsShow && imm != null) {
                    //弹出键盘
                    imm.showSoftInput(webViewEdit, InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });

    }

    /**
     * 图片点击
     *
     * @param mediaId      图片本地id
     * @param mediaUrl     图片地址 本地图片为 file://格式
     * @param uploadStatus 图片状态 failed 图片上传失败
     */
    @Override
    public void onMediaTapped(
            final String mediaId, final String mediaUrl, String uploadStatus) {
        switch (uploadStatus) {
            case "failed":
                webViewEdit.post(new Runnable() {
                    @Override
                    public void run() {
                        //移除失败提示
                        uploadImage(mediaId, HtmlUtils.getFileSrcToPath(mediaUrl));
                        webViewEdit.execJavaScriptFromString("ZSSEditor.unmarkImageUploadFailed("
                                + mediaId + ");");
                    }
                });
                break;
        }
    }

    /**
     * js 请求返回
     *
     * @param inputArgs 返回数据
     *                  getHTMLForCallback htm内容返回
     *                  getFailedMedia 上传失败的图片ids
     */
    @Override
    public void onGetHtmlResponse(Map<String, String> inputArgs) {
        String functionId = inputArgs.get("function");
        if (TextUtils.isEmpty(functionId)) {
            return;
        }

        switch (functionId) {
            case "getHTMLForCallback":
                String fieldId = inputArgs.get("id");
                String fieldContents = inputArgs.get("contents");
                if (!fieldId.isEmpty()) {
                    switch (fieldId) {
                        case "zss_field_content":
                            if (!TextUtils.isEmpty(fieldContents)) {
                                String regEx = "<a[^>]+>|</a>";
                                Pattern p = Pattern.compile(regEx);
                                Matcher m = p.matcher(fieldContents);
                                fieldContents = m.replaceAll("");
                            }
                            if (contentSubscriber != null) {
                                contentSubscriber.onNext(fieldContents);
                                contentSubscriber.onCompleted();
                            }
                            break;
                    }
                }
                break;
            case "getFailedMedia":
                String[] mediaIds = inputArgs.get("ids")
                        .split(",");
                List<String> mFailedMediaIds = new ArrayList<>();
                for (String mediaId : mediaIds) {
                    if (!mediaId.equals("")) {
                        mFailedMediaIds.add(mediaId);
                    }
                }
                if (failedMediaSubscriber != null) {
                    failedMediaSubscriber.onNext(mFailedMediaIds);
                    failedMediaSubscriber.onCompleted();
                }
                break;
            case "getLocalMedia":
                mediaIds = inputArgs.get("ids")
                        .split(",");
                List<String> mLocalMediaIds = new ArrayList<>();
                for (String mediaId : mediaIds) {
                    if (!mediaId.equals("")) {
                        mLocalMediaIds.add(mediaId);
                    }
                }
                if (localMediaSubscriber != null) {
                    localMediaSubscriber.onNext(mLocalMediaIds);
                    localMediaSubscriber.onCompleted();
                }
                break;
        }
    }
}
