package me.suncloud.marrymemo.api.communitythreads;

import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPostPraiseBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPostPraiseResult;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPraisedAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadIdBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThreadPostDeleteIdBody;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoPraiseBody;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.List;

import me.suncloud.marrymemo.model.community.HljCommunityPostsData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/8/25.
 */
public class CommunityThreadApi {

    /**
     * 请求某个用户发表的话题列表
     *
     * @param userId
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getUserThreadsObb(
            long userId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .getMyThreadList(userId, page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 话题(0楼)的点赞用户列表
     *
     * @param postId thread里0楼的post id
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CommunityPraisedAuthor>>> getPraisedAuthorsObb(
            long postId, int page) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .getPraisedAuthors(postId, 20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityPraisedAuthor>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 热帖排行榜
     *
     * @param type
     * @return
     */
    public static Observable<HljHttpData<List<CommunityThread>>> getHotThreadRanks(
            String type, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .getHotThreadRanks(type, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取话题详情
     *
     * @param id
     * @return
     */
    public static Observable<CommunityThread> getCommunityThread(long id) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .getCommunityThread(id)
                .map(new HljHttpResultFunc<CommunityThread>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取话题评论列表
     *
     * @param threadId  话题id
     * @param no        起始楼层
     * @param onlyWatch 是否只看楼主的参数，"original"，就是只看楼主，""则是看全部
     * @param order     排序参数
     * @return
     */
    public static Observable<HljCommunityPostsData> getCommunityPosts(
            long threadId, int no, String onlyWatch, String order) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                //                .getCommunityPosts(204165L, 20, no, onlyWatch, order)
                .getCommunityPosts(threadId, 20, no, onlyWatch, order)
                .map(new HljHttpResultFunc<HljCommunityPostsData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取话题的热门评论
     *
     * @param id 话题id
     * @return
     */
    public static Observable<List<CommunityPost>> getHotCommunityPost(long id) {
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                //                                                .getHotCommunityPost(204165L)
                .getHotCommunityPost(id)
                .map(new HljHttpResultFunc<List<CommunityPost>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 话题点赞或post点赞，
     *
     * @param postId 话题本身的post的id，或者post的id
     * @return
     */
    public static Observable<CommunityPostPraiseResult> postPraise(long postId) {
        CommunityPostPraiseBody body = new CommunityPostPraiseBody(postId);
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .postPraise(body)
                .map(new HljHttpResultFunc<CommunityPostPraiseResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏话题
     *
     * @param threadId
     * @return
     */
    public static Observable<Object> postThreadCollect(long threadId) {
        CommunityThreadIdBody body = new CommunityThreadIdBody(threadId);
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .postThreadCollect(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除post
     *
     * @param threadId
     * @param postId
     * @return
     */
    public static Observable<Object> deletePost(long threadId, long postId) {
        CommunityThreadPostDeleteIdBody body = new CommunityThreadPostDeleteIdBody(threadId,
                postId);
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .deletePost(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 点赞婚纱照组图
     *
     * @param id
     * @return
     */
    public static Observable<CommunityPostPraiseResult> praiseWeddingPhotoGroup(long id) {
        WeddingPhotoPraiseBody body = new WeddingPhotoPraiseBody(id);
        return HljHttp.getRetrofit()
                .create(CommunityThreadService.class)
                .praiseGroupPhoto(body)
                .map(new HljHttpResultFunc<CommunityPostPraiseResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static final String COMMUNITY_POST_ORDER_ASC = "asc";
    public static final String COMMUNITY_POST_ORDER_DESC = "desc";
    public static final String COMMUNITY_POST_PARAMS_ORIGINAL = "original";
    public static final String COMMUNITY_POST_PARAMS_ALL = "";
}