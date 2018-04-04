package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

public class TakeVideoActivity extends Activity implements SurfaceHolder.Callback {
    private ImageButton start;
    private TextView time;
    private MediaRecorder mediarecorder;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private Handler handler;
    private boolean isStop;
    private Camera camera;
    private int seconds;
    private int minutes;
    private int hours;
    private int videoTime;
    private View okView;
    private String path;
    private Point point;
    private int limitTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_take_video);
        limitTime = getIntent().getIntExtra("limit", 0);
        point = JSONUtil.getDeviceSize(this);
        handler = new Handler();
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        start = (ImageButton) this.findViewById(R.id.start);
        time = (TextView) findViewById(R.id.time);
        okView = findViewById(R.id.ok);
        okView.setVisibility(View.INVISIBLE);
        okView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (!JSONUtil.isEmpty(path)) {
                    intent.putExtra("path", path);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        time.setText(hours / 10 + "" + hours % 10 + ":" + minutes / 10 + "" + minutes % 10 + ":"
                + seconds / 10 + seconds % 10);
        start.setOnClickListener(new TestVideoListener());
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        ViewGroup.MarginLayoutParams params;
        params = (MarginLayoutParams) surfaceview.getLayoutParams();
        params.height = Math.round(point.x * 640 / 480);
        SurfaceHolder holder = surfaceview.getHolder();
        holder.addCallback(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

    }

    public boolean prepareVideoRecorder() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = Camera.open();
        try {
            camera.autoFocus(null);
        } catch (RuntimeException e) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(parameters);
        }
        mediarecorder = new MediaRecorder();
        camera.setDisplayOrientation(90);
        camera.unlock();
        mediarecorder.setCamera(camera);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            mediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        path = FileUtil.createVideoFile()
                .getAbsolutePath();
        mediarecorder.setOutputFile(path);
        mediarecorder.setVideoSize(640, 480);
        mediarecorder.setVideoEncodingBitRate(716800);
        mediarecorder.setAudioEncodingBitRate(131072);
        mediarecorder.setAudioSamplingRate(49152);
        return true;

    }

    private Runnable runnable = new Runnable() {
        public void run() {
            videoTime++;
            seconds++;
            if (seconds >= 60) {
                seconds = 0;
                minutes++;
                if (minutes >= 60) {
                    minutes = 0;
                    hours++;
                }
            }
            time.setText(hours / 10 + "" + hours % 10 + ":" + minutes / 10 + "" + minutes % 10 +
                    ":" + seconds / 10 + seconds % 10);
            if (limitTime != 0 && videoTime >= limitTime) {
                mediarecorder.stop();
                mediarecorder.release();
                mediarecorder = null;
                start.setImageResource(R.drawable.icon_triangle_right_white);
                isStop = true;
                okView.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void releaseMediaRecorder() {
        if (mediarecorder != null) {
            mediarecorder.reset();
            mediarecorder.release();
            mediarecorder = null;
            camera.lock();
        }

    }

    public void takeVideo() {
        if (mediarecorder == null) {
            if (!JSONUtil.isEmpty(path)) {

                final Dialog dialog = new Dialog(this, R.style.BubbleDialogTheme);
                View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.img_notice);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
                Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
                tvCancel.setVisibility(View.VISIBLE);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                tvMsg.setText(R.string.label_rephotograph);
                imageView.setImageResource(R.drawable.icon_notice_bell_primary);

                tvConfirm.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        File file = new File(path);
                        if (file.exists()) {
                            new File(path).delete();
                        }
                        time.setText("00:00:00");
                        if (prepareVideoRecorder()) {
                            try {
                                mediarecorder.prepare();
                                mediarecorder.start();
                                videoTime = 0;
                                seconds = 0;
                                minutes = 0;
                                hours = 0;
                                start.setImageResource(R.drawable.icon_round_rect_red);
                                handler.postDelayed(runnable, 1000);
                                okView.setVisibility(View.INVISIBLE);
                                isStop = false;
                            } catch (IllegalStateException e) {
                                releaseMediaRecorder();
                            } catch (IOException e) {
                                releaseMediaRecorder();
                            }
                        }
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.setContentView(view);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                dialog.show();
            } else {
                if (prepareVideoRecorder()) {
                    try {
                        mediarecorder.prepare();
                        mediarecorder.start();
                        start.setImageResource(R.drawable.icon_round_rect_red);
                        handler.postDelayed(runnable, 1000);
                        okView.setVisibility(View.INVISIBLE);
                        isStop = false;
                    } catch (IllegalStateException e) {
                        releaseMediaRecorder();
                    } catch (IOException e) {
                        releaseMediaRecorder();
                    }
                }
            }
        } else {
            mediarecorder.stop();
            mediarecorder.release();
            mediarecorder = null;
            start.setImageResource(R.drawable.icon_triangle_right_white);
            isStop = true;
            okView.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        if (mediarecorder != null) {
            mediarecorder.stop();
            mediarecorder.release();
            mediarecorder = null;
        }
        if (!JSONUtil.isEmpty(path)) {
            if (!isStop) {
                File file = new File(path);
                if (file.exists()) {
                    new File(path).delete();
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
        Camera.Parameters parameters = camera.getParameters();
        parameters.getPictureSize();
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (!sizeList.isEmpty()) {
            if (sizeList.size() > 1) {
                Iterator<Camera.Size> itor = sizeList.iterator();
                while (itor.hasNext()) {
                    Camera.Size cur = itor.next();
                    if (cur.width <= width && cur.height <= height) {
                        width = cur.width;
                        height = cur.height;
                        break;
                    }
                }
            } else {
                width = sizeList.get(0).width;
                height = sizeList.get(0).height;
            }
            parameters.setPreviewSize(width, height);
            camera.setParameters(parameters);
        }
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        camera = Camera.open();// 摄像头的初始化
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        surfaceview = null;
        surfaceHolder = null;
        mediarecorder = null;
    }

    class TestVideoListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == start) {
                takeVideo();
            }
        }
    }


}
