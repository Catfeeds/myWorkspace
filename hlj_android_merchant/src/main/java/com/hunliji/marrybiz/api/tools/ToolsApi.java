package com.hunliji.marrybiz.api.tools;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.WXWall;
import com.hunliji.marrybiz.model.tools.CheckInfo;
import com.hunliji.marrybiz.model.tools.ItemMonth;
import com.hunliji.marrybiz.model.tools.Schedule;
import com.hunliji.marrybiz.model.wrapper.HljHttpSchedulesData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen_bin on 2016/9/13 0013.
 */
public class ToolsApi {

    /**
     * 获取指定日期下的日程列表
     *
     * @param date
     * @return
     */
    public static Observable<HljHttpSchedulesData> getScheduleListObb(String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getScheduleList(map)
                .map(new HljHttpResultFunc<HljHttpSchedulesData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 某个商家下的所有日程项
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpSchedulesData> getScheduleListByToDoObb(
            int todo, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getScheduleListByToDo(todo, page, perPage)
                .map(new HljHttpResultFunc<HljHttpSchedulesData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取指定月份的事件
     *
     * @param date
     * @return
     */
    public static Observable<HljHttpData<List<ItemMonth>>> getItemMonthListObb(String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getItemMonthList(map)
                .map(new HljHttpResultFunc<HljHttpData<List<ItemMonth>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 是否已满的开关
     *
     * @param date
     * @param status
     * @return
     */
    public static Observable changeStatusObb(String date, int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("status", status);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .changeStatus(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建档期
     *
     * @param schedule
     * @return
     */
    public static Observable<Schedule> createScheduleObb(Schedule schedule) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .createSchedule(schedule)
                .map(new HljHttpResultFunc<Schedule>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改档期
     *
     * @param schedule
     * @return
     */
    public static Observable<Schedule> updateScheduleObb(Schedule schedule) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .updateSchedule(schedule)
                .map(new HljHttpResultFunc<Schedule>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 删除日程
     *
     * @param id
     * @return
     */
    public static Observable<Schedule> deleteScheduleObb(long id) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .deleteSchedule(id)
                .map(new HljHttpResultFunc<Schedule>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家店铺检测
     *
     * @param url
     * @return
     */
    public static Observable<CheckInfo> checkShopObb(String url) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .checkShop(url)
                .map(new HljHttpResultFunc<CheckInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 微信墙信息
     */
    public static Observable<WXWall> getWXWall() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getWXWall()
                .map(new HljHttpResultFunc<WXWall>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}