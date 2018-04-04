/**
 *
 */
package me.suncloud.marrymemo;

import android.content.Context;
import android.util.Log;

import com.hunliji.hljcommonlibrary.models.realm.CrashInfo;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import io.realm.Realm;

/**
 * @author iDay
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Constants.DEBUG) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            } else {
                //                mDefaultHandler.uncaughtException(thread, ex);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        TCAgent.onError(mContext, ex);
        ex.printStackTrace();

        if (Constants.DEBUG) {
            saveCrashInfoToRealm(ex);
        }else {
            MobclickAgent.reportError(mContext, throwableString(ex));
        }
        return true;
    }

    private String throwableString(Throwable ex) {
        if (ex == null) {
            return null;
        } else {
            String string = null;
            try {
                Writer info = new StringWriter();
                PrintWriter printWriter = new PrintWriter(info);
                ex.printStackTrace(printWriter);

                Throwable cause = ex.getCause();
                while (cause != null) {
                    cause.printStackTrace(printWriter);
                    cause = cause.getCause();
                }

                string = info.toString();
                printWriter.close();
                info.close();
                string="context:"+HljViewTracker.getPageHistoryName()+"\n"+string;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return string;
        }
    }

    /**
     * 保存错误信息到realm中
     *
     * @param ex
     * @return
     */
    private void saveCrashInfoToRealm(Throwable ex) {
        CrashInfo crashInfo = new CrashInfo(Constants.APP_VERSION, throwableString(ex));
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(crashInfo);
        realm.commitTransaction();
        realm.close();
    }

}
