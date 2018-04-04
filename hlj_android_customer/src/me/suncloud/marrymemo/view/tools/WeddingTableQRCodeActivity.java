package me.suncloud.marrymemo.view.tools;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 查座二维码
 * Created by chen_bin on 2017/11/22 0022.
 */
@RuntimePermissions
public class WeddingTableQRCodeActivity extends HljBaseActivity {

    @BindView(R.id.img_qr_code)
    ImageView imgQRCode;

    private Dialog progressDialog;
    private Bitmap codeBitmap;
    private String url;
    private int codeSize;

    private Subscription getQRCodeSub;

    public final static String ARG_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_table_qr_code);
        ButterKnife.bind(this);
        initValues();
        getQRCode();
    }

    private void initValues() {
        codeSize = CommonUtil.dp2px(this, 200);
        url = getIntent().getStringExtra(ARG_URL);
    }

    private void getQRCode() {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (getQRCodeSub == null || getQRCodeSub.isUnsubscribed()) {
            getQRCodeSub = Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    Bitmap bitmap = null;
                    try {
                        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
                        qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
                        qrParam.put(EncodeHintType.MARGIN, 0);
                        BitMatrix bitMatrix = new MultiFormatWriter().encode(url,
                                BarcodeFormat.QR_CODE,
                                codeSize,
                                codeSize,
                                qrParam);
                        int w = bitMatrix.getWidth();
                        int h = bitMatrix.getHeight();
                        int[] data = new int[w * h];
                        for (int y = 0; y < h; y++) {
                            for (int x = 0; x < w; x++) {
                                if (bitMatrix.get(x, y))
                                    data[y * w + x] = 0xff000000;
                                else
                                    data[y * w + x] = -1;
                            }
                        }
                        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(data, 0, w, 0, 0, w, h);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            if (bitmap != null) {
                                codeBitmap = bitmap;
                                imgQRCode.setImageBitmap(bitmap);
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        WeddingTableQRCodeActivityPermissionsDispatcher.saveQRCodeWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void saveQRCode() {
        if (codeBitmap == null) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        FileUtil.saveImageToLocalFile(this, codeBitmap, new Action1<String>() {
            @Override
            public void call(String s) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (codeBitmap != null && !codeBitmap.isRecycled()) {
                    codeBitmap.recycle();
                }
            }
        });
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_external_storage___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WeddingTableQRCodeActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getQRCodeSub);
    }
}