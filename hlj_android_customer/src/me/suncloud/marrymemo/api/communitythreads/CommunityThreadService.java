package me.suncloud.marrymemo.api.communitythreads;

import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPostPraiseBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPostPraiseResult;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPraisedAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadIdBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPostDeleteIdBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoPraiseBody;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.community.HljCommunityPostsData;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by werther on 16/8/25.
 */
public interface CommunityThreadService {
    @GET("p/wedding/index.php/home/APICommunityThread/my_thread_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getMyThreadList(
            @Query("user_id") long userId, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 话题(0楼)的点赞用户列表
     *
     * @param postId
     * @return
     */
    @GET("/p/wedding/index.php/Home/APICommunityPostPraise/list")
    Observable<HljHttpResult<HljHttpData<List<CommunityPraisedAuthor>>>> getPraisedAuthors(
            @Query("community_post_id") long postId,
            @Query("per_page") int perPage,
            @Query("page") int page);

    /**
     * 热帖排行榜
     *
     * @param type
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/Home/APICommunityThread/top_posts_list")
    Observable<HljHttpResult<HljHttpData<List<CommunityThread>>>> getHotThreadRanks(
            @Query("type") String type, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 获取话题详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/Home/APICommunityThread/ThreadDetailV2")
    Observable<HljHttpResult<CommunityThread>> getCommunityThread(@Query("id") long id);

    /**
     * 获取话题评论列表
     *
     * @param threadId  话题id
     * @param perPage
     * @param no        起始楼层
     * @param onlyWatch 是否只看楼主
     * @param order     排序参数
     * @return
     */
    //    @GET("http://www7.hunliji.com/p/wedding/Home/APICommunityPost/PostListV2")
    @GET("p/wedding/Home/APICommunityPost/PostListV2")
    Observable<HljHttpResult<HljCommunityPostsData>> getCommunityPosts(
            @Query("thread_id") long threadId,
            @Query("per_page") int perPage,
            @Query("no") int no,
            @Query("only_watch") String onlyWatch,
            @Query("order") String order);

    /**
     * 获取话题的热门评论
     *
     * @param threadId 话题id
     * @return
     */
    //    @GET("http://www7.hunliji.com/p/wedding/Home/APICommunityPost/HotPostsV2")
    @GET("p/wedding/Home/APICommunityPost/HotPostsV2")
    Observable<HljHttpResult<List<CommunityPost>>> getHotCommunityPost(
            @Query("thread_id") long threadId);

    /**
     * post点赞
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Home/APICommunityPostPraise/PostPraises")
    Observable<HljHttpResult<CommunityPostPraiseResult>> postPraise(
            @Body CommunityPostPraiseBody body);

    /**
     * 收藏thread
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APICommunityThread/community_thread_collect")
    Observable<HljHttpResult> postThreadCollect(@Body CommunityThreadIdBody body);

    /**
     * 删除post
     *
     * @param body
     * @return
     */
    @POST("p/wedding/Home/APICommunityPost/del_my_community_post")
    Observable<HljHttpResult> deletePost(@Body CommunityThreadPostDeleteIdBody body);

    /**
     * 点赞婚纱照组图
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/home/APICommunityPraise/like")
    Observable<HljHttpResult<CommunityPostPraiseResult>> praiseGroupPhoto(
            @Body WeddingPhotoPraiseBody body);
}
