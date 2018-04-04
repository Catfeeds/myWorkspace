package com.hunliji.hljvideolibrary.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljvideolibrary.HljVideo;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.utils.Degrees;
import com.hunliji.hljvideolibrary.utils.FileUtils;
import com.hunliji.hljvideolibrary.utils.TrimVideoUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;

@RuntimePermissions
public class TakingVideoActivity extends Activity implements SurfaceHolder.Callback {
    @BindView(R2.id.surface_view)
    SurfaceView surfaceView;
    @BindView(R2.id.btn_close)
    ImageButton btnClose;
    @BindView(R2.id.btn_switch_camera)
    ImageButton btnSwitchCamera;
    @BindView(R2.id.btn_flash)
    ImageButton btnFlash;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.tv_video_hint)
    TextView tvVideoHint;
    @BindView(R2.id.progress_bar_layout)
    RelativeLayout progressBarLayout;
    @BindView(R2.id.btn_take_action)
    ImageButton btnTakeAction;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_import_video)
    TextView tvImportVideo;
    @BindView(R2.id.btn_confirm)
    ImageButton btnConfirm;
    @BindView(R2.id.btn_retake)
    Button btnRetake;
    @BindView(R2.id.bottom_action_layout)
    RelativeLayout bottomActionLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.frame_divider_layout)
    FrameLayout frameDividerLayout;

    private boolean isRecording = false; // 是否正在拍摄
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private MediaRecorder mediaRecorder;
    private String filePath;
    private int currentCameraId;
    private Handler handler;
    private Dialog confirmBackDlg;

    private final static float VIDEO_LIMIT_SECONDS_MAX = 60.0f; // 单位，秒
    private final static float VIDEO_LIMIT_SECONDS_MIN = 5.0f;
    private final static int UPDATE_INTERVAL_MILLS = 100; // 单位，毫秒
    private final static int START_RECORDER_OFFSET = 1000;
    private final static int VIDEO_QUALITY = CamcorderProfile.QUALITY_720P;
    private double videoLength = 0.0f; // 单位，秒
    private int mDisplayOrientation;
    private boolean haveTakeVideoHint = false;
    private long startMills;
    private Subscription rxBusEventSub;
    private Camera.Size viewSize;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isRecording) {
                return;
            }
            long now = System.currentTimeMillis();
            long videoLengthMills = now - startMills;

            BigDecimal b1 = new BigDecimal(videoLengthMills);
            BigDecimal b2 = new BigDecimal(1000);
            videoLength = b1.divide(b2, 4, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            String timeStr = String.format("%.1f", videoLength);
            tvTime.setText(timeStr + "秒");

            progressBar.setMax((int) (VIDEO_LIMIT_SECONDS_MAX * 1000));
            progressBar.setProgress(Math.round(videoLengthMills));

            if (tvVideoHint.getVisibility() == View.VISIBLE && videoLength >=
                    VIDEO_LIMIT_SECONDS_MIN) {
                tvVideoHint.setVisibility(View.GONE);
            }
            if (videoLength >= VIDEO_LIMIT_SECONDS_MAX) {
                stopRecording();
            } else if (handler != null) {
                handler.postDelayed(this, UPDATE_INTERVAL_MILLS);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_video);
        ButterKnife.bind(this);
        initViews();
        registerRxBusEvent();
    }

    private void initViews() {
        handler = new Handler();
        showVideoHint();
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CREATE_NOTE_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    private void showVideoHint() {
        SharedPreferences preferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        haveTakeVideoHint = preferences.getBoolean(HljVideo.PrefKeys.HINT_HAVE_TAKE_VIDEO,
                haveTakeVideoHint);
        if (!haveTakeVideoHint) {
            tvVideoHint.setVisibility(View.VISIBLE);
        } else {
            tvVideoHint.setVisibility(View.GONE);
        }
    }

    private void setShowVideoHint() {
        tvVideoHint.setText(R.string.msg_video_to_short);
        tvVideoHint.setVisibility(View.VISIBLE);

        if (!haveTakeVideoHint) {
            haveTakeVideoHint = true;
            SharedPreferences preferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                    Context.MODE_PRIVATE);
            preferences.edit()
                    .putBoolean(HljVideo.PrefKeys.HINT_HAVE_TAKE_VIDEO, haveTakeVideoHint)
                    .apply();
        }
    }

    private void initCamera() {
        mHolder = surfaceView.getHolder();

        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private boolean prepareVideoRecorder() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        if (mCamera == null) {
            return false;
        }
        mCamera.stopPreview();
        try {
            mCamera.autoFocus(null);
        } catch (RuntimeException e) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                mCamera.setParameters(parameters);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        mediaRecorder = new MediaRecorder();
        setCameraDisplayOrientation(mCamera.getParameters());
        //        setCameraDisplayOrientation(currentCameraId, mCamera);
        mCamera.unlock();

        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile profile = null;
        try {
            profile = CamcorderProfile.get(currentCameraId, VIDEO_QUALITY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile == null) {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncodingBitRate(24000);
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.setAudioSamplingRate(16000);
            mediaRecorder.setVideoFrameRate(30);
            if (viewSize != null) {
                mediaRecorder.setVideoSize(viewSize.width, viewSize.height);
            }
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            mediaRecorder.setProfile(profile);
        }

        mediaRecorder.setOrientationHint(mDisplayOrientation);
        mediaRecorder.setPreviewDisplay(mHolder.getSurface());

        filePath = FileUtil.createVideoFile()
                .getAbsolutePath();

        mediaRecorder.setOutputFile(filePath);
        //        mediaRecorder.setVideoEncodingBitRate(716800);
        //        mediaRecorder.setAudioEncodingBitRate(131072);
        //        mediaRecorder.setAudioSamplingRate(49152);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("Preparing Camera",
                    "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("Preparing Camera", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId);
        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_fail_to_open_camera, Toast.LENGTH_SHORT)
                    .show();
        }
        return c;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            if (mCamera != null) {
                mCamera.lock();           // lock camera for later use
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(rxBusEventSub);
        }
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(filePath)) {
            if (confirmBackDlg != null && confirmBackDlg.isShowing()) {
                confirmBackDlg.cancel();
            }
            if (confirmBackDlg == null) {
                confirmBackDlg = DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.msg_confirm_quit_video_take),
                        getString(R.string.label_confirm),
                        getString(R.string.label_continue),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quitTakingVideo();
                            }
                        },
                        null);
            }
            confirmBackDlg.show();
        } else {
            quitTakingVideo();
        }
    }

    private void quitTakingVideo() {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            filePath = "";
        }
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @OnClick(R2.id.btn_close)
    void onClose() {
        onBackPressed();
    }


    @OnClick(R2.id.btn_take_action)
    void onTakeAction() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        // initialize video camera
        if (prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            startMills = System.currentTimeMillis();
            mediaRecorder.start();

            // 设置隐藏hint
            setShowVideoHint();

            // inform the user that recording has started
            videoLength = 0;
            btnTakeAction.setImageResource(R.mipmap.icon_video_take_stop);
            btnTakeAction.setVisibility(View.VISIBLE);
            btnRetake.setVisibility(View.INVISIBLE);
            tvImportVideo.setVisibility(View.INVISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);
            btnFlash.setVisibility(View.INVISIBLE);
            btnSwitchCamera.setVisibility(View.INVISIBLE);
            btnClose.setVisibility(View.INVISIBLE);
            isRecording = true;
            handler.postDelayed(runnable, START_RECORDER_OFFSET);
        } else {
            releaseMediaRecorder();
        }
    }

    private void stopRecording() {
        if (videoLength < VIDEO_LIMIT_SECONDS_MIN) {
            // 摄录时间小于5秒，不予许停止
            //            Toast.makeText(this, R.string.msg_video_to_short, Toast.LENGTH_SHORT)
            //                    .show();
            return;
        }
        // stop recording and release camera
        //        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        if (mCamera != null) {
            mCamera.lock();         // take camera access back from MediaRecorder
        }

        isRecording = false;
        btnTakeAction.setVisibility(View.INVISIBLE);
        btnRetake.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);

        handler.removeCallbacks(runnable);
        stopFlash();
    }

    private void stopFlash() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getFlashMode()
                .equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            onCheckFlash();
        }
    }

    @OnClick(R2.id.btn_switch_camera)
    void onSwitchCamera() {
        if (isRecording) {
            return;
        }
        if (mCamera == null) {
            Toast.makeText(this, R.string.msg_fail_to_open_camera, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mCamera.stopPreview();
        mCamera.release();

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        mCamera = getCameraInstance();
        setCameraDisplayOrientation(mCamera.getParameters());
        //        setCameraDisplayOrientation(currentCameraId, mCamera);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
        }
        mCamera.startPreview();
    }

    @OnClick(R2.id.btn_flash)
    void onCheckFlash() {
        if (mCamera == null) {
            Toast.makeText(this, R.string.msg_fail_to_open_camera, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mCamera.stopPreview();
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getFlashMode()
                .equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            btnFlash.setImageResource(R.mipmap.icon_flash_off_white_44_44);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            btnFlash.setImageResource(R.mipmap.icon_flash_on_white_44_44);
        }
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    @OnClick(R2.id.tv_import_video)
    void onImportVideo() {
        TakingVideoActivityPermissionsDispatcher.importVideoWithCheck(this);
    }

    @OnClick(R2.id.btn_confirm)
    void onConfirmTakingVideo() {
        if (!TextUtils.isEmpty(filePath)) {
            Uri uri = Uri.parse(filePath);
            //更新媒体库
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Video.Media.TITLE, "My video title");
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, filePath);
            Uri uri1 = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    values);
            Intent infoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            infoIntent.setData(uri1);
            sendBroadcast(infoIntent);

            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Note.CREATE_NOTE)
                    .withParcelable("uri", Uri.parse(filePath))
                    .withString("coverPath", TrimVideoUtils.getVideoCoverPathFromFrame(uri))
                    .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                    .navigation(this, new NavCallback() {
                        @Override
                        public void onArrival(Postcard postcard) {
                            finish();
                        }
                    });

        }
    }

    @OnClick(R2.id.btn_retake)
    void onRetake() {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            filePath = "";
        }
        isRecording = false;
        tvTime.setText("");
        progressBar.setProgress(0);
        btnTakeAction.setImageResource(R.mipmap.icon_video_take_start);
        btnTakeAction.setVisibility(View.VISIBLE);
        btnRetake.setVisibility(View.INVISIBLE);
        tvImportVideo.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.INVISIBLE);
        btnFlash.setVisibility(View.VISIBLE);
        btnSwitchCamera.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == HljVideo.RequestCode.IMPORT_VIDEO) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        filePath = "";
                    }
                    Intent intent = new Intent(this, VideoTrimActivity.class);
                    intent.putExtra(VideoTrimActivity.KEY_VIDEO_URI,
                            FileUtils.getPath(this, selectedUri));
                    startActivity(intent);
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        filePath = "";
                    }
                } else {
                    Toast.makeText(this,
                            R.string.msg_cannot_retrieve_selected_video,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void importVideo() {
        Intent intent = new Intent();
        intent.setTypeAndNormalize("video/mp4");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                HljVideo.RequestCode.IMPORT_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        TakingVideoActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 默认使用后置镜头
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = getCameraInstance();
        mHolder = holder;
        if (mCamera == null) {
            Toast.makeText(this, R.string.msg_fail_to_open_camera, Toast.LENGTH_SHORT)
                    .show();
            quitTakingVideo();
            return;
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
        if (mHolder.getSurface() == null || mCamera == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.getPictureSize();
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (sizeList != null && !sizeList.isEmpty()) {
            viewSize = getOptimalPreviewSize(sizeList,
                    Math.max(width, height),
                    Math.min(width, height));

            parameters.setPreviewSize(viewSize.width, viewSize.height);
            mCamera.setParameters(parameters);
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mDisplayOrientation = result;
        camera.setDisplayOrientation(mDisplayOrientation);
    }


    private void setCameraDisplayOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        final int deviceOrientation = Degrees.getDisplayRotation(this);
        mDisplayOrientation = Degrees.getDisplayOrientation(info.orientation,
                deviceOrientation,
                info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        Log.d("CameraFragment",
                String.format("Orientations: Sensor = %d˚, Device = %d˚, Display = %d˚",
                        info.orientation,
                        deviceOrientation,
                        mDisplayOrientation));

        int previewOrientation;
        int jpegOrientation;
        if (isChromium()) {
            previewOrientation = 0;
            jpegOrientation = 0;
        } else {
            jpegOrientation = previewOrientation = mDisplayOrientation;

            if (Degrees.isPortrait(deviceOrientation) && info.facing == Camera.CameraInfo
                    .CAMERA_FACING_FRONT) {
                previewOrientation = Degrees.mirror(mDisplayOrientation);
            }
        }

        parameters.setRotation(jpegOrientation);
        mCamera.setDisplayOrientation(previewOrientation);
    }

    public static boolean isChromium() {
        return Build.BRAND.equalsIgnoreCase("chromium") && Build.MANUFACTURER.equalsIgnoreCase(
                "chromium");
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(rxBusEventSub);
    }
}
