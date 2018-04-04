package com.hunliji.hljpushlibrary;

import android.net.Uri;
import android.text.TextUtils;

import com.hunliji.hljpushlibrary.websocket.PushSocket;

/**
 * Created by wangtao on 2017/11/28.
 */

public class HljPush {

    public static String PUSH_HOST = "http://notify.hunliji.com:8100/api/ws";

    public static void setPushHost(String pushHost) {
        if (TextUtils.isEmpty(pushHost) || PUSH_HOST.equals(pushHost)) {
            return;
        }
        if (PushSocket.INSTANCE.isConnected()) {
            PushSocket.INSTANCE.onClosed();
        }
        PUSH_HOST = pushHost;
    }

    public static String getOriginPath(String host) {
        Uri uri = Uri.parse(host);
        String scheme = uri.getScheme();
        int port = uri.getPort();
        boolean isDefaultPort = false;
        if (scheme.equals("http") && port == 80) {
            isDefaultPort = true;
        } else if (scheme.equals("https") && port == 443) {
            isDefaultPort = true;
        }
        if (isDefaultPort) {
            return scheme + "://" + uri.getHost();
        } else {
            return scheme + "://" + uri.getHost() + ":" + port;
        }

    }
}
