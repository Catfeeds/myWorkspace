package com.hunliji.hljcommonlibrary.utils;


import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;

public class SpanUtil {

    public static void setClickableSpan(
            TextView tv,
            NoUnderlineSpan clickableSpan,
            String text,
            int start,
            int end,
            int colorLink) {
        SpannableString sp = new SpannableString(text);
        sp.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(sp);
        tv.setLinkTextColor(colorLink);
        tv.setHighlightColor(Color.TRANSPARENT);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
