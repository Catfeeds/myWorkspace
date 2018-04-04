package com.hunliji.hljcommonlibrary.views.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by hua_rong on 2017/5/24.
 *  无下划线,可点击的文本
 */

public abstract class NoUnderlineSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }

}
