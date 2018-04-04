package me.suncloud.marrymemo.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by luohanlin on 2017/5/19.
 */

public class DisableLayoutManager extends LinearLayoutManager {

    private boolean isScrollDisable;

    public DisableLayoutManager(Context context) {
        super(context);
    }

    public boolean isScrollDisable() {
        return isScrollDisable;
    }

    public void setScrollDisable(boolean scrollDisable) {
        isScrollDisable = scrollDisable;
    }

    @Override
    public boolean canScrollVertically() {
        return !isScrollDisable && super.canScrollVertically();
    }
}
