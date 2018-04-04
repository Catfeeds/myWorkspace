package com.hunliji.marrybiz.api.reservation;

import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.marrybiz.model.reservation.Reservation;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 预约相关
 * Created by jinxin on 2017/5/23 0023.
 */

public interface ReservationService {

    /**
     * 获得预约列表
     * tab 1待确认列表 2确认列表 3预约历史
     *
     * @return
     */
    @GET("p/wedding/index.php/Admin/APIMerchantAppointment/list")
    Observable<HljHttpResult<HljHttpData<List<Reservation>>>> getReservationList(
            @Query("tab") int tab, @Query("page") int page, @Query("per_page") int perPage);

    /**
     * 编辑预约信息
     * @return
     */
    @POST("p/wedding/index.php/Admin/APIMerchantAppointment/edit")
    Observable<HljHttpResult> editReservation(@Body Map<String, Object> map);
}
