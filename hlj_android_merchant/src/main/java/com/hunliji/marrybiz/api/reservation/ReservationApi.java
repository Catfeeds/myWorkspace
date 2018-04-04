package com.hunliji.marrybiz.api.reservation;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.model.reservation.Reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 预约相关api
 * Created by jinxin on 2017/5/23 0023.
 */

public class ReservationApi {

    /**
     * 获得预约列表
     * tab 1待确认列表 2确认列表 3预约历史
     *
     * @return
     */
    public static Observable<HljHttpData<List<Reservation>>> getReservationList(int tab, int page) {
        return HljHttp.getRetrofit()
                .create(ReservationService.class)
                .getReservationList(tab, page, Constants.PER_PAGE)
                .map(new HljHttpResultFunc<HljHttpData<List<Reservation>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预约
     * @param id
     * @param time 预约时间
     * @return
     */
    public static Observable<HljHttpResult> reservation(long id ,String time){
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("go_time", time);
        return HljHttp.getRetrofit()
                .create(ReservationService.class)
                .editReservation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 到店 未到店
     * @param id
     * @param arrive 到店状态：0 未处理 1到店 2未到店
     * @return
     */
    public static Observable<HljHttpResult>  arriveReservation(long id ,int arrive){
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("arrive_status", arrive);
        return HljHttp.getRetrofit()
                .create(ReservationService.class)
                .editReservation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除预约
     * @param id
     * @param delete
     * @return
     */
    public static Observable<HljHttpResult> deleteReservation(long id,int delete){
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("deleted", delete);
        return HljHttp.getRetrofit()
                .create(ReservationService.class)
                .editReservation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 查看预约
     * @param lookTime 查看的时间 时间戳的格式
     * @return
     */
    public static Observable<HljHttpResult> lookReservation(long id, long lookTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("look_at", lookTime);
        return HljHttp.getRetrofit()
                .create(ReservationService.class)
                .editReservation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
