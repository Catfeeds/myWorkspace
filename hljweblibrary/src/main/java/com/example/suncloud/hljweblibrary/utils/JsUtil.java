package com.example.suncloud.hljweblibrary.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.suncloud.hljweblibrary.api.JsApi;
import com.example.suncloud.hljweblibrary.models.JsInfo;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.EncodeUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/2/19.
 */
public class JsUtil {

    private static JsUtil INSTANCE;
    private WeakReference<Context> contextWeakReference;
    private Subscription jsInfoSubscription;

    public static JsUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JsUtil();
        }
        return INSTANCE;
    }

    public void loadJsInfo(Context context) {
        this.contextWeakReference = new WeakReference<>(context.getApplicationContext());
        if (jsInfoSubscription != null && !jsInfoSubscription.isUnsubscribed()) {
            return;
        }
        jsInfoSubscription = JsApi.getJsInfo()
                .filter(new Func1<JsInfo, Boolean>() {
                    @Override
                    public Boolean call(JsInfo jsInfo) {
                        String md5 = jsInfo.getJsCdnMd5();
                        String path = jsInfo.getJsCdnUrl();
                        if (TextUtils.isEmpty(md5) || TextUtils.isEmpty(path)) {
                            return false;
                        }
                        if (contextWeakReference == null || contextWeakReference.get() == null) {
                            return false;
                        }
                        Context context = contextWeakReference.get();
                        String localMd5 = context.getSharedPreferences(HljCommon.FileNames
                                .PREF_FILE,
                                Context.MODE_PRIVATE)
                                .getString("js_md5", null);
                        if (TextUtils.isEmpty(localMd5)) {
                            //没数据是检测一遍老数据，以后可以去掉
                            localMd5 = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                                    .getString("js_md5", null);
                        }
                        return !md5.equals(localMd5);
                    }
                })
                .flatMap(new Func1<JsInfo, Observable<String>>() {
                    @Override
                    public Observable<String> call(JsInfo jsInfo) {
                        if (contextWeakReference == null || contextWeakReference.get() == null) {
                            return Observable.error(new RuntimeException("context is null"));
                        }
                        String md5 = jsInfo.getJsCdnMd5();
                        Uri uri = Uri.parse(jsInfo.getJsCdnUrl());
                        String name = FileUtil.removeFileSeparator(uri.getPath());
                        Context context = contextWeakReference.get();
                        File zipFile = context.getFileStreamPath(name);
                        if (zipFile != null && zipFile.exists()) {
                            String checkMd5 = EncodeUtil.md5sum(zipFile);
                            if (md5.equalsIgnoreCase(checkMd5)) {
                                return unZipObservable(context, jsInfo);
                            }
                        }
                        return downZipObservable(context,
                                jsInfo).flatMap(new Func1<JsInfo, Observable<String>>() {
                            @Override
                            public Observable<String> call(JsInfo jsInfo) {
                                if (contextWeakReference == null || contextWeakReference.get() ==
                                        null) {
                                    return Observable.error(new RuntimeException("context is " +
                                            "null"));
                                }
                                return unZipObservable(contextWeakReference.get(), jsInfo);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String md5) {
                        if (!TextUtils.isEmpty(md5)) {
                            if (contextWeakReference != null && contextWeakReference.get() !=
                                    null) {
                                contextWeakReference.get()
                                        .getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                                                Context.MODE_PRIVATE)
                                        .edit()
                                        .putString("js_md5", md5)
                                        .apply();
                            }
                        }
                    }
                });
    }

    private Observable<JsInfo> downZipObservable(final Context context, final JsInfo jsInfo) {
        return Observable.create(new Observable.OnSubscribe<JsInfo>() {
            @Override
            public void call(Subscriber<? super JsInfo> subscriber) {
                try {
                    URL url = new URL(jsInfo.getJsCdnUrl());
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    InputStream input = new BufferedInputStream(url.openStream());
                    FileOutputStream out = context.openFileOutput(FileUtil.removeFileSeparator
                            (url.getPath()),
                            Context.MODE_PRIVATE);
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.flush();
                    out.close();
                    input.close();
                    subscriber.onNext(jsInfo);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<String> unZipObservable(final Context context, final JsInfo jsInfo) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Uri uri = Uri.parse(jsInfo.getJsCdnUrl());
                String name = FileUtil.removeFileSeparator(uri.getPath());
                try {
                    boolean isUnzip = FileUtil.unZipFile(context.getFileStreamPath(name)
                                    .getPath(),
                            context.getFilesDir()
                                    .getPath());
                    if (isUnzip) {
                        subscriber.onNext(jsInfo.getJsCdnMd5());
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public InputStream getJs(String name, Context context) {
        String localMd5 = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE)
                .getString("js_md5", null);
        if (TextUtils.isEmpty(localMd5)) {
            //没数据是检测一边老老数据，以后可以去掉
            localMd5 = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                    .getString("js_md5", null);
        }
        if (TextUtils.isEmpty(localMd5)) {
            return null;
        }
        name = FileUtil.splitFileSeparator(name);
        File file = new File(context.getFilesDir() + "/local_web_res/" + name);
        if (!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
