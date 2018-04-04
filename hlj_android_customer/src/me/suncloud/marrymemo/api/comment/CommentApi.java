package me.suncloud.marrymemo.api.comment;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.wrappers.HljHttpCommentsData;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2018/1/8 0008.
 */
public class CommentApi {

    public final static String EDIT_COMMENT_UTL = "p/wedding/index" + "" + "" + "" + "" + "" + ""
            + ".php/Home/APIOrderComment/editComment";
    public final static String COMMENT_SERVICE_ORDER_URL = "p/wedding/index" + "" + "" + "" + ""
            + ".php/home/APIOrder/CommentOrder";
    public final static String COMMENT_SERVICE_MERCHANT_URL = "p/wedding/index" + "" + "" + "" +
            ".php/Home/APIOrderComment/commentMerchant";


    /**
     * 商家主页评论列表
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
        final User user = Session.getInstance()
                .getCurrentUser(context);
        final long userId = user == null ? 0 : user.getId();
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getMerchantComments(merchantId, markId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpCommentsData>())
                .map(new Func1<HljHttpCommentsData, HljHttpCommentsData>() {
                    @Override
                    public HljHttpCommentsData call(HljHttpCommentsData hljHttpCommentsData) {
                        if (hljHttpCommentsData != null && !hljHttpCommentsData.isEmpty()) {
                            int firstSixMonthAgoIndex = -1;
                            for (int i = 0, size = hljHttpCommentsData.getData()
                                    .size(); i < size; i++) {
                                ServiceComment comment = hljHttpCommentsData.getData()
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
     * 本地服务评价详情
     *
     * @return
     */
    public static Observable<HljHttpResult<ServiceComment>> getServiceCommentDetailObb(
            long id) {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getServiceCommentDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 套餐评价标签列表（新）
     *
     * @param merchantId
     * @return
     */
    public static Observable<HljHttpData<List<ServiceCommentMark>>> getServiceCommentMarksObb(
            long merchantId) {
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .getServiceCommentMarks(merchantId)
                .map(new HljHttpResultFunc<HljHttpData<List<ServiceCommentMark>>>())
                .map(new Func1<HljHttpData<List<ServiceCommentMark>>,
                        HljHttpData<List<ServiceCommentMark>>>() {
                    @Override
                    public HljHttpData<List<ServiceCommentMark>> call
                            (HljHttpData<List<ServiceCommentMark>> listHljHttpData) {
                        if (listHljHttpData != null && !listHljHttpData.isEmpty()) {
                            ServiceCommentMark mark = new ServiceCommentMark();
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
     * 本地服务评价
     *
     * @param comment
     * @return
     */
    public static Observable commentServiceObb(ServiceComment comment, boolean isSyncNote) {
        Map<String, Object> map = new HashMap<>();
        map.put("rating", comment.getRating());
        map.put("content", comment.getContent());
        map.put("rating", comment.getRating());
        if (!CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            JsonArray photosArray = new JsonArray();
            for (Photo photo : comment.getPhotos()) {
                if (!TextUtils.isEmpty(photo.getImagePath())) {
                    JsonObject photoJson = new JsonObject();
                    photoJson.addProperty("img", photo.getImagePath());
                    photoJson.addProperty("height", photo.getHeight());
                    photoJson.addProperty("width", photo.getWidth());
                    photosArray.add(photoJson);
                }
            }
            if (photosArray.size() > 0) {
                map.put("imgs", photosArray);
            }
        }
        String url;
        if (comment.getId() > 0) { //修改评价
            map.put("id", comment.getId());
            url = EDIT_COMMENT_UTL;
        } else {
            map.put("is_sync_note", isSyncNote ? 1 : 0);
            if (!TextUtils.isEmpty(comment.getSubOrderNo())) {
                //评价关联订单
                map.put("order_no", comment.getSubOrderNo());
                url = COMMENT_SERVICE_ORDER_URL;
            } else {
                //评价关联订单
                map.put("merchant_id",
                        comment.getMerchant()
                                .getId());
                map.put("know_type", comment.getKnowType());
                url = COMMENT_SERVICE_MERCHANT_URL;
            }
        }
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .commentServiceOrder(url, map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 评价婚品订单
     *
     * @param productOrder
     * @return
     */
    public static Observable commentProductOrderObb(
            ProductOrder productOrder, List<ProductComment> comments, boolean isSyncNote) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_sync_note", isSyncNote ? 1 : 0);
        map.put("merchant_id",
                productOrder.getMerchant()
                        .getId());
        map.put("order_no", productOrder.getOrderNo());
        map.put("order_id", productOrder.getId());
        JsonArray commentsArray = new JsonArray();
        for (ProductComment comment : comments) {
            if (comment.getRating() == ProductComment.INIT_RATING) { //如果当前子订单没有选择评价等级则剔除当前项。
                continue;
            }
            JsonObject commentJson = new JsonObject();
            commentJson.addProperty("order_sub_id", comment.getSubOrderId());
            commentJson.addProperty("rating", comment.getRating());
            commentJson.addProperty("product_id",
                    comment.getProduct()
                            .getId());
            commentJson.addProperty("content", comment.getContent());
            List<Photo> photos = comment.getPhotos();
            if (!CommonUtil.isCollectionEmpty(photos)) {
                JsonArray photosArray = new JsonArray();
                for (Photo photo : photos) {
                    if (!TextUtils.isEmpty(photo.getImagePath())) {
                        JsonObject photoJson = new JsonObject();
                        photoJson.addProperty("img", photo.getImagePath());
                        photoJson.addProperty("height", photo.getHeight());
                        photoJson.addProperty("width", photo.getWidth());
                        photosArray.add(photoJson);
                    }
                }
                if (photosArray.size() > 0) {
                    commentJson.add("photos", photosArray);
                }
            }
            commentsArray.add(commentJson);
        }
        if (commentsArray.size() > 0) {
            map.put("comments", commentsArray);
        }
        return HljHttp.getRetrofit()
                .create(CommentService.class)
                .commentProductOrder(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
