package me.suncloud.marrymemo.api.tools;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.tools.CalendarBrandConf;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.model.tools.wrappers.HljWeddingCalendarItemsData;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingGuestsData;
import me.suncloud.marrymemo.model.user.wrappers.HljWeddingTablesData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/11/17.
 */

public class ToolsApi {


    /**
     * 获取婚礼预算的cpm列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getBudgetCpmList(double budget) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getBudgetCpmList(budget)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 预算
     *
     * @return
     */
    public static Observable<JsonElement> getBudgetInfoObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getBudgetInfo()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取app分享数据
     *
     * @return
     */
    public static Observable<ShareInfo> getAppShareObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getAppShare()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return
     */
    public static Observable<HljWeddingTablesData> getTablesObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getTables()
                .map(new HljHttpResultFunc<HljWeddingTablesData>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加一桌
     *
     * @return
     */
    public static Observable<List<WeddingTable>> addTableObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .addTable()
                .map(new HljHttpResultFunc<WeddingTable>())
                .map(new Func1<WeddingTable, List<WeddingTable>>() {
                    @Override
                    public List<WeddingTable> call(WeddingTable table) {
                        List<WeddingTable> tables = new ArrayList<>();
                        if (table != null) {
                            tables.add(table);
                        }
                        return tables;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除本桌
     *
     * @param id
     * @return
     */
    public static Observable deleteTableObb(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .deleteTable(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新桌与宾客信息
     *
     * @param table
     * @return
     */
    public static Observable updateTableObb(WeddingTable table) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .updateTable(table)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 桌子换顺序 更新 序号
     *
     * @param ids
     * @return
     */
    public static Observable updateTableNoObb(String ids) {
        Map<String, Object> map = new HashMap<>();
        map.put("ids", ids);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .updateTableNo(map)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我的宾客名单（从宾客名单导入）
     *
     * @return
     */
    public static Observable<HljWeddingGuestsData> getGuestsObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getGuests()
                .map(new HljHttpResultFunc<HljWeddingGuestsData>())
                .map(new Func1<HljWeddingGuestsData, HljWeddingGuestsData>() {
                    @Override
                    public HljWeddingGuestsData call(HljWeddingGuestsData guestsData) {
                        if (guestsData != null && !guestsData.isEmpty()) {
                            List<String> firstChars = new ArrayList<>();
                            for (WeddingGuest guest : guestsData.getData()) {
                                String firstChar = guest.getFirstChar();
                                if (!TextUtils.isEmpty(firstChar) && !firstChars.contains
                                        (firstChar)) {
                                    firstChars.add(firstChar);
                                }
                            }
                            guestsData.setFirstChars(firstChars);
                        }
                        return guestsData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏列表
     *
     * @return
     */
    public static Observable<HljWeddingCalendarItemsData> getCalendarItemsObb() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getCalendarItems()
                .map(new Func1<HljHttpResult<HljWeddingCalendarItemsData>,
                        HljWeddingCalendarItemsData>() {

                    @Override
                    public HljWeddingCalendarItemsData call
                            (HljHttpResult<HljWeddingCalendarItemsData> result) {
                        HljWeddingCalendarItemsData calendarItemsData = null;
                        if (result != null) {
                            calendarItemsData = result.getData();
                        }
                        return calendarItemsData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 结婚日期人数和热度
     *
     * @param date
     * @return
     */
    public static Observable<WeddingCalendarItem> getCalendarItemByDateObb(String date) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getCalendarItemByDate(date)
                .map(new HljHttpResultFunc<WeddingCalendarItem>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏
     *
     * @param date
     * @return
     */
    public static Observable<WeddingCalendarItem> collectCalendarItemObb(String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .collectCalendarItem(map)
                .map(new HljHttpResultFunc<WeddingCalendarItem>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消收藏
     *
     * @param id
     * @return
     */
    public static Observable unCollectCalendarItemObb(long id) {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .unCollectCalendarItem(id)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取日历中的品牌推广配置信息
     *
     * @return
     */
    public static Observable<CalendarBrandConf> getCalendarBrandConf() {
        return HljHttp.getRetrofit()
                .create(ToolsService.class)
                .getCalendarConf()
                .map(new HljHttpResultFunc<CalendarBrandConf>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
