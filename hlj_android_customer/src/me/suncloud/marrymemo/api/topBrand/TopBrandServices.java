package me.suncloud.marrymemo.api.topBrand;

import com.hunliji.hljhttplibrary.entities.HljHttpResult;

import java.util.List;

import me.suncloud.marrymemo.model.topBrand.CostEffective;
import me.suncloud.marrymemo.model.topBrand.WeddingBrand;
import retrofit2.http.GET;
import rx.Observable;

/**
 * 品牌馆 性价比top
 * Created by jinxin on 2016/11/14.
 */

public interface TopBrandServices {

    /**
     * 品牌馆api
     *
     * @return
     */
    @GET("/p/wedding/home/APIContentBrandHall/index")
    Observable<HljHttpResult<WeddingBrand>> getBrand();

    /**
     * 性价比api
     *
     * @return
     */
    @GET("/p/wedding/index.php/home/APIAdCostEffective/list")
    Observable<HljHttpResult<List<CostEffective>>> getCostEffective();

}
