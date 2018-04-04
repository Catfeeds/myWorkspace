package com.hunliji.hljvideolibrary.interfaces;

import android.net.Uri;

public interface OnTrimVideoListener {

    void onTrimResult(final Uri uri);

    void onTrimError(String errorMsg);
}
