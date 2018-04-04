package com.hunliji.hljmaplibrary.modules;

/**
 * Created by wangtao on 2017/11/14.
 */

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.MapLibraryService;
import com.hunliji.hljmaplibrary.HljMap;

@Route(path = RouterPath.ServicePath.Map.MAP_LIBRARY_SERVICE)
public class MapLibraryImpl implements MapLibraryService {
    @Override
    public void init(Context context) {

    }

    @Override
    public String getAMapUrl(
            double longitude,
            double latitude,
            int width,
            int height,
            int zoom,
            String markerIconPath) {
        return HljMap.getAMapUrl(longitude,latitude,width,height,zoom,markerIconPath);

    }
}
