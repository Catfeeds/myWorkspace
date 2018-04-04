package com.hunliji.hljvideolibrary.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.player.EasyVideoCallback;
import com.hunliji.hljvideolibrary.player.EasyVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoPreviewActivity extends HljBaseNoBarActivity implements EasyVideoCallback {

    @BindView(R2.id.btn_close)
    ImageButton btnClose;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.player)
    EasyVideoPlayer player;
    @BindView(R2.id.action_layout)
    LinearLayout actionLayout;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        uri = getIntent().getParcelableExtra("uri");
        if (uri != null && uri.getScheme() != null && (uri.getScheme()
                .equals("http") || uri.getScheme()
                .equals("https"))) {
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        setActionBarPadding(actionLayout);
        setSwipeBackEnable(false);

        player.setCallback(this);
        if (uri == null) {
            Toast.makeText(this, "视频资源错误", Toast.LENGTH_SHORT)
                    .show();
        } else {
            player.setSource(uri);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onBackPressed() {
        player.release();
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @OnClick(R2.id.btn_close)
    void onClose() {
        onBackPressed();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
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
}
