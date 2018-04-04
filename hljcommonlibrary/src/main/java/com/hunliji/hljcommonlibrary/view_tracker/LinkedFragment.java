package com.hunliji.hljcommonlibrary.view_tracker;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * Created by luohanlin on 23/03/2017.
 * 曝光统计时用于记录当前页面中fragment层级结构
 */

public class LinkedFragment {
    private Fragment fragment;
    private LinkedFragment linkedParent;
    private String idString;

    private String pageName; //有pageName时当作页面处理

    public LinkedFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public LinkedFragment(Fragment fragment, String pageName) {
        this.fragment = fragment;
        this.pageName = pageName;
    }

    public void setLinkedParent(LinkedFragment linkedParent) {
        this.linkedParent = linkedParent;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public LinkedFragment getLinkedParent() {
        return linkedParent;
    }

    public String getPageName() {
        return pageName;
    }

    public VTMetaData getPageData() {
        if (fragment != null && fragment instanceof TrackedFragmentInterface) {
            return ((TrackedFragmentInterface) fragment).getPageTrackData();
        }
        return null;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getIdString() {
        if (!TextUtils.isEmpty(idString)) {
            return idString;
        }
        if (fragment != null && fragment.getView() != null) {
            idString = HljViewTracker.viewIdString(fragment.getView());
            return idString;
        }
        return "0";
    }
}
