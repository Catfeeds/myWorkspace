package com.hunliji.hljquestionanswer.api;

import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaListWrappers;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaVipMerchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljquestionanswer.models.AnswerComment;
import com.hunliji.hljquestionanswer.models.AnswerCommentResponse;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;
import com.hunliji.hljquestionanswer.models.PostReportIdBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionResult;
import com.hunliji.hljquestionanswer.models.wrappers.RecQaWrappers;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 16/8/17.
 * 问答API接口Http方法汇总
 */
public class QuestionAnswerApi {

    public static final int TYPE_COMMUNITY_QA = 1;
    public static final int TYPE_MERCHANT_QA = 2;

    /**
     * 获取热门问答列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Answer>>> getHotQaAnswerObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getHotQaAnswer(20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取全部问答列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Question>>> getLatestQaAnswerObb(
            String url, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getLatestQaAnswer(url, 20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取推荐问答列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<RecQaWrappers>>> getMerchantQuestionObb(
            String url, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMerchantQuestion(url, 20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<RecQaWrappers>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取热门问答banner
     *
     * @return
     */
    public static Observable<PosterData> getQaBannerObb(
            int id, String appVersion, long city) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getQaBanner("p/wedding/index" + "" + ".php/home/APIPosterBlock/block_info",
                        id,
                        appVersion,
                        city)
                .map(new HljHttpResultFunc<PosterData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取问题详情/编辑
     *
     * @param questionId 编辑问题时取questionId
     * @return
     */
    public static Observable<Question> getQuestionDetail(long questionId) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getQuestionDetail("p/wedding/index" + "" + ".php/home/APIQaQuestion/edit",
                        questionId)
                .map(new HljHttpResultFunc<Question>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取问题详情回答列表
     *
     * @param questionId
     * @param page
     * @param type       新增时：1社区问答 2商家问答
     * @return
     */
    public static Observable<HljHttpCountData<List<Answer>>> getAnswerList(
            long questionId, int page, int type) {
        String url;
        if (type == TYPE_MERCHANT_QA) {
            url = "/p/wedding/index.php/home/APIQaAnswer/merchantQuestionAnswer";
        } else {
            url = "p/wedding/index.php/home/APIQaAnswer/questionAnswer";
        }
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getAnswerList(url, questionId, 20, page)
                .map(new HljHttpResultFunc<HljHttpCountData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取回答详情
     *
     * @param answerId
     * @return
     */
    public static Observable<Answer> getAnswerDetail(long answerId) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getAnswerDetail("p/wedding/index.php/Home/APIQaAnswer/answerDetail", answerId, 1)
                .map(new HljHttpResultFunc<Answer>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取回答评论列表（通用评论接口）
     *
     * @param entityId
     * @param entityType 类型 SubPage，QaAnswer;
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<AnswerComment>>> getAnswerCommentList(
            long entityId, String entityType, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getAnswerCommentList("p/wedding/index" + "" + "" + "" + "" + "" + "" + "" + "" +
                                ".php/home/APICommunityComment/latest_list",
                        entityId,
                        entityType,
                        20,
                        page)
                .map(new HljHttpResultFunc<HljHttpData<List<AnswerComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发表回答的评论
     *
     * @param body
     * @return
     */
    public static Observable<AnswerCommentResponse> postCommentObb(
            PostCommentBody body) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .postAnswerComment("p/wedding/index.php/Home/APICommunityComment/addFunc", body)
                .map(new HljHttpResultFunc<AnswerCommentResponse>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 回答点赞，反对
     *
     * @param body
     * @return
     */
    public static Observable postPraiseAnswerObb(PostPraiseIdBody body) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .postAnswerPraise("p/wedding/index.php/home/APIQaAnswer/voteAnswer", body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 举报通用接口，反对
     * <p/>
     * 类型 thread:话题 post:回帖 question:问题 answer:回答
     *
     * @param body
     * @return
     */
    public static Observable<HljHttpResult<Object>> postReportObb(
            PostReportIdBody body) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .postReport("p/wedding/index" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" +
                                ".php/home/APICommunityReport/community_report",
                        body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 问题标签列表
     *
     * @param page 页数
     * @return
     */

    public static Observable<HljHttpData<List<Mark>>> getMarksObb(int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMarks(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Mark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关联标签列表(问答首页)
     *
     * @param page 页数
     * @return
     */

    public static Observable<HljHttpCountData<List<Mark>>> getMarkListObb(
            int page, int perPage, long cid) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMarkList(page, perPage, cid)
                .map(new HljHttpResultFunc<HljHttpCountData<List<Mark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 发布/编辑问题
     */
    public static Observable<PostQuestionResult> postQuestionObb(
            PostQuestionBody qBody, int type) {
        qBody.setType(type);
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .postQuestion(qBody)
                .map(new HljHttpResultFunc<PostQuestionResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 我的提问
     */
    public static Observable<HljHttpData<List<Question>>> getMyQuestionsObb(int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMyQuestions(page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 用户的回答列表
     *
     * @param userId 用户id
     * @param page   分页
     * @return
     */
    public static Observable<HljHttpData<List<Answer>>> getUserAnswersObb(
            long userId, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getOwnerAnswers(userId, null, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家的回答列表
     *
     * @param merchantId 商家id
     * @param page       分页
     * @return
     */
    public static Observable<HljHttpData<List<Answer>>> getMerchantAnswersObb(
            long merchantId, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getOwnerAnswers(null, merchantId, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 我的回答
     */
    public static Observable<HljHttpData<List<Answer>>> getMyFollowAnswersObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMyFollowAnswers(page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我关注的问题
     */
    public static Observable<HljHttpData<List<RecQaWrappers>>> getMyMerchantFollowObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMyMerchantFollow(page)
                .map(new HljHttpResultFunc<HljHttpData<List<RecQaWrappers>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签详情
     */
    public static Observable<Mark> getMaskObb(long id) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMark(id)
                .map(new HljHttpResultFunc<Mark>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 标签下问题
     */
    public static Observable<HljHttpData<List<Question>>> getMarkQuestionsObb(
            long id, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMarkQuestions(id, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签下回答
     */
    public static Observable<HljHttpData<List<Answer>>> getMarkAnswersObb(
            long id, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMarkAnswers(id, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发布/编辑回答  1社区问答 2商家问答
     */
    public static Observable<HljHttpResult<BasePostResult>> postAnswerObb(
            PostAnswerBody aBody, int type) {
        String url;
        if (type == TYPE_MERCHANT_QA) {
            url = "/p/wedding/index.php/Home/APIQaAnswer/merchant_edit";
        } else {
            url = "p/wedding/index.php/Home/APIQaAnswer/edit";
        }
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .postAnswer(url, aBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取问答列表(混杂)
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<QaListWrappers>>> getQaListObb(
            String url, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getQaList(url, 20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<QaListWrappers>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<HljHttpData<List<Question>>> getSameQuestions(
            String keyword, int perPage, int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getSameQuestions(keyword, perPage, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 本周热门回答
     */
    public static Observable<HljHttpData<List<Answer>>> getWeekAnswersObb(int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getWeekAnswers(page)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 本周热门问题
     */
    public static Observable<HljHttpData<List<Question>>> getWeekQuestionsObb(int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getWeekQuestions(page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 相关问题
     */
    public static Observable<HljHttpResult<HljHttpData<List<Question>>>> getRelatedQuestionObb
    (long questionId) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getRelatedQuestion(questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 问答列表
     *
     * @return
     */
    public static Observable<HljHttpQuestion<List<Question>>> getQAList(
            long merchantId, int page, int count) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getQAList(merchantId, page, count)
                .map(new HljHttpResultFunc<HljHttpQuestion<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取热门问答列表
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<Question>>> getHotQaQuestionObb(
            int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getHotQaQuestions(20, page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 标签下问题
     */
    public static Observable<HljHttpData<List<Question>>> getMarkQuestionsV2Obb(int page) {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getMarkQuestionsV2(page)
                .map(new HljHttpResultFunc<HljHttpData<List<Question>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取大V商家
     */
    public static Observable<List<QaVipMerchant>> getQaVipMerchantList() {
        return HljHttp.getRetrofit()
                .create(QuestionAnswerService.class)
                .getQaVipMerchantList()
                .map(new HljHttpResultFunc<List<QaVipMerchant>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
