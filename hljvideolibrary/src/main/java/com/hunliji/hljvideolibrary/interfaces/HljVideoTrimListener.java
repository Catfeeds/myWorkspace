package com.hunliji.hljvideolibrary.interfaces;

/**
 * Created by luohanlin on 2017/6/22.
 */

public interface HljVideoTrimListener {
    void onSeeking(float startPositionInMs, float endPositionInMs, float videoTotalMs);
}
