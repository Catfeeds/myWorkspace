package com.hunliji.hljcardcustomerlibrary.views.delegates;

import android.view.ViewGroup;

/**
 * Created by wangtao on 2017/11/24.
 */

public interface CardListBarDelegate {
    void inflateActionBar(ViewGroup parent);

    void isHasOld(boolean isHasOld);


    void unbind();
}
