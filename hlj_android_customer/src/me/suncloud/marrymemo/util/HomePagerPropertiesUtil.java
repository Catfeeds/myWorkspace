package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.home.HomeApi;
import me.suncloud.marrymemo.api.home.HomeService;
import me.suncloud.marrymemo.model.FeedProperty;
import me.suncloud.marrymemo.model.MerchantProperty;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 15/11/13.
 * 专门用于properties的同步和读取
 */
public class HomePagerPropertiesUtil {

    /**
     * @param mContext
     * @return
     */
    public static Observable<List<FeedProperty>> getPropertiesObb(final Context mContext,final long city){
        return Observable.create(new Observable.OnSubscribe<List<FeedProperty>>() {
            @Override
            public void call(Subscriber<? super List<FeedProperty>> subscriber) {
                //从本地获取FeedProperty文件
                ArrayList<FeedProperty> properties = new ArrayList<>();
                JsonElement jsonElement = null;
                try {
                    InputStream in;
                    if (mContext.getFileStreamPath(Constants.FEEDS_PAGE_FILE) != null && mContext.getFileStreamPath(
                            Constants.FEEDS_PAGE_FILE)
                            .exists()) {
                        in = mContext.openFileInput(Constants.FEEDS_PAGE_FILE);
                    } else {
                        in = mContext.getResources().openRawResource(R.raw.feedspage);
                    }
                    jsonElement=new JsonParser().parse(new InputStreamReader(in));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (jsonElement != null) {
                    for (FeedProperty feedProperty : GsonUtil.getGsonInstance()
                            .fromJson(jsonElement, FeedProperty[].class)) {
                        //空数据过滤
                        if (!TextUtils.isEmpty(feedProperty.getStringId())) {
                            properties.add(feedProperty);
                        }
                    }
                    subscriber.onNext(properties);
                }
                subscriber.onCompleted();
            }
        }).concatWith(HomeApi.getFeedProperties(mContext,city)) //从服务器同步FeedProperty
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
