package me.suncloud.marrymemo.api.tools;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.tools.CalendarBrandConf;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.model.tools.wrappers.HljWeddingCalendarItemsData;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingGuestsData;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingTablesData;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by werther on 16/11/17.
 */

public interface ToolsService {

    @GET("p/wedding/index.php/Home/APIBudget/wedding_photography_cpm")
    Observable<HljHttpResult<HljHttpData<List<Work>>>> getBudgetCpmList(
            @Query("budget") double budget);

    /**
     * 预算
     *
     * @return
     */
    @GET("p/wedding/home/APIBudget/info")
    Observable<HljHttpResult<JsonElement>> getBudgetInfo();

    /**
     * 获取应用分享信息
     *
     * @return
     */
    @GET("p/wedding/Home/APISetting/app_share")
    Observable<ShareInfo> getAppShare();

    /**
     * @return
     */
    @GET("p/wedding/Home/APIWeddingTable/list")
    Observable<HljHttpResult<HljWeddingTablesData>> getTables();

    /**
     * 添加一桌
     *
     * @return
     */
    @POST("p/wedding/Home/APIWeddingTable/add")
    Observable<HljHttpResult<WeddingTable>> addTable();

    /**
     * 删除本桌
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APIWeddingTable/del")
    Observable<HljHttpResult> deleteTable(@Body Map<String, Object> map);


    /**
     * 更新桌与宾客信息
     *
     * @param table
     * @return
     */
    @POST("p/wedding/Home/APIWeddingTable/update")
    Observable<HljHttpResult> updateTable(@Body WeddingTable table);

    /**
     * 桌子换顺序 更新 序号
     *
     * @param map
     * @return
     */
    @POST("p/wedding/Home/APIWeddingTable/update_table_no")
    Observable<HljHttpResult> updateTableNo(@Body Map<String, Object> map);

    /**
     * 我的宾客名单（从宾客名单导入）
     *
     * @return
     */
    @GET("p/wedding/Home/APIWeddingGuest/my_all_guest")
    Observable<HljHttpResult<HljWeddingGuestsData>> getGuests();

    /**
     * 收藏列表
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICalendar/index")
    Observable<HljHttpResult<HljWeddingCalendarItemsData>> getCalendarItems();

    /**
     * 结婚日期人数和热度
     *
     * @return
     */
    @GET("p/wedding/index.php/Home/APICalendar/day")
    Observable<HljHttpResult<WeddingCalendarItem>> getCalendarItemByDate(
            @Query("date") String date);

    /**
     * 收藏
     *
     * @param map
     * @return
     */
    @POST("p/wedding/index.php/Home/APICalendar/collect")
    Observable<HljHttpResult<WeddingCalendarItem>> collectCalendarItem(
            @Body Map<String, Object> map);

    /**
     * 取消收藏
     *
     * @param id
     * @return
     */
    @DELETE("p/wedding/index.php/Home/APICalendar/collect")
    Observable<HljHttpResult> unCollectCalendarItem(@Query("id") long id);

    @GET("p/wedding/index.php/home/ActivityWed/calendarCof")
    Observable<HljHttpResult<CalendarBrandConf>> getCalendarConf();
}
