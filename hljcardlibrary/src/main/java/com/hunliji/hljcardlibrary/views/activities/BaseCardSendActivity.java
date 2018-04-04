package com.hunliji.hljcardlibrary.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.ImageInfo;
import com.hunliji.hljcardlibrary.models.SendCardBody;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.MinProgramShareInfo;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PhotoListUploadUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljsharelibrary.utils.ShareMinProgramUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.internal.util.SubscriptionList;

/**
 * Created by  hua_rong  on 2017/09/11.
 * 发送
 */
@RuntimePermissions
public abstract class BaseCardSendActivity extends HljBaseNoBarActivity {

    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.iv_share)
    ImageView ivShare;
    @BindView(R2.id.rl_pic)
    RelativeLayout rlPic;
    @BindView(R2.id.et_share_content)
    EditText etShareContent;
    @BindView(R2.id.ll_pengyou)
    LinearLayout llPengyou;
    @BindView(R2.id.ll_wx)
    LinearLayout llWx;
    @BindView(R2.id.ll_qq)
    LinearLayout llQq;
    @BindView(R2.id.ll_zone)
    LinearLayout llZone;
    @BindView(R2.id.ll_sina)
    LinearLayout llSina;
    @BindView(R2.id.ll_message)
    LinearLayout llMessage;
    @BindView(R2.id.ll_view)
    LinearLayout llView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    private Dialog dialog;
    private Uri cropUri;
    private String picPath;
    protected Card card;
    private HljHttpSubscriber subscriber;
    private String title;
    private String desc;
    private HljRoundProgressDialog progressDialog;
    private SubscriptionList uploadSubscriptions;
    private ArrayList<Photo> photos;
    private HljHttpSubscriber shareInfoSubscriber;
    private int width;
    private int height;
    private String shareLink;
    public MinProgramShareInfo shareInfo;
    protected Handler shareHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onHandShareMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        card = getIntent().getParcelableExtra("card");
        if (card == null) {
            return;
        }
        setContentView(R.layout.activity_card_send___card);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        initValue();
        initView();
        onLoad();
        initNetError();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(llPengyou)
                .tagName("card_share_timeline_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
        HljVTTagger.buildTagger(llWx)
                .tagName("card_share_session_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
        HljVTTagger.buildTagger(llQq)
                .tagName("card_share_qq_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
        HljVTTagger.buildTagger(llZone)
                .tagName("card_share_qqzone_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
        HljVTTagger.buildTagger(llSina)
                .tagName("card_share_weibo_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
        HljVTTagger.buildTagger(llMessage)
                .tagName("card_share_sms_button")
                .dataType("Card")
                .dataId(card.getId())
                .hitTag();
    }

    private void initValue() {
        photos = new ArrayList<>();
        Point point = CommonUtil.getDeviceSize(this);
        width = point.x * 5 / 16;
        height = point.x * 5 / 16;
        rlPic.getLayoutParams().width = width;
        rlPic.getLayoutParams().height = height;
        etTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etTitle.setFocusableInTouchMode(true);
                return false;
            }
        });
    }


    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }


    private void onLoad() {
        Observable<MinProgramShareInfo> observable = CardApi.getShareInfo(card.getId());
        shareInfoSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<MinProgramShareInfo>() {

                    @Override
                    public void onNext(MinProgramShareInfo shareInfo) {
                        BaseCardSendActivity.this.shareInfo = shareInfo;
                        shareLink = shareInfo.getUrl();
                        picPath = shareInfo.getIcon();
                        loadShareImage();
                        showEditContentWithTitle(shareInfo);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        hideKeyboard(null);
                    }
                })
                .setProgressBar(progressBar)
                .setContentView(llView)
                .setEmptyView(emptyView)
                .build();
        observable.subscribe(shareInfoSubscriber);
    }

    private void loadShareImage() {
        CardPage cardPage = card.getFrontPage();
        if (TextUtils.isEmpty(picPath) && cardPage != null) {
            if (!CommonUtil.isCollectionEmpty(cardPage.getImageInfos())) {
                for (ImageInfo imageInfo : cardPage.getImageInfos()) {
                    if (!imageInfo.isVideo()) {
                        picPath = imageInfo.getH5ImagePath();
                        break;
                    }
                }
            }
            if (TextUtils.isEmpty(picPath)) {
                File cardFile = PageImageUtil.getCardThumbFile(this, card.getId());
                if (cardFile != null && cardFile.exists() && cardFile.length() > 0) {
                    picPath = cardFile.getAbsolutePath();
                }
            }
            if (TextUtils.isEmpty(picPath)) {
                Template template = cardPage.getTemplate();
                if (template != null) {
                    picPath = template.getThumbPath();
                }
            }
        }
        if (!TextUtils.isEmpty(picPath)) {
            Glide.with(this)
                    .load(ImagePath.buildPath(picPath)
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(ivShare);
        }
    }

    /**
     * 显示的布局
     */
    protected abstract void initView();

    /**
     * 分享的标题 和内容
     *
     * @param shareInfo
     */
    public abstract void showEditContentWithTitle(MinProgramShareInfo shareInfo);


    @OnClick(R2.id.home)
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @OnClick(R2.id.rl_pic)
    public void showPopup(View view) {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.hlj_dialog_add_menu___cm);
            dialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.findViewById(R.id.action_gallery)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            BaseCardSendActivityPermissionsDispatcher.onReadPhotosWithCheck(
                                    BaseCardSendActivity.this);
                        }
                    });
            dialog.findViewById(R.id.action_camera_photo)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            BaseCardSendActivityPermissionsDispatcher.onTakePhotosWithCheck(
                                    BaseCardSendActivity.this);
                        }
                    });
            Window win = dialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(this);
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        dialog.show();
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

    private enum ShareType {
        WX, PENGYOU, SINA, QQ, ZONE, MESSAGE
    }

    @OnClick(R2.id.ll_message)
    public void onShareMessage() {
        sendShareInfo(ShareType.MESSAGE);
    }

    @OnClick(R2.id.ll_pengyou)
    void onSharePengyou() {
        sendShareInfo(ShareType.PENGYOU);
    }

    @OnClick({R2.id.ll_wx, R2.id.ll_yk_wx})
    void onShareWx() {
        sendShareInfo(ShareType.WX);
    }

    @OnClick(R2.id.ll_sina)
    void onShareSina() {
        sendShareInfo(ShareType.SINA);
    }

    @OnClick({R2.id.ll_qq, R2.id.ll_yk_qq})
    void onShareQQ() {
        sendShareInfo(ShareType.QQ);
    }

    @OnClick(R2.id.ll_zone)
    void onShareZone() {
        sendShareInfo(ShareType.ZONE);
    }

    //点赞dialog
    protected void onPraiseDialog() {

    }

    private void sendShareInfo(final ShareType shareType) {
        if (shareInfo == null || TextUtils.isEmpty(picPath)) {
            return;
        }
        title = etTitle.getText()
                .toString()
                .trim();
        desc = etShareContent.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(BaseCardSendActivity.this, "分享标题不能为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "分享内容不能为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //如果分享的是本地图片，先上传
        if (isLocalPath(picPath)) {
            photos.clear();
            Photo photo = new Photo();
            photo.setImagePath(picPath);
            photos.add(photo);
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = DialogUtil.getRoundProgress(this);
            progressDialog.show();
            if (uploadSubscriptions != null) {
                uploadSubscriptions.clear();
            }
            uploadSubscriptions = new SubscriptionList();
            new PhotoListUploadUtil(this,
                    HljCard.getCardHost(),
                    photos,
                    progressDialog,
                    uploadSubscriptions,
                    new OnFinishedListener() {
                        @Override
                        public void onFinished(Object... objects) {
                            if (!CommonUtil.isCollectionEmpty(photos)) {
                                Photo photo = photos.get(0);
                                if (photo != null) {
                                    picPath = photo.getImagePath();
                                }
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                submit(shareType);
                            }
                        }
                    }).startUpload();
        } else {
            submit(shareType);
        }
    }

    private boolean isLocalPath(String path) {
        return !TextUtils.isEmpty(path) && !path.startsWith("http://") && !path.startsWith(
                "https://");
    }

    private void submit(final ShareType shareType) {
        if (TextUtils.isEmpty(picPath)) {
            ToastUtil.showToast(this, "请设置分享封面图", 0);
            return;
        }
        onPraiseDialog();
        CommonUtil.unSubscribeSubs(subscriber);
        SendCardBody sendCardBody = new SendCardBody();
        sendCardBody.setCardId(card.getId());
        MinProgramShareInfo shareBody = new MinProgramShareInfo(title,
                desc,
                null,
                picPath,
                null,
                null);
        sendCardBody.setShare(shareBody);
        Observable<HljHttpResult> observable = CardApi.sendCard(sendCardBody);
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .build();
        observable.subscribe(subscriber);
        goShare(shareType);
    }


    private void goShare(ShareType shareType) {
        ShareUtil shareUtil = new ShareUtil(this,
                shareLink,
                title,
                desc,
                null,
                picPath,
                shareHandler);
        switch (shareType) {
            case QQ:
                shareUtil.shareToQQ();
                break;
            case ZONE:
                shareUtil.shareToQQZone();
                break;
            case WX:
                if (TextUtils.isEmpty(shareInfo.getProgramPath())) {
                    //普通分享
                    shareUtil.shareToWeiXin();
                } else {
                    //小程序分享
                    shareInfo.setHdImagePath(picPath);
                    ShareMinProgramUtil shareMinProgramUtil = new ShareMinProgramUtil(
                            BaseCardSendActivity.this,
                            shareInfo,
                            null);
                    shareMinProgramUtil.shareToWeiXin();
                }
                break;
            case PENGYOU:
                shareUtil.shareToPengYou();
                break;
            case MESSAGE:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:"));
                intent.putExtra("sms_body",
                        shareUtil.getDescription() + ":" + shareUtil.getWebPath());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "该设备不支持短信。", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case SINA:
                shareUtil.shareToWeiBo();
                break;
        }
    }

    //分享回调
    protected void onHandShareMessage(Message msg) {

    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRationaleCamera(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_camera___cm));
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onRationaleReadExternal(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaseCardSendActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private enum Source {
        GALLERY, CAMERA, CROP
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Source.CROP.ordinal()) {
                if (cropUri == null) {
                    return;
                }
                String path = ImageUtil.getImagePathForUri(cropUri, this);
                if (!TextUtils.isEmpty(path)) {
                    picPath = path;
                    Glide.with(this)
                            .asBitmap()
                            .load(path)
                            .into(ivShare);
                }
                return;
            }
            File file = null;
            if (requestCode == Source.CAMERA.ordinal()) {
                String path = Environment.getExternalStorageDirectory() + File.separator + "temp"
                        + ".jpg";
                file = new File(path);
            }
            if (requestCode == Source.GALLERY.ordinal()) {
                if (data == null) {
                    return;
                }
                String pathName = ImageUtil.getImagePathForUri(data.getData(), this);
                if (pathName != null) {
                    file = new File(pathName);
                }
            }
            if (file != null) {
                showPhotoCrop(file);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showPhotoCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        cropUri = Uri.fromFile(FileUtil.createCropImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Source.CROP.ordinal());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber, uploadSubscriptions, shareInfoSubscriber);
    }
}

