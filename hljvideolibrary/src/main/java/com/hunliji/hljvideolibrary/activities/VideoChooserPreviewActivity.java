package com.hunliji.hljvideolibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.models.VideoRxEvent;
import com.hunliji.hljvideolibrary.player.EasyVideoCallback;
import com.hunliji.hljvideolibrary.player.EasyVideoPlayer;

import java.io.File;

import rx.Subscription;

/**
 * Created by wangtao on 2017/7/22.
 */

public class VideoChooserPreviewActivity extends HljBaseNoBarActivity implements EasyVideoCallback {

    private EasyVideoPlayer player;
    private TextView tvMaxSec;
    private LinearLayout editHintLayout;
    private Button btnEdit;
    private Button btnEdit2;
    private Button btnChooseOk;

    private Photo photo;
    private Uri uri;
    private boolean isStartPause;

    private int minDurationInSeconds;
    private int maxDurationInSeconds;

    private Subscription rxSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview___img);
        setActionBarPadding(findViewById(R.id.action_holder_layout));
        setSwipeBackEnable(false);
        initValues();
        initViews();
        registerRxBus();
    }


    private void initValues() {
        minDurationInSeconds = getIntent().getIntExtra(BaseVideoTrimActivity.ARGS_MIN_VIDEO_LENGTH,
                1);
        maxDurationInSeconds = getIntent().getIntExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH,
                6);

        photo = getIntent().getParcelableExtra("photo");
        uri = Uri.fromFile(new File(photo.getImagePath()));
        isStartPause = true;
    }

    private void initViews() {
        player = (EasyVideoPlayer) findViewById(R.id.player);
        tvMaxSec = (TextView) findViewById(R.id.tv_max_sec);
        editHintLayout = (LinearLayout) findViewById(R.id.edit_hint_layout);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit2 = (Button) findViewById(R.id.btn_edit2);
        btnChooseOk = (Button) findViewById(R.id.btn_choose_ok);

        player.setCallback(this);
        if (uri == null) {
            ToastUtil.showToast(this, "视频资源错误", 0);
        } else {
            player.setSource(uri);
        }
        if (photo.getDuration() > maxDurationInSeconds * 1000) {
            editHintLayout.setVisibility(View.VISIBLE);
            btnEdit2.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnChooseOk.setVisibility(View.GONE);
            tvMaxSec.setText(String.valueOf(maxDurationInSeconds));
        } else {
            editHintLayout.setVisibility(View.GONE);
            btnEdit2.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            btnChooseOk.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
        btnEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
        btnChooseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoose();
            }
        });
    }


    private void registerRxBus() {
        if (rxSubscription != null && !rxSubscription.isUnsubscribed()) {
            return;
        }
        rxSubscription = RxBus.getDefault()
                .toObservable(VideoRxEvent.class)
                .subscribe(new RxBusSubscriber<VideoRxEvent>() {
                    @Override
                    protected void onEvent(VideoRxEvent videoRxEvent) {
                        switch (videoRxEvent.getType()) {
                            case VIDEO_CALLBACK:
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void onStarted(final EasyVideoPlayer player) {
        if (isStartPause) {
            isStartPause = false;
            player.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.pause();
                }
            }, 100);
        }

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {

    }


    public void onEdit() {
        Intent intent = new Intent(this, ChosenVideoTrimActivity.class);
        intent.putExtra("photo", photo);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MIN_VIDEO_LENGTH, minDurationInSeconds);
        intent.putExtra(BaseVideoTrimActivity.ARGS_MAX_VIDEO_LENGTH, maxDurationInSeconds);
        startActivity(intent);
    }

    public void onChoose() {
        if (photo.getDuration() > maxDurationInSeconds * 1000) {
            onEdit();
        } else {
            onBackPressed();
            RxBus.getDefault()
                    .post(new VideoRxEvent(VideoRxEvent.RxEventType.VIDEO_CALLBACK, photo));
        }
    }

    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(rxSubscription);
        super.onFinish();
    }
}
