package com.hunliji.hljcommonlibrary.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;


public class HljRoundProgressDialog extends Dialog {
    private long max;
    private View completeView;
    private TextView msgView;
    private TextView valueView;
    private CusImage cusImage;
    private OnCompleteListener onCompleteListener;
    private Handler mViewUpdateHandler;
    private long value;
    private String message;

    public HljRoundProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public HljRoundProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hlj_dialog_round_progress___cm);
        cusImage = (CusImage) findViewById(R.id.cusImage);
        valueView = (TextView) findViewById(R.id.value);
        completeView = findViewById(R.id.complete);
        msgView = (TextView) findViewById(R.id.msg);
        mViewUpdateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                msgView.setText(message);
                int progress = 0;
                if (max > 0) {
                    progress = Math.round(value * 100 / max);
                }
                valueView.setText(String.valueOf(progress));
                if (progress == 0) {
                    cusImage.reset();
                } else {
                    cusImage.progress(progress);
                }
                return false;
            }
        });
        super.onCreate(savedInstanceState);
    }

    public void reset() {
        completeView.setVisibility(View.GONE);
        valueView.setVisibility(View.VISIBLE);
        cusImage.reset();
        if (completeView.getScaleX() > 1) {
            completeView.setScaleX(1);
        }
        if (completeView.getScaleY() > 1) {
            completeView.setScaleY(1);
        }
        setCancelable(true);
    }

    public synchronized void setMax(long max) {
        this.max = max;
        onProgressChanged();
    }

    public void setMessage(String message) {
        this.message = message;
        onProgressChanged();
    }

    public synchronized void setProgress(long value) {
        this.value = value;
        onProgressChanged();
    }

    public void onProgressFinish() {
        valueView.setVisibility(View.GONE);
        cusImage.progress(100);
        completeView.setVisibility(View.VISIBLE);
    }

    public void onComplete(OnCompleteListener listener) {
        this.onCompleteListener = listener;
        if (completeView.getScaleX() > 1) {
            completeView.setScaleX(1);
        }
        if (completeView.getScaleY() > 1) {
            completeView.setScaleY(1);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(completeView, "scaleX", 2f);
        ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(completeView, "scaleY", 2f);
        animatorSet.play(scaleAnimationX)
                .with(scaleAnimationY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onCompleteListener != null) {
                    onCompleteListener.onCompleted();
                }
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    public interface OnCompleteListener {

        void onCompleted();

    }

    private void onProgressChanged() {
        if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }
}