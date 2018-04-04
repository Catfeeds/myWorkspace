package me.suncloud.marrymemo.api.marry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.HashMap;
import java.util.List;

import me.suncloud.marrymemo.model.marry.GuestCount;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.model.marry.RecordBook;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hua_rong on 2017/11/7
 * 记账本 && 结婚任务
 */

public interface MarryService {

    /**
     * 用户帐目列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIUserCashBook/list")
    Observable<HljHttpResult<HljHttpData<List<MarryBook>>>> getCashBookList();

    /**
     * 增加
     * 编辑时传 id
     *
     * @param hashMap
     * @return
     */
    @POST("/p/wedding/index.php/home/APIUserCashBook/edit")
    Observable<HljHttpResult> postCashBookAdd(@Body HashMap<String, Object> hashMap);

    /**
     * 删除
     *
     * @param id 账目id
     * @return
     */
    @DELETE("/p/wedding/index.php/home/APIUserCashBook/book")
    Observable<HljHttpResult> deleteBook(@Query("id") long id);

    /**
     * 分类列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIUserCashBook/types")
    Observable<HljHttpResult<HljHttpData<List<RecordBook>>>> getBookSortTypes();


    /**
     * 编辑任务
     *
     * @param jsonObject
     * @return
     */
    @POST("/p/wedding/Home/APIToDoV3/update")
    Observable<HljHttpResult> updateTask(@Body JsonObject jsonObject);

    /**
     * 完成任务
     *
     * @param jsonObject
     * @return
     */
    @POST("/p/wedding/Home/APIToDoV3/check")
    Observable<HljHttpResult> checkTask(@Body JsonObject jsonObject);


    /**
     * 任务列表
     */
    @GET("/p/wedding/Home/APIToDoV3/index")
    Observable<HljHttpResult<HljHttpData<List<MarryTask>>>> getMarryTasks(
            @Query("to_do") Integer toDo);

    /**
     * 待选宾客数
     */
    @GET("/p/wedding/index.php/home/APIUserCashBook/guest_name_count")
    Observable<HljHttpResult<GuestCount>> getGuestNameCount();

    /**
     * 待选宾客列表
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIUserCashBook/guest_name_list")
    Observable<HljHttpResult<HljHttpData<JsonArray>>> getGuestNameList();


    /**
     * 批量导入礼金账本
     *
     * @param hashMap
     * @return
     */
    @POST("/p/wedding/index.php/home/APIUserCashBook/batch_add")
    Observable<HljHttpResult> postBatchAdd(@Body HashMap<String, Object> hashMap);


}
