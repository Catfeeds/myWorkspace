package com.hunliji.hljhttplibrary.api.newsearch;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.models.search.NewSearchTips;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by hua_rong on 2018/1/15
 */

public interface NewSearchService {


    /**
     * 获取酒店、商家列表
     *
     * @param cityCode  可选参数 0全国
     * @param cpmCount  cpm套餐的数量
     * @param isCpmOnly 默认0否 1是
     * @param keyword
     * @param type
     * @param filterMap
     * @param sortMap
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<Merchant>>>> getMerchantList(
            @Query("city_code") long cityCode,
            @Query("cpm_count") int cpmCount,
            @Query("is_cpm_only") int isCpmOnly,
            @Query("keyword") String keyword,
            @Query("type") String type,
            @QueryMap Map<String, String> filterMap,
            @QueryMap Map<String, String> sortMap,
            @Query("page") int page);


    /**
     * 获取套餐、案例列表
     *
     * @param cityCode  可选参数 0全国
     * @param cpmCount  cpm套餐的数量
     * @param keyword
     * @param type
     * @param filterMap
     * @param sortMap
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<Work>>>> getWordCaseList(
            @Query("city_code") long cityCode,
            @Query("cpm_count") int cpmCount,
            @Query("keyword") String keyword,
            @Query("type") String type,
            @QueryMap Map<String, String> filterMap,
            @QueryMap Map<String, String> sortMap,
            @Query("page") int page);

    /**
     * 获取婚品列表
     *
     * @param cityCode  可选参数 0全国
     * @param keyword
     * @param type
     * @param filterMap
     * @param sortMap
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<ShopProduct>>>> getShopProductList(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") String type,
            @QueryMap Map<String, String> filterMap,
            @QueryMap Map<String, String> sortMap,
            @Query("page") int page);

    /**
     * 获取婚车列表
     *
     * @param cityCode  可选参数 0全国
     * @param keyword
     * @param type
     * @param filterMap
     * @param sortMap
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<WeddingCarProduct>>>> getWeddingCarList(
            @Query("city_code") long cityCode,
            @Query("keyword") String keyword,
            @Query("type") String type,
            @QueryMap Map<String, String> filterMap,
            @QueryMap Map<String, String> sortMap,
            @Query("page") int page);

    /**
     * 获取帖子列表
     *
     * @param keyword
     * @param type
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<CommunityThread>>>> getThreadsList(
            @Query("keyword") String keyword,
            @Query("type") String type,
            @Query("page") int page,
            @QueryMap Map<String, String> sortMap);

    /**
     * 获取笔记列表
     *
     * @param keyword
     * @param type
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpSearch<List<Note>>>> getNoteList(
            @Query("keyword") String keyword,
            @Query("type") String type,
            @Query("page") int page,
            @QueryMap Map<String, String> sortMap);

    /**
     * 获取问答列表
     *
     * @param keyword
     * @param type
     * @param page
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchV3/list")
    Observable<HljHttpResult<HljHttpData<List<Answer>>>> getQaList(
            @Query("keyword") String keyword,
            @Query("type") String type,
            @Query("page") int page,
            @QueryMap Map<String, String> sortMap);

    /**
     * 获取搜索热词
     *
     * @param category 分类
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchWordV2/index")
    Observable<HljHttpResult<HljHttpData<List<NewHotKeyWord>>>> getHotSearchWords(@Query("category") String category);


    /**
     * 输入框热搜词
     *
     * @param inputType 0无 1首页 2发现 3新娘说 4婚礼购
     * @return
     */
    @GET("/p/wedding/index.php/home/APISearchWordV2/InputWord")
    Observable<HljHttpResult<NewHotKeyWord>> getInputWord(@Query("input_type") int inputType);

    /**
     * 下拉提示
     *
     * @param keyword
     * @return
     */
    @GET("/p/wedding/index.php/Home/APISearchV3/tips")
    Observable<HljHttpResult<NewSearchTips>> getSearchTips(@Query("keyword") String keyword);

}
