package com.hunliji.marrybiz.api.comment;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.comment.ComplainDetail;
import com.hunliji.marrybiz.model.comment.DelCommentBody;
import com.hunliji.marrybiz.model.comment.SubmitAppealBody;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/4/17.
 * 评论管理
 */

public class CommentApi {

    /**
     * 获取近期评论概述
     *
     * @return
     */
    public static Observable<CommentStatistics> getCommentStatistics() {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getCommentStatistics()
                .map(new HljHttpResultFunc<CommentStatistics>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家评论列表
     *
     * @param page
     * @param merchant_id
     * @param reputation  reputation 0/1/2/3
     * @return
     */
    public static Observable<HljHttpData<List<ServiceComment>>> getMerchantCommentList(
            int page, long merchant_id, long reputation) {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getMerchantCommentList(page, 20, -1, merchant_id, reputation)
                .map(new HljHttpResultFunc<HljHttpData<List<ServiceComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取评论详情
     *
     * @param order_comment_id
     * @return
     */
    public static Observable<HljHttpResult<ServiceComment>> getOrderCommentDetail(long order_comment_id) {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getOrderCommentDetail(order_comment_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    public static Observable<Object> delComment(long id) {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .delComment(new DelCommentBody(id))
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 申诉
     *
     * @param body
     * @return
     */
    public static Observable<Object> submitAppeal(SubmitAppealBody body) {
        String url;
        if (body.getEntityId() == null) {
            url = "/p/wedding/index.php/Admin/APIOrderCommentAppeal/submitAppeal";
        } else {
            url = "/p/wedding/Admin/APIQaMerchantAppeal/submit_appeal";
        }
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .submitAppeal(url, body)
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 申诉详情
     *
     * @param id   申诉id
     * @param type type=1 评论申诉
     * @return
     */
    public static Observable<ComplainDetail> getComplainDetail(long id, int type) {
        String url = null;
        if (type == ComplainDetail.TYPE_COMMENT) {
            url = "/p/wedding/index.php/Admin/APIOrderCommentAppeal/detail";
        } else if (type == ComplainDetail.TYPE_QUESTION) {
            url = "/p/wedding/admin/APIQaMerchantAppeal/detail";
        }
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getComplainDetail(url, id)
                .map(new HljHttpResultFunc<ComplainDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 申诉详情
     *
     * @param id 评论id
     * @return
     */
    public static Observable<HljHttpResult> CheckCommunityAppeal(long id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("community_comment_id", id);
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .CheckCommunityAppeal(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
