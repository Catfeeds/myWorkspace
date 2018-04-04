package me.suncloud.marrymemo.api.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import org.joda.time.DateTime;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.FeedProperty;
import me.suncloud.marrymemo.model.home.FeedList;
import me.suncloud.marrymemo.model.home.HomeFeed;
import me.suncloud.marrymemo.model.home.HomeFeedList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 首页api
 * Created by wangtao on 2016/12/5.
 */

public class HomeApi {


    /**
     * 获取首页分类并存到本地文件
     *
     * @param mContext 当前上下文，文件写入
     * @return
     */
    public static Observable<List<FeedProperty>> getFeedProperties(
            final Context mContext, long city) {
        return HljHttp.getRetrofit()
                .create(HomeService.class)
                .getFeedProperties(city)
                .map(new Func1<HljHttpResult<JsonElement>, List<FeedProperty>>() {
                    @Override
                    public List<FeedProperty> call(HljHttpResult<JsonElement> httpResult) {
                        List<FeedProperty> properties = new ArrayList<>();

                        if (httpResult.getStatus()
                                .getRetCode() != 0) {
                            readLocalFeedProperty(mContext, properties);
                            return properties;
                        }

                        try {
                            JsonArray array = httpResult.getData()
                                    .getAsJsonArray();
                            if (array != null && array.size() > 0) {
                                for (FeedProperty feedProperty : GsonUtil.getGsonInstance()
                                        .fromJson(array, FeedProperty[].class)) {
                                    //空数据过滤
                                    if (!TextUtils.isEmpty(feedProperty.getStringId())) {
                                        properties.add(feedProperty);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (properties.isEmpty()) {
                            readLocalFeedProperty(mContext, properties);
                        }
                        return properties;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static void readLocalFeedProperty(Context mContext, List<FeedProperty> properties) {
        InputStream in = mContext.getResources()
                .openRawResource(R.raw.feedspage);
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(in));
        for (FeedProperty feedProperty : GsonUtil.getGsonInstance()
                .fromJson(jsonElement, FeedProperty[].class)) {
            //空数据过滤
            if (!TextUtils.isEmpty(feedProperty.getStringId())) {
                properties.add(feedProperty);
            }
        }
    }

    /**
     * feed流
     *
     * @param cid        城市id
     * @param propertyId 首页分类id
     * @param page       分页
     * @param lastTime   请求时间戳
     */
    public static Observable<FeedList> getFeedList(
            long cid, String propertyId, int page, int perPage, long lastTime) {
        return HljHttp.getRetrofit()
                .create(HomeService.class)
                .getFeedList(cid, propertyId, page, perPage, lastTime > 0 ? lastTime : null)
                .map(new HljHttpResultFunc<FeedList>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取首页feed流列表
     *
     * @param cid
     * @param propertyIdStr
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HomeFeedList<List<HomeFeed>>> getHomeFeedList(
            long cid, String propertyIdStr, final int page, int perPage) {
        Map<String, Object> map = new HashMap<>();
        map.put("cid", cid);
        if (propertyIdStr != null && !propertyIdStr.equals("0")) {
            map.put("property_id", propertyIdStr);
        }
        return HljHttp.getRetrofit()
                .create(HomeService.class)
                .getHomeFeedList(map, page, perPage)
                .map(new HljHttpResultFunc<HomeFeedList<List<HomeFeed>>>())
                .map(new Func1<HomeFeedList<List<HomeFeed>>, HomeFeedList<List<HomeFeed>>>() {
                    @Override
                    public HomeFeedList<List<HomeFeed>> call(
                            HomeFeedList<List<HomeFeed>> homeFeedList) {
                        Log.d("Feed Optimization", new DateTime().toString() + "  mix data");
                        if (page == 1) {
                            //第一页加载额外数据
                            homeFeedList.parseAndMixedList(homeFeedList.getData());
                        }else {
                            homeFeedList.setMixedList(homeFeedList.getData());
                        }
                        return homeFeedList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取最新的直播信息
     * http://doc.hunliji.com/workspace/myWorkspace.do?projectId=25#4051
     *
     * @return
     */
    public static Observable<LiveChannel> getLatestLiveInfo() {
        return HljHttp.getRetrofit()
                .create(HomeService.class)
                .getLatestLiveInfo()
                .map(new HljHttpResultFunc<LiveChannel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
