package com.hunliji.hljnotelibrary.views.activities;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ZxingUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.KeyBackEditText;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.models.NotePoster;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 生成结婚海报
 * Created by jinxin on 2017/6/24 0024.
 */
@RuntimePermissions
public class NoteCreateWeddingPosterActivity extends HljBaseActivity implements TextView
        .OnEditorActionListener, KeyBackEditText.OnKeyPreImeListener {

    @Override
    public String pageTrackTagName() {
        return "生成海报页";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra("id", 0);
        return new VTMetaData(id, "Note");
    }

    @BindView(R2.id.img_cover)
    ImageView imgCover;
    @BindView(R2.id.img_triangle)
    ImageView imgTriangle;
    @BindView(R2.id.cover_layout)
    RelativeLayout coverLayout;
    @BindView(R2.id.tv_des)
    TextView tvDes;
    @BindView(R2.id.img_des_refresh)
    ImageView imgDesRefresh;
    @BindView(R2.id.tv_name_bride)
    TextView tvBrideName;
    @BindView(R2.id.tv_name_groom)
    TextView tvGroomName;
    @BindView(R2.id.img_edit_name)
    ImageView imgEditName;
    @BindView(R2.id.layout_name)
    LinearLayout layoutName;
    @BindView(R2.id.img_code)
    ImageView imgCode;
    @BindView(R2.id.card_view)
    CardView cardView;
    @BindView(R2.id.tv_share)
    TextView tvShare;
    @BindView(R2.id.tv_save)
    TextView tvSave;
    @BindView(R2.id.layout_edit_name)
    LinearLayout layoutEditName;
    @BindView(R2.id.et_bride)
    KeyBackEditText editBride;
    @BindView(R2.id.et_groom)
    KeyBackEditText editGroom;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.layout_content)
    RelativeLayout layoutContent;

    private int coverWidth;
    private List<String> desList;
    private int currentDesPosition;
    private String brideName;
    private String groomName;
    private String cover;
    private String imgCodeUrl;
    private InputMethodManager inputManager;
    private HljHttpSubscriber loadSubscriber;
    private long id;
    private int rawWidth;
    private int rawHeight;
    private String content;

    private List<String> deleteFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wedding_poster___note);
        ButterKnife.bind(this);

        initConstant();
        initLoad();
    }

    private void initConstant() {
        id = getIntent().getLongExtra("id", 0);
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        coverWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 32);
        desList = new ArrayList<>();
    }

    private void initWidget() {
        int coverHeight = coverWidth * 9 / 16;
        coverLayout.getLayoutParams().height = coverHeight;
        Glide.with(this)
                .load(ImagePath.buildPath(cover)
                        .width(coverWidth)
                        .height(coverHeight)
                        .cropPath())
                .into(imgCover);
        drawTriangle();
        if (!TextUtils.isEmpty(content)) {
            tvDes.setText(content);
        } else {
            if (!desList.isEmpty()) {
                tvDes.setText(desList.get(0));
                currentDesPosition = 0;
            }
        }
        tvBrideName.setText(brideName);
        tvGroomName.setText(groomName);
        if (!brideName.equals("新娘名称")) {
            editBride.setText(brideName);
        }
        if (!groomName.equals("新郎名称")) {
            editGroom.setText(groomName);
        }

        editBride.setOnEditorActionListener(this);
        editGroom.setOnEditorActionListener(this);
        editBride.setOnKeyPreImeListener(this);
        editGroom.setOnKeyPreImeListener(this);
        cardView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rawWidth = cardView.getWidth();
                        rawHeight = cardView.getHeight();
                    }
                });
        setImgCode();
    }

    private void initLoad() {
        deleteFiles=new ArrayList<>();
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(layoutContent)
                .setOnNextListener(new SubscriberOnNextListener<NotePoster>() {

                    @Override
                    public void onNext(NotePoster notePoster) {
                        setNotePoster(notePoster);
                    }
                })
                .build();
        Observable<NotePoster> obb = NoteApi.getNotePoster(id);
        obb.subscribe(loadSubscriber);
    }

    private void setNotePoster(NotePoster notePoster) {
        if (notePoster == null || notePoster.getId() == 0) {
            return;
        }
        cardView.setVisibility(View.VISIBLE);
        brideName = notePoster.getBrideName();
        groomName = notePoster.getGroomName();

        if (TextUtils.isEmpty(brideName)) {
            brideName = "新娘名称";
        }

        if (TextUtils.isEmpty(groomName)) {
            groomName = "新郎名称";
        }
        content = notePoster.getContent();
        if (notePoster.getPosterText() != null) {
            desList.addAll(notePoster.getPosterText());
        }
        if (notePoster.getCover() != null && notePoster.getCover()
                .getPhoto() != null) {
            cover = notePoster.getCover()
                    .getPhoto()
                    .getImagePath();
        }
        imgCodeUrl = notePoster.getQrcode();
        initWidget();
    }

    @OnClick(R2.id.tv_share)
    void onShare() {
        NoteCreateWeddingPosterActivityPermissionsDispatcher.onShareLocalWithCheck(this);
    }

    @OnClick(R2.id.tv_save)
    void onSave() {
        NoteCreateWeddingPosterActivityPermissionsDispatcher.onSaveLocalWithCheck(this);
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    public void onSaveLocal() {
        onSavePrepare();
        int width = 750;
        float rate = rawHeight * 1.0f / rawWidth;
        int height = (int) (width * rate);
        FileUtil.saveImageToLocalFile(this,
                ImageUtil.getViewScreenShoot(cardView, width, height),
                new Action1<String>() {
                    @Override
                    public void call(String path) {
                        onSaveNext();
                    }
                });
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    public void onShareLocal() {
        onSavePrepare();
        final Bitmap bitmap = getCardScreenShot();
        if (bitmap == null) {
            return;
        }
        FileUtil.saveImageToLocalFileWithOutNotify(bitmap, new Action1<String>() {
            @Override
            public void call(String path) {
                onSaveNext();
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                deleteFiles.add(path);
                ShareDialogUtil.onLocalImageShare(NoteCreateWeddingPosterActivity.this,
                        bitmap,
                        path);
            }
        });
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void showRationaleForStorage(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    private void onSavePrepare() {
        if (!TextUtils.isEmpty(editBride.getText())) {
            tvBrideName.setText(editBride.getText());
        }
        if (!TextUtils.isEmpty(editGroom.getText())) {
            tvGroomName.setText(editGroom.getText());
        }
        imgEditName.setVisibility(View.GONE);
        imgDesRefresh.setVisibility(View.GONE);
        cardView.setRadius(0F);
        cardView.requestLayout();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onSaveNext() {
        progressBar.setVisibility(View.GONE);
        imgEditName.setVisibility(View.VISIBLE);
        imgDesRefresh.setVisibility(View.VISIBLE);
        cardView.setRadius(CommonUtil.dp2px(NoteCreateWeddingPosterActivity.this, 4));
        cardView.requestLayout();
    }

    private Bitmap getCardScreenShot() {
        int width = 750;
        float rate = rawHeight * 1.0f / rawWidth;
        int height = (int) (width * rate);
        return ImageUtil.getViewScreenShoot(cardView, width, height);
    }

    public void setImgCode() {
        int imgWidth = CommonUtil.dp2px(this, 70);
        Observable<Bitmap> observable = ZxingUtil.createQRImage1(imgCodeUrl,
                imgWidth,
                imgWidth,
                null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        if (bitmap != null) {
                            imgCode.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    @OnClick(R2.id.img_des_refresh)
    void onDesRefresh() {
        currentDesPosition++;
        int position = currentDesPosition % desList.size();
        tvDes.setText(desList.get(position));
    }


    @OnClick(R2.id.layout_name)
    void onNameEdit() {
        showInputMethod(R2.id.et_bride);
    }

    @OnClick(R2.id.tv_confirm)
    void onNameConfirm() {
        if (!TextUtils.isEmpty(editGroom.getText())) {
            tvGroomName.setText(editGroom.getText());
        } else {
            Toast.makeText(this, "请输入新郎姓名", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!TextUtils.isEmpty(editBride.getText())) {
            tvBrideName.setText(editBride.getText());
        } else {
            Toast.makeText(this, "请输入新娘姓名", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        hideEditNameLayout();
    }

    private void showInputMethod(int id) {
        if (layoutEditName.getVisibility() != View.VISIBLE) {
            layoutEditName.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutEditName.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
        IBinder token = null;
        switch (id) {
            case R2.id.et_bride:
                token = editBride.getWindowToken();
                editBride.requestFocus();
                break;
            case R2.id.et_groom:
                token = editGroom.getWindowToken();
                editGroom.requestFocus();
                break;
            default:
                break;
        }

        if (token != null) {
            inputManager.toggleSoftInputFromWindow(token,
                    InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void hideEditNameLayout() {
        IBinder token = null;
        if (editBride.hasFocus()) {
            token = editBride.getWindowToken();
        }
        if (editGroom.hasFocus()) {
            token = editGroom.getWindowToken();
        }
        if (token != null) {
            inputManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        layoutEditName.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutEditName.setVisibility(View.GONE);
            }
        }, 100);
    }

    private void drawTriangle() {
        int width = coverWidth;
        int height = (int) (Math.tan(Math.PI * 8 * 1.0D / 180) * coverWidth);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ContextCompat.getColor(this, R.color.colorWhite));
        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(0, height);
        path.lineTo(width, height);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
        imgTriangle.setImageBitmap(bitmap);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id = v.getId();
        switch (id) {
            case R2.id.et_bride:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(editGroom);
                    onNameConfirm();
                }
                break;
            case R2.id.et_groom:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    showInputMethod(R2.id.et_groom);
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NoteCreateWeddingPosterActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscriber);
        if(!CommonUtil.isCollectionEmpty(deleteFiles)){
            for(String filePath:deleteFiles){
                FileUtil.deleteFile(filePath);
            }
        }
        super.onFinish();
    }

    @Override
    public void onKeyPreIme(int viewId, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            layoutEditName.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutEditName.setVisibility(View.GONE);
                }
            }, 100);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }
}
