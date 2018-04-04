package me.suncloud.marrymemo.api.buywork;

import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.util.HashMap;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.buyWork.SetMeal;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 买套餐api
 * Created by jinxin on 2016/12/6 0006.
 */

public class BuyWorkApi {
    /**
     * 买套餐列表
     *
     * @param propertyId
     * @param categoryId
     * @param cityCode
     * @param sort
     * @param filter
     * @param page
     * @return
     */
    public static Observable<SetMeal> getBuyWork(
            long categoryId,
            long propertyId,
            long cityCode,
            String sort,
            BuyWorkFilter filter,
            int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            if (filter.getPriceMin() >= 0) {
                filterMap.put("filter[price_min]", String.valueOf(filter.getPriceMin()));
            }
            if (filter.getPriceMax() > filter.getPriceMin()) {
                filterMap.put("filter[price_max]", String.valueOf(filter.getPriceMax()));
            }
            if (filter.getTags() != null) {
                List<String> tags = filter.getTags();
                for (int i = 0, size = tags.size(); i < size; i++) {
                    filterMap.put("filter[tags]["+i+"]", tags.get(i));
                }
            }
        }
        return HljHttp.getRetrofit()
                .create(BuyWorkService.class)
                .getBuyWork(categoryId,
                        propertyId,
                        cityCode,
                        sort,
                        filterMap,
                        page,
                        Constants.PER_PAGE)
                .map(new HljHttpResultFunc<SetMeal>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
