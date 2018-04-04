package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.SplashUtil;

/**
 * Created by mo_yu on 2016/4/18. 启动页
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            Intent i;
            i = new Intent(SplashActivity.this, HomeActivity.class);
            SplashActivity.this.startActivity(i);
            SplashActivity.this.finish();
            SplashActivity.this.overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(startRunnable, 2000);
        SplashUtil.getInstance()
                .loadSplash(this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        if (isFinishing()) {
                            return;
                        }
                        ImageView imageView = (ImageView) findViewById(R.id.image_view);
                        final Poster poster = SplashUtil.getInstance()
                                .getPoster();
                        if (poster != null && poster.getId() > 0) {
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.removeCallbacks(startRunnable);
                                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                                    i.putExtra("poster", poster);
                                    startActivity(i);
                                    finish();
                                    SplashActivity.this.overridePendingTransition(R.anim
                                                    .activity_anim_default,
                                            R.anim.activity_anim_default);
                                }
                            });
                        }
                        handler.removeCallbacks(startRunnable);
                        imageView.setImageBitmap((Bitmap) obj);
                        View splash = findViewById(R.id.splash);
                        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,
                                R.anim.fade_out);
                        animation.setFillAfter(true);
                        splash.startAnimation(animation);
                        handler.postDelayed(startRunnable, 3000);
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                });
    }

    public void onSkip(View view) {
        handler.removeCallbacks(startRunnable);
        Intent i;
        i = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(i);
        this.finish();
        SplashActivity.this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    @Override
    protected void onResume() {
        WebSocket.getInstance()
                .socketConnect(this);
        super.onResume();
    }
}
