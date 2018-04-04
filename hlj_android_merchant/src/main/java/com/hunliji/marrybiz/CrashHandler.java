/**
 *
 */
package com.hunliji.marrybiz;

import android.content.Context;

import com.hunliji.hljcommonlibrary.models.realm.CrashInfo;
import com.tendcloud.tenddata.TCAgent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import io.realm.Realm;

public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private static CrashHandler INSTANCE;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (Constants.DEBUG) {
            saveCrashInfo2Realm(ex);
        }
        mDefaultHandler.uncaughtException(thread, ex);
        TCAgent.onError(mContext, ex);
    }

    private void saveCrashInfo2Realm(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();

        CrashInfo crashInfo = new CrashInfo(Constants.APP_VERSION, result);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(crashInfo);
        realm.commitTransaction();
        realm.close();
    }

}
