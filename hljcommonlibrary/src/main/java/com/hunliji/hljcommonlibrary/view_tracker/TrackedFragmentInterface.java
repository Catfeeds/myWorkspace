package com.hunliji.hljcommonlibrary.view_tracker;

/**
 * Created by luohanlin on 17/03/2017.
 * 曝光统计的视图中的fragment需要实现此接口并且在相应的位置调用相应的方法
 */

public interface TrackedFragmentInterface {

    void setFragmentTagOnResume();

    void setFragmentTagOnPause();

    void setFragmentTagOnHiddenChanged(boolean hidden);

    void setFragmentTagOnUserVisibleChanged(boolean isVisible);

    String getFragmentPageTagName();

    String fragmentPageTrackTagName();

    VTMetaData getPageTrackData();

    VTMetaData pageTrackData();
}
