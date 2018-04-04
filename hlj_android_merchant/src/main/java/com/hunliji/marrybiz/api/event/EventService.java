package com.hunliji.marrybiz.api.event;

import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.event.EventExample;
import com.hunliji.marrybiz.model.event.EventPoint;
import com.hunliji.marrybiz.model.event.EventWallet;
import com.hunliji.marrybiz.model.event.RechargeRecord;
import com.hunliji.marrybiz.model.event.RecordInfo;
import com.hunliji.marrybiz.model.wrapper.HljHttpSignUpsData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 商家活动相关API服务接口
 * Created by chen_bin on 2016/9/6 0006.
 */
public interface EventService {

    /**
     * 申请活动
     *
     * @param body
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIFinderActivityApply/apply")
    Observable<HljHttpResult> applyEvent(@Body RecordInfo body);

    /**
     * 用户签到
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIFinderActivity/check_in")
    Observable<HljHttpResult> checkIn(@Body Map<String, Object> map);

    /**
     * 根据凭证获取信息
     *
     * @param validCode
     * @return
     */
    @GET("p/wedding/index.php/Home/APIFinderActivity/infoByCode")
    Observable<HljHttpResult<SignUpInfo>> getSignUpInfoByCode(
            @Query("valid_code") String validCode);

    /**
     * 活动列表-新(传1 待上线 ；不传 已上线)
     *
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIFinderActivity/index")
    Observable<HljHttpResult<HljHttpData<List<EventInfo>>>> getEvents(
            @Query("is_not_publish") String isNotPublish,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 活动申请记录列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivityApply/list")
    Observable<HljHttpResult<HljHttpData<List<RecordInfo>>>> getRecordEvents(
            @Query("page") int page, @Query("per_page") int perPage);


    /**
     * 活动范例列表
     *
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivityExample/list")
    Observable<HljHttpResult<HljHttpData<List<EventExample>>>> getEventExamples(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 获取申请记录详情
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivityApply/detail")
    Observable<HljHttpResult<RecordInfo>> getRecordDetail(@Query("id") long id);

    /**
     * 报名列表
     *
     * @param id
     * @param status
     * @param page
     * @param perPage
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivitySignUp/list")
    Observable<HljHttpResult<HljHttpSignUpsData>> getSignUpList(
            @Query("activity_id") long id,
            @Query("status") String status,
            @Query("page") int page,
            @Query("per_page") int perPage);

    /**
     * 活动管理 - 活动点基本信息
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivity/activityPoint")
    Observable<HljHttpResult<EventPoint>> getEventPoint();

    /**
     * 活动钱包
     *
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIFinderActivityWallet/wallet")
    Observable<HljHttpResult<EventWallet>> getEventWallet();

    /**
     * 活动管理-流水明细
     *
     * @return
     */
    @GET("/p/wedding/index.php/Admin/APIFinderActivityWallet/index")
    Observable<HljHttpResult<HljHttpData<List<RechargeRecord>>>> getPunchWaterObb(
            @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 活动管理 - get 联系报名用户
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIFinderActivity/payViewPhone")
    Observable<HljHttpResult<EventPoint>> getPayViewPhone(@Query("id") long id);
}