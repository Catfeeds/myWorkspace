package me.suncloud.marrymemo.api.community;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.HotCommunityChannel;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.community.HljHttpCommunityFeedData;
import me.suncloud.marrymemo.model.community.ObtainCommunityMaterial;
import me.suncloud.marrymemo.model.community.PosterWatchFeed;
import me.suncloud.marrymemo.model.community.QaLiveEntranceData;
import me.suncloud.marrymemo.model.community.SimilarWeddingDetail;
import me.suncloud.marrymemo.model.community.wrappers.HljWeddingBiblesData;
import me.suncloud.marrymemo.model.realm.WeddingPhotoDraft;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mo_yu on 2016/8/30.关注feed流
 */
public interface CommunityService {

    /**
     * 我关注的频道
     *
     * @return
     */
    @GET("p/wedding/Home/APICommunityChannel/index")
    Observable<HljHttpResult<HljHttpData<List<CommunityChannel>>>> getIndexList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 热门推荐频道
     *
     * @param city
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/Home/APICommunitySetup/HotCommunityChannel")
    Observable<HljHttpResult<HljHttpData<List<HotCommunityChannel>>>> getHotList(
            @Query("city") Long city, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 精选频道
     *
     * @param city
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APICommunitySetup/ChoiceCommunityChannel")
    Observable<HljHttpResult<List<HotCommunityChannel>>> getChoiceList(
            @Query("city") Long city, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 热门推荐列表
     *
     * @param isNew   是否是刷新 0历史 1刷新
     * @param offset  偏移量（当前数据个数）
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityHistory/recommendCommunity")
    Observable<HljHttpResult<HljHttpCountData<List<CommunityFeed>>>> getRecommendThreadList(
            @Query("is_new") int isNew,
            @Query("per_page") int perPage,
            @Query("offset") int offset);

    /**
     * 精编话题列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityThread/richThreadList")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getRichThreadList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 有奖话题列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityThread/prize_thread_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getPrizeThreadList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 话题列表
     *
     * @param url
     * @param lastId
     * @param page
     * @param perPage
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getCommunityThreads(
            @Url String url,
            @Query("last_id") long lastId,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 社区话题频道详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/Home/APICommunityChannel/detail")
    Observable<HljHttpResult<CommunityChannel>> getCommunityChannelDetail(
            @Query("id") long id);

    /**
     * 社区频道置顶话题列表
     *
     * @param communityChannelId
     * @param city
     * @return
     */
    @GET("p/wedding/Home/APICommunityChannel/top_threads")
    Observable<HljHttpResult<List<CommunityThread>>> getTopThreads(
            @Query("community_channel_id") long communityChannelId, @Query("city") long city);

    /**
     * 根据用户定位的城市，获取一个当地频道
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICommunityChannel/local_channel")
    Observable<HljHttpResult<CommunityChannel>> getLocalChannel();

    /**
     * 同城备婚下最新4个用户的数据
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICommunityThread/newest_thread")
    Observable<HljHttpResult<HljHttpData<List<CommunityAuthor>>>> getNewestThreads(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 发布话题
     *
     * @param weddingPhotoDraft 草稿与上传使用同一个模型
     * @return
     */
    @POST("p/wedding/Home/APICommunityThread/create_wedding_photo")
    Observable<HljHttpResult<BasePostResult>> createWeddingPhoto(
            @Body WeddingPhotoDraft weddingPhotoDraft);

    /**
     * 我发布的话题列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityThread/my_thread_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getPublishThreads(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 我收藏的话题列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityThread/my_collect_thread_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getCollectThreads(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 我回帖的话题列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityPost/myCommunityPostList")
    Observable<HljHttpResult<HljHttpData<List<CommunityPost>>>> getPostThreads(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 社区首页问答、直播入口数据
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICommunitySetup/qa_live_entrance")
    Observable<HljHttpResult<QaLiveEntranceData>> getQaLiveEntranceData();

    /**
     * 获取一个关注的随机频道
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICommunityChannel/oneChannel")
    Observable<HljHttpResult<CommunityChannel>> getOneChannel();

    /**
     * 发布话题
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APICommunityThread/CreateNewThreadV2")
    Observable<HljHttpResult<JsonElement>> createThread(@Body Map<String, Object> map);

    /**
     * 编辑话题
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APICommunityThread/modify_thread")
    Observable<HljHttpResult<JsonElement>> modifyThread(@Body Map<String, Object> map);

    /**
     * 回帖
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APICommunityPost/CreateNewPostV2")
    Observable<HljHttpResult> createPost(@Body Map<String, Object> map);

    /**
     * 同婚期详情
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityCombine/similar_wedding_detail")
    Observable<HljHttpResult<SimilarWeddingDetail>> getSimilarWeddingDetail();

    /**
     * 同婚期列表
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV3/similar_wedding_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityFeed>>>> getSimilarWeddingFeeds(
            @Query("is_refined") Integer type,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 结婚宝典
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIMarryBible/list")
    Observable<HljHttpResult<HljWeddingBiblesData>> getWeddingBibles();


    /**
     * 结婚宝典同城讨论
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV3/localMarryBible")
    Observable<HljHttpResult<HljHttpData<List<CommunityFeed>>>> getWeddingBibleFeeds(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 首页固定频道列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityChannel/home_fixed_channel")
    Observable<HljHttpResult<List<CommunityChannel>>> getHomeFixedChannelsObb();

    /**
     * 用户常逛新娘圈列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityChannel/recent_scan_channel")
    Observable<HljHttpResult<HljHttpData<List<CommunityChannel>>>> getRecentScanChannels(
            @Query("recent_ids") String recentIds);

    /**
     * 清单、婚礼花费、新娘购物tab
     *
     * @param tabId   1清单、2婚礼花费、3新娘购物t
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISearchV3/community_tab_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityFeed>>>> getRecommendNormalList(
            @Query("tab_id") int tabId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 精选tab
     *
     * @return
     */
    @GET("p/wedding/Home/APICommunityCombine/choice_list")
    Observable<HljHttpResult<HljHttpCommunityFeedData<List<CommunityFeed>>>> getCommunityChoiceList(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 推荐新娘圈列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityChannel/recommend_channel")
    Observable<HljHttpResult<HljHttpData<List<CommunityChannel>>>> recommendChannelList();

    /**
     * 我的新娘圈列表
     *
     * @return
     */
    @GET("p/wedding/Home/APICommunityChannel/user_channel")
    Observable<HljHttpResult<HljHttpData<List<CommunityChannel>>>> myChannelList();

    /**
     * 新娘圈领资料
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIMaterialTask/list")
    Observable<HljHttpResult<ObtainCommunityMaterial>> ObtainMaterialList();

    /**
     * 领资料总人数
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIMaterialTask/totalCount")
    Observable<HljHttpResult<String>> getMaterialTaskCount();

    /**
     * 获取poster点击量
     *
     * @return
     */
    @GET("p/wedding/Home/APICommunityCombine/today_watch_count")
    Observable<HljHttpResult<HljHttpData<List<PosterWatchFeed>>>> getPosterWatchCount();

    /**
     * 点击poster
     *
     * @return
     */
    @POST("p/wedding/Home/APICommunityCombine/add_today_watch_count")
    Observable<HljHttpResult> addPosterWatchCount(@Body Map<String, Object> map);

    /**
     * 新娘说活动详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityActivity/detail")
    Observable<HljHttpResult<CommunityEvent>> getCommunityEventDetail(@Query("id") long id);

    /**
     * 活动帖子列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityActivity/communityThreadList")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getCommunityEventThreadList(
            @Query("community_activity_id") long id,
            @Query("per_page") int perPage,
            @Query("page") int page);

    /**
     * 关注话题
     *
     * @param params
     * @return
     */
    @POST("p/wedding/Home/APICommunitySetup/follow")
    Observable<HljHttpResult> followCommunityChannel(@Body Map<String, Object> params);

    /**
     * 取消关注话题
     *
     * @param params
     * @return
     */
    @POST("p/wedding/Home/APICommunitySetup/unfollow")
    Observable<HljHttpResult> unFollowCommunityChannel(@Body Map<String, Object> params);

}
