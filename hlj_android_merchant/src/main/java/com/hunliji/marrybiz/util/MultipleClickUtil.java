package com.hunliji.marrybiz.util;

/**
 * 判断按钮多重点击的工具类 不过只能判断双击
 * Created by jinxin on 2015/9/11.
 */
public class MultipleClickUtil {
    private static long lastClickTime;
    //默认时间间隔是500 ms
    private static int timeGap = 500;

    /**
     * 判断快速双击
     *
     * @param timeGap 时间间隔
     * @return true 快速双击  false
     */
    public synchronized static boolean isFastClick(int timeGap) {
        if (timeGap >= 0) {
            MultipleClickUtil.timeGap = timeGap;
        }
        long time = System.currentTimeMillis();
        if (time - lastClickTime > MultipleClickUtil.timeGap) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        MultipleClickUtil.timeGap = 500;
        return true;
    }

    /**
     * 判断快速双击
     *
     * @return true 快速双击  false
     */
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime > MultipleClickUtil.timeGap) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }
}
