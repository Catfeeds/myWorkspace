package com.hunliji.hljhttplibrary.api.search;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.search.CommunitySearchResult;
import com.hunliji.hljcommonlibrary.models.search.MerchantFilter;
import com.hunliji.hljcommonlibrary.models.search.NoteTypeSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ServiceSearchResult;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by werther on 16/11/29.
 */
public interface SearchService {


    /**
     * 服务类搜索结果统计
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<ServiceSearchResult>> searchServiceResultCount(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type);

    /**
     * 服务类搜索结果详细
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param pId
     * @param priceMin
     * @param priceMax
     * @param sort
     * @param page
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<ServiceSearchResult>> searchService(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type,
            @Query("filter[property_id]") long pId,
            @Query("filter[price_min]") double priceMin,
            @Query("filter[price_max]") double priceMax,
            @Query("sort") String sort,
            @Query("page") int page);

    /**
     * 服务类搜索结果详细
     *
     * @param subPath   url的后缀，list 或者 around_list
     * @param cityCode
     * @param keyword
     * @param type
     * @param filterMap
     * @param sort
     * @param page
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/{sub_path}")
    Observable<HljHttpResult<ServiceSearchResult>> searchService(
            @Path("sub_path") String subPath,
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type,
            @QueryMap Map<String, String> filterMap,
            @Query("sort") String sort,
            @Query("page") int page);

    @GET("p/wedding/index.php/home/APIMerchant/merchant_filter?city=0")
    Observable<HljHttpResult<MerchantFilter>> getMerchantFilterData();

    /**
     * 婚品类搜索结果详细
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param filterMap
     * @param sort
     * @param page
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<ProductSearchResult>> searchProduct(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type,
            @QueryMap Map<String, String> filterMap,
            @Query("sort") String sort,
            @Query("page") int page);

    /**
     * 社区类搜索结果
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @param page
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<CommunitySearchResult>> searchCommunityResult(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type,
            @Query("page") int page);

    /**
     * 消费过的商家列表
     *
     * @param page
     * @param per_page
     * @return
     */
    @GET("p/wedding/Home/APIUser/consumed_merchant")
    Observable<HljHttpResult<HljHttpData<List<Merchant>>>> getConsumedMerchants(
            @Query("page") int page, @Query("per_page") int per_page);

    /**
     * 搜索笔记结果统计
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<NoteTypeSearchResult>> searchNoteResultCount(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") int type);

    /**
     * 搜索笔记
     *
     * @param cityCode
     * @param keyword
     * @param type
     * @return
     */
    @GET("/p/wedding/Home/APISearchV2/list")
    Observable<HljHttpResult<NoteTypeSearchResult>> searchNotes(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("sort") String sort,
            @Query("type") int type,
            @Query("page") int page);

}
