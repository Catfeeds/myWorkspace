package me.suncloud.marrymemo.util.modules;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.TrackerService;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import me.suncloud.marrymemo.util.GoogleAnalyticsUtil;

/**
 * Created by wangtao on 2017/12/25.
 */

@Route(path = RouterPath.ServicePath.TRACKER)
public class TrackerImpl implements TrackerService {
    @Override
    public void onFragmentPageResume(RefreshFragment fragment) {
        if(fragment.getActivity()!=null) {
            GoogleAnalyticsUtil.getInstance(fragment.getContext())
                    .sendScreen(fragment.getActivity(), fragment.fragmentPageName());
        }
    }

    @Override
    public void init(Context context) {

    }
}
