package me.suncloud.marrymemo.api.product;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.wrappers.FreeShippingFeeWrapper;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResult;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResultList;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.Category;
import me.suncloud.marrymemo.model.orders.ProductOrderExpressInfo;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.model.wrappers.HljHttpMarksData;
import me.suncloud.marrymemo.model.wrappers.ProductCartBody;
import me.suncloud.marrymemo.model.wrappers.ProductCollect;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/11.
 */

public class ProductApi {

    public static Observable<ShopProduct> getShopProduct(long id, long cid) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getShopProduct(id, cid)
                .map(new HljHttpResultFunc<ShopProduct>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Merchant> getDetailMerchant(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getDetailMerchant(id)
                .map(new HljHttpResultFunc<Merchant>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏
     *
     * @param id 商品id
     * @return boolean 收藏结果
     */
    public static Observable<Boolean> collect(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .collect(new ProductCollect(id))
                .map(new HljHttpResultFunc<ProductCollect>())
                .map(new Func1<ProductCollect, Boolean>() {
                    @Override
                    public Boolean call(ProductCollect productCollect) {
                        return "insert".equals(productCollect.getAction());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消收藏
     *
     * @param id 商品id
     * @return boolean 取消收藏结果
     */
    public static Observable<Boolean> cancelCollect(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .cancelCollect(new ProductCollect(id))
                .map(new HljHttpResultFunc<ProductCollect>())
                .map(new Func1<ProductCollect, Boolean>() {
                    @Override
                    public Boolean call(ProductCollect productCollect) {
                        return "delete".equals(productCollect.getAction());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加购物车
     *
     * @param skuId     规格id
     * @param productId 商品id
     * @param quantity  数量
     * @return long 购物车id
     */
    public static Observable<Long> addCart(long skuId, long productId, int quantity) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .addCart(new ProductCartBody(skuId, productId, quantity))
                .map(new HljHttpResultFunc<ProductCartBody>())
                .map(new Func1<ProductCartBody, Long>() {
                    @Override
                    public Long call(ProductCartBody body) {
                        return body.getId();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * chen_bin
     * 婚品分类列表
     *
     * @return
     */
    public static Observable<HljHttpData<List<Category>>> getProductCategoriesObb() {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductCategories()
                .map(new HljHttpResultFunc<HljHttpData<List<Category>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品频道tab分类
     *
     * @return
     */
    public static Observable<HljHttpData<List<Label>>> getProductLabelsObb(int type) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductLabels(type)
                .map(new HljHttpResultFunc<HljHttpData<List<Label>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品专题列表页
     *
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpData<List<ProductTopic>>> getProductSubPagesObb(
            int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductSubPages(page, perPage)
                .map(new HljHttpResultFunc<HljHttpData<List<ProductTopic>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品专题标签列表页
     *
     * @param markId
     * @param page
     * @param perPage
     * @return
     */
    public static Observable<HljHttpMarksData<List<ProductTopic>>> getProductSubPagesByMarkIdObb(
            long markId, int page, int perPage) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductSubPagesByMarkId(markId, page, perPage)
                .map(new HljHttpResultFunc<HljHttpMarksData<List<ProductTopic>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品专题详情
     *
     * @param id
     * @return
     */
    public static Observable<TopicUrl> getProductSubPageDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductSubPageDetail(id)
                .map(new HljHttpResultFunc<TopicUrl>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得婚品全部分类
     *
     * @param needSaveLocal 需要将数据保存到本地
     * @return
     */
    public static Observable<List<ShopCategory>> getProductProperty(
            final Context mContext, String url, final boolean needSaveLocal) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductProperty(url)
                .map(new Func1<HljHttpResult<JsonElement>, List<ShopCategory>>() {
                    @Override
                    public List<ShopCategory> call(HljHttpResult<JsonElement> hljResult) {
                        if (hljResult.getStatus()
                                .getRetCode() != 0) {
                            throw new HljApiException(hljResult.getStatus());
                        }
                        JsonArray array = hljResult.getData()
                                .getAsJsonObject()
                                .getAsJsonArray("list");
                        if (array != null && array.size() > 0) {
                            if (needSaveLocal) {
                                try {
                                    //保存数据
                                    OutputStreamWriter out = new OutputStreamWriter(mContext
                                            .openFileOutput(
                                            Constants.PROPERTIES3_FILE,
                                            Context.MODE_PRIVATE));
                                    out.write(array.toString());
                                    out.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            List<ShopCategory> categories = new ArrayList<>();
                            for (ShopCategory shopCategory : GsonUtil.getGsonInstance()
                                    .fromJson(array, ShopCategory[].class)) {
                                //空数据过滤
                                if (shopCategory.getId() > 0) {
                                    categories.add(shopCategory);
                                }
                            }
                            return categories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得婚品分类页 专题
     *
     * @return
     */
    public static Observable<Map<Long, ProductTopic>> getProductSubPage(String url) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductSubPage(url)
                .map(new HljHttpResultFunc<Map<Long, ProductTopic>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品标签
     *
     * @param entityId
     * @return
     */
    public static Observable<HljHttpData<List<CategoryMark>>> getShopProductTags(long entityId) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getShopProductTags(entityId)
                .map(new HljHttpResultFunc<HljHttpData<List<CategoryMark>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 婚品分类详情页 婚品列表
     *
     * @return
     */
    public static Observable<ProductSearchResultList> getCategoryDetailProductList(
            long categoryId, String tags, String order, int page) {
        HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put("filter[category]", String.valueOf(categoryId));
        filterMap.put("filter[tags]", TextUtils.isEmpty(tags) ? "" : tags);
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getCategoryDetailProductList(filterMap, 2, order, page)
                .flatMap(new Func1<HljHttpResult<ProductSearchResult>,
                        Observable<ProductSearchResultList>>() {
                    @Override
                    public Observable<ProductSearchResultList> call(
                            HljHttpResult<ProductSearchResult> productSearchResultHljHttpResult) {
                        if (productSearchResultHljHttpResult == null) {
                            return null;
                        }
                        ProductSearchResult result = productSearchResultHljHttpResult.getData();
                        if (result == null) {
                            return null;
                        }
                        Observable<ProductSearchResultList> obb = Observable.just(result
                                .getProductList());
                        return obb;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得购物车列表
     * 为使用老model 手动解析数据
     *
     * @return
     */
    public static Observable<List<ShoppingCartGroup>> getShoppingCartItem() {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getShoppingCartItem()
                .map(new HljHttpResultFunc<List<ShoppingCartGroup>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * (1:用户行为,婚品精选 2:购物车 3:支付成功)
     * 婚品推荐list
     *
     * @param
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getUserRecommendProduct(int type) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getUserRecommendProduct(type)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚礼购首页的人气推荐列表，与上面这个接口一样，但是需要分页
     *
     * @param page
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getHomeRecommendProduct(int page) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getHomeRecommendProduct(page)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获得订单物流状态信息
     *
     * @param id
     * @return
     */
    public static Observable<ProductOrderExpressInfo> getOrderExpressInfo(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getOrderExpressInfo(id)
                .map(new HljHttpResultFunc<ProductOrderExpressInfo>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取每周上新婚品
     *
     * @return
     */
    public static Observable<HljHttpData<List<ShopProduct>>> getWeekProducts() {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getWeekNewProducts(1, 3)
                .map(new HljHttpResultFunc<HljHttpData<List<ShopProduct>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<FreeShippingFeeWrapper> getFreeShippingFeeObb(
            long addressId, long productId) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getFreeShippingFee(addressId, productId)
                .map(new HljHttpResultFunc<FreeShippingFeeWrapper>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取婚品商家优惠券列表
     *
     * @param id
     * @return
     */
    public static Observable<HljHttpData<List<CouponInfo>>> getProductMerchantCoupons(long id) {
        return HljHttp.getRetrofit()
                .create(ProductService.class)
                .getProductMerchantCoupons(id)
                .map(new HljHttpResultFunc<HljHttpData<List<CouponInfo>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}