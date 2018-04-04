package com.hunliji.hljvideolibrary.interfaces;


import com.hunliji.hljvideolibrary.view.HljRangeSeekBarView;

public interface HljOnRangeSeekBarListener {
    void onCreate(HljRangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeek(HljRangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStart(HljRangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStop(HljRangeSeekBarView rangeSeekBarView, int index, float value);
}
