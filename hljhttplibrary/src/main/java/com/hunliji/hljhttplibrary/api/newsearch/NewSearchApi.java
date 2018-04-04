package com.hunliji.hljhttplibrary.api.newsearch;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.models.search.NewSearchTips;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.R;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpSearch;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/1/12
 * 新搜索
 */

public class NewSearchApi {


    public final static String SORT_PRICE_ASC = "price_asc";//升序
    public final static String SORT_PRICE_DESC = "price_desc";//降序
    public final static String SORT_DEFAULT = "default";
    public final static String SORT_NEWEST = "newest";//最新
    public final static String SORT_HOT = "hot";//热度
    public final static String SORT_SOLD_DESC = "sold_desc";//销量


    /**
     * 本地参数
     */
    public static final String ARG_KEY_WORD = "keyword";
    public static final String ARG_SEARCH_TYPE = "search_type";
    //输入框类型 0无 1首页2发现 3新娘说 4婚礼购
    public static final String ARG_SEARCH_INPUT_TYPE = "search_input_type";

    public enum InputType {
        //0无 1首页 2发现 3新娘说 4婚礼购
        TYPE_HOME_PAGE(1), TYPE_FINDER(2), TYPE_SOCIAL_HOME(3), TYPE_PRODUCT_HOME(4);

        private int type;

        public int getType() {
            return type;
        }

        InputType(int type) {
            this.type = type;
        }
    }

    public enum SearchType {

        SEARCH_TYPE_MERCHANT("merchant", "商家", R.mipmap.icon_search_shop), SEARCH_TYPE_WORK(
                "package",
                "套餐",
                R.mipmap.icon_search_work), SEARCH_TYPE_PRODUCT("shop_product",
                "商品",
                R.mipmap.icon_search_product), SEARCH_TYPE_HOTEL("hotel",
                "酒店",
                R.mipmap.icon_search_hotel), SEARCH_TYPE_CAR("car",
                "婚车",
                R.mipmap.icon_search_car), SEARCH_TYPE_CASE("example",
                "案例",
                R.mipmap.icon_search_case), SEARCH_TYPE_NOTE("note",
                "笔记",
                R.mipmap.icon_search_note), SEARCH_TYPE_QA("qa",
                "问答",
                R.mipmap.icon_search_qa), SEARCH_TYPE_THREAD("community_thread",
                "帖子",
                R.mipmap.icon_search_thread);

        private String type;
        private String name;
        private int imageResId;

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }

        SearchType(String type, String name, int imageResId) {
            this.type = type;
            this.name = name;
            this.imageResId = imageResId;
        }
    }

    /**
     * 获取下拉提示的title
     */
    public static String getTitle(String keyword, String category) {
        if (TextUtils.isEmpty(keyword)) {
            return null;
        }
        SearchType searchType = NewSearchApi.getSearchType(category);
        if (searchType == null) {
            return null;
        }
        return keyword + "的" + searchType.getName();
    }

    public static String getSearchName(String category) {
        if (TextUtils.isEmpty(category)) {
            return null;
        }
        SearchType searchType = NewSearchApi.getSearchType(category);
        if (searchType == null) {
            return null;
        }
        return searchType.getName();
    }


    /**
     * 获取本地图片资源
     */
    public static int getImageResId(String category) {
        SearchType searchType = NewSearchApi.getSearchType(category);
        if (searchType == null) {
            return 0;
        }
        return searchType.getImageResId();
    }


    /**
     * 通过分类拿到对应的枚举状态
     */
    public static SearchType getSearchType(String category) {
        if (!TextUtils.isEmpty(category)) {
            for (SearchType searchType : SearchType.values()) {
                if (searchType.getType()
                        .equals(category)) {
                    return searchType;
                }
            }
        }
        return null;
    }

    /**
     * 获取商家、酒店列表
     */
    public static Observable<HljHttpSearch<List<Merchant>>> getMerchantList(
            long cityCode,
            int cpmCount,
            int isCpmOnly,
            String keyword,
            SearchType searchType,
            SearchFilter filter,
            String sort,
            final int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            //商家
            if (searchType == SearchType.SEARCH_TYPE_MERCHANT && filter.getPropertyId() > 0) {
                filterMap.put("filter[property_id]", String.valueOf(filter.getPropertyId()));
            }
            if (searchType == SearchType.SEARCH_TYPE_HOTEL) {
                DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
                double filterPriceMin = filter.getPriceMin();
                double filterPriceMax = filter.getPriceMax();
                if (filterPriceMin > 0) {
                    filterMap.put("filter[price_start]", decimalFormat.format(filterPriceMin));
                }
                if (filterPriceMax > filterPriceMin) {
                    filterMap.put("filter[price_end]", decimalFormat.format(filterPriceMax));
                }
                if (filter.getTableMin() > 0) {
                    filterMap.put("filter[table_min]", String.valueOf(filter.getTableMin()));
                }
                if (filter.getTableMax() > 0) {
                    filterMap.put("filter[table_max]", String.valueOf(filter.getTableMax()));
                }
            }
        }
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getMerchantList(cityCode,
                        cpmCount,
                        isCpmOnly,
                        keyword,
                        searchType.type,
                        filterMap,
                        getSort(sort),
                        page)
                .map(new HljHttpResultFunc<HljHttpSearch<List<Merchant>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取套餐案例列表
     */
    public static Observable<HljHttpSearch<List<Work>>> getWordCaseList(
            long cityCode,
            int cpmCount,
            String keyword,
            SearchType searchType,
            SearchFilter filter,
            String sort,
            int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            if (filter.getPropertyId() > 0) {
                filterMap.put("filter[property_id]", String.valueOf(filter.getPropertyId()));
            }
            if (searchType == SearchType.SEARCH_TYPE_WORK) {
                filterMap = filterPrice(filter, filterMap);
            }
        }
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getWordCaseList(cityCode,
                        cpmCount,
                        keyword,
                        searchType.type,
                        filterMap,
                        getSort(sort),
                        page)
                .map(new HljHttpResultFunc<HljHttpSearch<List<Work>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取笔记列表
     */
    public static Observable<HljHttpSearch<List<Note>>> getNoteList(
            String keyword, SearchType searchType, int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put("sort[]", "default");
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getNoteList(keyword, searchType.type, page, filterMap)
                .map(new HljHttpResultFunc<HljHttpSearch<List<Note>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取问答列表
     */
    public static Observable<HljHttpData<List<Answer>>> getQaList(
            String keyword, SearchType searchType, int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put("sort[]", "default");
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getQaList(keyword, searchType.type, page, filterMap)
                .map(new HljHttpResultFunc<HljHttpData<List<Answer>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取问答列表
     */
    public static Observable<HljHttpSearch<List<CommunityThread>>> getThreadsList(
            String keyword, SearchType searchType, int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put("sort[]", "default");
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getThreadsList(keyword, searchType.type, page, filterMap)
                .map(new HljHttpResultFunc<HljHttpSearch<List<CommunityThread>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚品列表
     */
    public static Observable<HljHttpSearch<List<ShopProduct>>> getShopProductList(
            long cityCode,
            String keyword,
            SearchType searchType,
            SearchFilter filter,
            String sort,
            int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            if (filter.getCategoryId() > 0) {
                filterMap.put("filter[cate_id_one_level]", String.valueOf(filter.getCategoryId()));
            }
            filterMap = filterPrice(filter, filterMap);
            //shiping  0 包邮  can_refund   1 消费保障
            long service = filter.getProductService();// 婚品搜索筛选选项：1包邮 2消费保障
            if (service == 1) {
                filterMap.put("filter[shiping]", String.valueOf(0));
            } else if (service == 2) {
                filterMap.put("filter[can_refund]", String.valueOf(1));
            }
        }
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getShopProductList(cityCode,
                        keyword,
                        searchType.type,
                        filterMap,
                        getSort(sort),
                        page)
                .map(new HljHttpResultFunc<HljHttpSearch<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚车列表
     */
    public static Observable<HljHttpSearch<List<WeddingCarProduct>>> getWeddingCarList(
            long cityCode,
            String keyword,
            SearchType searchType,
            SearchFilter filter,
            String sort,
            int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        if (filter != null) {
            filterMap = filterPrice(filter, filterMap);
            if (filter.getPropertyId() > 0) {
                //1套餐 2自选
                filterMap.put("filter[type]", String.valueOf(filter.getPropertyId()));
            }
        }
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getWeddingCarList(cityCode,
                        keyword,
                        searchType.type,
                        filterMap,
                        getSort(sort),
                        page)
                .map(new HljHttpResultFunc<HljHttpSearch<List<WeddingCarProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取搜索热词
     *
     * @param searchType 搜索分类
     */
    public static Observable<List<NewHotKeyWord>> getHotSearchWords(
            SearchType searchType, final NewHotKeyWord hotKeyWord) {
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getHotSearchWords(searchType == null ? null : searchType.getType())
                .map(new HljHttpResultFunc<HljHttpData<List<NewHotKeyWord>>>())
                .concatMap(new Func1<HljHttpData<List<NewHotKeyWord>>, Observable<? extends
                        List<NewHotKeyWord>>>() {
                    @Override
                    public Observable<? extends List<NewHotKeyWord>> call
                            (HljHttpData<List<NewHotKeyWord>> listHljHttpData) {
                        return Observable.from(listHljHttpData.getData())
                                .distinct(new Func1<NewHotKeyWord, String>() {
                                    @Override
                                    public String call(NewHotKeyWord newHotKeyWord) {
                                        return newHotKeyWord.getCategory() + newHotKeyWord
                                                .getTitle();
                                    }
                                })
                                .filter(new Func1<NewHotKeyWord, Boolean>() {
                                    @Override
                                    public Boolean call(NewHotKeyWord newHotKeyWord) {
                                        return !(hotKeyWord != null && (hotKeyWord.getTitle() +
                                                hotKeyWord.getCategory()).equals(
                                                newHotKeyWord.getTitle() + newHotKeyWord
                                                        .getCategory()));
                                    }
                                })
                                .toList();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 输入框热搜词
     */
    public static Observable<NewHotKeyWord> getInputWord(InputType inputType) {
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getInputWord(inputType == null ? 0 : inputType.getType())
                .map(new HljHttpResultFunc<NewHotKeyWord>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 处理筛选价格
     */
    private static HashMap<String, String> filterPrice(
            SearchFilter filter, HashMap<String, String> filterMap) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        double filterPriceMin = filter.getPriceMin();
        double filterPriceMax = filter.getPriceMax();
        //只有最小值的筛选
        if (filterPriceMin > 0 && filterPriceMax == 0) {
            filterMap.put("filter[actual_price]", decimalFormat.format(filterPriceMin) + ",");
        }
        if (filterPriceMax > filterPriceMin) {
            filterMap.put("filter[actual_price]",
                    decimalFormat.format(filterPriceMin) + "," + decimalFormat.format
                            (filterPriceMax));
        }
        return filterMap;
    }

    /**
     * 获取排序的条件，后期支持多排序
     */
    private static HashMap<String, String> getSort(String sort) {
        HashMap<String, String> sortMap = new HashMap<>();
        if (!TextUtils.isEmpty(sort)) {
            sortMap.put("sort[]", sort);
        }
        return sortMap;
    }

    /**
     * 获取下拉提示
     */
    public static Observable<NewSearchTips> getSearchTips(
            String keyword) {
        return HljHttp.getRetrofit()
                .create(NewSearchService.class)
                .getSearchTips(keyword)
                .map(new HljHttpResultFunc<NewSearchTips>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
