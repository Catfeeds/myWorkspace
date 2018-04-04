package com.hunliji.marrybiz.api.comment;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.comment.ComplainDetail;
import com.hunliji.marrybiz.model.comment.DelCommentBody;
import com.hunliji.marrybiz.model.comment.SubmitAppealBody;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by hua_rong on 2017/4/17.
 * 评论管理
 */

public interface CommentService {


    /**
     * 获取评论概述一系列数据
     *
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIOrder/comment_statistics")
    Observable<HljHttpResult<CommentStatistics>> getCommentStatistics();

    /**
     * 商家评论列表
     *
     * @param merchant_id
     * @param reputation  is_merchant 是否是商家
     * @return
     */
    @GET("/p/wedding/index.php/Home/APIOrderComment/merchantCommentList")
    Observable<HljHttpResult<HljHttpData<List<ServiceComment>>>> getMerchantCommentList(
            @Query("page") int page,
            @Query("per_page") int per_page,
            @Query("photos_filter") long photosFilter,
            @Query("merchant_id") long merchant_id,
            @Query("reputation") long reputation);

    /**
     * 商家评论详情页
     *
     * @param order_comment_id
     * @return
     */
    @GET("/p/wedding/index.php/Home/APIOrderComment/orderCommentDetail")
    Observable<HljHttpResult<ServiceComment>> getOrderCommentDetail(
            @Query("order_comment_id") long order_comment_id);

    /**
     * 删除评论
     *
     * @param body
     * @return
     */
    @POST("/p/wedding/index.php/Home/APICommunityComment/deleteFunc")
    Observable<HljHttpResult<Object>> delComment(@Body DelCommentBody body);


    /**
     * 评论申诉
     *
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult<Object>> submitAppeal(@Url String url, @Body SubmitAppealBody body);


    /**
     * 申诉详情
     *
     * @param id 申诉id
     * @return
     */
    @GET
    Observable<HljHttpResult<ComplainDetail>> getComplainDetail(
            @Url String url,
            @Query("id") long id);

    /**
     * 评论申诉检查
     */
    @POST("/p/wedding/index.php/Admin/APIOrderCommentAppeal/CheckCommunityAppeal")
    Observable<HljHttpResult> CheckCommunityAppeal(@Body JsonObject jsonObject);


}
