package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hunliji.marrybiz.R;
import com.umeng.analytics.MobclickAgent;


public class VideoViewActivity extends Activity {
    private VideoView video;
    private View progressBar;
    private boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        video = (VideoView) findViewById(R.id.video);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final MediaController mc = new MediaController(this);
        video.setMediaController(mc);
        String path = getIntent().getStringExtra("path");
        boolean orientation = getIntent().getBooleanExtra("orientation", false);
        if (orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            progressBar.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(path));
            MediaController mediaController = new MediaController(this);
            video.setMediaController(mediaController);
            video.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            video.start();
        } else {
            video.setVideoPath(path);
            video.start();
        }

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                onBackPressed();
            }
        });

        video.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                video.stopPlayback();
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        if (video != null && !isFinish) {
            video.stopPlayback();
        }
        isFinish = true;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }


    @Override
    protected void onStart() {
        EasyTracker.getInstance(this)
                .activityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance(this)
                .activityStop(this);
        super.onStop();
    }
}
