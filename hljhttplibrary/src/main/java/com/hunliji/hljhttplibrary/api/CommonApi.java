package com.hunliji.hljhttplibrary.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.CertifyCodeMsg;
import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.PostCommentBody;
import com.hunliji.hljcommonlibrary.models.PostIdBody;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 通用请求API
 * Created by chen_bin on 2017/4/17
 */
public class CommonApi {

    /**
     * 通用点赞APICommunityPraise
     *
     * @param body
     * @return
     */
    public static Observable postPraiseObb(PostPraiseBody body) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .postPraise(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用收藏
     *
     * @param body
     * @return
     */
    public static Observable postCollectObb(PostCollectBody body, boolean isCollect) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .postCollect(isCollect ? "p/wedding/index.php/Home/APIFollow/unfollow" :
                                "p/wedding/index.php/Home/APIFollow/follow",
                        body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用评论
     *
     * @param body
     * @return
     */
    public static Observable<RepliedComment> addFuncObb(PostCommentBody body) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .addFunc(body)
                .map(new HljHttpResultFunc<RepliedComment>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用删除
     */
    public static Observable deleteFuncObb(long id) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .deleteFunc(new PostIdBody(id))
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家婚品分类
     *
     * @param merchantId
     * @return
     */
    public static Observable<HljHttpData<List<ShopCategory>>> getMerchantProductCategoriesObb(
            long merchantId) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getMerchantProductCategories(merchantId)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopCategory>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品列表
     *
     * @param url     请求url
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getProductsObb(
            String url, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getProducts(url, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用评论列表（热门）
     *
     * @param entityId   专题、笔记id
     * @param entityType 专题类型 SubPage，QaAnswer;笔记Note
     * @return
     */
    public static Observable<HljHttpData<List<RepliedComment>>> getHotCommentsObb(
            long entityId, String entityType) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getHotComments(entityId, entityType)
                .map(new HljHttpResultFunc<HljHttpData<List<RepliedComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用评论列表（最新）
     *
     * @param entityId   专题、笔记id
     * @param entityType 专题类型 SubPage，QaAnswer;笔记Note
     * @return
     */
    public static Observable<HljHttpData<List<Comment>>> getLatestCommentsObb(
            long entityId, String entityType, int page) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getLatestComments(entityId, entityType, page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Comment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用评论列表
     *
     * @param entityId   专题、笔记id
     * @param entityType 专题类型 SubPage，QaAnswer;笔记Note
     * @return
     */
    public static Observable<HljHttpData<List<RepliedComment>>> getCommonCommentsObb(
            long entityId, String entityType, int page) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getCommonComments(entityId, entityType, page, HljCommon.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<RepliedComment>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * banner
     *
     * @param id
     * @param city
     * @return
     */
    public static Observable<PosterData> getBanner(Context context, long id, long city) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getBanner(id, CommonUtil.getAppVersion(context), city)
                .map(new HljHttpResultFunc<PosterData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关注用户
     *
     * @param userId 用户id
     * @return
     */
    public static Observable followOrUnfollowUserObb(long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .followOrUnfollowUser(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 举报通用接口，反对
     * <p/>
     * 类型 thread:话题 post:回帖 question:问题 answer:回答
     *
     * @return
     */
    public static Observable postReportObb(
            long id, String kind, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("kind", kind);
        map.put("message", message);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .report(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 本地服务底部筛选标签组区域
     *
     * @param entityId
     * @return
     */
    public static Observable<HljHttpData<List<CategoryMark>>> getServiceMarksObb(
            long entityId, long secondCategoryId) {
        Map<String, String> params = new HashMap<>();
        if (entityId > 0) {
            params.put("entity_id", String.valueOf(entityId));
        }
        if (secondCategoryId > 0) {
            params.put("second_category_id", String.valueOf(secondCategoryId));
        }
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getServiceMarks(params)
                .map(new HljHttpResultFunc<HljHttpData<List<CategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品标签
     *
     * @param entityId
     * @return
     */
    public static Observable<HljHttpData<List<CategoryMark>>> getShopProductTags(long entityId) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getShopProductTags(entityId)
                .map(new HljHttpResultFunc<HljHttpData<List<CategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 通过cid获取所在省信息
     *
     * @param cid 城市id
     * @return
     */
    public static Observable<ParentArea> getCityDetailObb(long cid) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getCityDetail(cid)
                .map(new HljHttpResultFunc<ParentArea>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通过cid获取所在省信息
     *
     * @param cid 城市id
     * @return
     */
    public static Observable<List<ChildrenArea>> getChildrenCitiesObb(long cid) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getChildrenCities(cid)
                .map(new HljHttpResultFunc<List<ChildrenArea>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关注商家
     *
     * @param id 商家id
     * @return
     */
    public static Observable postMerchantFollowObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("merchant_id", id);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .postMerchantFollow(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消商家关注
     *
     * @param id 商家id
     * @return
     */
    public static Observable deleteMerchantFollowObb(long id) {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .deleteMerchantFollow(id)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取省市区数据
     *
     * @return
     */
    public static Observable<List<ChildrenArea>> getAllAddressObb() {
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getAllAddress()
                .map(new HljHttpResultFunc<List<ChildrenArea>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得验证码
     * post 请求
     *
     * @return
     */
    public static Observable<HljHttpResult<CertifyCodeMsg>> getPostCertifyCode(
            String flag, String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", flag);
        map.put("phone", phone);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getCertifyCode(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用验证码预验证接口
     *
     * @return
     */
    public static Observable preCheckSmsCodeObb(String smsCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("sms_code", smsCode);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .preCheckSmsCode(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取系统维护状态
     *
     * @return
     */
    public static Observable<JsonElement> getServiceState() {
        String url;
        if (HljHttp.getHOST()
                .startsWith("http://www.hunliji.com")) {
            url = "http://notify.hunliji.com/notify/api/servers/stat/maintain_stat";
        } else {
            url = "http://admintest.hunliji.com/notify/api/servers/stat/maintain_stat";
        }
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .getServiceState(url)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区点赞APICommunityPraise
     *
     * @return
     */
    public static Observable postThreadPraiseObb(long postId) {
        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .postThreadPraise(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改绑定手机号
     *
     * @return
     */
    public static Observable modifyBindPhoneObb(String phone,String smsCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("sms_code", smsCode);
        return HljHttp.getRetrofit()
                .create(CommonService.class)
                .modifyBindPhone(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}