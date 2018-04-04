package com.hunliji.hljcarlibrary.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.hunliji.hljcarlibrary.models.Brand;
import com.hunliji.hljcarlibrary.models.CarFilter;
import com.hunliji.hljcarlibrary.models.CarLesson;
import com.hunliji.hljcarlibrary.models.CarMerchantContactInfo;
import com.hunliji.hljcarlibrary.models.HljCarHttpData;
import com.hunliji.hljcarlibrary.models.HljHttpCommentsData;
import com.hunliji.hljcarlibrary.models.SecKill;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarComment;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/12/26.婚车api
 */

public class WeddingCarApi {

    /**
     * 获得婚车详情
     *
     * @param id
     * @return
     */
    public static Observable<WeddingCarProduct> getWeddingCarProductDetail(long id) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getWeddingCarProductDetail(id)
                .map(new HljHttpResultFunc<WeddingCarProduct>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚车热门品牌
     *
     * @return
     */
    public static Observable<List<Brand>> getHotBrandsObb() {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getHotBrands()
                .map(new HljHttpResultFunc<List<Brand>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚车筛选选项
     *
     * @param cid 城市id
     * @param tab meal 精选套餐（默认），optional 个性自选
     * @return
     */
    public static Observable<CarFilter> getCarFiltersObb(long cid, String tab) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getCarFilters(cid, tab)
                .map(new HljHttpResultFunc<CarFilter>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得婚车评价
     *
     * @param merchantId
     * @return
     */
    public static Observable<WeddingCarComment> getWeddingCarProductComment(long merchantId) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getWeddingCarProductComment(merchantId, 1, 1)
                .map(new HljHttpResultFunc<HljHttpData<List<WeddingCarComment>>>())
                .map(new Func1<HljHttpData<List<WeddingCarComment>>, WeddingCarComment>() {
                    @Override
                    public WeddingCarComment call(
                            HljHttpData<List<WeddingCarComment>> listHljHttpData) {
                        return (listHljHttpData == null || listHljHttpData.getData() == null ||
                                listHljHttpData.getData()
                                .isEmpty()) ? null : listHljHttpData.getData()
                                .get(0);
                    }
                })
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 精选婚车套餐列表
     * <p>
     * product_brand_id   婚车品牌
     * sort      排序条件
     * order     排序方式
     *
     * @return
     */
    public static Observable<HljHttpData<List<WeddingCarProduct>>> getHotCarMeals(String url) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getHotCarMeals(url)
                .map(new HljHttpResultFunc<HljHttpData<List<WeddingCarProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 自选婚车列表
     * <p>
     * color_title     颜色筛选
     * product_brand_id   婚车品牌
     * sort      排序条件
     * order     排序方式
     *
     * @return
     */
    public static Observable<HljHttpData<List<WeddingCarProduct>>> getSelfCarsObb(String url) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getSelfCars(url)
                .map(new HljHttpResultFunc<HljHttpData<List<WeddingCarProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预约看车
     *
     * @return
     */
    public static Observable<HljHttpResult> orderCar(long merchantId, long carWorkId) {
        Map<String, Object> params = new HashMap<>();
        params.put("form_id", carWorkId);
        params.put("from_type", 4);
        params.put("merchant_id", merchantId);
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .orderCar(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚车必修课列表
     *
     * @param perPage
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<CarLesson>>> getCarLessonsObb(
            int perPage, int page) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getCarLessons(perPage, page)
                .map(new HljHttpResultFunc<HljHttpData<List<CarLesson>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 特价秒杀列表
     *
     * @param perPage
     * @param page
     * @return
     */
    public static Observable<HljCarHttpData<List<SecKill>>> getSecKillsObb(
            long cid, int perPage, int page) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getSecKills(cid, perPage, page)
                .map(new HljHttpResultFunc<HljCarHttpData<List<SecKill>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚车商家联系信息
     *
     * @param cid
     * @return
     */
    public static Observable<CarMerchantContactInfo> getCarMerchantContactInfoObb(
            long cid) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getCarMerchantContactInfo(cid)
                .map(new HljHttpResultFunc<CarMerchantContactInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚车评价标签列表（新）
     *
     * @param merchantId
     * @return
     */
    public static Observable<HljHttpData<List<CommentMark>>>
    getWeddingCarMarks(
            long merchantId) {
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getWeddingCarMarks(merchantId)
                .map(new HljHttpResultFunc<HljHttpData<List<CommentMark>>>())
                .map(new Func1<HljHttpData<List<CommentMark>>,
                        HljHttpData<List<CommentMark>>>() {
                    @Override
                    public HljHttpData<List<CommentMark>> call
                            (HljHttpData<List<CommentMark>> listHljHttpData) {
                        if (listHljHttpData != null && !listHljHttpData.isEmpty()) {
                            CommentMark mark = new CommentMark();
                            mark.setName("全部");
                            listHljHttpData.getData()
                                    .add(0, mark);
                        }
                        return listHljHttpData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚车评论列表
     *
     * @param context
     * @param merchantId
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpCommentsData> getMerchantCommentsObb(
            final Context context,
            final long merchantId,
            final long markId,
            final int page,
            int perPage) {
        final User user = UserSession.getInstance().getUser(context);
        final long userId = user == null ? 0 : user.getId();
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .getMerchantComments(merchantId, markId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpCommentsData>())
                .map(new Func1<HljHttpCommentsData, HljHttpCommentsData>() {
                    @Override
                    public HljHttpCommentsData call(HljHttpCommentsData hljHttpCommentsData) {
                        if (hljHttpCommentsData != null && !hljHttpCommentsData.isEmpty()) {
                            int firstSixMonthAgoIndex = -1;
                            for (int i = 0, size = hljHttpCommentsData.getData()
                                    .size(); i < size; i++) {
                                WeddingCarComment comment = hljHttpCommentsData.getData()
                                        .get(i);
                                if (comment.getRating() <= 2) {
                                    if (!CommonUtil.isCollectionEmpty(comment.getPraisedUsers())) {
                                        ArrayList<Author> praisedUsers = new ArrayList<>();
                                        for (Author author : comment.getPraisedUsers()) {
                                            if (userId > 0 && author.getId() == userId) {
                                                praisedUsers.add(author);
                                                break;
                                            }
                                        }
                                        comment.setLikesCount(praisedUsers.size());
                                        comment.setPraisedUsers(praisedUsers);
                                    }
                                    if (!CommonUtil.isCollectionEmpty(comment.getRepliedComments
                                            ())) {
                                        ArrayList<RepliedComment> repliedComments = new
                                                ArrayList<>();
                                        for (RepliedComment repliedComment : comment
                                                .getRepliedComments()) {
                                            if (repliedComment.getUser() != null &&
                                                    repliedComment.getUser()
                                                            .isMerchant() && (repliedComment.getReplyUser
                                                    () == null || repliedComment.getReplyUser()
                                                    .getId() == 0)) {
                                                repliedComments.add(repliedComment);
                                                break;
                                            }
                                        }
                                        comment.setCommentCount(repliedComments.size());
                                        comment.setRepliedComments(repliedComments);
                                    }
                                }
                                if (firstSixMonthAgoIndex < 0 && comment.getCreatedAt() != null
                                        && markId == ServiceCommentMark.ID_BAD_REPUTATION) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(comment.getCreatedAt()
                                            .getMillis());
                                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 6);
                                    if (cal.getTimeInMillis() < HljTimeUtils
                                            .getServerCurrentTimeMillis()) {
                                        firstSixMonthAgoIndex = i;
                                        hljHttpCommentsData.setFirstSixMonthAgoIndex(
                                                firstSixMonthAgoIndex);
                                    }
                                }
                            }
                        }
                        return hljHttpCommentsData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚车商家 请求让服务器自动发送商家轻松聊信息
     *
     * @param id 商家ID
     * @return
     */
    public static Observable<JsonElement> postMerchantChatLinkTrigger(
            long id, String carSpeech) {
        Map<String, Object> body = new HashMap<>();
        body.put("merchant_id", id);
        body.put("car_speech", carSpeech);
        body.put("type", "car");
        return HljHttp.getRetrofit()
                .create(WeddingCarService.class)
                .postMerchantChatLinkTrigger(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
