package me.suncloud.marrymemo.api.work_case;

import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.modelwrappers.MerchantServiceFilter;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljcommonlibrary.models.search.WorksSearchResult;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.model.wrappers.WorksData;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 套餐案例api
 * Created by chen_bin on 2016/12/6 0006.
 */
public class WorkApi {

    /**
     * 推荐套餐列表
     *
     * @param merchantId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getMerchantRecommendWorksObb(
            long merchantId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getMerchantRecommendWorks(merchantId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家主页套餐和案例的列表
     *
     * @param id
     * @param kind
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getMerchantWorksAndCasesObb(
            long id, String kind, String sort, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getMerchantWorksAndCases(id, kind, sort, page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 案例列表
     *
     * @param perPage 每页返回数
     * @param page    当前页
     * @param queries 筛选参数
     *                <p>
     *                first_query_time 时间戳
     *                sort[key] 排序字段 1
     *                sort[order] 排序字段 2
     *                city 城市id
     *                property 分类
     *                category_id 二级分类
     *                </p>
     * @return 给WorksData 设置 page 和 perPage后返回
     */
    public static Observable<HljHttpData<List<Work>>> getCasesObb(
            final int perPage, final int page, Map<String, String> queries) {
        if (queries == null) {
            queries = new HashMap<>();
        }
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getCases(perPage, page, queries)
                .map(new Func1<WorksData, HljHttpData<List<Work>>>() {
                    @Override
                    public HljHttpData<List<Work>> call(WorksData worksData) {
                        worksData.setCurrentPage(page);
                        worksData.setPerPage(perPage);
                        return worksData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static HashMap<String, String> generateWorkFilterMap(
            MerchantServiceFilter serviceFilter) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (serviceFilter != null) {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
            if (serviceFilter.getCategoryMarkId() > 0) {
                filterMap.put("filter[category_tag_id]",
                        String.valueOf(serviceFilter.getCategoryMarkId()));
            }
            if (serviceFilter.getPriceMin() > 0) {
                filterMap.put("filter[price_min]",
                        decimalFormat.format(serviceFilter.getPriceMin()));
            }
            if (serviceFilter.getPriceMax() > serviceFilter.getPriceMin() && serviceFilter
                    .getPriceMax() > 0) {
                filterMap.put("filter[price_max]",
                        decimalFormat.format(serviceFilter.getPriceMax()));
            }
            if (serviceFilter.getSaleWay() > 0) {
                filterMap.put("filter[sale_way]", String.valueOf(serviceFilter.getSaleWay()));
            }
            if (serviceFilter.getShopAreaId() > 0) {
                filterMap.put("filter[shop_area_id]",
                        String.valueOf(serviceFilter.getShopAreaId()));
            }
            if (!CommonUtil.isCollectionEmpty(serviceFilter.getTags())) {
                List<String> tags = serviceFilter.getTags();
                for (int i = 0, size = tags.size(); i < size; i++) {
                    filterMap.put("filter[tags][" + i + "]", tags.get(i));
                }
            }
            if (serviceFilter.getFilterSecondCategory() > 0) {
                filterMap.put("filter[filter_second_category]",
                        String.valueOf(serviceFilter.getFilterSecondCategory()));
            }
        }
        return filterMap;
    }

    /**
     * 套餐列表
     *
     * @param cityCode
     * @param propertyId
     * @param categoryId
     * @param serviceFilter
     * @param sort
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<WorksSearchResult> getWorksObb(
            long cityCode,
            long propertyId,
            long categoryId,
            MerchantServiceFilter serviceFilter,
            String keyword,
            String sort,
            final int page,
            int perPage) {
        HashMap<String, String> filterMap = generateWorkFilterMap(serviceFilter);
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getWorks(cityCode,
                        propertyId,
                        categoryId == 0 ? null : categoryId,
                        filterMap,
                        keyword,
                        sort,
                        page,
                        perPage)
                .map(new HljHttpResultFunc<ServiceSearchResult>())
                .map(new Func1<ServiceSearchResult, WorksSearchResult>() {
                    @Override
                    public WorksSearchResult call(ServiceSearchResult serviceSearchResult) {
                        return serviceSearchResult == null ? null : serviceSearchResult
                                .getWorksSearchResult();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 商家列表哦
     *
     * @param propertyId 司仪 11
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getMerchantList(long propertyId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getMerchantList(propertyId)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * cpm商家列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getCPMMerchantList(long propertyId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getCPMMerchantList(propertyId)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 微旅拍列表
     *
     * @param type 1精选2短途2远方
     * @return
     */
    public static Observable<HljHttpData<List<Work>>> getMicroTravelList(int type) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getMicroTravelList(type)
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 老接口，获取推荐套餐
     *
     * @param cid
     * @param workId
     * @return
     */
    public static Observable<List<Work>> getRecommendsMeals(long cid, long workId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getRecommendMeals(cid, workId)
                .map(new Func1<WorksData, List<Work>>() {
                    @Override
                    public List<Work> call(WorksData worksData) {
                        return worksData.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取99抢旅拍的套餐列表
     */
    public static Observable<HljHttpData<List<Work>>> getNinetyNineWorks() {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getNinetyNineWorks()
                .map(new HljHttpResultFunc<HljHttpData<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得套餐详情 相关客照列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<CasePhoto>>> getCasePhotoList(long workId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getCasePhotoList(workId)
                .map(new HljHttpResultFunc<HljHttpData<List<CasePhoto>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 客照详情
     *
     * @param casePhotoId 客照（案例）ID
     * @return
     */
    public static Observable<CasePhoto> getCasePhotoDetail(long casePhotoId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .getCasePhotoDetail(casePhotoId)
                .map(new HljHttpResultFunc<CasePhoto>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 客照收藏
     *
     * @param casePhotoId
     * @return
     */
    public static Observable<Object> postCaseCollect(long casePhotoId) {
        return HljHttp.getRetrofit()
                .create(WorkService.class)
                .postCaseCollect(casePhotoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}