package com.hunliji.marrybiz.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.hunliji.marrybiz.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

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
}

