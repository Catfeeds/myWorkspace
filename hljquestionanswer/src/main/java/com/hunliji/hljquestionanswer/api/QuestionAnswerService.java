package com.hunliji.hljquestionanswer.api;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaListWrappers;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaVipMerchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljquestionanswer.models.AnswerComment;
import com.hunliji.hljquestionanswer.models.AnswerCommentResponse;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljquestionanswer.models.LatestQuestion;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;
import com.hunliji.hljquestionanswer.models.PostReportIdBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionResult;
import com.hunliji.hljquestionanswer.models.wrappers.RecQaWrappers;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mo_yu on 16/8/17.
 * 问答所有需要的接口定义
 */
public interface QuestionAnswerService {

    /**
     * 获取热门问答列表
     *
     * @param perPage 每页数目
     * @param page    页码
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunitySetup/hotQaAnswer")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getHotQaAnswer(
            @Query("per_page") int perPage, @Query("page") int page);

    /**
     * 获取全部问答列表
     *
     * @param perPage        每页数目
     * @param page           页码
     * @param lastAnswerTime 最后时间 （时间戳）分页时带上，防止重复
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<LatestQuestion>>>> getLatestQaAnswer(
            @Url String url,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("last_answer_time") long lastAnswerTime);

    /**
     * 获取全部问答列表
     *
     * @param perPage 每页数目
     * @param page    页码
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getLatestQaAnswer(
            @Url String url, @Query("per_page") int perPage, @Query("page") int page);

    /**
     * 获取推荐问答列表
     *
     * @param perPage 每页数目
     * @param page    页码
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<RecQaWrappers>>>> getMerchantQuestion(
            @Url String url, @Query("per_page") int perPage, @Query("page") int page);

    /**
     * 获取热门问答banner
     *
     * @param id
     * @param appVersion 版本号
     * @param city       城市id
     * @return
     */
    @GET
    Observable<HljHttpResult<PosterData>> getQaBanner(
            @Url String url,
            @Query("id") long id,
            @Query("app_version") String appVersion,
            @Query("city") long city);

    /**
     * 获取问题详情
     *
     * @param questionId 问题id
     * @return
     */
    @GET
    Observable<HljHttpResult<Question>> getQuestionDetail(
            @Url String url, @Query("id") long questionId);

    /**
     * 获取问题详情回答列表
     *
     * @param questionId 问题详情
     * @param perPage    每页数目
     * @param page       页码
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpCountData<List<Answer>>>> getAnswerList(
            @Url String url,
            @Query("question_id") long questionId,
            @Query("per_page") int perPage,
            @Query("page") int page);

    /**
     * 获取回答详情
     *
     * @param answerId 回答id
     * @return
     */
    @GET
    Observable<HljHttpResult<Answer>> getAnswerDetail(
            @Url String url,
            @Query("answer_id") long answerId,
            @Query("show_question") int show_question);

    /**
     * 获取回答评论列表
     *
     * @param entityId   回答ID
     * @param entityType 类型
     * @param perPage    每页数目
     * @param page       页码
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<AnswerComment>>>> getAnswerCommentList(
            @Url String url,
            @Query("entity_id") long entityId,
            @Query("entity_type") String entityType,
            @Query("per_page") int perPage,
            @Query("page") int page);


    /**
     * 点赞回答
     *
     * @param url
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult> postAnswerPraise(
            @Url String url, @Body PostPraiseIdBody body);

    /**
     * 问题标签列表(提问时选标签用)
     *
     * @param page 页数
     * @return
     */

    @GET("p/wedding/index.php/home/APIQaQuestion/marks")
    Observable<HljHttpResult<HljHttpData<List<Mark>>>> getMarks(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 关联标签列表(问答首页)
     *
     * @param page 页数
     * @return
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/recommend_mark_list")
    Observable<HljHttpResult<HljHttpCountData<List<Mark>>>> getMarkList(
            @Query("page") int page, @Query("per_page") int perPage, @Query("city") long city);

    /**
     * 添加评论
     *
     * @param url
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult<AnswerCommentResponse>> postAnswerComment(
            @Url String url, @Body PostCommentBody body);

    /**
     * 发布/编辑问题
     */
    @POST("p/wedding/index.php/home/APIQaQuestion/edit")
    Observable<HljHttpResult<PostQuestionResult>> postQuestion(@Body PostQuestionBody qBody);


    /**
     * 我的提问列表
     *
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/myQuestion?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getMyQuestions(@Query("page") int page);

    /**
     * 获取对应id用户的回答列表 传userId获取用户回答 传merchantId获取商家回答 商家列表排序规则不同
     *
     * @param userId     用户id
     * @param merchantId 商家id
     * @param page       页数
     * @return
     */
    @GET("p/wedding/index.php/home/APIQaAnswer/myAnswerV2?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getOwnerAnswers(
            @Query("user_id") Long userId,
            @Query("merchant_id") Long merchantId,
            @Query("page") int page);

    /**
     * 我收藏的回答列表
     *
     * @param page 页数
     */
    @GET("p/wedding/index.php/Home/APIQaAnswer/myFollowAnswer?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getMyFollowAnswers(
            @Query("page") int page);

    /**
     * 我关注的问题列表
     *
     * @param page 页数
     */
    @GET("p/wedding/index.php/Admin/APIQaQuestion/followQuestion?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<RecQaWrappers>>>> getMyMerchantFollow(
            @Query("page") int page);


    /**
     * 举报
     * <p/>
     * 类型 thread:话题 post:回帖 question:问题 answer:回答
     *
     * @param url
     * @param body
     * @return
     */
    @POST
    Observable<HljHttpResult<Object>> postReport(
            @Url String url, @Body PostReportIdBody body);


    /**
     * 标签详情
     *
     * @param id 标签id
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/markDetail")
    Observable<HljHttpResult<Mark>> getMark(@Query("mark_id") long id);

    /**
     * 标签下问题
     *
     * @param id   标签id
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/markQuestion?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getMarkQuestions(
            @Query("mark_id") long id, @Query("page") int page);

    /**
     * 标签下回答
     *
     * @param id   标签id
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaAnswer/markHotAnswer?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getMarkAnswers(
            @Query("mark_id") long id, @Query("page") int page);

    /**
     * 发布/编辑回答
     */
    @POST
    Observable<HljHttpResult<BasePostResult>> postAnswer(
            @Url String url, @Body PostAnswerBody body);


    /**
     * 获取问答列表(混杂)
     *
     * @param perPage 每页数目
     * @param page    页码
     * @return
     */
    @GET
    Observable<HljHttpResult<HljHttpData<List<QaListWrappers>>>> getQaList(
            @Url String url, @Query("per_page") int perPage, @Query("page") int page);


    /**
     * 检索已存在的问题
     *
     * @param keyword 关键字
     * @param perPage 每页数目
     * @param page    页码
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/tip")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getSameQuestions(
            @Query("keyword") String keyword,
            @Query("per_page") int perPage,
            @Query("page") int page);

    /**
     * 本周热门问题
     *
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/week_question?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getWeekQuestions(
            @Query("page") int page);

    /**
     * 本周热门回答
     *
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaAnswer/week_answer?per_page=10")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getWeekAnswers(@Query("page") int page);

    /**
     * 相关问题
     *
     * @param questionId
     */
    @GET("p/wedding/index.php/Home/APIQaQuestion/relatedQuestion")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getRelatedQuestion(@Query("question_id") long questionId);


    /***
     * 问答列表
     */
    @GET("/p/wedding/Home/APIQaQuestion/list")
    Observable<HljHttpResult<HljHttpQuestion<List<Question>>>> getQAList(
            @Query("merchant_id") long merchant_id,
            @Query("page") int page,
            @Query("per_page") int per_page);

    /**
     * 获取热门问答列表
     *
     * @param perPage 每页数目
     * @param page    页码
     * @return
     */
    @GET("p/wedding/index.php/home/APICommunitySetup/hotQaAnswerV2")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getHotQaQuestions(
            @Query("per_page") int perPage, @Query("page") int page);

    /**
     * 标签下问题
     *
     * @param id   标签id
     * @param page 页数
     */
    @GET("p/wedding/index.php/home/APIQaQuestion/markQuestionV2?per_page=20")
    Observable<HljHttpResult<HljHttpData<List<Question>>>> getMarkQuestionsV2(@Query("page") int page);

    /**
     * 获取问答大v商家
     * @return
     */
    @GET("p/wedding/index.php/Home/APIQaQuestion/active_merchant")
    Observable<HljHttpResult<List<QaVipMerchant>>> getQaVipMerchantList();
}
