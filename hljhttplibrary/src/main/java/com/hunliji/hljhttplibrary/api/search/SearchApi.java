package com.hunliji.hljhttplibrary.api.search;

import android.text.TextUtils;
import android.util.Log;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.search.CommunitySearchResult;
import com.hunliji.hljcommonlibrary.models.search.MerchantFilter;
import com.hunliji.hljcommonlibrary.models.search.NoteTypeSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by werther on 16/11/29.
 */
public class SearchApi {

    public enum Type {
        SEARCH_TYPE_SERVICE(1),
        SEARCH_TYPE_PRODUCT(2),
        SEARCH_TYPE_COMMUNITY(3),
        SEARCH_TYPE_NOTE(4);

        private int type;

        public int getType() {
            return type;
        }

        Type(int type) {
            this.type = type;
        }
    }

    public enum SubType {
        SUB_SEARCH_TYPE_WORK(11, "套餐"),
        SUB_SEARCH_TYPE_CASE(12, "案例"),
        SUB_SEARCH_TYPE_MERCHANT(13, "商家"),
        SUB_SEARCH_TYPE_HOTEL(14, "酒店"),

        SUB_SEARCH_TYPE_PRODUCT(2, "婚品"),

        SUB_SEARCH_TYPE_QA(31, "问答"),
        SUB_SEARCH_TYPE_THREAD(32, "话题"),
        SUB_SEARCH_TYPE_TOPIC(33, "专栏"),

        SUB_SEARCH_TYPE_NOTE(41, "笔记"),
        // 笔记搜索结果下的套餐列表，与11相同
        SUB_SEARCH_TYPE_NOTE_WORK(42, "套餐"),
        // 同上，与2相同;
        SUB_SEARCH_TYPE_NOTE_PRODUCT(43, "婚品");

        private int type;
        private String name;

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        SubType(int type, String name) {
            this.type = type;
            this.name = name;
        }
    }


    public static final String TAG = SearchApi.class.getSimpleName();

    /**
     * 服务类搜索结果统计
     *
     * @param cityCode
     * @param keyword
     * @return
     */
    public static Observable<ServiceSearchResult> searchServiceResultCount(
            long cityCode, String keyword) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchServiceResultCount(cityCode, keyword, Type.SEARCH_TYPE_SERVICE.type)
                .map(new HljHttpResultFunc<ServiceSearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 服务类搜索结果详细
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param filter
     * @param sort
     * @param page
     * @return
     */
    public static Observable<ServiceSearchResult> searchService(
            long cityCode,
            String keyword,
            final SubType type,
            SearchFilter filter,
            String sort,
            final int page) {
        Log.d(TAG, "Service type = " + type + "result page = " + page);
        HashMap<String, String> filterMap = generateServiceFilterMap(type, filter);
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchService("list", cityCode, keyword, type.type, filterMap, sort, page)
                .map(new HljHttpResultFunc<ServiceSearchResult>())
                .map(new Func1<ServiceSearchResult, ServiceSearchResult>() {
                    @Override
                    public ServiceSearchResult call(ServiceSearchResult serviceSearchResult) {
                        Log.d(TAG, "Result type = " + type + "result page = " + page);
                        serviceSearchResult.setPage(page);
                        return serviceSearchResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static HashMap<String, String> generateServiceFilterMap(
            SubType type, SearchFilter filter) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
            if (filter.getPropertyId() > 0) {
                filterMap.put("filter[property_id]", String.valueOf(filter.getPropertyId()));
            }
            if (type == SubType.SUB_SEARCH_TYPE_WORK || type == SubType.SUB_SEARCH_TYPE_HOTEL) {
                if (filter.getPriceMin() > 0) {
                    filterMap.put("filter[price_min]", decimalFormat.format(filter.getPriceMin()));
                }
                if (filter.getPriceMax() > filter.getPriceMin() && filter.getPriceMin() > 0) {
                    filterMap.put("filter[price_max]", decimalFormat.format(filter.getPriceMax()));
                }
            }
            if (type == SubType.SUB_SEARCH_TYPE_HOTEL) {
                if (filter.getTableMin() > 0) {
                    filterMap.put("filter[table_min]", String.valueOf(filter.getTableMin()));
                }
                if (filter.getTableMax() > 0) {
                    filterMap.put("filter[table_max]", String.valueOf(filter.getTableMax()));
                }
            }
        }

        return filterMap;
    }

    /**
     * 服务类周边城市搜索结果
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param filter
     * @param sort
     * @param page
     * @return
     */
    public static Observable<ServiceSearchResult> searchAroundService(
            long cityCode,
            String keyword,
            final SubType type,
            SearchFilter filter,
            String sort,
            final int page) {
        HashMap<String, String> filterMap = generateServiceFilterMap(type, filter);
        Log.d(TAG, "Around type = " + type + " page = " + page);
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchService("around_list", cityCode, keyword, type.type, filterMap, sort, page)
                .map(new HljHttpResultFunc<ServiceSearchResult>())
                .map(new Func1<ServiceSearchResult, ServiceSearchResult>() {
                    @Override
                    public ServiceSearchResult call(ServiceSearchResult serviceSearchResult) {
                        Log.d(TAG, "Around Result type = " + type + " page = " + page);
                        serviceSearchResult.setPage(page);
                        return serviceSearchResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品类搜索结果详细
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param filter
     * @param sort
     * @param page
     * @return
     */
    public static Observable<ProductSearchResult> searchProduct(
            long cityCode,
            String keyword,
            SubType type,
            SearchFilter filter,
            String sort,
            int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00"); //格式化设置
            if (filter.getCategoryId() > 0) {
                filterMap.put("filter[category_id]", String.valueOf(filter.getCategoryId()));
            }
            if (filter.getPriceMin() > 0) {
                filterMap.put("filter[price_min]", decimalFormat.format(filter.getPriceMin()));
            }
            if (filter.getPriceMax() > filter.getPriceMin()) {
                filterMap.put("filter[price_max]", decimalFormat.format(filter.getPriceMax()));
            }
            if (filter.getProductService() > 0) {
                filterMap.put("filter[service]", String.valueOf(filter.getProductService()));
            }
            if (!TextUtils.isEmpty(filter.getTags())) {
                filterMap.put("filter[tags]", filter.getTags());
            }
        }
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchProduct(cityCode, keyword, type.type, filterMap, sort, page)
                .map(new HljHttpResultFunc<ProductSearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区类搜索结果统计
     *
     * @param cityCode
     * @param keyword
     * @return
     */
    public static Observable<CommunitySearchResult> searchCommunityResultCount(
            long cityCode, String keyword) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchCommunityResult(cityCode, keyword, Type.SEARCH_TYPE_COMMUNITY.type, 1)
                .map(new HljHttpResultFunc<CommunitySearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 社区类搜索结果
     *
     * @param cityCode
     * @param keyword
     * @param subType
     * @param page
     * @return
     */
    public static Observable<CommunitySearchResult> searchCommunityResult(
            long cityCode, String keyword, SubType subType, int page) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchCommunityResult(cityCode, keyword, subType.type, page)
                .map(new HljHttpResultFunc<CommunitySearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家的各种分类筛选数据
     *
     * @return
     */
    public static Observable<MerchantFilter> getMerchantFilter() {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .getMerchantFilterData()
                .map(new HljHttpResultFunc<MerchantFilter>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 消费过的商家列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Merchant>>> getConsumedMerchantsObb(int page) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .getConsumedMerchants(page, 20)
                .map(new HljHttpResultFunc<HljHttpData<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取note搜索结果统计
     *
     * @param cityCode
     * @param keyword
     * @return
     */
    public static Observable<NoteTypeSearchResult> searchNoteResultCount(
            long cityCode, String keyword) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchNoteResultCount(cityCode, keyword, SearchApi.Type.SEARCH_TYPE_NOTE.getType())
                .map(new HljHttpResultFunc<NoteTypeSearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索笔记结果
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param sort
     * @param page
     * @return
     */
    public static Observable<NoteTypeSearchResult> searchNotes(
            long cityCode,
            String keyword,
            final SearchApi.SubType type,
            String sort,
            final int page) {
        return HljHttp.getRetrofit()
                .create(SearchService.class)
                .searchNotes(cityCode, keyword, sort, type.getType(), page)
                .map(new HljHttpResultFunc<NoteTypeSearchResult>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
