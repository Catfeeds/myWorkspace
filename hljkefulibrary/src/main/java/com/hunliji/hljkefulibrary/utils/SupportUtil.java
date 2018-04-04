package com.hunliji.hljkefulibrary.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljkefulibrary.api.KeFuApi;
import com.hunliji.hljkefulibrary.moudles.Support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Suncloud on 2015/12/2.
 */
public class SupportUtil {

    private static SupportUtil INSTANCE;
    private List<Support> supports;
    private Subscription supportSubscription;
    private SparseArray<GetSupportCallback> requestListenerArray;


    private SupportUtil(Context context) {
        getSupportsFromFile(context);
    }

    public static SupportUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SupportUtil(context.getApplicationContext());
        }
        return INSTANCE;
    }

    /**
     * 异步获取客服
     *
     * @param kind            小于零代表需要返回support列表
     * @param requestListener
     */
    public void getSupport(Context context, int kind, GetSupportCallback requestListener) {
        if (supports != null && !supports.isEmpty()) {
            if (requestListener == null) {
                return;
            }
            if (kind < 0) {
                requestListener.onSupportsCompleted(supports);
                return;
            } else {
                for (Support support : supports) {
                    if (support.getKind() == kind) {
                        requestListener.onSupportCompleted(support);
                        return;
                    }
                }
            }
        }
        if (requestListenerArray == null) {
            requestListenerArray = new SparseArray<>();
        }
        if (requestListener != null) {
            requestListenerArray.put(kind, requestListener);
        }
        loadSupports(context);
    }

    /**
     * 异步获取客服列表
     *
     * @param requestListener
     */
    public void getSupports(Context context, GetSupportCallback requestListener) {
        getSupport(context, -1, requestListener);
    }

    private void getSupportsFromFile(Context mContext) {
        try {
            if (mContext.getFileStreamPath(HljCommon.FileNames.SUPPORTS_FILE) != null && mContext
                    .getFileStreamPath(
                            HljCommon.FileNames.SUPPORTS_FILE)
                    .exists()) {
                InputStream in = mContext.openFileInput(HljCommon.FileNames.SUPPORTS_FILE);
                JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(in));
                in.close();
                supports = GsonUtil.getGsonInstance()
                        .fromJson(jsonElement, new TypeToken<List<Support>>() {}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadSupports(mContext);
    }

    private void loadSupports(final Context mContext) {
        if (!CommonUtil.isUnsubscribed(supportSubscription)) {
            return;
        }
        supportSubscription = KeFuApi.getSupports(mContext)
                .subscribe(new Subscriber<List<Support>>() {
                    @Override
                    public void onCompleted() {
                        if (requestListenerArray != null) {
                            requestListenerArray.clear();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestListenerArray != null && requestListenerArray.size() > 0) {
                            for (int i = 0, size = requestListenerArray.size(); i < size; i++) {
                                requestListenerArray.valueAt(i)
                                        .onFailed();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<Support> supports) {
                        if(CommonUtil.isCollectionEmpty(supports)){
                            if (requestListenerArray != null && requestListenerArray.size() > 0) {
                                for (int i = 0, size = requestListenerArray.size(); i < size; i++) {
                                    requestListenerArray.valueAt(i)
                                            .onFailed();
                                }
                            }
                            return;
                        }
                        SupportUtil.this.supports = supports;
                        if (requestListenerArray != null && requestListenerArray.size() > 0) {
                            for (int i = 0, size = requestListenerArray.size(); i < size; i++) {
                                int kind = requestListenerArray.keyAt(i);
                                GetSupportCallback listener = requestListenerArray.valueAt(i);
                                if (kind < 0) {
                                    listener.onSupportsCompleted(supports);
                                } else {
                                    boolean isCompleted = false;
                                    for (Support support : supports) {
                                        if (support.getKind() == kind) {
                                            listener.onSupportCompleted(support);
                                            isCompleted = true;
                                            break;
                                        }
                                    }
                                    if (!isCompleted) {
                                        listener.onFailed();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public static class SimpleSupportCallback implements GetSupportCallback {

        @Override
        public void onSupportCompleted(Support support) {

        }

        @Override
        public void onSupportsCompleted(List<Support> supports) {

        }

        @Override
        public void onFailed() {

        }
    }

    public interface GetSupportCallback {

        void onSupportCompleted(Support support);

        void onSupportsCompleted(List<Support> supports);

        void onFailed();
    }


}
