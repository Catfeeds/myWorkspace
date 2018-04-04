package me.suncloud.marrymemo.api.finder;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.subpage.MarkedKeyword;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import me.suncloud.marrymemo.model.finder.CPMFeed;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.finder.CaseCategories;
import me.suncloud.marrymemo.model.finder.EntityComment;
import me.suncloud.marrymemo.model.finder.FindTabConfig;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.model.wrappers.HljHttpMarksData;
import me.suncloud.marrymemo.model.wrappers.HljHttpSubPageCategoryMarksData;
import me.suncloud.marrymemo.model.wrappers.HljHttpTopicsData;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public interface FinderService {

    /**
     * 专题列表推荐标签
     *
     * @return
     */
    @GET("p/wedding/Home/APISearchWord")
    Observable<HljHttpResult<HljHttpData<List<MarkedKeyword>>>> getMarkedKeywords(
            @Query("category") int category, @Query("type") int type);

    /**
     * 专题列表
     *
     * @param id
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPage/SubPagesListV3")
    Observable<HljHttpResult<HljHttpTopicsData>> getSubPageList(
            @Query("cate_id") long id, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 通过标签获取专题列表
     *
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPage/listByMark")
    Observable<HljHttpResult<HljHttpMarksData<List<TopicUrl>>>> getListByMarkId(
            @Query("mark_id") long markId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 标签组专题列表
     *
     * @param id
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APISubPageSetUp/subPages")
    Observable<HljHttpResult<HljHttpMarksData<List<TopicUrl>>>> getListByMarkGroupId(
            @Query("mark_group_id") long id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 专题详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPage/SubPageDetailV4")
    Observable<HljHttpResult<TopicUrl>> getSubPageDetail(@Query("id") long id);

    /**
     * 我的专题收藏列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPage/collect_list")
    Observable<HljHttpResult<HljHttpData<List<TopicUrl>>>> getSubPageCollectList(
            @Query("page") long page, @Query("per_page") int perPage);

    /**
     * 专题热门评论列表
     *
     * @param entityId
     * @param entityType
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunityComment/hot_list")
    Observable<HljHttpResult<HljHttpData<List<EntityComment>>>> getSubPageHotComments(
            @Query("entity_id") long entityId,
            @Query("entity_type") String entityType,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 专题最新评论列表
     */
    @GET("p/wedding/index.php/home/APICommunityComment/latest_list")
    Observable<HljHttpResult<HljHttpData<List<EntityComment>>>> getSubPageNewComments(
            @Query("entity_id") long entityId,
            @Query("entity_type") String entityType,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 推荐标签组列表
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPageSetUp/list")
    Observable<HljHttpResult<HljHttpSubPageCategoryMarksData>> getSubPageCategoryMarks();

    /**
     * 专栏排行榜
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/home/APISubPage/SubPagesRankingList")
    Observable<HljHttpResult<HljHttpData<List<TopicUrl>>>> getSubPageRanks(
            @Query("type") int type, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 推荐 1-3广告位
     *
     * @return
     */
    @GET("p/wedding/home/APISetMeal/cpmList")
    Observable<HljHttpResult<List<CPMFeed>>> getFinderCPMs(@Query("num") int num);

    /**
     * 保存备婚信息
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/home/APIUserPrepare/save")
    Observable<HljHttpResult<Object>> saveUserPrepare(@Body Map<String, Object> map);

    /**
     * 发现页-发现tab-精选/好评（笔记推荐列表）
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinder/recommends")
    Observable<HljHttpResult<HljHttpData<List<FinderFeed>>>> getFinderRecommendFeeds(
            @Query("last_id") long lastId, @Query("tab") String tab);

    /**
     * 更新数据
     *
     * @param ids
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinder/updates")
    Observable<HljHttpResult<HljHttpData<List<FinderFeed>>>> syncFinderRecommendFeeds(@Query("ids") String ids);

    /**
     * 发现页feeds流找相似
     *
     * @param excludeIds
     * @param id"
     * @param type
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinder/sames")
    Observable<HljHttpResult<HljHttpData<List<FinderFeed>>>> getFinderSimilarFeeds(
            @Query("exclude_ids") String excludeIds,
            @Query("id") long id,
            @Query("type") String type);

    /**
     * 找相似案例
     *
     * @param map
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinder/sames")
    Observable<HljHttpResult<HljHttpData<List<FinderFeed>>>> getSameCases(@QueryMap Map<String,
            Object> map);

    /**
     * 相关热门案例
     *
     * @param id
     * @param page
     * @return
     */
    @GET("p/wedding/index.php/home/APISetMeal/list")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getHotCases(
            @Query("id") long id,
            @Query("kind") String kind,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 发现页笔记列表
     *
     * @param lastPostAt
     * @param notebookType
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinder/notes")
    Observable<HljHttpResult<HljHttpData<List<FinderFeed>>>> getFinderNotes(
            @Query("last_post_at") String lastPostAt,
            @Query("note_book_type") int notebookType,
            @Query("per_page") int perPage);

    /**
     * 推荐笔记列表是否有新数据
     *
     * @return
     */
    @GET("p/wedding/index.php/home/APIFinder/hasNewRecommends")
    Observable<HljHttpResult<JsonElement>> hasNewRecommendNotes(
            @Query("last_id") long lastId, @Query("timestamp") long timestamp);


    /**
     * 案例类别列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIExample/categories")
    Observable<HljHttpResult<HljHttpData<List<CaseCategories>>>> getCaseCategories();

    /**
     * 案例列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APIExample/index")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getFinderCases(
            @Query("property_ids") String ids,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * 收藏案例
     *
     * @param map
     * @return
     */
    @POST("/p/wedding/index.php/home/APISetMeal/addCollection")
    Observable<HljHttpResult> postCaseCollect(@Body Map<String,Object> map);

    /**
     * 取消收藏案例
     *
     * @return
     */
    @POST("p/wedding/index.php/home/APISetMeal/cancelCollection")
    Observable<HljHttpResult> deleteCaseCollect(@Body Map<String,Object> map);

    /**
     * 发现页面德芙活动的配置信息
     * @return
     */
    @GET("p/wedding/index.php/home/APIDeFuChoActivity/tabConf")
    Observable<HljHttpResult<FindTabConfig>> getDeFuConf();
}