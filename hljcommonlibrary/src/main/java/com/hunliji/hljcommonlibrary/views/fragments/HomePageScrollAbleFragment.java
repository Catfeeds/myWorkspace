package com.hunliji.hljcommonlibrary.views.fragments;


import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;

/**
 * 首页定制的ScrollAbleFragment,包含一些首页特定的方法
 * Created by jinxin on 2016/7/5.
 */
public abstract class HomePageScrollAbleFragment extends ScrollAbleFragment implements
        ScrollableHelper.ScrollableContainer {

    public abstract void scrollTop();
}
