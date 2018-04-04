package me.suncloud.marrymemo.util;

import android.content.Context;

import com.tendcloud.tenddata.TCAgent;

import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.task.HttpGetTask;

/**
 * Created by Suncloud on 2015/4/9.
 */
public class TrackerUtil {

    public static void sendTracker() {
    }

    public static void sendEvent(Context context, String EVENT_ID, String EVENT_LABEL,
                                 Map<String, Object> kv) {
        if (!JSONUtil.isEmpty(EVENT_ID)) {
            if (!JSONUtil.isEmpty(EVENT_LABEL)) {
                if (kv != null) {
                    TCAgent.onEvent(context, EVENT_ID, EVENT_LABEL, kv);
                    return;
                }
                TCAgent.onEvent(context, EVENT_ID, EVENT_LABEL);
                return;
            }
            TCAgent.onEvent(context, EVENT_ID);
        }
    }

    public static void onTCAgentPageStart(Context context, String page) {
        if (!JSONUtil.isEmpty(page)) {
            TCAgent.onPageStart(context, page);
        }
    }

    public static void onTCAgentPageEnd(Context context, String page) {
        if (!JSONUtil.isEmpty(page)) {
            TCAgent.onPageEnd(context, page);
        }

    }

    public static void onPVTracker(Context context, int type) {
        DataConfig dataConfig = Session.getInstance().getDataConfig(context);
        if (dataConfig != null) {
            String url = null;
            switch (type) {
                case 0:
                    url = dataConfig.getBootScreenPv();
                    break;
                case 1:
                    url = dataConfig.getBootScreenClick();
                    break;
                case 2:
                    url = dataConfig.getBrideSaidByPv();
                    break;
                case 3:
                    url = dataConfig.getBrideSaidByClick();
                    break;
                case 4:
                    url = dataConfig.getHomePageShufflingPv();
                    break;
                case 5:
                    url = dataConfig.getHomePageShufflingClick();
                    break;
            }
            if (!JSONUtil.isEmpty(url)) {
                new HttpGetTask(null).executeOnExecutor(Constants.PVTHEADPOOL, dataConfig
                        .getBootScreenPv());
            }
        }
    }

}
