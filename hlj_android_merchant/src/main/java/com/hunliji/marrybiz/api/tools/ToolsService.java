package com.hunliji.marrybiz.api.tools;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.WXWall;
import com.hunliji.marrybiz.model.tools.CheckInfo;
import com.hunliji.marrybiz.model.tools.ItemMonth;
import com.hunliji.marrybiz.model.tools.Schedule;
import com.hunliji.marrybiz.model.wrapper.HljHttpSchedulesData;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public interface ToolsService {

    /**
     * 获取指定日期的日程列表
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCalender/list")
    Observable<HljHttpResult<HljHttpSchedulesData>> getScheduleList(
            @Body Map<String, Object> map);

    /**
     * 某个商家下的所有日程项
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantCalenderItem/all_items")
    Observable<HljHttpResult<HljHttpSchedulesData>> getScheduleListByToDo(
            @Query("todo") int toDo, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 获取指定月份的事件
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCalender/itemByMonth")
    Observable<HljHttpResult<HljHttpData<List<ItemMonth>>>> getItemMonthList(
            @Body Map<String, Object> map);

    /**
     * 是否已满的开关
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCalender/changeStatus")
    Observable<HljHttpResult> changeStatus(@Body Map<String, Object> map);

    /**
     * 创建档期
     *
     * @param schedule
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCalender/createCalenderItem")
    Observable<HljHttpResult<Schedule>> createSchedule(@Body Schedule schedule);

    /**
     * 修改档期
     *
     * @param schedule
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantCalender/updateCalenderItem")
    Observable<HljHttpResult<Schedule>> updateSchedule(@Body Schedule schedule);


    /**
     * 删除日程
     *
     * @param id
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantCalender/delCalenderItem")
    Observable<HljHttpResult<Schedule>> deleteSchedule(@Query("id") long id);

    /**
     * 商家检测
     *
     * @param url
     * @return
     */
    @GET
    Observable<HljHttpResult<CheckInfo>> checkShop(@Url String url);


    /**
     * 微信墙信息
     */
    @GET("p/wedding/index.php/home/APIInvation/getWxWallLink")
    Observable<HljHttpResult<WXWall>> getWXWall();
}
