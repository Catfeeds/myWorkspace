package me.suncloud.marrymemo.util.modules;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.WeddingCarRouteService;

import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;

/**
 * Created by jinxin on 2017/12/29 0029.
 */
@Route(path = RouterPath.ServicePath.WEDDING_CAR_SERVICE)
public class WeddingCarRouteImpl implements WeddingCarRouteService {

    private NoticeUtil noticeUtil;

    @Override
    public void onNoticeInit(
            Context mContext, TextView tvCount, View msgView) {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(mContext, tvCount, msgView);
        }
    }

    @Override
    public void onNoticeResume() {
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    public void onNoticePause() {
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    public String getMemberRemind(Context mContext) {
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(mContext);
        if (dataConfig != null) {
            return dataConfig.getCarDetailMemberRemind();
        }
        return null;
    }

    @Override
    public void gotoWeddingCarActivity(Context context, String cityName, long cityCode) {
        if (TextUtils.isEmpty(cityName) || cityCode <= 0) {
            return;
        }
            me.suncloud.marrymemo.model.City customerCity = new me.suncloud.marrymemo.model.City(
                    cityCode,
                    cityName);
            DataConfig.gotoWeddingCarActivity(context, customerCity);
    }

    @Override
    public void init(Context context) {

    }
}
