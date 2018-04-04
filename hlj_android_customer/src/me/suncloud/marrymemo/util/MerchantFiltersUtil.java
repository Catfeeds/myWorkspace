package me.suncloud.marrymemo.util;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.hunliji.hljcommonlibrary.models.search.MerchantFilter;
import com.hunliji.hljhttplibrary.api.search.SearchApi;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import rx.Subscriber;

/**
 * Created by werther on 15/11/13.
 * 专门用于properties的同步和读取
 */
public class MerchantFiltersUtil {

    /**
     * 从本地存储的文件中读取properties列表
     *
     * @param mContext
     * @return
     */
    public static MerchantFilter getMerchantFilterFromFile(Context mContext) {
        MerchantFilter merchantFilter = null;
        JsonElement jsonElement = null;
        try {
            if (mContext.getFileStreamPath(Constants.MERCHANT_FILTER_FILE) != null && mContext
                    .getFileStreamPath(
                    Constants.MERCHANT_FILTER_FILE)
                    .exists()) {
                InputStream in = mContext.openFileInput(Constants.MERCHANT_FILTER_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                jsonElement = new JsonPrimitive(jsonStr);
            } else {
                jsonElement = (new JsonPrimitive(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.merchant_filters))));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (jsonElement != null) {
            merchantFilter = GsonUtil.getGsonInstance()
                    .fromJson(jsonElement, MerchantFilter.class);
        }

        return merchantFilter;
    }

    public static void SyncMerchantFilterData(
            final Context context,
            final OnFinishedListener onFinishedListener) {
        Subscriber<MerchantFilter> subscriber = new Subscriber<MerchantFilter>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MerchantFilter merchantFilter) {
                if (onFinishedListener != null) {
                    onFinishedListener.onFinish(merchantFilter);
                }
                OutputStreamWriter out;
                try {
                    out = new OutputStreamWriter(context.openFileOutput(Constants
                            .MERCHANT_FILTER_FILE,
                            Context.MODE_PRIVATE));
                    out.write(GsonUtil.getGsonInstance()
                            .toJson(merchantFilter));
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        SearchApi.getMerchantFilter()
                .subscribe(subscriber);
    }

    public interface OnFinishedListener {
        void onFinish(MerchantFilter merchantFilter);
    }
}
