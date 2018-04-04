package com.hunliji.hljcommonlibrary.views.fragments;


import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;

/**
 * Created by Suncloud on 2016/7/5.
 */
public abstract class ScrollAbleFragment extends RefreshFragment implements ScrollableHelper.ScrollableContainer {


    @Override
    public boolean isDisable() {
        return false;
    }
}
