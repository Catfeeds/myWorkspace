package me.suncloud.marrymemo.api.community;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.HotCommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.community.HljHttpCommunityFeedData;
import me.suncloud.marrymemo.model.community.ObtainCommunityMaterial;
import me.suncloud.marrymemo.model.community.PosterWatchFeed;
import me.suncloud.marrymemo.model.community.QaLiveEntranceData;
import me.suncloud.marrymemo.model.community.SimilarWeddingDetail;
import me.suncloud.marrymemo.model.community.wrappers.HljWeddingBiblesData;
import me.suncloud.marrymemo.model.realm.WeddingPhotoDraft;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/8/30.关注feed流
 */
public class CommunityApi {

    /**
     * 已登录的状态下请求已关注的频道
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityChannel>>> getIndexListObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getIndexList(1, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityChannel>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 未关注频道或未登录，获取热门频道推荐列表
     *
     * @param city 城市id（cid）
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<HotCommunityChannel>>> getHotListObb(
            Long city, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getHotList(city, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<HotCommunityChannel>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取精选频道数据(配置的频道)
     *
     * @param city 城市id（cid）
     * @param page
     * @return
     */
    public static Observable<List<HotCommunityChannel>> getChoiceListObb(
            Long city, int page, int per_page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getChoiceList(city, page, per_page)
                .map(new HljHttpResultFunc<List<HotCommunityChannel>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区首页热门推荐列表
     *
     * @param isNew   是否是刷新 0历史 1刷新
     * @param offset  偏移量（当前数据个数）
     * @param perPage
     * @return
     */
    public static Observable<HljHttpCountData<List<CommunityFeed>>> getRecommendListObb(
            int isNew, int perPage, int offset) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getRecommendThreadList(isNew, perPage, offset)
                .map(new HljHttpResultFunc<HljHttpCountData<List<CommunityFeed>>>())
                .map(new Func1<HljHttpCountData<List<CommunityFeed>>,
                        HljHttpCountData<List<CommunityFeed>>>() {
                    @Override
                    public HljHttpCountData<List<CommunityFeed>> call(
                            final HljHttpCountData<List<CommunityFeed>> listHljHttpCountData) {
                        if (listHljHttpCountData != null && !CommonUtil.isCollectionEmpty(
                                listHljHttpCountData.getData())) {
                            for (int i = 0, size = listHljHttpCountData.getData()
                                    .size(); i < Math.min(size, 2); i++) {
                                listHljHttpCountData.getData()
                                        .get(i)
                                        .getEntity();
                            }
                            if (listHljHttpCountData.getData()
                                    .size() > 2) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 2, size = listHljHttpCountData.getData()
                                                .size(); i < size; i++) {
                                            listHljHttpCountData.getData()
                                                    .get(i)
                                                    .getEntity();
                                        }
                                    }
                                }).start();
                            }
                        }
                        return listHljHttpCountData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 精编话题列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getRichThreadListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getRichThreadList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 精编话题列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getPrizeThreadListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getPrizeThreadList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区通用话题列表
     *
     * @param url
     * @param lastId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getCommunityThreadsObb(
            String url, long lastId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCommunityThreads(url, lastId, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区频道置顶话题列表
     *
     * @param city
     * @param communityChannelId
     * @return
     */
    public static Observable<List<CommunityThread>> getTopThreadsObb(
            long communityChannelId, long city) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getTopThreads(communityChannelId, city)
                .map(new HljHttpResultFunc<List<CommunityThread>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区话题频道详情
     *
     * @param id
     * @return
     */
    public static Observable<CommunityChannel> getCommunityChannelDetailObb(
            long id) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCommunityChannelDetail(id)
                .map(new HljHttpResultFunc<CommunityChannel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据用户定位的城市，获取一个当地频道
     *
     * @return
     */
    public static Observable<CommunityChannel> getLocalChannelObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getLocalChannel()
                .map(new HljHttpResultFunc<CommunityChannel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 同城备婚下最新4个用户的数据
     *
     * @return
     */
    public static Observable<HljHttpData<List<CommunityAuthor>>> getNewestThreadsObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getNewestThreads(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityAuthor>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发布话题
     *
     * @param weddingPhotoDraft 草稿与上传使用同一个模型
     * @return
     */
    public static Observable<BasePostResult> createWeddingPhoto(
            WeddingPhotoDraft weddingPhotoDraft) {
        if (weddingPhotoDraft.getMerchantId() != null && weddingPhotoDraft.getMerchantId() > 0) {
            weddingPhotoDraft.setUnrecordedMerchantName(null);
        } else if (!TextUtils.isEmpty(weddingPhotoDraft.getUnrecordedMerchantName())) {
            weddingPhotoDraft.setMerchantId(null);
        } else {
            weddingPhotoDraft.setMerchantId(null);
            weddingPhotoDraft.setUnrecordedMerchantName(null);
        }
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .createWeddingPhoto(weddingPhotoDraft)
                .map(new HljHttpResultFunc<BasePostResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我发布的话题列表
     *
     * @param userId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getPublishThreadsObb(
            long userId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getPublishThreads(userId, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我收藏的话题列表
     *
     * @param userId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getCollectThreadsObb(
            long userId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCollectThreads(userId, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我回帖的话题列表
     *
     * @param userId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityPost>>> getPostThreadsObb(
            long userId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getPostThreads(userId, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityPost>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区首页直播、问答入口数据
     *
     * @return
     */
    public static Observable<QaLiveEntranceData> getQaLiveEntranceData() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getQaLiveEntranceData()
                .map(new HljHttpResultFunc<QaLiveEntranceData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取一个关注的随机频道
     *
     * @return
     */
    public static Observable<CommunityChannel> getOneChannelObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getOneChannel()
                .map(new HljHttpResultFunc<CommunityChannel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发布话题
     *
     * @param map
     * @return
     */
    public static Observable<JsonElement> createThreadObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .createThread(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 编辑话题
     *
     * @param map
     * @return
     */
    public static Observable<JsonElement> modifyThreadObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .modifyThread(map)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 回帖
     *
     * @param map
     * @return
     */
    public static Observable createPostObb(Map<String, Object> map) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .createPost(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 同婚期详情
     *
     * @return
     */
    public static Observable<SimilarWeddingDetail> getSimilarWeddingDetailObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getSimilarWeddingDetail()
                .map(new HljHttpResultFunc<SimilarWeddingDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 同婚期列表
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<CommunityFeed>>> getSimilarWeddingFeedsObb(
            Integer type, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getSimilarWeddingFeeds(type, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityFeed>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 结婚宝典
     *
     * @return
     */
    public static Observable<HljWeddingBiblesData> getWeddingBiblesObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getWeddingBibles()
                .map(new HljHttpResultFunc<HljWeddingBiblesData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 结婚宝典同城讨论
     *
     * @return
     */
    public static Observable<HljHttpData<List<CommunityFeed>>> getWeddingBibleFeedsObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getWeddingBibleFeeds(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityFeed>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 首页固定频道列表
     *
     * @return
     */
    public static Observable<List<CommunityChannel>> getHomeFixedChannels() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getHomeFixedChannelsObb()
                .map(new HljHttpResultFunc<List<CommunityChannel>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户常逛新娘圈列表
     *
     * @return
     */
    public static Observable<List<CommunityChannel>> getRecentScanChannelsObb(String recentIds) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getRecentScanChannels(recentIds)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityChannel>>>())
                .map(new Func1<HljHttpData<List<CommunityChannel>>, List<CommunityChannel>>() {
                    @Override
                    public List<CommunityChannel> call(
                            HljHttpData<List<CommunityChannel>> listHljHttpData) {
                        return listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 清单、婚礼花费、新娘购物tab
     *
     * @param tabId   1清单、2婚礼花费、3新娘购物t
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<CommunityFeed>>> getRecommendNormalListObb(
            int tabId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getRecommendNormalList(tabId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityFeed>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 精选tab
     *
     * @return
     */
    public static Observable<HljHttpCommunityFeedData<List<CommunityFeed>>>
    getCommunityChoiceListObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCommunityChoiceList(page, perPage)
                .map(new HljHttpResultFunc<HljHttpCommunityFeedData<List<CommunityFeed>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 推荐新娘圈列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<CommunityChannel>>> recommendChannelList() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .recommendChannelList()
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityChannel>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的新娘圈列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<CommunityChannel>>> myChannelList() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .myChannelList()
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityChannel>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 新娘圈领资料
     *
     * @return
     */
    public static Observable<ObtainCommunityMaterial> ObtainMaterialList() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .ObtainMaterialList()
                .map(new HljHttpResultFunc<ObtainCommunityMaterial>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 领资料总人数
     *
     * @return
     */
    public static Observable<String> getMaterialTaskCountObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getMaterialTaskCount()
                .map(new HljHttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取poster点击量
     *
     * @return
     */
    public static Observable<List<PosterWatchFeed>> getPosterWatchCountObb() {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getPosterWatchCount()
                .map(new HljHttpResultFunc<HljHttpData<List<PosterWatchFeed>>>())
                .map(new Func1<HljHttpData<List<PosterWatchFeed>>, List<PosterWatchFeed>>() {
                    @Override
                    public List<PosterWatchFeed> call(HljHttpData<List<PosterWatchFeed>> listHljHttpData) {
                        return listHljHttpData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 点击poster
     *
     * @return
     */
    public static Observable addPosterWatchCountObb(String type) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",type);
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .addPosterWatchCount(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 新娘说活动详情
     *
     * @param id
     * @return
     */
    public static Observable<CommunityEvent> getCommunityEventDetail(long id) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCommunityEventDetail(id)
                .map(new HljHttpResultFunc<CommunityEvent>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 活动帖子列表
     *
     * @param communityActivityId
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getCommunityEventThreadList(
            long communityActivityId,
            int page) {
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .getCommunityEventThreadList(communityActivityId, HljCommon.PER_PAGE, page)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 关注话题
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpResult> followCommunityChannel(
            long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .followCommunityChannel(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 取消关注话题
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpResult> unFollowCommunityChannel(
            long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return HljHttp.getRetrofit()
                .create(CommunityService.class)
                .unFollowCommunityChannel(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
