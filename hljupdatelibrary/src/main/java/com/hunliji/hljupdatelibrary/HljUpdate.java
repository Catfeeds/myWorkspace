package com.hunliji.hljupdatelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EncodeUtil;
import com.hunliji.hljupdatelibrary.activities.UpdateDialogActivity;
import com.hunliji.hljupdatelibrary.api.UpdateApi;
import com.hunliji.hljupdatelibrary.models.UpdateInfo;
import com.hunliji.hljupdatelibrary.service.DownloadService;
import com.hunliji.hljupdatelibrary.utils.StorageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Suncloud on 2016/9/19.
 */
public class HljUpdate {

    private UpdateInfo updateInfo;
    private Subscription updateSubscription;
    private WeakReference<Context> contextWeakReference;

    private static HljUpdate hljUpdate;

    public static HljUpdate getInstance() {
        if (hljUpdate == null) {
            hljUpdate = new HljUpdate();
        }
        return hljUpdate;
    }

    public void update(Context context) {
        if (updateInfo == null) {
            updateCheck(context);
        } else {
            if (!hasUpdate(getContext()) || updateInfo.getSupportCode() <= ChannelUtil
                    .getVersionCode(
                    context)) {
                return;
            }
            Intent intent = new Intent(context, UpdateDialogActivity.class);
            intent.putExtra("updateInfo", updateInfo);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.fade_in,
                        R.anim.activity_anim_default);
            }
        }
    }

    /**
     * 升级检测有更新跳转升级界面
     *
     * @param context
     */
    public void updateCheck(Context context) {
        updateCheck(context,
                UpdateApi.getUpdateInfo(ChannelUtil.getVersionCode(context),
                        ChannelUtil.getChannel(context)));
    }

    public void updateCheck(Context context, Observable<UpdateInfo> observable) {
        if (!CommonUtil.isNetworkConnected(context) || observable == null) {
            return;
        }
        contextWeakReference = new WeakReference<>(context);
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            return;
        }
        updateSubscription = observable.timeout(1, TimeUnit.MINUTES)
                .subscribe(new Subscriber<UpdateInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UpdateInfo info) {
                        if (info == null) {
                            return;
                        }
                        if (getContext() == null) {
                            return;
                        }
                        saveUpdateInfo(getContext(), info);
                        if (!hasUpdate(getContext()) || isIgnore(getContext(), info)) {
                            return;
                        }
                        Intent intent = new Intent(getContext(), UpdateDialogActivity.class);
                        intent.putExtra("updateInfo", updateInfo);
                        getContext().startActivity(intent);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).overridePendingTransition(R.anim.fade_in,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
    }


    private Context getContext() {
        if (contextWeakReference == null) {
            return null;
        }
        return contextWeakReference.get();
    }


    /***
     * 获取安装包文件,如果MD5值不同则删除
     *
     * @param md5 文件MD5值
     * @return 安装包文件
     */
    public static File downloadedFile(Context context, String md5) {
        if (TextUtils.isEmpty(md5)) {
            return null;
        }

        File apkFile = new File(StorageUtils.getCacheDirectory(context), "hunliji.apk");
        if (apkFile.exists()) {
            String checkMd5 = EncodeUtil.md5sum(apkFile);
            if (md5.equalsIgnoreCase(checkMd5)) {
                return apkFile;
            } else {
                apkFile.deleteOnExit();
            }
        }
        return null;
    }


    /**
     * 版本忽略判断 非最低支持，非建议升级
     *
     * @param context
     * @param info    更新信息
     * @return
     */
    private static boolean isIgnore(Context context, UpdateInfo info) {
        return !(info.getSupportCode() > ChannelUtil.getVersionCode(context) || info
                .getSuggestCode() > ChannelUtil.getVersionCode(
                context)) && !TextUtils.isEmpty(info.getMd5()) && info.getMd5()
                .equalsIgnoreCase(context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                        Context.MODE_PRIVATE)
                        .getString("ignore", ""));
    }

    /**
     * 保存忽略MD5
     *
     * @param context
     * @param md5     版本MD5号
     */
    public static void ignoreUpdate(Context context, String md5) {
        if (TextUtils.isEmpty(md5)) {
            return;
        }
        context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("ignore", md5)
                .apply();
    }

    /**
     * 保存更新信息
     *
     * @param context
     * @param info
     */
    private void saveUpdateInfo(Context context, UpdateInfo info) {
        if (info == null) {
            return;
        }
        this.updateInfo = info;
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(HljCommon.FileNames
                            .UPLOAD_FILE,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(new Gson().toJson(updateInfo));
                out.flush();
                out.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    /**
     * 获取更新信息，优先取线上信息
     *
     * @param context
     * @return
     */
    public UpdateInfo getUpdateInfo(Context context) {
        if (updateInfo == null) {
            try {
                if (context.getFileStreamPath(HljCommon.FileNames.UPLOAD_FILE) != null && context
                        .getFileStreamPath(
                        HljCommon.FileNames.UPLOAD_FILE)
                        .exists()) {
                    InputStream in = context.openFileInput(HljCommon.FileNames.UPLOAD_FILE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    return new Gson().fromJson(stream.toString(), UpdateInfo.class);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return updateInfo;
    }

    /**
     * 是否有更新
     *
     * @param context
     * @return
     */
    public boolean hasUpdate(Context context) {
        return updateInfo != null && updateInfo.getVersionCode() > ChannelUtil.getVersionCode(
                context);
    }

    /**
     * 更新操作 已下载安装，未下载启动下载
     *
     * @param context
     * @param updateInfo
     */
    public static void onUpdate(Context context, UpdateInfo updateInfo) {
        File apkFile = HljUpdate.downloadedFile(context, updateInfo.getMd5());
        if (apkFile != null && apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(context,
                        context.getPackageName(),
                        apkFile), "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile),
                        "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
            intent.putExtra("updateInfo", updateInfo);
            context.startService(intent);
        }
    }
}
