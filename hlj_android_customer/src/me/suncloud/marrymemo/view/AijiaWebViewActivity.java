package me.suncloud.marrymemo.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.AijiaWebHandler;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.FileChooserClient;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 通用的WebViewActivity
 */
@RuntimePermissions
public class AijiaWebViewActivity extends HljBaseNoBarActivity implements OnClickListener {
    private WebView webView;
    private Dialog dialog;
    private ProgressBar progressBar;
    private ValueCallback<Uri[]> mUploadMessages;
    private ValueCallback<Uri> mUploadMessage;
    private AijiaWebHandler webHandler;
    private TextView tvTitle;
    private TextView hintView;
    private Uri imgUri;
    private int payResult = 2; // 默认的支付结果为:取消支付
    private boolean manualClose; // 是否要手动关闭所有爱家webview页面
    public static final int PAY_RESULT_FAIL = 0;
    public static final int PAY_RESULT_SUCCESS = 1;
    public static final int PAY_RESULT_CANCEL = 2;
    public static final String AIJIA_PAY_RESULT = "aijia_pay_result";
    public static final String MANUAL_CLOSE = "manual_close";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_web_view);
        setDefaultStatusBarPadding();

        hintView = (TextView) findViewById(R.id.hint);
        tvTitle = (TextView) findViewById(R.id.title);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        String path = getIntent().getStringExtra("path");
        City city = (City) getIntent().getSerializableExtra("city");
        User user = Session.getInstance()
                .getCurrentUser(this);
        Map<String, String> header = new HashMap<>();
        header.put("appver", Constants.APP_VERSION);
        header.put("phone",
                DeviceUuidFactory.getInstance()
                        .getDeviceUuidString());
        header.put("city",
                LocationSession.getInstance()
                        .getLocalString(null));
        if (Constants.DEBUG) {
            header.put("test", "1");
        }
        header.put("appName", "weddingUser");
        header.put("devicekind", "android");
        if (user != null && user.getId()
                .intValue() != 0) {
            path += (path.contains("?") ? "&" : "?") + "user_id=" + user.getId();
            header.put("token", user.getToken());
            header.put("secret", JSONUtil.getMD5(user.getToken() + Constants.LOGIN_SEED));
        }
        path += (path.contains("?") ? "&" : "?") + "appver=" + Constants.APP_VERSION;
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        if (Constants.DEBUG) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        webHandler = new AijiaWebHandler(this, webView);

        // 爱家理财js调用的函数接口
        webView.addJavascriptInterface(webHandler, "wedding");

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

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!JSONUtil.isEmpty(url) && url.startsWith("tel:")) {
                    callUp(Uri.parse(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hintView.setVisibility(webView.canGoBack() || hintView.getVisibility() != View
                        .GONE ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebChromeClient(new FileChooserClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
                if (fileChooserParams.getAcceptTypes() != null && fileChooserParams
                        .getAcceptTypes().length > 0) {
                    intent.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    intent.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        Constants.RequestCode.FILECHOOSER_RESULTCODE);
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
                if (JSONUtil.isEmpty(acceptType)) {
                    intent.setType("*/*");
                } else {
                    intent.setType(acceptType);
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        Constants.RequestCode.FILECHOOSER_RESULTCODE);
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (mUploadMessage != null)
                    return;
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (JSONUtil.isEmpty(acceptType)) {
                    intent.setType("*/*");
                } else {
                    intent.setType(acceptType);
                }
                startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                        Constants.RequestCode.FILECHOOSER_RESULTCODE);
            }

            // For Android < 3.0
            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!title.startsWith("www") && !title.startsWith("http") && !title.equals
                        ("test")) {
                    tvTitle.setText(title);
                }
                super.onReceivedTitle(view, title);
            }
        });
        if (!header.isEmpty()) {
            webView.loadUrl(path, header);
        } else {
            webView.loadUrl(path);
        }
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Intent intent = getIntent();
            intent.putExtra(AIJIA_PAY_RESULT, payResult);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    /**
     * 手动退出webview activity页面
     * 如果前一个也是webview activity的话,继续返回
     */
    public void manualCloseWebView() {
        Intent intent = getIntent();
        intent.putExtra(AIJIA_PAY_RESULT, payResult);
        intent.putExtra(MANUAL_CLOSE, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onForward(View v) {
        webView.goForward();
    }

    public void onBack(View v) {
        webView.goBack();
    }

    public void onRefresh(View v) {
        webView.reload();
    }

    public void onUploadImg() {
        if ((dialog != null && dialog.isShowing())) {
            return;
        }
        if (dialog == null) {
            dialog = Util.initSelectImgDialog(this, this);
        }
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_gallery:
                // 相册
                AijiaWebViewActivityPermissionsDispatcher.onReadPhotosWithCheck(this);
                dialog.dismiss();
                break;
            case R.id.action_camera_photo:
                // 拍照
                AijiaWebViewActivityPermissionsDispatcher.onTakePhotosWithCheck(this);
                dialog.dismiss();
                break;
            case R.id.action_cancel:
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (webView != null) {
            webView.loadUrl("about:blank");
            setContentView(new FrameLayout(this));
        }
    }

    /**
     * 爱家回调返回的支付结果,
     *
     * @param result
     * @param resultMsg
     */
    public void onPayResult(int result, String resultMsg) {
        if (result == PAY_RESULT_FAIL && !JSONUtil.isEmpty(resultMsg)) {
            Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT)
                    .show();
        }
        Intent intent = getIntent();
        intent.putExtra(AIJIA_PAY_RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webHandler != null) {
            webHandler.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.AIJIA_WEB_VIEW:
                    if (data != null) {
                        // 可能从第二个第三个AijiaWebViewActivity返回来支付结果,需要在这里记录下来,在下一次返回传回上一个Activity
                        // 默认是取消支付
                        payResult = data.getIntExtra(AIJIA_PAY_RESULT, PAY_RESULT_CANCEL);
                        manualClose = data.getBooleanExtra(MANUAL_CLOSE, false);
                        if (manualClose) {
                            // 如果上一个返回的webview要求手动关闭所有的webview页面,则继续关闭
                            manualCloseWebView();
                        }
                    }
                    break;
                default:
                    if (requestCode == Source.CAMERA.ordinal() || requestCode == Source.GALLERY
                            .ordinal()) {
                        if (requestCode == Source.CAMERA.ordinal()) {
                            String path = Environment.getExternalStorageDirectory() + File
                                    .separator + "temp.jpg";
                            imgUri = Uri.fromFile(new File(path));
                        }
                        if (requestCode == Source.GALLERY.ordinal()) {
                            if (data == null) {
                                return;
                            }
                            imgUri = Uri.fromFile(new File(JSONUtil.getImagePathForUri(data
                                            .getData(),
                                    AijiaWebViewActivity.this)));
                        }
                        if (imgUri != null) {
                            // 压缩处理图片,然后回调JS接口
                            String encodeImgStr = Util.encodeImageBase64(JSONUtil
                                    .getImagePathForUri(
                                    imgUri,
                                    this));
                            webHandler.invokeUpImgCallBack(encodeImgStr);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private enum Source {
        GALLERY, CAMERA
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onReadPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Source.GALLERY.ordinal());
        dialog.dismiss();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this,
                    getPackageName(),
                    new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "temp.jpg"));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Source.CAMERA.ordinal());
        dialog.dismiss();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_camera));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.permission_r_for_read_external_storage));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AijiaWebViewActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
