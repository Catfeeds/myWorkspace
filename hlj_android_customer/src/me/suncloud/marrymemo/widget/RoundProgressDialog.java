package me.suncloud.marrymemo.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import me.suncloud.marrymemo.R;

public class RoundProgressDialog extends Dialog {
    private long maxLength;
    private View complateView;
    private TextView msgView;
    private TextView valueView;
    private CusImage cusImage;
    private OnUpLoadComplate onUpLoadComplate;

    public RoundProgressDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    public RoundProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_round_progress);
        cusImage = (CusImage) findViewById(R.id.cusImage);
        valueView = (TextView) findViewById(R.id.value);
        complateView = findViewById(R.id.complate);
        msgView = (TextView) findViewById(R.id.msg);

    }

    public void reset() {
        complateView.setVisibility(View.GONE);
        valueView.setVisibility(View.VISIBLE);
        cusImage.reset();
        if (complateView.getScaleX() > 1) {
            complateView.setScaleX(1);
        }
        if (complateView.getScaleY() > 1) {
            complateView.setScaleY(1);
        }
        setCancelable(true);
    }

    public void setMax(long max) {
        maxLength = max;
    }

    public void setMessage(String message) {
        msgView.setText(message);
    }

    public void setProgress(long value) {
        int progress = 0;
        if (maxLength > 0) {
            progress = Math.round(value * 100 / maxLength);
        }
        valueView.setText(String.valueOf(progress));
        if (progress == 0) {
            cusImage.reset();
        } else {
            cusImage.setupprogress(progress);
        }
    }

    public void onLoadComplate() {
        valueView.setVisibility(View.GONE);
        cusImage.setupprogress(100);
        complateView.setVisibility(View.VISIBLE);
    }

    public void onComplate() {
        if (complateView.getScaleX() > 1) {
            complateView.setScaleX(1);
        }
        if (complateView.getScaleY() > 1) {
            complateView.setScaleY(1);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(complateView, "scaleX", 2f);
        ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(complateView, "scaleY", 2f);
        animatorSet.play(scaleAnimationX)
                .with(scaleAnimationY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onUpLoadComplate != null) {
                    onUpLoadComplate.onUpLoadCompleted();
                }
                complateView.setScaleX(1);
                complateView.setScaleY(1);
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

    public void onComplate(OnUpLoadComplate onUpLoadComplate) {
        this.onUpLoadComplate = onUpLoadComplate;
        onComplate();
    }

    public void setOnUpLoadComplate(OnUpLoadComplate onUpLoadComplate) {
        this.onUpLoadComplate = onUpLoadComplate;
    }

    public interface OnUpLoadComplate {

        void onUpLoadCompleted();

    }

}