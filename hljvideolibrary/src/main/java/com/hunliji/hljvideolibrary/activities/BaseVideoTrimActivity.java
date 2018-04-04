package com.hunliji.hljvideolibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HLjProgressDialog;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.interfaces.HljVideoTrimListener;
import com.hunliji.hljvideolibrary.view.HljVideoTrimView;

import java.util.Formatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class BaseVideoTrimActivity extends HljBaseNoBarActivity implements HljVideoTrimListener {

    @BindView(R2.id.video_trim_view)
    protected HljVideoTrimView videoTrimView;
    @BindView(R2.id.tv_cancel)
    TextView tvCancel;
    @BindView(R2.id.tv_current_video_length)
    TextView tvCurrentVideoLength;
    @BindView(R2.id.tv_finish)
    TextView tvFinish;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;

    public static final String ARGS_MIN_VIDEO_LENGTH = "min";
    public static final String ARGS_MAX_VIDEO_LENGTH = "max";
    public static final String ARGS_URI_VIDEO_PATH = "uri";

    protected Uri uri;

    protected int minDurationInSeconds;
    protected int maxDurationInSeconds;
    private Subscriber<Uri> trimSub;
    private HLjProgressDialog progressDialog;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_video_trim);
        ButterKnife.bind(this);

        setSwipeBackEnable(false);

        initValues();
        initVideo();
    }

    protected void initValues() {
        minDurationInSeconds = getIntent().getIntExtra(ARGS_MIN_VIDEO_LENGTH, 1);
        maxDurationInSeconds = getIntent().getIntExtra(ARGS_MAX_VIDEO_LENGTH, 6);
        uri = getIntent().getParcelableExtra(ARGS_URI_VIDEO_PATH);
    }

    private void initVideo() {
        progressDialog = DialogUtil.getProgressDialog(this);
        progressDialog.setMessage("正在处理...");
        progressDialog.setCancelable(false);

        videoTrimView.setVideoSeekListener(this);
        videoTrimView.setDestinationPath(FileUtil.getVideoAlbumDir()
                .getPath() + "/");
        videoTrimView.initWithParams(uri, minDurationInSeconds, maxDurationInSeconds);
    }

    @OnClick(R2.id.tv_finish)
    void onConfirmTrim() {
        if (resultUri == null) {
            progressDialog.show();
            trim();
        } else {
            onTrimDone(resultUri);
        }
    }

    private void trim() {
        if (trimSub != null) {
            trimSub.unsubscribe();
            trimSub = null;
        }
        trimSub = new Subscriber<Uri>() {
            @Override
            public void onCompleted() {
                progressDialog.cancel();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(BaseVideoTrimActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                progressDialog.cancel();
                BaseVideoTrimActivity.super.onBackPressed();
            }

            @Override
            public void onNext(Uri uri) {
                resultUri = uri;
                if (resultUri != null) {
                    onTrimDone(resultUri);
                }
            }
        };
        videoTrimView.setTrimSubscriber(trimSub);
        videoTrimView.startTrim();
    }

    protected void onTrimDone(Uri uri) {
        Intent intent = getIntent();
        intent.putExtra(ARGS_URI_VIDEO_PATH, uri);
        setResult(RESULT_OK, intent);
        BaseVideoTrimActivity.super.onBackPressed();
    }

    @OnClick(R2.id.tv_cancel)
    void onCancel() {
        onBackPressed();
    }

    @Override
    public void onSeeking(
            float startPositionInMs, float endPositionInMs, float videoTotalMs) {
        this.resultUri = null;
        setTvCurrentVideoLengthText(tvCurrentVideoLength, videoTotalMs);

        //        tvCurrentVideoLength.setText(String.valueOf(videoTotalMs / 1000f));
        //        tvCancel.setText(String.valueOf(startPositionInMs / 1000f));
        //        tvFinish.setText(String.valueOf(endPositionInMs / 1000f));
    }

    protected void setTvCurrentVideoLengthText(TextView tvLength, float videoTotalMs) {
        Formatter mFormatter = new Formatter();
        int seconds = (int) (videoTotalMs / 1000);
        String secondsStr = mFormatter.format("%3d", seconds)
                .toString();
        tvCurrentVideoLength.setText(secondsStr);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(trimSub);
        super.onFinish();
    }

    private String stringForTime(int timeInSeconds) {
        int seconds = timeInSeconds % 60;
        int minutes = (timeInSeconds / 60) % 60;
        int hours = timeInSeconds / 3600;

        Formatter mFormatter = new Formatter();
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else if (minutes > 0) {
            return mFormatter.format("%02d:%02d", minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%2d", seconds)
                    .toString();
        }
    }
}
