package me.suncloud.marrymemo.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.hunliji.hunlijicalendar.ResizeAnimation;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import me.suncloud.marrymemo.R;

/**
 * Created by LuoHanLin on 15/1/16.
 */
public class AnimUtil {

    public static Animation animationArrowUp;
    public static Animation animationArrowDown;

    public static Animation getAnimArrowUp(Context context) {
        if (animationArrowUp == null) {
            animationArrowUp = AnimationUtils.loadAnimation(context, R.anim.arrow_up);
        }
        return animationArrowUp;
    }

    public static Animation getAnimArrowDown(Context context) {
        if (animationArrowDown == null) {
            animationArrowDown = AnimationUtils.loadAnimation(context, R.anim.arrow_down);
        }
        return animationArrowDown;
    }

    public static void showMenu2Animation(final View menuBgView, final View view) {
        menuBgView.setVisibility(View.VISIBLE);
        Animation scaleAnimation = new ScaleAnimation(1,
                1,
                0,
                1,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0);
        scaleAnimation.setDuration(150);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuBgView.setBackgroundResource(R.color.transparent_black);
            }
        });
        view.startAnimation(scaleAnimation);
    }

    public static void hideMenu2Animation(final View menuBgView, final View view) {
        Animation scaleAnimation = new ScaleAnimation(1,
                1,
                1,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0);
        scaleAnimation.setDuration(150);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                menuBgView.setBackgroundResource(android.R.color.transparent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                menuBgView.setVisibility(View.GONE);
            }
        });
        view.startAnimation(scaleAnimation);
    }

    public static void showMenu3Animation(final View view, final View view2) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(),
                R.anim.slide_in_right);
        view.setVisibility(View.VISIBLE);
        view2.requestLayout();
        view.startAnimation(animation);
    }


    public static void hideMenu3Animation(final View view, final View view2) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(),
                R.anim.slide_out_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                view2.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public static void showResizeAnimation(final View view, int height) {
        if (height > 0) {
            ResizeAnimation resizeAnimation = new ResizeAnimation(view, 0, height);
            view.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = 0;
            resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            resizeAnimation.setDuration(400);
            view.startAnimation(resizeAnimation);
        }
    }

    public static void hideResizeAnimation(final View view, int height) {
        if (height > 0) {
            ResizeAnimation resizeAnimation = new ResizeAnimation(view, height, 0);
            resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            resizeAnimation.setDuration(400);
            view.startAnimation(resizeAnimation);
        }
    }

    public static void showMenuAnimation(final View menuBgView, final View view) {
        menuBgView.setVisibility(View.VISIBLE);
        TranslateAnimation scaleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                -1,
                Animation.RELATIVE_TO_SELF,
                0);
        scaleAnimation.setDuration(250);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuBgView.setBackgroundResource(R.color.transparent_black);
            }
        });
        view.startAnimation(scaleAnimation);
    }

    public static void hideMenuAnimation(final View menuBgView, final View view) {
        TranslateAnimation scaleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                -1);
        scaleAnimation.setDuration(250);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                menuBgView.setBackgroundResource(android.R.color.transparent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                menuBgView.setVisibility(View.GONE);
            }
        });
        view.startAnimation(scaleAnimation);
    }

    public static void pulseAnimate(View target) {
        AnimatorSet animatorSet = new AnimatorSet();

        // 恢复默认状态
        ViewHelper.setAlpha(target, 1);
        ViewHelper.setScaleX(target, 1);
        ViewHelper.setScaleY(target, 1);
        ViewHelper.setTranslationX(target, 0);
        ViewHelper.setTranslationY(target, 0);
        ViewHelper.setRotation(target, 0);
        ViewHelper.setRotationY(target, 0);
        ViewHelper.setRotationX(target, 0);
        ViewHelper.setPivotX(target, target.getMeasuredWidth() / 2.0f);
        ViewHelper.setPivotY(target, target.getMeasuredHeight() / 2.0f);

        animatorSet.setDuration(700);
        animatorSet.playTogether(ObjectAnimator.ofFloat(target, "scaleY", 1, 1.5f, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1, 1.5f, 1));
        animatorSet.start();
    }

    public static void zoomInUpAnimate(final View target) {
        AnimatorSet animatorSet = new AnimatorSet();

        // 恢复默认状态
        ViewHelper.setAlpha(target, 1);
        ViewHelper.setScaleX(target, 1);
        ViewHelper.setScaleY(target, 1);
        ViewHelper.setTranslationX(target, 0);
        ViewHelper.setTranslationY(target, 0);
        ViewHelper.setRotation(target, 0);
        ViewHelper.setRotationY(target, 0);
        ViewHelper.setRotationX(target, 0);
        ViewHelper.setPivotX(target, target.getMeasuredWidth() / 2.0f);
        ViewHelper.setPivotY(target, target.getMeasuredHeight() / 2.0f);

        animatorSet.setDuration(3000);
        animatorSet.playTogether(ObjectAnimator.ofFloat(target, "scaleX", 0.1f, 0.475f, 1),
                ObjectAnimator.ofFloat(target, "scaleY", 0.1f, 0.475f, 1));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                target.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public static void swapViewsHorizontal(
            final View leftView,
            final View rightView,
            float leftWidth,
            float rightWidth,
            float leftX,
            float rightX,
            long duration) {

        AnimatorSet animatorSet = new AnimatorSet();

        float scale1 = rightWidth / leftWidth;
        float scale2 = leftWidth / rightWidth;

        ViewHelper.setPivotX(leftView, leftWidth / 2.0f);
        ViewHelper.setPivotY(leftView, leftWidth / 2.0f);
        ViewHelper.setPivotX(rightView, rightWidth / 2.0f);
        ViewHelper.setPivotY(rightView, rightWidth / 2.0f);

        float tx1 = (leftX + leftWidth / 2.0f) - (rightX + rightWidth / 2.0f);
        float tx2 = (rightX + rightWidth / 2.0f) - (leftX + leftWidth / 2.0f);

        if (leftView.getScaleX() != 1) {
            scale1 = scale2 = 1;
            tx1 = tx2 = 0;
        }
        animatorSet.setDuration(duration);
        animatorSet.playTogether(ObjectAnimator.ofFloat(leftView, "translationX", tx2),
                ObjectAnimator.ofFloat(rightView, "translationX", tx1),
                ObjectAnimator.ofFloat(leftView, "scaleX", scale1),
                ObjectAnimator.ofFloat(leftView, "scaleY", scale1),
                ObjectAnimator.ofFloat(rightView, "scaleX", scale2),
                ObjectAnimator.ofFloat(rightView, "scaleY", scale2));

        animatorSet.start();
    }

    public static void shakeAnimate(final View target) {
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.setDuration(1000);
        animatorSet.playTogether(ObjectAnimator.ofFloat(target,
                "translationY",
                0,
                0,
                -30,
                0,
                -15,
                0,
                0));
        animatorSet.start();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }

}

