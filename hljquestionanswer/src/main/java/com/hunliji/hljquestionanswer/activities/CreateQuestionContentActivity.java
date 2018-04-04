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
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.editor.EditorWebViewAbstract;
import com.hunliji.hljquestionanswer.editor.EditorWebViewCompatibility;
import com.hunliji.hljquestionanswer.editor.HtmlUtils;
import com.hunliji.hljquestionanswer.editor.JsCallbackReceiver;
import com.hunliji.hljquestionanswer.editor.OnJsEditorStateChangedListener;
import com.hunliji.hljquestionanswer.models.QARxEvent;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionResult;
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

/**
 * Created by wangtao on 2016/12/21.
 */

@RuntimePermissions
public class CreateQuestionContentActivity extends HljBaseNoBarActivity implements
        OnJsEditorStateChangedListener {

    @BindView(R2.id.web_view_edit)
    EditorWebViewAbstract webViewEdit;
    @BindView(R2.id.btn_keyboard)
    View btnKeyboard;
    @BindView(R2.id.layout)
    LinearLayout layout;
    @BindView(R2.id.loading)
    UploadingTextView loading;


    private Question question;
    private String title;
    private long markId;
    private String mContentHtml;

    private boolean immIsShow;
    private InputMethodManager imm;

    private ArrayList<Subscriber> subscribers; //图片上传订阅列表
    private Subscription postSubscription; //问题上传订阅
    private Dialog progressDialog;

    private final int PHOTO_FROM_GALLERY = 1;

    private boolean isDomLoaded;
    private Subscriber<? super String> contentSubscriber;
    private Subscriber<? super List<String>> failedMediaSubscriber;
    private Subscriber<? super List<String>> localMediaSubscriber;
    private Subscription jsSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");
        question = getIntent().getParcelableExtra("question");
        markId = getIntent().getLongExtra("markId", 0);
        mContentHtml = getIntent().getStringExtra("content");
        if (mContentHtml == null) {
            mContentHtml = "";
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_content___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);
        initView();
        initJsEditor();
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
     * 返回时检测内容判断是否提示
     */
    @Override
    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        if (!isDomLoaded) {
            super.onBackPressed();
            return;
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
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (EditWebUtil.isEditEmpty(s)) {
                            return null;
                        }
                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        CreateQuestionContentActivity.super.onBackPressed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        CreateQuestionContentActivity.super.onBackPressed();
                    }

                    @Override
                    public void onNext(String s) {
                        Intent intent = getIntent();
                        intent.putExtra("content", s);
                        setResult(RESULT_OK, intent);
                    }
                });
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
                            ToastUtil.showToast(CreateQuestionContentActivity.this,
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
                            DialogUtil.createSingleButtonDialog(CreateQuestionContentActivity.this,
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
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (EditWebUtil.isEditEmpty(s)) {
                            return null;
                        }
                        return s;
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
                        onPostQuestion(s);
                    }
                });
    }

    /**
     * 添加图片每次一张
     */
    @OnClick(R2.id.btn_add)
    public void addImage() {
        CreateQuestionContentActivityPermissionsDispatcher.onActionPickWithCheck(this);
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
     * 提问
     */
    @SuppressWarnings("unchecked")
    private void onPostQuestion(String mContentHtml) {
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        PostQuestionBody body = new PostQuestionBody();
        if (question != null) {
            body.setId(question.getId());
        }
        body.setContent(mContentHtml);
        body.setTitle(title);
        List<Long> ids = new ArrayList<>();
        if (question == null) {
            if (markId > 0) {
                ids.add(markId);
            }
        } else if (question.getMarks() != null) {
            for (Mark mark : question.getMarks()) {
                ids.add(mark.getId());
            }
        }
        body.setMarks(ids);
        if (postSubscription != null && !postSubscription.isUnsubscribed()) {
            postSubscription.unsubscribe();
        }
        postSubscription = QuestionAnswerApi.postQuestionObb(body, 1)
                .subscribe(HljHttpSubscriber.buildSubscriber(CreateQuestionContentActivity.this)
                        .setProgressDialog(progressDialog)
                        .setOnNextListener(new SubscriberOnNextListener<PostQuestionResult>() {
                            @Override
                            public void onNext(final PostQuestionResult result) {
                                //提问完成
                                RxBus.getDefault()
                                        .post(new QARxEvent(QARxEvent.RxEventType
                                                .QUESTION_POST_DONE,
                                                null));
                                if (result != null) {
                                    if (result.isExist()) {
                                        // 已有相同问题
                                        ToastUtil.showToast(CreateQuestionContentActivity.this,
                                                null,
                                                R.string.msg_question_is_exist___qa);
                                    }
                                    if ((question == null || result.isExist()) && result.getId()
                                            > 0) {
                                        // 非编辑问题或有相同问题跳转问题详情
                                        Intent intent = new Intent(CreateQuestionContentActivity
                                                .this, QuestionDetailActivity.class);
                                        intent.putExtra("questionId", result.getId());
                                        startActivity(intent);
                                    }
                                }
                                finish();
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        })
                        .build());
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
        CreateQuestionContentActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PHOTO_FROM_GALLERY:
                    String path = ImageUtil.getImagePathForUri(data.getData(), this);
                    // 先插入图片上传成功后替换，通过时间戳关联
                    String idStr = System.currentTimeMillis() + "";

                    webViewEdit.execJavaScriptFromString("ZSSEditor.insertLocalImage(" + idStr +
                            ", '" + path + "');");
                    uploadImage(idStr, path);


                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    protected void onFinish() {
        if (subscribers != null) {
            for (Subscriber subscriber : subscribers) {
                CommonUtil.unSubscribeSubs(subscriber);
            }
        }
        CommonUtil.unSubscribeSubs(postSubscription,
                jsSubscription,
                contentSubscriber,
                failedMediaSubscriber,
                localMediaSubscriber);
        webViewEdit.loadUrl("about:blank");
        webViewEdit.destroy();
        super.onFinish();
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
                        "ZSSEditor.getField('zss_field_content').setMultiline" + "('true');");

                //提示内容
                webViewEdit.execJavaScriptFromString("ZSSEditor.getField('zss_field_content')" +
                        "" + ".setPlaceholderText('" + HtmlUtils.escapeHtml(
                        getString(R.string.hint_question_content___qa)) + "');");
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
                        webViewEdit.execJavaScriptFromString("ZSSEditor.unmarkImageUploadFailed("
                                + mediaId + ");");
                        uploadImage(mediaId, HtmlUtils.getFileSrcToPath(mediaUrl));
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
