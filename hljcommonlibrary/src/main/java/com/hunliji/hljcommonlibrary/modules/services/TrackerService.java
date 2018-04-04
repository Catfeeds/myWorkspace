package com.hunliji.hljcommonlibrary.modules.services;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;


/**
 * Created by wangtao on 2017/12/25.
 */

public interface TrackerService extends IProvider {
    void onFragmentPageResume(RefreshFragment fragment);
}
