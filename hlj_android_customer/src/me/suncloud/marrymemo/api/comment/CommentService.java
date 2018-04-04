package me.suncloud.marrymemo.api.comment;

import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.wrappers.HljHttpCommentsData;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by chen_bin on 2018/1/8 0008.
 */
public interface CommentService {

    /**
     * 商家主页评论
     *
     * @param merchantId
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Home/APIOrderComment/merchantCommentList")
    Observable<HljHttpResult<HljHttpCommentsData>> getMerchantComments(
            @Query("merchant_id") long merchantId,
            @Query("tag_id") long markId,
            @Query("page") int page,
            @Query("per_page") int perPage);


    /**
     * orderComment评论详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Home/APIOrderComment/orderCommentDetail")
    Observable<HljHttpResult<ServiceComment>> getServiceCommentDetail(
            @Query("order_comment_id") long id);

    /**
     * 套餐评价标签列表（新）
     *
     * @param merchantId
     * @return
     */
    @GET("p/wedding/home/APIOrderComment/order_comment_tags")
    Observable<HljHttpResult<HljHttpData<List<ServiceCommentMark>>>> getServiceCommentMarks(
            @Query("merchant_id") long merchantId);

    /**
     * 本地服务评价
     *
     * @param url
     * @param map
     * @return
     */
    @POST
    Observable<HljHttpResult> commentServiceOrder(@Url String url, @Body Map<String, Object> map);


    /**
     * 评价婚品订单
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Shop/APIShopComment/Comment")
    Observable<HljHttpResult> commentProductOrder(@Body Map<String, Object> map);
}
