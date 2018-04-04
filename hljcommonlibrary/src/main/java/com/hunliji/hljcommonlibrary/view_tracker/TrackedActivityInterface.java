package com.hunliji.hljcommonlibrary.view_tracker;


/**
 * Created by luohanlin on 17/03/2017.
 * 曝光统计的视图Activity需要实现此接口并且在相应的位置调用相应的方法
 */

public interface TrackedActivityInterface {
    void setActivityNameOnResume();

    void clearFragmentNameOnPause();

    String getLastPageName();

    VTMetaData pageTrackData();

    /**
     *用于单独设置统计的页面tag名称
     * @return
     */
    String pageTrackTagName();
}
