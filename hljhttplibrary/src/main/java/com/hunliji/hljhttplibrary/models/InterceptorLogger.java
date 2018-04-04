package com.hunliji.hljhttplibrary.models;

import com.hunliji.hljcommonlibrary.models.realm.HttpLogBlock;

import java.util.Date;

import io.realm.Realm;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by luohanlin on 2017/9/22.
 */

public class InterceptorLogger implements HttpLoggingInterceptor.Logger {

    private StringBuffer sbTemp;
    public static final String LINE_BREAK = "\r\n";
    private static final String END_TAG = "<-- END HTTP";
    public static final int LIMIT_TIME_WINDOW = 5 * 1000 * 1000; // 一个小时

    @Override
    public synchronized void log(String message) {
        Platform.get()
                .log(INFO, message, null);
        if (!message.startsWith(END_TAG)) {
            if (sbTemp == null) {
                sbTemp = new StringBuffer();
            }
            sbTemp.append(message);
            sbTemp.append(LINE_BREAK);
        } else {
            if (sbTemp == null) {
                sbTemp = new StringBuffer();
            }
            // 一个请求来回结束，写入文件
            sbTemp.append(message);
            saveLogToRealm(sbTemp.toString());
            sbTemp = null;
        }
    }

    private void saveLogToRealm(String string) {
        long timeWindowStart = System.currentTimeMillis() - LIMIT_TIME_WINDOW;
        HttpLogBlock block = new HttpLogBlock(string);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // 设置时间窗口，控制数量
        realm.where(HttpLogBlock.class)
                .lessThan("id", timeWindowStart)
                .findAll()
                .deleteAllFromRealm();
        // 插入新数据
        realm.copyToRealm(block);
        realm.commitTransaction();
        realm.close();
    }
}
