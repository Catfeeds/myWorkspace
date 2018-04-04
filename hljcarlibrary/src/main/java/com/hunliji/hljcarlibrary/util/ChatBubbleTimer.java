package com.hunliji.hljcarlibrary.util;

import android.os.CountDownTimer;

/**
 * Created by luohanlin on 2017/7/12.
 */

public class ChatBubbleTimer extends CountDownTimer {

    private boolean hasShowed; // 是否已经显示过
    private boolean showTimeOver; // 是否计时已过
    private boolean scrollDeltaOver; // 用户已经手动滑过约定位置
    private ShowBubbleCallBack callBack;


    /**
     * 轻松聊气泡计时器初始化
     *
     * @param millisInFuture 时间
     * @param hasShowed      是否已经显示了
     * @param callBack       时间到和滑动条件满足之后的返回回调
     */
    public ChatBubbleTimer(
            long millisInFuture, boolean hasShowed, ShowBubbleCallBack callBack) {
        super(millisInFuture, millisInFuture);
        this.hasShowed = hasShowed;
        this.callBack = callBack;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        if (hasShowed) {
            this.callBack.toggleBubble(false);
            return;
        }
        showTimeOver = true;
        if (scrollDeltaOver) {
            hasShowed = true;
            this.callBack.toggleBubble(true);
            this.start();
        }
    }

    public void overScrollDelta() {
        scrollDeltaOver = true;
        if (showTimeOver && !hasShowed) {
            hasShowed = true;
            this.callBack.toggleBubble(true);
        }
    }

    public interface ShowBubbleCallBack {
        void toggleBubble(boolean isShow);
    }
}
