package com.hunliji.hljvideolibrary.activities;

import android.app.Dialog;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HLjProgressDialog;
import com.hunliji.hljvideolibrary.HljVideoTrimmer;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.utils.TrimVideoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class VideoTrimActivity extends HljBaseNoBarActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.video_trimmer)
    HljVideoTrimmer videoTrimmer;
    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private String uriPath;
    private Dialog confirmBackDlg;
    public static final String KEY_VIDEO_URI = "uri";
    public static final String KEY_VIDEO_IMPORT_LENGTH_MAX = "video_import_length_max";
    public static final String KEY_VIDEO_LENGTH_MAX = "video_length_max";
    public static final String KEY_VIDEO_LENGTH_MIN = "video_length_min";
    private int videoImportLengthMax;
    private int videoLengthMax;
    private int videoLengthMin;
    private HLjProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trime);
        ButterKnife.bind(this);
        setActionBarPadding(findViewById(R.id.action_holder_layout));

        initValues();
    }

    private void initValues() {
        uriPath = getIntent().getStringExtra(KEY_VIDEO_URI);
        videoImportLengthMax = getIntent().getIntExtra(KEY_VIDEO_IMPORT_LENGTH_MAX, 600);
        videoLengthMax = getIntent().getIntExtra(KEY_VIDEO_LENGTH_MAX, 180);
        videoLengthMin = getIntent().getIntExtra(KEY_VIDEO_LENGTH_MIN, 5);

        // 检查视频长度，允许导入5秒~10分钟的视频
        if (!TextUtils.isEmpty(uriPath)) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this, Uri.parse(uriPath));
            long duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION));
            if (duration < videoLengthMin * 1000) {
                Dialog errorDlg = DialogUtil.createSingleButtonDialog(this,
                        getString(R.string.msg_error_video_to_short),
                        getString(R.string.label_confirm),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoTrimmer.destroy();
                                VideoTrimActivity.super.onBackPressed();
                            }
                        });
                errorDlg.setCancelable(false);
                errorDlg.setCanceledOnTouchOutside(false);
                errorDlg.show();
            } else if (duration > videoImportLengthMax * 1000) {
                Dialog errorDlg = DialogUtil.createSingleButtonDialog(this,
                        getString(R.string.msg_error_video_to_long),
                        getString(R.string.label_confirm),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoTrimmer.destroy();
                                VideoTrimActivity.super.onBackPressed();
                            }
                        });
                errorDlg.setCancelable(false);
                errorDlg.setCanceledOnTouchOutside(false);
                errorDlg.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (confirmBackDlg != null && confirmBackDlg.isShowing()) {
            confirmBackDlg.cancel();
        }
        if (confirmBackDlg == null) {
            confirmBackDlg = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_confirm_quit_video_trim),
                    getString(R.string.label_confirm),
                    getString(R.string.label_continue_edit),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoTrimmer.destroy();
                            VideoTrimActivity.super.onBackPressed();
                        }
                    },
                    null);
        }
        confirmBackDlg.show();
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_confirm)
    void onConfirmTrim() {
        if (progressDialog == null) {
            progressDialog = DialogUtil.getProgressDialog(this);
            progressDialog.setMessage("正在处理...");
            progressDialog.setCancelable(false);
        }
        if (!isFinishing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        videoTrimmer.confirmTrim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoTrimmer != null) {
            videoTrimmer.destroy();
        }
    }

    private void initViews() {
        setSwipeBackEnable(false);

        videoTrimmer.setMaxDuration(videoLengthMax);
        videoTrimmer.setMinDuration(videoLengthMin);
        videoTrimmer.setSubscriber(new Subscriber<Uri>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(VideoTrimActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                progressDialog.cancel();
                videoTrimmer.destroy();
                VideoTrimActivity.super.onBackPressed();
            }

            @Override
            public void onNext(Uri uri) {
                progressDialog.cancel();
                if (uri != null) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Note.CREATE_NOTE)
                            .withParcelable("uri", uri)
                            .withString("coverPath", TrimVideoUtils.getVideoCoverPathFromFrame(uri))
                            .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                            .navigation(VideoTrimActivity.this, new NavCallback() {
                                @Override
                                public void onArrival(Postcard postcard) {
                                    videoTrimmer.destroy();
                                    finish();
                                }
                            });
                }
            }
        });
        videoTrimmer.setVideoURI(Uri.parse(uriPath));
        videoTrimmer.setDestinationPath(FileUtil.getVideoAlbumDir()
                .getPath() + "/");
    }


}
